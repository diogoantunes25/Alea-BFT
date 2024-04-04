package pt.tecnico.ulisboa.hbbft.agreement.mvba;

import pt.tecnico.ulisboa.hbbft.NetworkInfo;
import pt.tecnico.ulisboa.hbbft.ProtocolMessage;
import pt.tecnico.ulisboa.hbbft.Step;
import pt.tecnico.ulisboa.hbbft.agreement.mvba.messages.VoteMessage;
import pt.tecnico.ulisboa.hbbft.agreement.vba.ValidatedBoolean;
import pt.tecnico.ulisboa.hbbft.binaryagreement.BinaryAgreementMessage;
import pt.tecnico.ulisboa.hbbft.utils.ThreshsigUtil;
import pt.tecnico.ulisboa.hbbft.utils.threshsig.ThreshSigUtils;
import pt.tecnico.ulisboa.hbbft.vbroadcast.IVBroadcast;
import pt.tecnico.ulisboa.hbbft.vbroadcast.VBroadcastMessage;
import pt.tecnico.ulisboa.hbbft.vbroadcast.VOutput;
import pt.tecnico.ulisboa.hbbft.vbroadcast.echo.EchoVBroadcast2;
import pt.tecnico.ulisboa.hbbft.vbroadcast.echo.IEchoVBroadcast;

import java.util.*;

public class MultiValueAgreement implements IMultiValueAgreement {

    // The protocol instance identifier.
    private final String pid;

    // The replica id.
    private final Integer replicaId;

    // The network configuration.
    private final NetworkInfo networkInfo;

    // Threshold signature utils.
    private final ThreshSigUtils sigUtils;

    private final Map<VcbcPid, IEchoVBroadcast> vcbcInstances = new HashMap<>();
    private final Map<Integer, VOutput> values = new TreeMap<>();

    private Long roundId = 0L;
    private final Map<Integer, Round> rounds = new HashMap<>();

    private byte[] output;

    public MultiValueAgreement(String pid, Integer replicaId, NetworkInfo networkInfo) {
        this.pid = pid;
        this.replicaId = replicaId;
        this.networkInfo = networkInfo;

        this.sigUtils = networkInfo.getSigUtils();
    }

    private Integer getCandidate(Long round) {
        return (int) ((round-1) % networkInfo.getN());
    }

    private IEchoVBroadcast getVCBCInstance(VcbcPid vcbcPid) {
        return this.vcbcInstances.computeIfAbsent(vcbcPid,
                p -> new EchoVBroadcast2(pid + "|" + p.toString(), replicaId, networkInfo, p.getProposer()));
    }

    private Round getRound(Integer candidate) {
        return this.rounds.computeIfAbsent(candidate, c -> new Round(pid, replicaId, networkInfo, c));
    }

    @Override
    public Step<byte[]> handleInput(byte[] input) {
        VcbcPid vcbcPid = new VcbcPid(replicaId);
        Step<VOutput> vcbcStep = this.getVCBCInstance(vcbcPid).handleInput(input);
        return this.handleVCbcStep(vcbcStep, vcbcPid);
    }

    @Override
    public Step<byte[]> handleMessage(ProtocolMessage message) {
        Step<byte[]> step = new Step<>();

        if (!message.getPid().startsWith(pid)) return step;

        if (message instanceof VBroadcastMessage) {
            return this.handleVcbcMessage((VBroadcastMessage) message);

        } else if (message instanceof VoteMessage) {
            return this.handleVoteMessage((VoteMessage) message);

        } else if (message instanceof BinaryAgreementMessage) {
            return this.handleVbaMessage((BinaryAgreementMessage) message);
        }

        return step;
    }

    @Override
    public boolean hasTerminated() {
        return this.output != null;
    }

    @Override
    public Optional<byte[]> deliver() {
        return Optional.ofNullable(this.output);
    }

    private Step<byte[]> handleVcbcMessage(VBroadcastMessage message) {
        VcbcPid vcbcPid = new VcbcPid(message.getPid().split("\\|")[1]);
        Step<VOutput> vcbcStep = this.getVCBCInstance(vcbcPid).handleMessage(message);
        return this.handleVCbcStep(vcbcStep, vcbcPid);
    }

    private Step<byte[]> handleVoteMessage(VoteMessage message) {
        Round round = this.getRound(message.getCandidate());
        Step<ValidatedBoolean> roundStep = round.handleVoteMessage(message);
        return this.handleRoundStep(roundStep);
    }

    private Step<byte[]> handleVbaMessage(BinaryAgreementMessage message) {
        Round round = this.getRound(Integer.valueOf(message.getPid().split("\\|")[1].split("-")[1]));
        Step<ValidatedBoolean> roundStep = round.handleVbaMessage(message);
        return this.handleRoundStep(roundStep);
    }

    private Step<byte[]> handleVCbcStep(Step<VOutput> vcbcStep, VcbcPid vcbcPid) {
        Step<byte[]> step = new Step<>(vcbcStep.getMessages());

        // ignore if VCBC output is empty
        Vector<VOutput> vcbcOutput = vcbcStep.getOutput();
        if (vcbcOutput.isEmpty()) return step;

        // TODO check if the validity predicate holds for vcbcOutput

        // save VCBC output value + proof
        if (this.values.containsKey(vcbcPid.getProposer())) return step;
        this.values.put(vcbcPid.getProposer(), vcbcOutput.firstElement());

        final int quorum = networkInfo.getN() - networkInfo.getF();
        if (this.values.size() == quorum) {
            step.add(this.updateRound());
        }
        return step;
    }

    private Step<byte[]> handleRoundStep(Step<ValidatedBoolean> roundStep) {
        Step<byte[]> step = new Step<>(roundStep.getMessages());

        final int candidate = this.getCandidate(this.roundId);
        Round round = this.getRound(candidate);
        if (!round.hasTerminated()) {
            return step;
        }

        ValidatedBoolean roundOutput = round.deliver().orElseThrow();
        if (roundOutput.getValue()) {
            IVBroadcast vcbc = this.getVCBCInstance(new VcbcPid(candidate));
            Optional<VOutput> vcbcOutput = vcbc.deliver();
            if (vcbcOutput.isPresent()) {
                this.output = vcbcOutput.get().getValue();
            } else {
                // TODO recover from round output
                this.output = roundOutput.getProof();
            }
            step.add(this.output);
        }

        step.add(this.updateRound());
        step.add(this.handleRoundStep(new Step<>()));

        return step;
    }

    private Step<byte[]> updateRound() {
        this.roundId += 1;

        if (this.hasTerminated()) return new Step<>();

        final int candidate = this.getCandidate(this.roundId);
        Round round = this.getRound(candidate);

        ValidatedBoolean roundInput;
        if (this.values.containsKey(candidate)) {
            VOutput vcbcOutput = this.values.get(candidate);
            roundInput = new ValidatedBoolean(true, vcbcOutput.getSignature());
        } else {
            roundInput = new ValidatedBoolean(false, null);
        }
        Step<ValidatedBoolean> roundStep = round.handleInput(roundInput);
        return new Step<>(roundStep.getMessages());
    }
}
