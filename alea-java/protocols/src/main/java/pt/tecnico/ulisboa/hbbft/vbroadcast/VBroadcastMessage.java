package pt.tecnico.ulisboa.hbbft.vbroadcast;

import pt.tecnico.ulisboa.hbbft.ProtocolMessage;

public class VBroadcastMessage extends ProtocolMessage {
    public VBroadcastMessage(String pid, Integer type, Integer sender) {
        super(pid, type, sender);
    }
}
