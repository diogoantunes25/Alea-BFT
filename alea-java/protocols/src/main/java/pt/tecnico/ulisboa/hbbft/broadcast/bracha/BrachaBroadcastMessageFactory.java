package pt.tecnico.ulisboa.hbbft.broadcast.bracha;

import pt.tecnico.ulisboa.hbbft.broadcast.bracha.messages.EchoMessage;
import pt.tecnico.ulisboa.hbbft.broadcast.bracha.messages.ReadyMessage;
import pt.tecnico.ulisboa.hbbft.broadcast.bracha.messages.SendMessage;

public class BrachaBroadcastMessageFactory {

    private final String bid;
    private final Integer replicaId;

    public BrachaBroadcastMessageFactory(String bid, Integer replicaId) {
        this.bid = bid;
        this.replicaId = replicaId;
    }

    public SendMessage createSendMessage(byte[] value) {
        return new SendMessage(bid, replicaId, value);
    }

    public EchoMessage createEchoMessage(byte[] value) {
        return new EchoMessage(bid, replicaId, value);
    }

    public ReadyMessage createReadyMessage(byte[] value) {
        return new ReadyMessage(bid, replicaId, value);
    }
}
