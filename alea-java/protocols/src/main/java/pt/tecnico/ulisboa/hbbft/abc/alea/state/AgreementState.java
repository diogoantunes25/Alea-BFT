package pt.tecnico.ulisboa.hbbft.abc.alea.state;

import pt.tecnico.ulisboa.hbbft.Step;
import pt.tecnico.ulisboa.hbbft.abc.Block;
import pt.tecnico.ulisboa.hbbft.abc.alea.Alea;
import pt.tecnico.ulisboa.hbbft.abc.alea.BaPid;
import pt.tecnico.ulisboa.hbbft.abc.alea.queue.PriorityQueue;
import pt.tecnico.ulisboa.hbbft.binaryagreement.IBinaryAgreement;

public abstract class AgreementState {

    protected Alea alea;
    protected Long epoch;

    public AgreementState(Alea alea, Long epoch) {
        this.alea = alea;
        this.epoch = epoch;
    }

    public int getLeader() {
        return (int) (epoch % this.alea.getNetworkInfo().getN());
    }

    public PriorityQueue getQueue() {
        return this.alea.getQueues().get(getLeader());
    }

    public IBinaryAgreement getBaInstance() {
        BaPid baPid = new BaPid("BA", epoch);
        return this.alea.getBinaryAgreementInstance(baPid);
    }

    public abstract Step<Block> tryProgress();

    public abstract boolean canProgress();
}
