package pt.tecnico.ulisboa.hbbft.abc.dumbo;

import pt.tecnico.ulisboa.hbbft.ProtocolMessage;

public class DumboMessage extends ProtocolMessage {

    private final Long epoch;
    private final ProtocolMessage content;

    public DumboMessage(String pid, Integer type, Integer sender, Long epoch, ProtocolMessage content) {
        super(pid, type, sender);
        this.epoch = epoch;
        this.content = content;
    }

    public Long getEpoch() {
        return epoch;
    }

    public ProtocolMessage getContent() {
        return content;
    }
}
