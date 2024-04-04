package pt.tecnico.ulisboa.hbbft.broadcast.bracha2.messages;

import pt.tecnico.ulisboa.hbbft.broadcast.BroadcastMessage;

import java.util.Arrays;
import java.util.List;

public class EchoMessage extends BroadcastMessage {

    public static final int ECHO = 122;

    private final byte[] value;

    private final long rootHash;

    private final List<Long> branch;

    private final int blockId;

    private final int inputSize;

    public EchoMessage(String pid, Integer sender, byte[] value, long rootHash, List<Long> branch, int blockId, int inputSize) {
        super(pid, ECHO, sender);
        this.value = value;
        this.rootHash = rootHash;
        this.branch = branch;
        this.blockId = blockId;
        this.inputSize = inputSize;
    }

    public long getRootHash() {
        return rootHash;
    }

    public List<Long> getBranch() {
        return branch;
    }

    public byte[] getValue() {
        return value;
    }

    public int getBlockId() {
        return blockId;
    }

    public int getInputSize() {
        return inputSize;
    }

    @Override
    public String toString() {
        return "EchoMessage{" +
                "parent=" + super.toString() +
                " ,rootHash=" + this.rootHash +
                " ,value=" + Arrays.toString(value) +
                " ,branch=" + String.format("%s", branch) +
                " ,rootHash=" + this.inputSize +
                '}';
    }
}
