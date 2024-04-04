package pt.tecnico.ulisboa.hbbft.binaryagreement.moustefaoui.messages;

import pt.tecnico.ulisboa.hbbft.binaryagreement.BinaryAgreementMessage;

public class TermMessage extends BinaryAgreementMessage {

    public static final int TERM = 44787;

    private final Boolean value;

    public TermMessage(String pid, Integer sender, Long round, Boolean value) {
        super(pid, TERM, sender, round);
        this.value = value;
    }

    public Boolean getValue() {
        return value;
    }

    @Override
    public Boolean canExpire() {
        return false;
    }

    @Override
    public String toString() {
        return "TermMessage{" +
                "parent=" + super.toString() +
                ", value=" + value +
                '}';
    }
}
