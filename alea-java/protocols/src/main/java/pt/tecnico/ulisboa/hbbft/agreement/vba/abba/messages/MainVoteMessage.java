package pt.tecnico.ulisboa.hbbft.agreement.vba.abba.messages;

import pt.tecnico.ulisboa.hbbft.agreement.vba.ValidatedBinaryAgreementMessage;
import pt.tecnico.ulisboa.hbbft.utils.threshsig.SigShare;

public class MainVoteMessage extends ValidatedBinaryAgreementMessage {

    public static final int MAIN_VOTE = 223;

    private final Boolean value;
    private final byte[] justification;
    private final SigShare share;

    public MainVoteMessage(String pid, Integer sender, Long round, Boolean value, byte[] justification, SigShare share) {
        super(pid, MAIN_VOTE, sender, round);
        this.value = value;
        this.justification = justification;
        this.share = share;
    }

    public Boolean getValue() {
        return value;
    }

    public SigShare getShare() {
        return share;
    }

    public byte[] getJustification() {
        return justification;
    }
}
