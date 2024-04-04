package pt.tecnico.ulisboa.hbbft.binaryagreement.moustefaoui.messages;

import pt.tecnico.ulisboa.hbbft.binaryagreement.BinaryAgreementMessage;

public class BValMessage extends BinaryAgreementMessage {

    public static final int BVAL = 44783;

    private final Boolean value;

    public BValMessage(String pid, Integer sender, Long round, Boolean value) {
        super(pid, BVAL, sender, round);
        this.value = value;
    }

    public Boolean getValue() {
        return value;
    }

    @Override
    public Boolean canExpire() {
        return true;
    }

    @Override
    public String toString() {
        return "BValMessage{" +
                "parent=" + super.toString() +
                ", value=" + value +
                '}';
    }
}
