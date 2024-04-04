package pt.tecnico.ulisboa.hbbft.subset.state;

import pt.tecnico.ulisboa.hbbft.Step;
import pt.tecnico.ulisboa.hbbft.binaryagreement.BinaryAgreementMessage;
import pt.tecnico.ulisboa.hbbft.binaryagreement.IBinaryAgreement;
import pt.tecnico.ulisboa.hbbft.broadcast.BroadcastMessage;
import pt.tecnico.ulisboa.hbbft.broadcast.IBroadcast;
import pt.tecnico.ulisboa.hbbft.subset.Proposal;
import pt.tecnico.ulisboa.hbbft.subset.ProposalState;
import pt.ulisboa.tecnico.utils.Utils;

// ABA accepted, but RCB did not deliver yet
public class AcceptedState extends ProposalState {

    public AcceptedState(Proposal proposal, IBroadcast bc, IBinaryAgreement ba) {
        super(proposal, bc, ba);
    }

    @Override
    public Boolean received() {
        return false;
    }

    @Override
    public Boolean accepted() {
        return true;
    }

    @Override
    public Boolean complete() {
        return false;
    }

    @Override
    public Step<byte[]> propose(byte[] value) {
        // Can't propose...
        return new Step<>();
    }

    @Override
    public Step<byte[]> vote(boolean value) {
        Step<Boolean> baStep = ba.handleInput(value);
        return new Step<>(baStep.getMessages());
    }

    @Override
    protected Step<byte[]> handleBroadcastMessage(BroadcastMessage broadcastMessage) {
        Step<byte[]> bcStep = bc.handleMessage(broadcastMessage);
        if (bc.hasTerminated()) {
            proposal.setState(new CompleteState<>(proposal, bc, ba));
        }
        return bcStep;
    }

    @Override
    protected Step<byte[]> handleBinaryAgreementMessage(BinaryAgreementMessage binaryAgreementMessage) {
        Step<Boolean> baStep = ba.handleMessage(binaryAgreementMessage);
        return new Step<>(baStep.getMessages());
    }
}
