package pt.tecnico.ulisboa.hbbft.election;

import pt.tecnico.ulisboa.hbbft.utils.threshsig.SigShare;
import pt.tecnico.ulisboa.hbbft.utils.threshsig.ThreshSigUtils;
import pt.tecnico.ulisboa.hbbft.utils.threshsig.exceptions.NotEnoughSharesException;

import java.math.BigInteger;
import java.util.Map;
import java.util.TreeMap;

public class Coin {

    private final String name;
    private final ThreshSigUtils sigUtils;

    private Map<Integer, SigShare> shares = new TreeMap<>();
    private BigInteger value;

    public Coin(String name, ThreshSigUtils sigUtils) {
        this.name = name;
        this.sigUtils = sigUtils;
    }

    public byte[] getMyShare() {
        byte[] toSign = name.getBytes();
        return sigUtils.sign(toSign).toBytes();
    }

    public void addShare(Integer replicaId, byte[] share) {
        if (this.hasDecided()) return;
        shares.putIfAbsent(replicaId, SigShare.fromBytes(share));

        if (shares.size() == sigUtils.getF()) {
            byte[] toSign = name.getBytes();
            try {
                value = new BigInteger(sigUtils.combine(toSign, shares.values()).toBytes());
            } catch (NotEnoughSharesException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    public Boolean hasDecided() {
        return value != null;
    }

    public BigInteger getValue() {
        return this.value;
    }
}
