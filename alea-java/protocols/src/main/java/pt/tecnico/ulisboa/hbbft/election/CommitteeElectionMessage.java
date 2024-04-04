package pt.tecnico.ulisboa.hbbft.election;

import pt.tecnico.ulisboa.hbbft.ProtocolMessage;

public class CommitteeElectionMessage extends ProtocolMessage {
    public CommitteeElectionMessage(String pid, Integer type, Integer sender) {
        super(pid, type, sender);
    }
}
