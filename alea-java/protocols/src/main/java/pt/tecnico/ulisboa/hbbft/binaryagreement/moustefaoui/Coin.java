package pt.tecnico.ulisboa.hbbft.binaryagreement.moustefaoui;


import pt.tecnico.ulisboa.hbbft.utils.threshsig.SigShare;
import pt.tecnico.ulisboa.hbbft.utils.threshsig.ThreshSigUtils;
import pt.tecnico.ulisboa.hbbft.utils.threshsig.exceptions.NotEnoughSharesException;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;


/**
 * Threshold Coin-Tossing Scheme
 *
 * Reference: "Random Oracles in Constantinople",
 * Christian Cachin et all , IBM Research
 */
public class Coin {

    private final String name;
    private Long round;
    private ThreshSigUtils sigUtils;

    private Map<Integer, SigShare> shares = new TreeMap<>();
    private BigInteger signature;
    private Boolean decision;

    public Coin(String name, ThreshSigUtils sigUtils, long round) {
        this.name = name;
        this.round = round;
        this.sigUtils = sigUtils;
    }

    public void reset(Long round) {
        this.round = round;
        this.shares = new TreeMap<>();
        this.signature = null;
        this.decision = null;
    }

    public byte[] getMyShare() {
        byte[] toSign = this.getCoinName().getBytes();
        byte[] share = sigUtils.sign(toSign).toBytes();
        return share;
    }

    public synchronized void addShare(Integer replicaId, byte[] share) {
        if (this.hasDecided()) return;
        this.shares.putIfAbsent(replicaId, SigShare.fromBytes(share));

        if (this.shares.size() == 2 * this.sigUtils.getF() + 1) {
            byte[] toSign = this.getCoinName().getBytes();
            try {
                this.signature = new BigInteger(sigUtils.combine(toSign, this.shares.values()).toBytes());
            } catch (NotEnoughSharesException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            // this.decision = (this.signature.mod(new BigInteger("2")).intValue() == 1);
            try {
                this.decision = (MessageDigest.getInstance("SHA-256").digest(this.signature.toByteArray())[0] & 1) == 1;
            }
            catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
    }

    public long getShareCount() {
        return this.shares.size();
    }

    public long getRequiredShareCount() {
        return this.sigUtils.getF();
    }


    public Boolean hasDecided() {
        return decision != null;
    }

    public Boolean getValue() {
        return this.decision;
    }

    private String getCoinName() {
        return String.format("%s-%d", name, round);
    }
}
