package pt.tecnico.ulisboa.hbbft.vbroadcast.echo.messages;

import pt.tecnico.ulisboa.hbbft.utils.threshsig.SigShare;
import pt.tecnico.ulisboa.hbbft.vbroadcast.VBroadcastMessage;

import java.util.Arrays;

public class EchoMessage extends VBroadcastMessage {

    public static final int ECHO = 132;

    private final byte[] value;
    private final SigShare share;

    public EchoMessage(String pid, Integer sender, byte[] value,  SigShare share) {
        super(pid, ECHO, sender);
        this.value = value;
        this.share = share;
    }

    public byte[] getValue() {
        return value;
    }

    public SigShare getShare() {
        return share;
    }

    @Override
    public String toString() {
        return "EchoMessage{" +
                "parent=" + super.toString() +
                ", value=" + Arrays.toString(value) +
                ", share=" + share +
                '}';
    }
}
