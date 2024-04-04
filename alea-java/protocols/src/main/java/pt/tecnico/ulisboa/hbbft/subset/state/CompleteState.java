package pt.tecnico.ulisboa.hbbft.subset.state;

import pt.tecnico.ulisboa.hbbft.Step;
import pt.tecnico.ulisboa.hbbft.binaryagreement.BinaryAgreementMessage;
import pt.tecnico.ulisboa.hbbft.binaryagreement.IBinaryAgreement;
import pt.tecnico.ulisboa.hbbft.broadcast.BroadcastMessage;
import pt.tecnico.ulisboa.hbbft.broadcast.IBroadcast;
import pt.tecnico.ulisboa.hbbft.subset.Proposal;
import pt.tecnico.ulisboa.hbbft.subset.ProposalState;
import pt.ulisboa.tecnico.utils.Utils;

// Both ABA and RCB have ended
public class CompleteState<T> extends ProposalState {

    public CompleteState(Proposal proposal, IBroadcast bc, IBinaryAgreement ba) {
        super(proposal, bc, ba);
        if (ba.deliver().orElseThrow()) {
            proposal.setResult(bc.deliver().orElseThrow());
        }
    }

    @Override
    public  Boolean received() {
        return true;
    }

    @Override
    public  Boolean accepted() {
        return ba.deliver().orElseThrow();
    }

    @Override
    public  Boolean complete() {
        return true;
    }

    @Override
    public  Step<byte[]> propose(byte[] value) {
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
        return bc.handleMessage(broadcastMessage);
    }

    @Override
    protected Step<byte[]> handleBinaryAgreementMessage(BinaryAgreementMessage binaryAgreementMessage) {
        Step<Boolean> baStep = ba.handleMessage(binaryAgreementMessage);
        return new Step<>(baStep.getMessages());
    }
}
