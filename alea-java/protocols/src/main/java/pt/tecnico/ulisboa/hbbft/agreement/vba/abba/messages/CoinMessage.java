package pt.tecnico.ulisboa.hbbft.agreement.vba.abba.messages;

import pt.tecnico.ulisboa.hbbft.agreement.vba.ValidatedBinaryAgreementMessage;
import pt.tecnico.ulisboa.hbbft.utils.threshsig.SigShare;

public class CoinMessage extends ValidatedBinaryAgreementMessage {

    public static final int COIN = 124;

    private final SigShare share;

    public CoinMessage(String pid, Integer sender, Long round, SigShare share) {
        super(pid, COIN, sender, round);
        this.share = share;
    }

    public SigShare getShare() {
        return share;
    }
}
