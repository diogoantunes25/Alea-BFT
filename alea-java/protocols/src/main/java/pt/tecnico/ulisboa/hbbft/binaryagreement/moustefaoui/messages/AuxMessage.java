package pt.tecnico.ulisboa.hbbft.binaryagreement.moustefaoui.messages;

import pt.tecnico.ulisboa.hbbft.binaryagreement.BinaryAgreementMessage;

public class AuxMessage extends BinaryAgreementMessage {

    public static final int AUX = 44784;

    private final Boolean value;

    public AuxMessage(String pid, Integer sender, Long round, Boolean value) {
        super(pid, AUX, sender, round);
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
        return "AuxMessage{" +
                "parent=" + super.toString() +
                ", value=" + value +
                '}';
    }
}
