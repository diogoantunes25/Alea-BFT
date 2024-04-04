package pt.tecnico.ulisboa.hbbft.vbroadcast.echo;

import pt.tecnico.ulisboa.hbbft.NetworkInfo;
import pt.tecnico.ulisboa.hbbft.Step;
import pt.tecnico.ulisboa.hbbft.utils.threshsig.SigShare;
import pt.tecnico.ulisboa.hbbft.utils.threshsig.Signature;
import pt.tecnico.ulisboa.hbbft.utils.threshsig.ThreshSigUtils;
import pt.tecnico.ulisboa.hbbft.utils.threshsig.exceptions.NotEnoughSharesException;
import pt.tecnico.ulisboa.hbbft.vbroadcast.VBroadcastMessage;
import pt.tecnico.ulisboa.hbbft.vbroadcast.VOutput;
import pt.tecnico.ulisboa.hbbft.vbroadcast.echo.messages.EchoMessage;
import pt.tecnico.ulisboa.hbbft.vbroadcast.echo.messages.FinalMessage;
import pt.tecnico.ulisboa.hbbft.vbroadcast.echo.messages.SendMessage;
import pt.ulisboa.tecnico.utils.Utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class EchoVBroadcast2 implements IEchoVBroadcast {

    // The protocol instance identifier.
    private final String pid;

    // The replica id.
    private final Integer replicaId;

    // The network configuration.
    private final NetworkInfo networkInfo;

    // The ID of the sending node.
    private final Integer proposerId;

    // Threshold signature utils.
    private final ThreshSigUtils sigUtils;

    private final EchoVBroadcastMessageFactory messageFactory;

    // If we are the proposer: whether we have already sent the `Send` message.
    private AtomicBoolean sendSent = new AtomicBoolean(false);

    // Whether we have already sent an `Echo` back to the proposer.
    private AtomicBoolean echoSent = new AtomicBoolean(false);

    // If we are the proposer: whether we have already sent the `Final` message.
    private AtomicBoolean finalSent = new AtomicBoolean(false);

    // The `Echo` messages received, by sender ID.
    private final Map<Integer, EchoMessage> echoMessages = new ConcurrentHashMap<>();

    // Which signatures were already verified
    private final Map<Integer, Boolean> verifiedEchos = new ConcurrentHashMap<>();

    private AtomicBoolean terminated = new AtomicBoolean(false);

    private byte[] input;

    private VOutput output;

    public EchoVBroadcast2(
            String pid,
            Integer replicaId,
            NetworkInfo networkInfo,
            Integer proposerId
    ) {
        this.pid = pid;
        this.replicaId = replicaId;
        this.networkInfo = networkInfo;
        this.proposerId = proposerId;

        this.sigUtils = networkInfo.getSigUtils();
        this.messageFactory = new EchoVBroadcastMessageFactory(pid, replicaId);
    }

    @Override
    public String getPid() {
        return this.pid;
    }

    @Override
    public Step<VOutput> handleInput(byte[] input) {
        Step<VOutput> step = new Step<>();

        // ignore if not proposer or already proposed
        if (!this.replicaId.equals(proposerId) || this.sendSent.get()) {
            return step;
        }

        Utils.vcbcInitStart(pid);
        byte[] hashedInput = hash(input);

        this.sendSent.set(true);
        this.input = input;

        SendMessage sendMessage = messageFactory.createSendMessage(hashedInput);
        step = this.send(sendMessage);

        return step;
    }

    @Override
    public Step<VOutput> handleMessage(VBroadcastMessage message) {

        // ignore messages with wrong pid
        if (!message.getPid().equals(pid)) {
            return new Step<>();
        }

        Step<VOutput> step;
        switch (message.getType()) {
            case SendMessage.SEND: {
                step = this.handleSendMessage((SendMessage) message);
                return step;
            }
            case EchoMessage.ECHO: {
                step = this.handleEchoMessage((EchoMessage) message);
                return step;
            }
            case FinalMessage.FINAL: {
                step = this.handleFinalMessage((FinalMessage) message);
                return step;
            }
            default: {
                return new Step<>();
            }
        }
    }

    @Override
    public boolean hasTerminated() {
        return this.terminated.get();
    }

    @Override
    public Optional<VOutput> deliver() {
        return Optional.ofNullable(this.output);
    }

    @Override
    public Step<VOutput> handleSendMessage(SendMessage sendMessage) {

        Step<VOutput> step = new Step<>();

        // ignore `Send` message not from the proposer or if already echoed
        if (!sendMessage.getSender().equals(this.proposerId) || this.echoSent.get()) {
            return step;
        }

        // Only non-leaders log this start (leader already did)
        if (sendMessage.getSender() != this.replicaId) {
            Utils.vcbcInitStart(pid);
        }

        // set `Echo` flag
        this.echoSent.set(true);

        // compute signature share

        SigShare share;
        byte[] value = sendMessage.getValue();
        byte[] toSign = String.format("%s-%s", pid, Base64.getEncoder().encodeToString(value)).getBytes();

        if (Utils.BYZ) {
            // Produce bad signatures (sign the wrong thing)
            (new Random()).nextBytes(toSign);
        }

        share = sigUtils.sign(toSign);

        // send `Echo` message back to proposer
        EchoMessage echoMessage = messageFactory.createEchoMessage(value, share);

        step = this.send(echoMessage, this.proposerId);

        Utils.vcbcInitEnd(pid);

        return step;
    }

    @Override
    public Step<VOutput> handleEchoMessage(EchoMessage echoMessage) {

        Step<VOutput> step = new Step<>();

        // ignore if not proposer, not proposed or duplicate `Echo`
        final int senderId = echoMessage.getSender();
        if (!this.replicaId.equals(proposerId) || !this.sendSent.get() || this.echoMessages.containsKey(senderId)) {
            return step;
        }

        // verify the value
        final byte[] value = echoMessage.getValue();
        if (value == null || !Arrays.equals(value, this.hash(input))) {
            return step;
        }

        final SigShare share = echoMessage.getShare();
        byte[] toVerify = String.format("%s-%s", pid, Base64.getEncoder().encodeToString(value)).getBytes();

        verifiedEchos.put(senderId, false);

        // verify threshold signature share
        if (!Utils.QUICK_SIG_VER) {
            boolean correct = sigUtils.verifyShare(toVerify, share, echoMessage.getSender());
            if (!correct) {
                return step;
            }

            verifiedEchos.put(senderId, true);
        }

        // save `Echo` message
        this.echoMessages.put(senderId, echoMessage);

        // already sent `Final` message
        if (this.finalSent.get()) {
            return step;
        }

        // upon receiving `2*f + 1` valid `Echo` messages
        int quorum = 2*networkInfo.getF() + 1;
        // logger.info("Quorum of {}", this.echoMessages.size());
        if (this.echoMessages.size() >= quorum && !finalSent.getAndSet(true)) {
            Set<SigShare> shares = this.echoMessages.values().stream()
                    .map(EchoMessage::getShare).collect(Collectors.toSet());
            Signature signature = null;

            try {
                try {
                    signature = sigUtils.combine(toVerify, shares);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    System.exit(2);
                }
            } catch (NotEnoughSharesException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }

            Utils.vcbcFinalStart(pid);

            // Send `Final` message to all replicas
            FinalMessage finalMessage = messageFactory.createFinalMessage(input, signature.toBytes());

            step.add(this.send(finalMessage));
        }

        return step;
    }

    @Override
    public Step<VOutput> handleFinalMessage(FinalMessage finalMessage) {
        Step<VOutput> step = new Step<>();

        // ignore if already decided
        if (terminated.getAndSet(true)) {
            return step;
        }

        // Only non-leaders log this start (leader already did)
        if (finalMessage.getSender() != this.replicaId) {
            Utils.vcbcFinalStart(pid);
        }

        // verify threshold signature
        final byte[] value = finalMessage.getValue();
        final byte[] hashedValue = this.hash(value);
        if (hashedValue == null) return step;
        final byte[] signature = finalMessage.getSignature();
        String toVerify = String.format("%s-%s", pid, Base64.getEncoder().encodeToString(hashedValue));

        boolean verified = sigUtils.verifyFull(toVerify.getBytes(), Signature.fromBytes(signature));

        if (!verified) {
            return step;
        }

        // deliver
        this.output = new VOutput(value, signature);
        step.add(this.output);

        Utils.vcbcFinalEnd(pid);

        return step;
    }

    // private byte[] hash(byte[] value) {
    //     return value;
    // }

    private byte[] hash(byte[] value) {
        byte[] hashed = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            hashed =  digest.digest(value);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hashed;
    }

    // Broadcasts message
    private Step<VOutput> send(VBroadcastMessage message) {

        Step<VOutput> step = new Step<>();

        // Send to itself
        step.add(this.handleMessage(message));

        // Send to remaining
        step.add(message, this.networkInfo.getValidatorSet().getAllIds().stream()
                .filter(id -> !id.equals(this.replicaId)).collect(Collectors.toList()));

        return step;
    }

    // Unicasts message
    private Step<VOutput> send(VBroadcastMessage message, int target) {
        Step<VOutput> step = new Step<>();
        if (target == this.replicaId) step.add(this.handleMessage(message));
        else step.add(message, target);
        return step;
    }
}
