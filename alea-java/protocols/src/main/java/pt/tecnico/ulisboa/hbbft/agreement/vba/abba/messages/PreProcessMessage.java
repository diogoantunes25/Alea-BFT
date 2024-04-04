package pt.tecnico.ulisboa.hbbft.agreement.vba.abba.messages;

import pt.tecnico.ulisboa.hbbft.agreement.vba.ValidatedBinaryAgreementMessage;
import pt.tecnico.ulisboa.hbbft.utils.threshsig.SigShare;

public class PreProcessMessage extends ValidatedBinaryAgreementMessage {

    public static final int PRE_PROCESS = 221;

    private final Boolean value;
    private final byte[] justification;
    private final SigShare share;

    public PreProcessMessage(String pid, Integer sender, Long round, Boolean value, byte[] justification, SigShare share) {
        super(pid, PRE_PROCESS, sender, round);
        this.value = value;
        this.justification = justification;
        this.share = share;
    }

    public Boolean getValue() {
        return value;
    }

    public byte[] getJustification() {
        return justification;
    }

    public SigShare getShare() {
        return share;
    }
}
