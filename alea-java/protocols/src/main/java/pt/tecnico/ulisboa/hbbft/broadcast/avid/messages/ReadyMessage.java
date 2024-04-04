package pt.tecnico.ulisboa.hbbft.broadcast.avid.messages;

import pt.tecnico.ulisboa.hbbft.broadcast.BroadcastMessage;

public class ReadyMessage extends BroadcastMessage {

    public static final int READY = 422;

    private final byte[] root;

    public ReadyMessage(String bid, Integer sender, byte[] root) {
        super(bid, READY, sender);
        this.root = root;
    }

    public byte[] getRoot() {
        return root;
    }
}
