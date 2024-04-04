package pt.tecnico.ulisboa.hbbft.agreement.vba;

import pt.tecnico.ulisboa.hbbft.ProtocolMessage;

public class ValidatedBinaryAgreementMessage extends ProtocolMessage {

    private final Long round;

    public ValidatedBinaryAgreementMessage(
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
}
