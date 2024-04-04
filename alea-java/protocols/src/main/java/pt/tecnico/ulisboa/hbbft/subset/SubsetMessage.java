package pt.tecnico.ulisboa.hbbft.subset;

import pt.tecnico.ulisboa.hbbft.ProtocolMessage;

public class SubsetMessage extends ProtocolMessage {

    private final Integer instance;
    private final ProtocolMessage content;

    public SubsetMessage(String pid, Integer type, Integer sender, Integer instance, ProtocolMessage content) {
        super(pid, type, sender);
        this.instance = instance;
        this.content = content;
    }

    public Integer getInstance() {
        return instance;
    }

    public ProtocolMessage getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "SubsetMessage{" +
                "parent=" + super.toString() +
                ", instance=" + instance +
                ", content=" + content +
                '}';
    }
}
