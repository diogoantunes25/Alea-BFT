package pt.tecnico.ulisboa.hbbft.subset;

import pt.tecnico.ulisboa.hbbft.ProtocolMessage;
import pt.tecnico.ulisboa.hbbft.Step;
import pt.tecnico.ulisboa.hbbft.binaryagreement.IBinaryAgreement;
import pt.tecnico.ulisboa.hbbft.broadcast.IBroadcast;
import pt.tecnico.ulisboa.hbbft.subset.state.OngoingState;

public class Proposal {

    private final Integer instance;
    private ProposalState state;

    private byte[] result;

    public Proposal(Integer instance, IBroadcast bc, IBinaryAgreement ba) {
        this.instance = instance;
        this.state = new OngoingState(this, bc, ba);
    }

    public Integer getInstance() {
        return instance;
    }

    public synchronized Boolean received() {
        return state.received();
    }

    public synchronized Boolean accepted() {
        return state.accepted();
    }

    public synchronized Boolean complete() {
        return state.complete();
    }

    public synchronized Step<byte[]> propose(byte[] value) {
        return state.propose(value);
    }

    public synchronized Step<byte[]> vote(boolean value) {
        return state.vote(value);
    }

    public synchronized Step<byte[]> handleMessage(ProtocolMessage message) {
        return state.handleMessage(message);
    }

    public synchronized byte[] getResult() {
        return this.result;
    }

    public synchronized void setResult(byte[] result) {
        this.result = result;
    }

    public synchronized void setState(ProposalState state) {
        this.state = state;
    }
}
