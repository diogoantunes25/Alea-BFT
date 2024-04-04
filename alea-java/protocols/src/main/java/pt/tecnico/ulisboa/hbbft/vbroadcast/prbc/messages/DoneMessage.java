package pt.tecnico.ulisboa.hbbft.vbroadcast.prbc.messages;

import pt.tecnico.ulisboa.hbbft.ProtocolMessage;
import pt.tecnico.ulisboa.hbbft.utils.threshsig.SigShare;

public class DoneMessage extends ProtocolMessage {

    public static final int DONE = 701;

    private final SigShare share;

    public DoneMessage(String pid, Integer sender, SigShare share) {
        super(pid, DONE, sender);
        this.share = share;
    }

    public SigShare getShare() {
        return share;
    }

    @Override
    public String toString() {
        return "DoneMessage{" +
                "parent=" + super.toString() +
                ", share=" + share +
                '}';
    }
}
