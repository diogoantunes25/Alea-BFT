package pt.tecnico.ulisboa.hbbft.abc.honeybadger.messages;

import pt.tecnico.ulisboa.hbbft.ProtocolMessage;

public class DecryptionMessage extends ProtocolMessage {

    public static final int DECRYPTION = 400;

    private final byte[] share;

    public DecryptionMessage(String pid, Integer sender, byte[] share) {
        super(pid, DECRYPTION, sender);
        this.share = share;
    }

    public byte[] getShare() {
        return share;
    }
}
