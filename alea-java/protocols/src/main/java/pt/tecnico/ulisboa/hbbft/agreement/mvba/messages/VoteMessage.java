package pt.tecnico.ulisboa.hbbft.agreement.mvba.messages;

import pt.tecnico.ulisboa.hbbft.ProtocolMessage;

public class VoteMessage extends ProtocolMessage {

    public static final int VOTE = 901;

    private final Integer candidate;
    private final Boolean vote;
    private final byte[] proof;

    public VoteMessage(String pid, Integer sender, Integer candidate, Boolean vote, byte[] proof) {
        super(pid, VOTE, sender);
        this.candidate = candidate;
        this.vote = vote;
        this.proof = proof != null ? proof : new byte[0];
    }

    public Integer getCandidate() {
        return candidate;
    }

    public Boolean getVote() {
        return vote;
    }

    public byte[] getProof() {
        return proof;
    }
}
