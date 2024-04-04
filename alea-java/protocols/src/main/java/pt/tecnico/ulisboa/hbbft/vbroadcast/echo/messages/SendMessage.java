package pt.tecnico.ulisboa.hbbft.vbroadcast.echo.messages;

import pt.tecnico.ulisboa.hbbft.vbroadcast.VBroadcastMessage;

import java.util.Arrays;

public class SendMessage extends VBroadcastMessage {

    public static final int SEND = 131;

    private final byte[] value;

    public SendMessage(String pid, Integer sender, byte[] value) {
        super(pid, SEND, sender);
        this.value = value;
    }

    public byte[] getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "SendMessage{" +
                "parent=" + super.toString() +
                ", value=" + Arrays.toString(value) +
                '}';
    }
}
