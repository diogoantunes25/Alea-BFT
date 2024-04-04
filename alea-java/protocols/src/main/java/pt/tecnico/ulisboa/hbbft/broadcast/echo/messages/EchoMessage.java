package pt.tecnico.ulisboa.hbbft.broadcast.echo.messages;

import pt.tecnico.ulisboa.hbbft.broadcast.BroadcastMessage;

public class EchoMessage extends BroadcastMessage {

    public static final int ECHO = 132;

    private final byte[] value;
    private final byte[] signature;

    public EchoMessage(String pid, Integer sender, byte[] value, byte[] signature) {
        super(pid, ECHO, sender);
        this.value = value;
        this.signature = signature;
    }

    public byte[] getValue() {
        return value;
    }

    public byte[] getSignature() {
        return signature;
    }
}
