package pt.tecnico.ulisboa.hbbft.binaryagreement.moustefaoui.messages;


import pt.tecnico.ulisboa.hbbft.binaryagreement.BinaryAgreementMessage;

import java.util.Arrays;

public class CoinMessage extends BinaryAgreementMessage {

    public static final int COIN = 44785;

    private final byte[] value;

    public CoinMessage(String pid, Integer sender, Long round, byte[] value) {
        super(pid, COIN, sender, round);
        this.value = value;
    }

    public byte[] getValue() {
        return value;
    }

    @Override
    public Boolean canExpire() {
        return true;
    }

    @Override
    public String toString() {
        return "CoinMessage{" +
                "parent=" + super.toString() +
                ", value=" + Arrays.toString(value) +
                '}';
    }
}
