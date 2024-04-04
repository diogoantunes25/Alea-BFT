package pt.tecnico.ulisboa.hbbft.abc.honeybadger;

import pt.tecnico.ulisboa.hbbft.ProtocolMessage;

public class HoneyBadgerMessage extends ProtocolMessage {

    private final Long epoch;
    private final ProtocolMessage content;

    public HoneyBadgerMessage(String pid, Integer type, Integer sender, Long epoch, ProtocolMessage content) {
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
