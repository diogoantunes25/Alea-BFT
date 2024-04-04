package pt.tecnico.ulisboa.hbbft.example.broadcast.echo;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import pt.tecnico.ulisboa.hbbft.MessageEncoder;
import pt.tecnico.ulisboa.hbbft.ProtocolMessage;
import pt.tecnico.ulisboa.hbbft.broadcast.BroadcastMessage;
import pt.tecnico.ulisboa.hbbft.broadcast.echo.messages.EchoMessage;
import pt.tecnico.ulisboa.hbbft.broadcast.echo.messages.FinalMessage;
import pt.tecnico.ulisboa.hbbft.broadcast.echo.messages.SendMessage;

import java.lang.reflect.Type;
import java.util.Base64;
import java.util.Map;

public class EchoBroadcastMessageEncoder implements MessageEncoder<String> {

    private Gson gson = new Gson();

    @Override
    public String encode(ProtocolMessage message) {
        switch (message.getType()) {
            case SendMessage.SEND:
                return this.encodeSendMessage((SendMessage) message);
            case EchoMessage.ECHO:
                return this.encodeEchoMessage((EchoMessage) message);
            case FinalMessage.FINAL:
                return this.encodeFinalMessage((FinalMessage) message);
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
        root.addProperty("signature", Base64.getEncoder().encodeToString(message.getSignature()));
        return root.toString();
    }

    private String encodeFinalMessage(FinalMessage message) {
        JsonObject root = encodeBroadcastMessage(message);
        root.addProperty("value", Base64.getEncoder().encodeToString(message.getValue()));
        root.addProperty("proof", gson.toJson(message.getProof()));
        return root.toString();
    }

    @Override
    public ProtocolMessage decode(String data) {
        JsonObject root = gson.fromJson(data, JsonObject.class);
        final String pid = root.get("pid").getAsString();
        final int type = root.get("type").getAsInt();
        final int sender = root.get("sender").getAsInt();
        final byte[] value = Base64.getDecoder().decode(root.get("value").getAsString().getBytes());

        switch (type) {
            case SendMessage.SEND: {
                return new SendMessage(pid, sender, value);
            }

            case EchoMessage.ECHO: {
                final byte[] signature = Base64.getDecoder().decode(root.get("signature").getAsString().getBytes());
                return new EchoMessage(pid, sender, value, signature);
            }

            case FinalMessage.FINAL: {
                Type proofType = new TypeToken<Map<Integer, byte[]>>(){}.getType();
                Map<Integer, byte[]> proof = gson.fromJson(root.get("proof").getAsString(), proofType);
                return new FinalMessage(pid, sender, value, proof);
            }

            default:
                return null; // FIXME throw exception
        }
    }
}
