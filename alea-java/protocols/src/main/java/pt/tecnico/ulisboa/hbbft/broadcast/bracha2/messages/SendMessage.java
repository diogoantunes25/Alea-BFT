package pt.tecnico.ulisboa.hbbft.broadcast.bracha2.messages;

import pt.tecnico.ulisboa.hbbft.broadcast.BroadcastMessage;

import java.util.Arrays;
import java.util.List;

public class SendMessage extends BroadcastMessage {

    public static final int SEND = 121;

    private final byte[] value;

    private final long rootHash;

    private final List<Long> branch;

    private final int inputSize;

    public SendMessage(String bid, Integer sender, byte[] value, long rootHash, List<Long> branch, int inputSize) {
        super(bid, SEND, sender);
        this.value = value;
        this.rootHash = rootHash;
        this.branch = branch;
        this.inputSize = inputSize;
    }

    public byte[] getValue() {
        return value;
    }

    public long getRootHash() {
        return rootHash;
    }

    public List<Long> getBranch() {
        return branch;
    }

    public int getInputSize() { return inputSize; }

    @Override
    public String toString() {
        return "SendMessage{" +
                "parent=" + super.toString() +
                ", value=" + Arrays.toString(value) +
                ", rootHash=" + rootHash +
                ", branch=" + branch +
                ", inputSize=" + inputSize +
                '}';
    }
}