package pt.tecnico.ulisboa.hbbft.agreement.vba.abba;

import pt.tecnico.ulisboa.hbbft.agreement.vba.abba.messages.CoinMessage;
import pt.tecnico.ulisboa.hbbft.agreement.vba.abba.messages.MainVoteMessage;
import pt.tecnico.ulisboa.hbbft.agreement.vba.abba.messages.PreProcessMessage;
import pt.tecnico.ulisboa.hbbft.agreement.vba.abba.messages.PreVoteMessage;
import pt.tecnico.ulisboa.hbbft.utils.threshsig.SigShare;

public class VbaMessageFactory {

    private final String pid;
    private final Integer replicaId;
    private final Long round;

    public VbaMessageFactory(String pid, Integer replicaId, Long round) {
        this.pid = pid;
        this.replicaId = replicaId;
        this.round = round;
    }

    public PreProcessMessage createPreProcessMessage(Boolean value, byte[] justification, SigShare share) {
        return new PreProcessMessage(pid, replicaId, round, value, justification, share);
    }

    public PreVoteMessage createPreVoteMessage(Boolean value, byte[] justification, SigShare share) {
        return new PreVoteMessage(pid, replicaId, round, value, justification, share);
    }

    public MainVoteMessage createMainVoteMessage(Boolean value, byte[] justification, SigShare share) {
        return new MainVoteMessage(pid, replicaId, round, value, justification, share);
    }

    public MainVoteMessage createMainVoteMessage(SigShare just1, SigShare just2, SigShare share) {
        return null;
        // TODO return new MainVoteMessage(pid, replicaId, round, just1, just2, share);
    }

    public CoinMessage createCoinMessage(SigShare coinShare) {
        return new CoinMessage(pid, replicaId, round, coinShare);
    }
}
