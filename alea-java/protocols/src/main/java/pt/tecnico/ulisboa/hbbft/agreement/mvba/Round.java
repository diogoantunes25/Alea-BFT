package pt.tecnico.ulisboa.hbbft.agreement.mvba;

import pt.tecnico.ulisboa.hbbft.IProtocol;
import pt.tecnico.ulisboa.hbbft.NetworkInfo;
import pt.tecnico.ulisboa.hbbft.ProtocolMessage;
import pt.tecnico.ulisboa.hbbft.Step;
import pt.tecnico.ulisboa.hbbft.agreement.mvba.messages.VoteMessage;
import pt.tecnico.ulisboa.hbbft.agreement.vba.IValidatedBinaryAgreement;
import pt.tecnico.ulisboa.hbbft.agreement.vba.ValidatedBinaryAgreementMessage;
import pt.tecnico.ulisboa.hbbft.agreement.vba.ValidatedBoolean;
import pt.tecnico.ulisboa.hbbft.agreement.vba.mock.MockValidatedByzantineAgreement;
import pt.tecnico.ulisboa.hbbft.binaryagreement.BinaryAgreementMessage;
import pt.tecnico.ulisboa.hbbft.binaryagreement.IBinaryAgreement;
import pt.tecnico.ulisboa.hbbft.binaryagreement.moustefaoui.MoustefaouiBinaryAgreement;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Round implements IProtocol<ValidatedBoolean, ValidatedBoolean, ProtocolMessage> {

    private final String pid;

    private final Integer replicaId;

    private final NetworkInfo networkInfo;

    private final Integer candidate;

    private final Map<Integer, VoteMessage> voteMessages;

    private final IBinaryAgreement vba;

    private ValidatedBoolean output;

    public Round(String pid, Integer replicaId, NetworkInfo networkInfo, Integer candidate) {
        this.pid = pid;
        this.replicaId = replicaId;
        this.networkInfo = networkInfo;
        this.candidate = candidate;

        this.voteMessages = new HashMap<>();
        //this.vba = new ValidatedBinaryAgreement(String.format("VBA-%d", candidate), replicaId, networkInfo, new VcbcValidator());
        //this.vba = new MockValidatedByzantineAgreement(String.format("%s|MVBA-%d", pid, candidate), 1L);
        this.vba = new MoustefaouiBinaryAgreement(String.format("%s|VBA-%d", pid, candidate), replicaId, networkInfo);
    }

    public Step<ValidatedBoolean> handleInput(ValidatedBoolean input) {
        Step<ValidatedBoolean> step = new Step<>();

        // ignore if already input
        if (this.voteMessages.containsKey(replicaId)) return step;

        // send `Vote` message to all parties
        VoteMessage voteMessage = new VoteMessage(pid, replicaId, candidate, input.getValue(), input.getProof());
        step.add(this.handleMessage(voteMessage));
        step.add(voteMessage, this.networkInfo.getValidatorSet().getAllIds().stream()
                .filter(id -> !id.equals(this.replicaId)).collect(Collectors.toList()));

        return step;
    }

    public Step<ValidatedBoolean> handleMessage(ProtocolMessage message) {
        Step<ValidatedBoolean> step = new Step<>();

        if (message instanceof VoteMessage) {
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
    public Optional<ValidatedBoolean> deliver() {
        return Optional.ofNullable(this.output);
    }

    public Step<ValidatedBoolean> handleVoteMessage(VoteMessage message) {
        Step<ValidatedBoolean> step = new Step<>();

        // ignore Vote messages for other rounds
        if (!message.getCandidate().equals(candidate)) return step;

        // TODO validate vote value

        // save Vote message
        final int senderId = message.getSender();
        if (voteMessages.containsKey(senderId)) return step;
        voteMessages.put(senderId, message);

        final int quorum = networkInfo.getN() - networkInfo.getF();
        if (voteMessages.size() == quorum) {
            Optional<VoteMessage> yesVote = voteMessages.values().stream()
                    .filter(VoteMessage::getVote).findAny();

            ValidatedBoolean vbaInput = yesVote
                    .map(voteMessage -> new ValidatedBoolean(true, voteMessage.getProof()))
                    .orElseGet(() -> new ValidatedBoolean(false, null));
            Step<Boolean> vbaStep = this.vba.handleInput(vbaInput.getValue());
            step.add(this.handleVbaStep(vbaStep));
        }

        return step;
    }

    public Step<ValidatedBoolean> handleVbaMessage(BinaryAgreementMessage message) {
        Step<ValidatedBoolean> step = new Step<>();

        // ignore VBA messages for other rounds
        // if (message.getRound().intValue() != candidate) return step;

        Step<Boolean> vbaStep = this.vba.handleMessage(message);
        step.add(this.handleVbaStep(vbaStep));

        return step;
    }

    private Step<ValidatedBoolean> handleVbaStep(Step<Boolean> vbaStep) {
        if (!vbaStep.getOutput().isEmpty()) {
            this.output = new ValidatedBoolean(vbaStep.getOutput().firstElement(), new byte[0]);
        }
        Step<ValidatedBoolean> step = new Step<>(vbaStep.getMessages());
        step.add(this.output);
        return step;
    }
}
