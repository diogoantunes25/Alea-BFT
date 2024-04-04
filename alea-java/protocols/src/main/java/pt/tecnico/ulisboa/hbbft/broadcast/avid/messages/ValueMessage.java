package pt.tecnico.ulisboa.hbbft.broadcast.avid.messages;

import pt.tecnico.ulisboa.hbbft.broadcast.BroadcastMessage;

import java.util.List;

public class ValueMessage extends BroadcastMessage {

    public static final int VALUE = 420;

    private final byte[] root;
    private final List<byte[]> branch;
    private final byte[] value;


    public ValueMessage(String bid, Integer sender, byte[] root, List<byte[]> branch, byte[] value) {
        super(bid, VALUE, sender);
        this.root = root;
        this.branch = branch;
        this.value = value;
    }

    public byte[] getRoot() {
        return root;
    }

    public List<byte[]> getBranch() {
        return branch;
    }

    public byte[] getValue() {
        return value;
    }
}
