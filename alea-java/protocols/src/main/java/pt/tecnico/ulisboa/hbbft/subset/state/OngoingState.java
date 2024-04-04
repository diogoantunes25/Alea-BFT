package pt.tecnico.ulisboa.hbbft.subset.state;

import pt.tecnico.ulisboa.hbbft.Step;
import pt.tecnico.ulisboa.hbbft.binaryagreement.BinaryAgreementMessage;
import pt.tecnico.ulisboa.hbbft.binaryagreement.IBinaryAgreement;
import pt.tecnico.ulisboa.hbbft.broadcast.BroadcastMessage;
import pt.tecnico.ulisboa.hbbft.broadcast.IBroadcast;
import pt.tecnico.ulisboa.hbbft.subset.Proposal;
import pt.tecnico.ulisboa.hbbft.subset.ProposalState;
// Neither ABA not RCB ended
public class OngoingState extends ProposalState {

    public OngoingState(Proposal proposal, IBroadcast bc, IBinaryAgreement ba) {
        super(proposal, bc, ba);
    }

    @Override
    public Boolean received() {
        return false;
    }

    @Override
    public Boolean accepted() {
        return false;
    }

    @Override
    public Boolean complete() {
        return false;
    }

    @Override
    public Step<byte[]> propose(byte[] value) {
        Step<byte[]> bcStep = bc.handleInput(value);
        return this.handleBroadcastStep(bcStep);
    }

    @Override
    public Step<byte[]> vote(boolean value) {
        Step<Boolean> baStep = ba.handleInput(value);
        return this.handleBinaryAgreementStep(baStep);
    }

    @Override
    protected Step<byte[]> handleBroadcastMessage(BroadcastMessage broadcastMessage) {
        Step<byte[]> bcStep = bc.handleMessage(broadcastMessage);
        return this.handleBroadcastStep(bcStep);
    }

    @Override
    protected Step<byte[]> handleBinaryAgreementMessage(BinaryAgreementMessage binaryAgreementMessage) {
        Step<Boolean> baStep = ba.handleMessage(binaryAgreementMessage);
        return this.handleBinaryAgreementStep(baStep);
    }

    private Step<byte[]> handleBroadcastStep(Step<byte[]> bcStep) {
        Step<byte[]> step = new Step<>(bcStep.getMessages());
        if (bc.hasTerminated()) {
            proposal.setState(new HasValueState<>(proposal, bc, ba));
            step.add(proposal.vote(true));
        }
        return step;
    }

    private Step<byte[]> handleBinaryAgreementStep(Step<Boolean> baStep) {
        if (ba.hasTerminated()) {
            if (ba.deliver().orElseThrow()) proposal.setState(new AcceptedState(proposal, bc, ba));
            else proposal.setState(new CompleteState<>(proposal, bc, ba));
        }
        return new Step<>(baStep.getMessages());
    }
}
