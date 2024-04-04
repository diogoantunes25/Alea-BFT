package pt.tecnico.ulisboa.hbbft.agreement.vba.messages;

import pt.tecnico.ulisboa.hbbft.agreement.vba.ValidatedBinaryAgreementMessage;

public class TurquoisMessage extends ValidatedBinaryAgreementMessage {

    public static final int TURQUOIS = 221;

    private final Boolean value;
    private final byte[] proof;

    public TurquoisMessage(String pid, Integer sender, Long round, Boolean value, byte[] proof) {
        super(pid, TURQUOIS, sender, round);
        this.value = value;
        this.proof = proof;
    }

    public Boolean getValue() {
        return value;
    }

    public byte[] getProof() {
        return proof;
    }
}
