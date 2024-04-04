package pt.tecnico.ulisboa.hbbft.example.abc.byzness;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import pt.tecnico.ulisboa.hbbft.MessageEncoder;
import pt.tecnico.ulisboa.hbbft.ProtocolMessage;
import pt.tecnico.ulisboa.hbbft.utils.threshsig.SigShare;
import pt.tecnico.ulisboa.hbbft.vbroadcast.VBroadcastMessage;
import pt.tecnico.ulisboa.hbbft.vbroadcast.echo.messages.*;

import java.util.Base64;

public class EchoVBroadcastMessageEncoder implements MessageEncoder<String> {

    private Gson gson = new Gson();

    @Override
    public String encode(ProtocolMessage message) {
        String encodedMessage;
        switch ((message.getType())) {
            case SendMessage.SEND:
                encodedMessage = this.encodeSendMessage((SendMessage) message);
                return encodedMessage;
            case EchoMessage.ECHO:
                encodedMessage = this.encodeEchoMessage((EchoMessage) message);
                return encodedMessage;
            case FinalMessage.FINAL:
                encodedMessage = this.encodeFinalMessage((FinalMessage) message);
                return encodedMessage;
            default:
                return null; // FIXME throw exception
        }
    }

    private JsonObject encodeVBroadcastMessage(VBroadcastMessage message) {
        JsonObject root = new JsonObject();
        root.addProperty("pid", message.getPid());
        root.addProperty("type", message.getType());
        root.addProperty("sender", message.getSender());
        return root;
    }

    private String encodeSendMessage(SendMessage message) {
        JsonObject root = encodeVBroadcastMessage(message);
        root.addProperty("value", Base64.getEncoder().encodeToString(message.getValue()));
        return root.toString();
    }

    private String encodeEchoMessage(EchoMessage message) {
        JsonObject root = encodeVBroadcastMessage(message);
        root.addProperty("value", Base64.getEncoder().encodeToString(message.getValue()));
        root.addProperty("share", Base64.getEncoder().encodeToString(message.getShare().toBytes()));
        return root.toString();
    }

    private String encodeFinalMessage(FinalMessage message) {
        JsonObject root = encodeVBroadcastMessage(message);
        root.addProperty("value", Base64.getEncoder().encodeToString(message.getValue()));
        root.addProperty("signature", Base64.getEncoder().encodeToString(message.getSignature()));
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
                final byte[] share = Base64.getDecoder().decode(root.get("share").getAsString().getBytes());
                return new EchoMessage(pid, sender, value, SigShare.fromBytes(share));
            }

            case FinalMessage.FINAL: {
                final byte[] signature = Base64.getDecoder().decode(root.get("signature").getAsString().getBytes());
                return new FinalMessage(pid, sender, value, signature);
            }

            default:
                return null; // FIXME throw exception
        }
    }
}
