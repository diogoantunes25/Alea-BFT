package pt.tecnico.ulisboa.hbbft.utils.threshsig.shoup;

import pt.tecnico.ulisboa.hbbft.utils.threshsig.AbstractThreshSigFactory;
import pt.tecnico.ulisboa.hbbft.utils.threshsig.ThreshSigUtils;

public class ShoupSigFactory implements AbstractThreshSigFactory {

    private final int KEY_SIZE = 256;
    private int _n;
    private int _f;
    private GroupKey _gk;
    private KeyShare[] _kss;

    @Override
    public void init(int n) {
        Dealer dealer = new Dealer(KEY_SIZE);
        _n = n;
        _f = (int) Math.floorDiv(_n-1, 3);
        dealer.generateKeys(_f, _n);
        _gk = dealer.getGroupKey();
        _kss = dealer.getShares();
    }

    @Override
    public int getN() {
        return _n;
    }

    @Override
    public ThreshSigUtils get(int id) {
        return new ShoupUtils(_n, _gk, _kss[id]);
    }
    
}
