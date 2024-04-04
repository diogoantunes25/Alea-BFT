package pt.tecnico.ulisboa.hbbft.vbroadcast.prbc;

import pt.tecnico.ulisboa.hbbft.NetworkInfo;
import pt.tecnico.ulisboa.hbbft.ProtocolMessage;
import pt.tecnico.ulisboa.hbbft.Step;
import pt.tecnico.ulisboa.hbbft.broadcast.BroadcastMessage;
import pt.tecnico.ulisboa.hbbft.broadcast.bracha.BrachaBroadcast;
import pt.tecnico.ulisboa.hbbft.utils.ThreshsigUtil;
import pt.tecnico.ulisboa.hbbft.utils.threshsig.SigShare;
import pt.tecnico.ulisboa.hbbft.utils.threshsig.ThreshSigUtils;
import pt.tecnico.ulisboa.hbbft.utils.threshsig.exceptions.NotEnoughSharesException;
import pt.tecnico.ulisboa.hbbft.vbroadcast.VOutput;
import pt.tecnico.ulisboa.hbbft.vbroadcast.prbc.messages.DoneMessage;

import java.util.*;
import java.util.stream.Collectors;

public class ProvableReliableBroadcast implements IProvableReliableBroadcast {

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

    // The reliable broadcast instance
    private final BrachaBroadcast rbc;

    // The `Done` messages received, by sender ID.
    private final Map<Integer, DoneMessage> doneMessages = new TreeMap<>();

    private VOutput output;

    public ProvableReliableBroadcast(
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

        this.rbc = new BrachaBroadcast(pid, replicaId, networkInfo, proposerId);
    }

    @Override
    public String getPid() {
        return this.pid;
    }

    @Override
    public Step<VOutput> handleInput(byte[] input) {
        // ignore input if not the proposer
        if (!this.replicaId.equals(proposerId)) return new Step<>();

        // forward the input input into an RBC instance
        Step<byte[]> rbcStep = this.rbc.handleInput(input);
        return this.handleRbcStep(rbcStep);
    }

    @Override
    public Step<VOutput> handleMessage(ProtocolMessage message) {
        // ignore messages tagged with a different PID
        if (!message.getPid().equals(pid)) return new Step<>();

        // route the message to the corresponding handler
        if (message instanceof BroadcastMessage) {
            // handle RBC sub-protocol message
            Step<byte[]> rbcStep = this.rbc.handleMessage((BroadcastMessage) message);
            return this.handleRbcStep(rbcStep);

        } else if (message.getType() == DoneMessage.DONE) {
            // handle Done message
            return this.handleDoneMessage((DoneMessage) message);
        }
        return new Step<>();
    }

    @Override
    public boolean hasTerminated() {
        return this.output != null;
    }

    @Override
    public Optional<VOutput> deliver() {
        return Optional.ofNullable(this.output);
    }

    private Step<VOutput> handleDoneMessage(DoneMessage doneMessage) {
        final int senderId = doneMessage.getSender();

        Step<VOutput> step = new Step<>();
        if (this.doneMessages.containsKey(senderId)) {
            return step;    // ignore duplicates
        }

        // verify signature share
        final SigShare share = doneMessage.getShare();
        byte[] toVerify = pid.getBytes();
        if (!sigUtils.verifyShare(toVerify, share, doneMessage.getSender())) {
            return step;    // invalid share
        }

        // save `Done` message
        this.doneMessages.put(senderId, doneMessage);

        return this.tryOutput();
    }

    private Step<VOutput> handleRbcStep(Step<byte[]> rbcStep) {
        Step<VOutput> step = new Step<>(rbcStep.getMessages());

        // ignore if RBC output is empty
        Vector<byte[]> output = rbcStep.getOutput();
        if (output.isEmpty()) return step;

        // compute threshold signature
        String toSign = String.format("%s", rbc.getPid());
        SigShare sigShare = sigUtils.sign(toSign.getBytes());

        // multicast `Done` message
        DoneMessage doneMessage = new DoneMessage(pid, replicaId, sigShare);
        step.add(this.send(doneMessage));

        return step;
    }

    private Step<VOutput> tryOutput() {
        Step<VOutput> step = new Step<>();

        // ignore if already output
        if (this.hasTerminated()) return step;

        // ignore if RBC hasn't terminated
        Optional<byte[]> rbcOutput = this.rbc.deliver();
        if (rbcOutput.isEmpty()) return step;

        // upon receiving f + 1 valid `Done` messages
        final int quorum = networkInfo.getF() + 1;
        if (this.doneMessages.size() < quorum) return step;

        // combine signature shares into a threshold signature
        Set<SigShare> shares = this.doneMessages.values().stream()
                .map(DoneMessage::getShare).collect(Collectors.toSet());
        byte[] signature = new byte[0];
        try {
            signature = sigUtils.combine(pid.getBytes(), shares).toBytes();
        } catch (NotEnoughSharesException e) {
            throw new RuntimeException(e);
        }
        // TODO verify signature validity

        this.output = new VOutput(rbcOutput.get(), signature);
        step.add(this.output);

        return step;
    }

    private Step<VOutput> send(ProtocolMessage message) {
        Step<VOutput> step = new Step<>();
        step.add(this.handleMessage(message));
        step.add(message, this.networkInfo.getValidatorSet().getAllIds().stream()
                .filter(id -> !id.equals(this.replicaId)).collect(Collectors.toList()));
        return step;
    }
}
