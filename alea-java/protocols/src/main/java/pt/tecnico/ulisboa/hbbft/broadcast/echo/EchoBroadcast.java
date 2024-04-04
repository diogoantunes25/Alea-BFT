package pt.tecnico.ulisboa.hbbft.broadcast.echo;

import pt.tecnico.ulisboa.hbbft.NetworkInfo;
import pt.tecnico.ulisboa.hbbft.Step;
import pt.tecnico.ulisboa.hbbft.broadcast.BroadcastMessage;
import pt.tecnico.ulisboa.hbbft.broadcast.IBroadcast;
import pt.tecnico.ulisboa.hbbft.broadcast.echo.messages.EchoMessage;
import pt.tecnico.ulisboa.hbbft.broadcast.echo.messages.FinalMessage;
import pt.tecnico.ulisboa.hbbft.broadcast.echo.messages.SendMessage;
import pt.tecnico.ulisboa.hbbft.broadcast.echo.utils.SignatureUtils;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;
import java.util.stream.Collectors;

public class EchoBroadcast implements IBroadcast {

    // The protocol instance identifier.
    private final String pid;

    // The replica id.
    private final Integer replicaId;

    // The network configuration.
    private final NetworkInfo networkInfo;

    // The ID of the sending node.
    private final Integer proposerId;

    private final PrivateKey privateKey;

    private final Map<Integer, PublicKey> publicKeys;

    // If we are the proposer: whether we have already sent the `Send` message.
    private Boolean sendSent = false;

    // Whether we have already sent an `Echo` back to the proposer.
    private Boolean echoSent = false;

    // If we are the proposer: whether we have already sent the `Final` message.
    private Boolean finalSent = false;

    // The `Echo` messages received, by sender ID.
    private Map<Integer, EchoMessage> echos = new TreeMap<>();

    // Whether we have already output a value.
    private Boolean decided = false;

    // The value to output when ready.
    private byte[] decidedValue;

    private final EchoBroadcastMessageFactory messageFactory;

    public EchoBroadcast(
            String pid,
            Integer replicaId,
            NetworkInfo networkInfo,
            Integer proposerId,
            PrivateKey privateKey,
            Map<Integer, PublicKey> publicKeys
    ) {
        this.pid = pid;
        this.replicaId = replicaId;
        this.networkInfo = networkInfo;
        this.proposerId = proposerId;

        this.privateKey = privateKey;
        this.publicKeys = publicKeys;

        this.messageFactory = new EchoBroadcastMessageFactory(pid, replicaId);
    }

    @Override
    public String getPid() {
        return this.pid;
    }

    @Override
    public Step<byte[]> handleInput(byte[] input) {
        Step<byte[]> step = new Step<>();
        if (!this.replicaId.equals(proposerId) || this.sendSent) {
            return step;
        }
        this.sendSent = true;
        SendMessage sendMessage = messageFactory.createSendMessage(input);
        return this.sendMessage(sendMessage);
    }

    @Override
    public Step<byte[]> handleMessage(BroadcastMessage message) {
        // Check if BID matches this instance
        if (!message.getPid().equals(pid)) return new Step<>();

        // Route the message
        switch (message.getType()) {
            case SendMessage.SEND: {
                return handleSendMessage((SendMessage) message);
            }
            case EchoMessage.ECHO: {
                return handleEchoMessage((EchoMessage) message);
            }
            case FinalMessage.FINAL: {
                return handleFinalMessage((FinalMessage) message);
            }
            default: {
                return new Step<>();
            }
        }
    }

    @Override
    public boolean hasTerminated() {
        return this.decided;
    }

    @Override
    public Optional<byte[]> deliver() {
        return Optional.ofNullable(this.decidedValue);
    }

    /**
     * Called by the protocol to indicate that
     * a {@link SendMessage} has been received.
     *
     * @param sendMessage the received message
     */
    private Step<byte[]> handleSendMessage(SendMessage sendMessage) {
        if (!sendMessage.getSender().equals(this.proposerId) || this.echoSent) {
            return new Step<>();
        }
        this.echoSent = true;

        // Compute signature
        byte[] value = sendMessage.getValue();
        String toSign = String.format("%s-%s", pid, Base64.getEncoder().encodeToString(value));
        byte[] signature = SignatureUtils.sign(this.privateKey, toSign.getBytes());

        // Send `Echo` message back to proposer
        EchoMessage echoMessage = messageFactory.createEchoMessage(value, signature);
        return this.sendMessage(echoMessage, this.proposerId);
    }

    /**
     * Called by the protocol to indicate that
     * a {@link EchoMessage} has been received.
     *
     * @param echoMessage the received message
     */
    private Step<byte[]> handleEchoMessage(EchoMessage echoMessage) {
        final int senderId = echoMessage.getSender();

        Step<byte[]> step = new Step<>();
        if (this.echos.containsKey(senderId)) {
            return step; // Duplicate `Echo` message
        }

        // Verify the signature
        final byte[] value = echoMessage.getValue();
        final byte[] signature = echoMessage.getSignature();
        String toVerify = String.format("%s-%s", pid, Base64.getEncoder().encodeToString(value));
        if (!SignatureUtils.verify(publicKeys.get(senderId), toVerify.getBytes(), signature)) {
            return step; // Invalid signature
        }

        // Save the `Echo` message
        this.echos.put(senderId, echoMessage);
        if (this.finalSent) {
            return step; // Already sent `Final` message
        }

        // Upon receiving `2*f + 1` valid `Echo`s for the same value
        int quorum = 2*networkInfo.getF() + 1;
        List<EchoMessage> validEchos = this.echos.values().stream()
                .filter(e -> Arrays.equals(e.getValue(), value)).collect(Collectors.toList());
        if (validEchos.size() == quorum) {
            this.finalSent = true;

            // Compute signature proof
            Map<Integer, byte[]> proof = validEchos.stream()
                    .collect(Collectors.toMap(EchoMessage::getSender, EchoMessage::getSignature));

            // Send `Final` message to all replicas
            FinalMessage finalMessage = messageFactory.createFinalMessage(value, proof);
            step.add(this.sendMessage(finalMessage));
        }

        return step;
    }

    /**
     * Called by the protocol to indicate that
     * a {@link FinalMessage} has been received.
     *
     * @param finalMessage the received message
     */
    private Step<byte[]> handleFinalMessage(FinalMessage finalMessage) {
        Step<byte[]> step = new Step<>();
        if (!finalMessage.getSender().equals(this.proposerId) || this.hasTerminated()) {
            return step; // Not proposer or already decided
        }

        // Verify all signatures the proof
        final byte[] value = finalMessage.getValue();
        final Map<Integer, byte[]> proof = finalMessage.getProof();
        String toVerify = String.format("%s-%s", pid, Base64.getEncoder().encodeToString(value));
        boolean verified = proof.entrySet().stream()
                .allMatch(e -> SignatureUtils.verify(publicKeys.get(e.getKey()), toVerify.getBytes(), e.getValue()));
        if (!verified) {
            return step; // Invalid proof
        }

        // Deliver
        this.decided = true;
        this.decidedValue = value;
        step.add(value);

        return step;
    }

    private Step<byte[]> sendMessage(BroadcastMessage message) {
        Step<byte[]> step = new Step<>();
        for (int id=0; id < this.networkInfo.getN(); id++)
            step.add(this.sendMessage(message, id));
        return step;
    }

    private Step<byte[]> sendMessage(BroadcastMessage message, int target) {
        Step<byte[]> step = new Step<>();
        if (target == this.replicaId) step.add(this.handleMessage(message));
        else step.add(message, target);
        return step;
    }
}
