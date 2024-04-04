package pt.tecnico.ulisboa.hbbft.broadcast.bracha2.messages;

import pt.tecnico.ulisboa.hbbft.broadcast.BroadcastMessage;

import java.util.Arrays;

public class ReadyMessage extends BroadcastMessage {

    public static final int READY = 123;

    private final long rootHash;

    public ReadyMessage(String pid, Integer sender, long rootHash) {
        super(pid, READY, sender);
        this.rootHash = rootHash;
    }

    public long getRootHash() {
        return rootHash;
    }

    @Override
    public String toString() {
        return "ReadyMessage{" +
                "parent=" + super.toString() +
                '}';
    }
}
