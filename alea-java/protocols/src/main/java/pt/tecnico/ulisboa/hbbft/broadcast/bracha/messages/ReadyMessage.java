package pt.tecnico.ulisboa.hbbft.broadcast.bracha.messages;

import pt.tecnico.ulisboa.hbbft.broadcast.BroadcastMessage;

import java.util.Arrays;

public class ReadyMessage extends BroadcastMessage {

    public static final int READY = 123;

    private final byte[] value;

    public ReadyMessage(String pid, Integer sender, byte[] value) {
        super(pid, READY, sender);
        this.value = value;
    }

    public byte[] getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "ReadyMessage{" +
                "parent=" + super.toString() +
//                ", value=" + new String(value) +
                '}';
    }
}
