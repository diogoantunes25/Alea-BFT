package pt.tecnico.ulisboa.hbbft.broadcast.bracha.messages;

import pt.tecnico.ulisboa.hbbft.broadcast.BroadcastMessage;

import java.util.Arrays;
import java.util.List;

public class SendMessage extends BroadcastMessage {

    public static final int SEND = 121;

    private final byte[] value;

    public SendMessage(String bid, Integer sender, byte[] value) {
        super(bid, SEND, sender);
        this.value = value;
    }

    public byte[] getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "SendMessage{" +
                "parent=" + super.toString() +
//                ", value=" + new String(value) +
                '}';
    }
}
