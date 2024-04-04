package pt.tecnico.ulisboa.hbbft;

public class ProtocolMessage {

    private final String pid;
    private final Integer type;
    private final Integer sender;

    public ProtocolMessage(String pid, Integer type, Integer sender) {
        this.pid = pid;
        this.type = type;
        this.sender = sender;
    }

    public String getPid() {
        return pid;
    }

    public Integer getType() {
        return type;
    }

    public Integer getSender() {
        return sender;
    }

    @Override
    public String toString() {
        return "ProtocolMessage{" +
                "pid='" + pid + '\'' +
                ", type=" + type +
                ", sender=" + sender +
                '}';
    }
}
