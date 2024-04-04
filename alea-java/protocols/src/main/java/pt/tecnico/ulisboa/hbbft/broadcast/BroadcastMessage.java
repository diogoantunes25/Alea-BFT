package pt.tecnico.ulisboa.hbbft.broadcast;

import pt.tecnico.ulisboa.hbbft.ProtocolMessage;

public class BroadcastMessage extends ProtocolMessage {

    public BroadcastMessage(String pid, Integer type, Integer sender) {
        super(pid, type, sender);
    }
}
