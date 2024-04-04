package pt.tecnico.ulisboa.hbbft.abc.alea;

import pt.tecnico.ulisboa.hbbft.ProtocolMessage;

public class AleaMessage extends ProtocolMessage {
    public AleaMessage(String pid, Integer type, Integer sender) {
        super(pid, type, sender);
    }
}
