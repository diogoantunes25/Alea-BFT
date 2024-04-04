package pt.tecnico.ulisboa.hbbft.broadcast.bracha2;

import pt.tecnico.ulisboa.hbbft.broadcast.bracha2.messages.EchoMessage;
import pt.tecnico.ulisboa.hbbft.broadcast.bracha2.messages.ReadyMessage;
import pt.tecnico.ulisboa.hbbft.broadcast.bracha2.messages.SendMessage;

import java.util.List;

public class BrachaBroadcast2MessageFactory {

    private final String bid;
    private final Integer replicaId;

    public BrachaBroadcast2MessageFactory(String bid, Integer replicaId) {
        this.bid = bid;
        this.replicaId = replicaId;
    }

    public SendMessage createSendMessage(byte[] value, long rootHash, List<Long> branch, int inputSize) {
        return new SendMessage(bid, replicaId, value, rootHash, branch, inputSize);
    }

    public EchoMessage createEchoMessage(byte[] value, long rootHash, List<Long> branch, int blockId, int inputSize) {
        return new EchoMessage(bid, replicaId, value, rootHash, branch, blockId, inputSize);
    }

    public ReadyMessage createReadyMessage(long rootHash) {
        return new ReadyMessage(bid, replicaId, rootHash);
    }
}
