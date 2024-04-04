package pt.tecnico.ulisboa.hbbft.utils.threshsig;

import java.util.Collection;

import pt.tecnico.ulisboa.hbbft.utils.threshsig.exceptions.NotEnoughSharesException;

public abstract class ThreshSigUtils {

    protected int _n;
    protected int _f;

    public ThreshSigUtils(int n) {
        _n = n;
        _f = (int) Math.floor((n - 1)/3);
    }

    /**
     * Signs payload
     * @param payload
     * @return
     */
    public abstract SigShare sign(byte[] payload);

    /**
     * Combines shares into single signature
     * @param shares Shares to be used
     * @return Resulting signature
     * @throws NotEnoughSharesException
     */
    public abstract Signature combine(byte[] value, Collection<SigShare> shares)
        throws NotEnoughSharesException, NotEnoughSharesException;

    /**
     * Verifies if sig is valid signature of payload
     * @param payload
     * @param sig
     * @return
     */
    public abstract boolean verifyFull(byte[] payload, Signature sig);

    /**
     * Verifies if share is signature share of id for payload
     * @param payload
     * @param share
     * @param id
     * @return
     */
    public abstract boolean verifyShare(byte[] payload, SigShare share, int id);

    public int getN() {
        return _n;
    }

    public int getF() {
        return _f;
    }
}