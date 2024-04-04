package pt.tecnico.ulisboa.hbbft.utils.threshsig.shoup;

import java.math.BigInteger;
import java.util.Collection;
import java.util.stream.Collectors;

import pt.tecnico.ulisboa.hbbft.utils.threshsig.shoup.ShoupSigShare;
import pt.tecnico.ulisboa.hbbft.utils.threshsig.SigShare;
import pt.tecnico.ulisboa.hbbft.utils.threshsig.Signature;
import pt.tecnico.ulisboa.hbbft.utils.threshsig.ThreshSigUtils;

public class ShoupUtils extends ThreshSigUtils {

    private KeyShare _ks;
    private GroupKey _gk;

    public ShoupUtils(int n, GroupKey gk, KeyShare ks) {
        super(n);
        _ks = ks;
        _gk = gk;
        Dealer.generateVerifiers(_gk.getModulus(), new KeyShare[]{_ks});
    }

    @Override
    public ShoupSigShare sign(byte[] payload) {
        return _ks.sign(payload);
    }

    @Override
    public Signature combine(byte[] value, Collection<SigShare> s) {
        ShoupSigShare[] shares = new ShoupSigShare[s.size()];
        int i = 0;
        for (SigShare share: s) shares[i++] = (ShoupSigShare) share;

        try {
            BigInteger signature = ShoupSigShare.combine(
                    value,
                    shares,
                    _gk.getK(),
                    _gk.getL(),
                    _gk.getModulus(),
                    _gk.getExponent()
            );

            return new ShoupSignature(signature);
        } catch (ThresholdSigException e) {
            e.printStackTrace();
            return ShoupSignature.getNull();
        } 
    }

    @Override
    public boolean verifyFull(byte[] payload, Signature sig) {
        // noop (as it was implemented)
        return true;
    }

    @Override
    public boolean verifyShare(byte[] payload, SigShare share, int id) {
        // noop (as it was implemented)
        return true;
    }
    
}
