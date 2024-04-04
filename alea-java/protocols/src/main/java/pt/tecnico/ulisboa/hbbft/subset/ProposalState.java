package pt.tecnico.ulisboa.hbbft.subset;

import pt.tecnico.ulisboa.hbbft.ProtocolMessage;
import pt.tecnico.ulisboa.hbbft.Step;
import pt.tecnico.ulisboa.hbbft.binaryagreement.BinaryAgreementMessage;
import pt.tecnico.ulisboa.hbbft.binaryagreement.IBinaryAgreement;
import pt.tecnico.ulisboa.hbbft.broadcast.BroadcastMessage;
import pt.tecnico.ulisboa.hbbft.broadcast.IBroadcast;

public abstract class ProposalState {

    protected Proposal proposal;

    protected IBroadcast bc;
    protected IBinaryAgreement ba;

    public ProposalState(Proposal proposal, IBroadcast bc, IBinaryAgreement ba) {
        this.proposal = proposal;
        this.bc = bc;
        this.ba = ba;
    }

    // Returns `true` if we already received the `Broadcast` result.
    public abstract Boolean received();

    // Returns `true` if this proposal has been accepted, even if we don't have the value yet.
    public abstract Boolean accepted();

    // Returns `true` if this proposal has been rejected, or accepted and output.
    public abstract Boolean complete();

    // Makes a proposal by broadcasting a value.
    public abstract Step<byte[]> propose(byte[] value);

    // Votes for the proposal, if still possible.
    public abstract Step<byte[]> vote(boolean value);

    public Step<byte[]> handleMessage(ProtocolMessage message) {
        if (message instanceof BroadcastMessage) {
            return handleBroadcastMessage((BroadcastMessage) message);
        } else if (message instanceof BinaryAgreementMessage) {
            return handleBinaryAgreementMessage((BinaryAgreementMessage) message);
        }
        return new Step<>();
    }

    protected abstract Step<byte[]> handleBroadcastMessage(BroadcastMessage broadcastMessage);

    protected abstract Step<byte[]> handleBinaryAgreementMessage(BinaryAgreementMessage binaryAgreementMessage);
}
