package pt.tecnico.ulisboa.hbbft.election;

import pt.tecnico.ulisboa.hbbft.IProtocol;
import pt.tecnico.ulisboa.hbbft.NetworkInfo;
import pt.tecnico.ulisboa.hbbft.Step;
import pt.tecnico.ulisboa.hbbft.election.messages.ShareMessage;

import java.math.BigInteger;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

public class CommitteeElection implements IProtocol<Boolean, Set<Integer>, CommitteeElectionMessage> {

    // The protocol instance identifier.
    private final String pid;

    // Our ID.
    private final Integer replicaId;

    // The network configuration.
    private final NetworkInfo networkInfo;

    // The common coin.
    private final Coin coin;

    // The number of replicas in the elected committee.
    private final Integer committeeSize;

    // Whether we have already multicast our share.
    private Boolean shareSent = false;

    private Set<Integer> decision = new TreeSet<>();

    public CommitteeElection(
            String pid,
            Integer replicaId,
            NetworkInfo networkInfo,
            Integer committeeSize
    ) {
        assert committeeSize <= networkInfo.getN();

        this.pid = pid;
        this.replicaId = replicaId;
        this.networkInfo = networkInfo;
        this.coin = new Coin(pid, networkInfo.getSigUtils());
        this.committeeSize = committeeSize;
    }

    // TODO @Override
    public String getPid() {
        return this.pid;
    }

    @Override
    public Step<Set<Integer>> handleInput(Boolean input) {
        if (this.shareSent) {
            return new Step<>();
        }
        this.shareSent = true;
        byte[] share = coin.getMyShare();
        ShareMessage shareMessage = new ShareMessage(pid, replicaId, share);
        return this.sendShareMessage(shareMessage);
    }

    @Override
    public Step<Set<Integer>> handleMessage(CommitteeElectionMessage message) {
        if (!message.getPid().equals(pid)) return new Step<>();
        switch (message.getType()) {
            case ShareMessage.SHARE:
                return this.handleShareMessage((ShareMessage) message);
            default:
                return new Step<>();
        }
    }

    @Override
    public boolean hasTerminated() {
        return !this.decision.isEmpty();
    }

    @Override
    public Optional<Set<Integer>> deliver() {
        if (hasTerminated()) return Optional.of(decision);
        else return Optional.empty();
    }

    private Step<Set<Integer>> handleShareMessage(ShareMessage message) {
        if (this.coin.hasDecided()) return new Step<>();

        final int senderId = message.getSender();
        final byte[] share = message.getShare();
        this.coin.addShare(senderId, share);

        return this.tryOutput();
    }

    private Step<Set<Integer>> sendShareMessage(ShareMessage message) {
        Step<Set<Integer>> step = new Step<>();
        for (int id=0; id < this.networkInfo.getN(); id++) {
            if (id == this.replicaId) step.add(this.handleShareMessage(message));
            else step.add(message, id);
        }
        return step;
    }

    private Step<Set<Integer>> tryOutput() {
        Step<Set<Integer>> step = new Step<>();
        if (!this.coin.hasDecided() || this.hasTerminated()) return new Step<>();

        final int baseline = coin.getValue().mod(BigInteger.valueOf(networkInfo.getN())).intValue();
        for (int i=0; i < committeeSize; i++)
            this.decision.add((baseline + i) % networkInfo.getN());
        step.add(this.decision);

        return step;
    }
}
