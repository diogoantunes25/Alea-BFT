package pt.tecnico.ulisboa.hbbft.election.messages;

import pt.tecnico.ulisboa.hbbft.election.CommitteeElectionMessage;

public class ShareMessage extends CommitteeElectionMessage {

    public static final int SHARE = 600;

    private final byte[] share;

    public ShareMessage(String pid, Integer sender, byte[] share) {
        super(pid, SHARE, sender);
        this.share = share;
    }

    public byte[] getShare() {
        return share;
    }
}
