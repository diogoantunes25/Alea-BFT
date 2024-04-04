package pt.tecnico.ulisboa.hbbft.broadcast.avid;

import pt.tecnico.ulisboa.hbbft.broadcast.avid.crypto.merkle.Proof;
import pt.tecnico.ulisboa.hbbft.broadcast.avid.messages.EchoMessage;
import pt.tecnico.ulisboa.hbbft.broadcast.avid.messages.ReadyMessage;
import pt.tecnico.ulisboa.hbbft.broadcast.avid.messages.ValueMessage;

public class AvidBroadcastMessageFactory {

    private final String bid;
    private final Integer replicaId;

    public AvidBroadcastMessageFactory(String bid, Integer replicaId) {
        this.bid = bid;
        this.replicaId = replicaId;
    }

    public ValueMessage createValueMessage(Proof proof) {
        return new ValueMessage(bid, replicaId, proof.getRootHash(), proof.getDigests(), proof.getValue());
    }

    public EchoMessage createEchoMessage(Proof proof) {
        return new EchoMessage(bid, replicaId, proof.getRootHash(), proof.getDigests(), proof.getValue());
    }

    public ReadyMessage createReadyMessage(Proof proof) {
        return new ReadyMessage(bid, replicaId, proof.getRootHash());
    }

    public ReadyMessage createReadyMessage(byte[] root) {
        return new ReadyMessage(bid, replicaId, root);
    }
}
