package pt.tecnico.ulisboa.hbbft.broadcast.echo.messages;

import pt.tecnico.ulisboa.hbbft.broadcast.BroadcastMessage;

public class SendMessage extends BroadcastMessage {

    public static final int SEND = 131;

    private final byte[] value;

    public SendMessage(String pid, Integer sender, byte[] value) {
        super(pid, SEND, sender);
        this.value = value;
    }

    public byte[] getValue() {
        return value;
    }
}