package pt.tecnico.ulisboa.hbbft.broadcast.bracha.messages;

import pt.tecnico.ulisboa.hbbft.broadcast.BroadcastMessage;

import java.util.Arrays;

public class EchoMessage extends BroadcastMessage {

    public static final int ECHO = 122;

    private final byte[] value;

    public EchoMessage(String pid, Integer sender, byte[] value) {
        super(pid, ECHO, sender);
        this.value = value;
    }

    public byte[] getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "EchoMessage{" +
                "parent=" + super.toString() +
//                ", value=" + new String(value) +
                '}';
    }
}
