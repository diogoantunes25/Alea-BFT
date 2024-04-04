package pt.tecnico.ulisboa.hbbft.utils.threshsig;

public interface AbstractThreshSigFactory {

    /**
     * Initializes public and private keys
     */
    public abstract void init(int n);

    /**
     * Gets utils needed for particular node
     * @param id Id of node
     * @return Utils to be used by node
     */
    public abstract ThreshSigUtils get(int id);

    public abstract int getN();
}