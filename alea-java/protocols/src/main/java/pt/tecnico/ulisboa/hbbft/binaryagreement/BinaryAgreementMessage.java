package pt.tecnico.ulisboa.hbbft.binaryagreement;

import pt.tecnico.ulisboa.hbbft.ProtocolMessage;

public abstract class BinaryAgreementMessage extends ProtocolMessage {

    private final Long round;

    public BinaryAgreementMessage(
            String pid,
            Integer type,
            Integer sender,
            Long round
    ) {
        super(pid, type, sender);
        this.round = round;
    }

    public Long getRound() {
        return round;
    }

    // Returns `true` if this message can be ignored if its round has already passed.
    public abstract Boolean canExpire();

    @Override
    public String toString() {
        return "BinaryAgreementMessage{" +
                "parent=" + super.toString() +
                ", round=" + round +
                '}';
    }
}
