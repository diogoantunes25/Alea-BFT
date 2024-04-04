package pt.tecnico.ulisboa.hbbft.vbroadcast.echo.messages;

import pt.tecnico.ulisboa.hbbft.vbroadcast.VBroadcastMessage;

import java.util.Arrays;

public class FinalMessage extends VBroadcastMessage {

    public static final int FINAL = 133;

    private final byte[] value;
    private final byte[] signature;

    public FinalMessage(String pid, Integer sender, byte[] value, byte[] signature) {
        super(pid, FINAL, sender);
        this.value = value;
        this.signature = signature;
    }

    public byte[] getValue() {
        return value;
    }

    public byte[] getSignature() {
        return signature;
    }

    @Override
    public String toString() {
        return "FinalMessage{" +
                "parent=" + super.toString() +
                ", value=" + value.length +
                ", signature=" + Arrays.toString(signature) +
                '}';
    }
}
