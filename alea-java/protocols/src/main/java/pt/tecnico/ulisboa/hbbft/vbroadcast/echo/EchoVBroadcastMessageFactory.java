package pt.tecnico.ulisboa.hbbft.vbroadcast.echo;

import pt.tecnico.ulisboa.hbbft.utils.threshsig.SigShare;
import pt.tecnico.ulisboa.hbbft.vbroadcast.echo.messages.EchoMessage;
import pt.tecnico.ulisboa.hbbft.vbroadcast.echo.messages.FinalMessage;
import pt.tecnico.ulisboa.hbbft.vbroadcast.echo.messages.SendMessage;

public class EchoVBroadcastMessageFactory {

    private final String pid;
    private final Integer replicaId;

    public EchoVBroadcastMessageFactory(String pid, Integer replicaId) {
        this.pid = pid;
        this.replicaId = replicaId;
    }

    public SendMessage createSendMessage(byte[] value) {
        return new SendMessage(pid, replicaId, value);
    }

    public EchoMessage createEchoMessage(byte[] value, SigShare share) {
        return new EchoMessage(pid, replicaId, value, share);
    }

    public FinalMessage createFinalMessage(byte[] value, byte[] signature) {
        return new FinalMessage(pid, replicaId, value, signature);
    }
}
