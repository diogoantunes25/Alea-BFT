package pt.tecnico.ulisboa.hbbft.utils.threshsig.dummy;

import pt.tecnico.ulisboa.hbbft.utils.threshsig.AbstractThreshSigFactory;
import pt.tecnico.ulisboa.hbbft.utils.threshsig.ThreshSigUtils;

public class DummySigFactory implements AbstractThreshSigFactory {

    private int _n;

    @Override
    public void init(int n) {
        _n = n;
    }

    @Override
    public ThreshSigUtils get(int id) {
        return new DummyUtils(_n);
    }

    @Override
    public int getN() {
        return _n;
    }
}