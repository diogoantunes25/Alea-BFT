package pt.tecnico.ulisboa.hbbft.subset.state;

import pt.tecnico.ulisboa.hbbft.Step;
import pt.tecnico.ulisboa.hbbft.binaryagreement.BinaryAgreementMessage;
import pt.tecnico.ulisboa.hbbft.binaryagreement.IBinaryAgreement;
import pt.tecnico.ulisboa.hbbft.broadcast.BroadcastMessage;
import pt.tecnico.ulisboa.hbbft.broadcast.IBroadcast;
import pt.tecnico.ulisboa.hbbft.subset.Proposal;
import pt.tecnico.ulisboa.hbbft.subset.ProposalState;
import pt.ulisboa.tecnico.utils.Utils;

// ABA not ended, but RCB already delivered
public class HasValueState<T> extends ProposalState {

    public HasValueState(Proposal proposal, IBroadcast bc, IBinaryAgreement ba) {
        super(proposal, bc, ba);
    }

    @Override
    public Boolean received() {
        return true;
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
        // Can't propose...
        return new Step<>();
    }

    @Override
    public Step<byte[]> vote(boolean value) {
        Step<Boolean> baStep = ba.handleInput(value);
        if (ba.hasTerminated()) {
            proposal.setState(new CompleteState<>(proposal, bc, ba));
        }
        return new Step<>(baStep.getMessages());
    }

    @Override
    protected Step<byte[]> handleBroadcastMessage(BroadcastMessage broadcastMessage) {
        return bc.handleMessage(broadcastMessage);
    }

    @Override
    protected Step<byte[]> handleBinaryAgreementMessage(BinaryAgreementMessage binaryAgreementMessage) {
        Step<Boolean> baStep = ba.handleMessage(binaryAgreementMessage);
        if (ba.hasTerminated()) {
            proposal.setState(new CompleteState<>(proposal, bc, ba));
        }
        return new Step<>(baStep.getMessages());
    }
}
