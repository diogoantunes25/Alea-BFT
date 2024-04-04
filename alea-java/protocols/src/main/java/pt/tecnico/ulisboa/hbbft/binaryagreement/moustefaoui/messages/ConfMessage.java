package pt.tecnico.ulisboa.hbbft.binaryagreement.moustefaoui.messages;

import pt.tecnico.ulisboa.hbbft.binaryagreement.BinaryAgreementMessage;
import pt.tecnico.ulisboa.hbbft.binaryagreement.moustefaoui.BoolSet;

public class ConfMessage extends BinaryAgreementMessage {

    public static final int CONF = 44786;

    private final BoolSet value;

    public ConfMessage(String pid, Integer sender, Long round, BoolSet value) {
        super(pid, CONF, sender, round);
        this.value = value;
    }

    public BoolSet getValue() {
        return value;
    }

    @Override
    public Boolean canExpire() {
        return true;
    }

    @Override
    public String toString() {
        return "ConfMessage{" +
                "parent=" + super.toString() +
                ", value=" + value +
                '}';
    }
}
