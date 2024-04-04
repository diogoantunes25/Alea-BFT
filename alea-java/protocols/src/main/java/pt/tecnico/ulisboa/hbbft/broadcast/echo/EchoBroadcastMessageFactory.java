package pt.tecnico.ulisboa.hbbft.broadcast.echo;

import pt.tecnico.ulisboa.hbbft.broadcast.echo.messages.EchoMessage;
import pt.tecnico.ulisboa.hbbft.broadcast.echo.messages.FinalMessage;
import pt.tecnico.ulisboa.hbbft.broadcast.echo.messages.SendMessage;

import java.util.Map;

public class EchoBroadcastMessageFactory {

    private final String pid;
    private final Integer replicaId;

    public EchoBroadcastMessageFactory(String pid, Integer replicaId) {
        this.pid = pid;
        this.replicaId = replicaId;
    }

    public SendMessage createSendMessage(byte[] value) {
        return new SendMessage(pid, replicaId, value);
    }

    public EchoMessage createEchoMessage(byte[] value, byte[] signature) {
        return new EchoMessage(pid, replicaId, value, signature);
    }

    public FinalMessage createFinalMessage(byte[] value, Map<Integer, byte[]> proof) {
        return new FinalMessage(pid, replicaId, value, proof);
    }
}
