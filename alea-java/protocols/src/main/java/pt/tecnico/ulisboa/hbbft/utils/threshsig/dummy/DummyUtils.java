package pt.tecnico.ulisboa.hbbft.utils.threshsig.dummy;

import pt.tecnico.ulisboa.hbbft.utils.threshsig.SigShare;
import pt.tecnico.ulisboa.hbbft.utils.threshsig.Signature;
import pt.tecnico.ulisboa.hbbft.utils.threshsig.ThreshSigUtils;
import pt.tecnico.ulisboa.hbbft.utils.threshsig.exceptions.NotEnoughSharesException;

import java.util.Collection;


public class DummyUtils extends ThreshSigUtils {

    private int _n;
    private int _f;

    public DummyUtils(int n) {
        super(n);
    }
    
    @Override
    public SigShare sign(byte[] payload) {
        return new DummySigShare();
    }

    @Override
    public Signature combine(byte[] value, Collection<SigShare> shares)
        throws NotEnoughSharesException {
        if (shares.size() < _f) throw new NotEnoughSharesException(shares.size(), _f);

        return new DummySignature();
    }

    @Override
    public boolean verifyFull(byte[] payload, Signature sig) {
        return true;
    }

    @Override
    public boolean verifyShare(byte[] payload, SigShare share, int id) {
        return true;
    }
    
}
