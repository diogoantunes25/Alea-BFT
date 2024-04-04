package pt.tecnico.ulisboa.hbbft.example.broadcast.bracha;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import pt.tecnico.ulisboa.hbbft.MessageEncoder;
import pt.tecnico.ulisboa.hbbft.ProtocolMessage;
import pt.tecnico.ulisboa.hbbft.broadcast.BroadcastMessage;
import pt.tecnico.ulisboa.hbbft.broadcast.bracha.messages.EchoMessage;
import pt.tecnico.ulisboa.hbbft.broadcast.bracha.messages.ReadyMessage;
import pt.tecnico.ulisboa.hbbft.broadcast.bracha.messages.SendMessage;

import java.util.Base64;

public class BrachaBroadcastMessageEncoder implements MessageEncoder<String> {

    private final Gson gson = new Gson();

    @Override
    public String encode(ProtocolMessage message) {
        switch (message.getType()) {
            case SendMessage.SEND:
                return this.encodeSendMessage((SendMessage) message);
            case EchoMessage.ECHO:
                return this.encodeEchoMessage((EchoMessage) message);
            case ReadyMessage.READY:
                return this.encodeReadyMessage((ReadyMessage) message);
            default:
                return null; // FIXME throw exception
        }
    }

    private JsonObject encodeBroadcastMessage(BroadcastMessage message) {
        JsonObject root = new JsonObject();
        root.addProperty("pid", message.getPid());
        root.addProperty("type", message.getType());
        root.addProperty("sender", message.getSender());
        return root;
    }

    private String encodeSendMessage(SendMessage message) {
        JsonObject root = encodeBroadcastMessage(message);
        root.addProperty("value", Base64.getEncoder().encodeToString(message.getValue()));
        return root.toString();
    }

    private String encodeEchoMessage(EchoMessage message) {
        JsonObject root = encodeBroadcastMessage(message);
        root.addProperty("value", Base64.getEncoder().encodeToString(message.getValue()));
        return root.toString();
    }

    private String encodeReadyMessage(ReadyMessage message) {
        JsonObject root = encodeBroadcastMessage(message);
        root.addProperty("value", Base64.getEncoder().encodeToString(message.getValue()));
        return root.toString();
    }

    @Override
    public ProtocolMessage decode(String data) {
        JsonObject root = gson.fromJson(data, JsonObject.class);
        final String bid = root.get("pid").getAsString();
        final int type = root.get("type").getAsInt();
        final int sender = root.get("sender").getAsInt();
        final byte[] value = Base64.getDecoder().decode(root.get("value").getAsString().getBytes());

        switch (type) {
            case SendMessage.SEND:
                return new SendMessage(bid, sender, value);
            case EchoMessage.ECHO:
                return new EchoMessage(bid, sender, value);
            case ReadyMessage.READY:
                return new ReadyMessage(bid, sender, value);
            default:
                return null; // FIXME throw exception
        }
    }
}
