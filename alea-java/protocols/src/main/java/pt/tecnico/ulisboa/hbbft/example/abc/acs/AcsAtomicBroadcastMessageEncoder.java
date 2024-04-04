package pt.tecnico.ulisboa.hbbft.example.abc.acs;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import pt.tecnico.ulisboa.hbbft.MessageEncoder;
import pt.tecnico.ulisboa.hbbft.ProtocolMessage;
import pt.tecnico.ulisboa.hbbft.abc.acs.messages.DecryptionMessage;
import pt.tecnico.ulisboa.hbbft.subset.SubsetMessage;

import java.util.Base64;

public class AcsAtomicBroadcastMessageEncoder implements MessageEncoder<String> {

    private final Gson gson = new Gson();

    private final MessageEncoder<String> acsEncoder;

    public AcsAtomicBroadcastMessageEncoder(MessageEncoder<String> acsEncoder) {
        this.acsEncoder = acsEncoder;
    }

    @Override
    public String encode(ProtocolMessage message) {
        if (message instanceof SubsetMessage) {
            return this.acsEncoder.encode(message);

        } else if (message instanceof DecryptionMessage) {
            return this.encodeDecryptionMessage((DecryptionMessage) message);

        } else {
            return null;  // FIXME
        }
    }

    private String encodeDecryptionMessage(DecryptionMessage message) {
        JsonObject root = new JsonObject();
        root.addProperty("pid", message.getPid());
        root.addProperty("type", message.getType());
        root.addProperty("sender", message.getSender());
        root.addProperty("share", Base64.getEncoder().encodeToString(message.getShare()));
        return root.toString();
    }

    @Override
    public ProtocolMessage decode(String data) {
        JsonObject root = gson.fromJson(data, JsonObject.class);

        final int type = root.get("type").getAsInt();

        if (type == DecryptionMessage.DECRYPTION) {
            return this.decodeDecryptMessage(data);
        } else {
            return this.acsEncoder.decode(data);
        }
    }

    private ProtocolMessage decodeDecryptMessage(String data) {
        JsonObject root = gson.fromJson(data, JsonObject.class);
        final String pid = root.get("pid").getAsString();
        final int senderId = root.get("sender").getAsInt();
        final byte[] share = Base64.getDecoder().decode(root.get("share").getAsString().getBytes());
        return new DecryptionMessage(pid, senderId, share);
    }
}
