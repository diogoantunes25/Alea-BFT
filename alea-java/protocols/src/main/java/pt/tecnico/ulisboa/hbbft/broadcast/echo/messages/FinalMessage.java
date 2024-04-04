package pt.tecnico.ulisboa.hbbft.broadcast.echo.messages;

import pt.tecnico.ulisboa.hbbft.broadcast.BroadcastMessage;

import java.util.Map;

public class FinalMessage extends BroadcastMessage {

    public static final int FINAL = 133;

    private final byte[] value;
    private final Map<Integer, byte[]> proof;

    public FinalMessage(String pid, Integer sender, byte[] value, Map<Integer, byte[]> proof) {
        super(pid, FINAL, sender);
        this.value = value;
        this.proof = proof;
    }

    public byte[] getValue() {
        return value;
    }

    public Map<Integer, byte[]> getProof() {
        return proof;
    }
}
