package pt.tecnico.ulisboa.hbbft.example.broadcast.bracha2;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import pt.tecnico.ulisboa.hbbft.MessageEncoder;
import pt.tecnico.ulisboa.hbbft.ProtocolMessage;
import pt.tecnico.ulisboa.hbbft.broadcast.BroadcastMessage;
import pt.tecnico.ulisboa.hbbft.broadcast.bracha2.messages.EchoMessage;
import pt.tecnico.ulisboa.hbbft.broadcast.bracha2.messages.ReadyMessage;
import pt.tecnico.ulisboa.hbbft.broadcast.bracha2.messages.SendMessage;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class BrachaBroadcast2MessageEncoder implements MessageEncoder<String> {

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
        root.addProperty("rootHash", message.getRootHash());
        root.addProperty("inputSize", message.getInputSize());
        JsonArray branch = new JsonArray();
        message.getBranch().stream().forEach(branch::add);
        root.add("branch", branch);
        return root.toString();
    }

    private String encodeEchoMessage(EchoMessage message) {
        JsonObject root = encodeBroadcastMessage(message);
        root.addProperty("value", Base64.getEncoder().encodeToString(message.getValue()));
        root.addProperty("rootHash", message.getRootHash());
        root.addProperty("blockId", message.getBlockId());
        root.addProperty("inputSize", message.getInputSize());
        JsonArray branch = new JsonArray();
        message.getBranch().stream().forEach(branch::add);
        root.add("branch", branch);

        return root.toString();
    }

    private String encodeReadyMessage(ReadyMessage message) {
        JsonObject root = encodeBroadcastMessage(message);
        root.addProperty("rootHash", message.getRootHash());
        return root.toString();
    }

    @Override
    public ProtocolMessage decode(String data) {

        JsonObject root = gson.fromJson(data, JsonObject.class);
        final String bid = root.get("pid").getAsString();
        final int type = root.get("type").getAsInt();
        final int sender = root.get("sender").getAsInt();
        final long rootHash = root.get("rootHash").getAsLong();
        List<Long> branch = new ArrayList<>();
        final int inputSize;
        final byte[] value;
        switch (type) {
            case SendMessage.SEND:
                inputSize = root.get("inputSize").getAsInt();
                value = Base64.getDecoder().decode(root.get("value").getAsString().getBytes());
                root.get("branch").getAsJsonArray().forEach(jsonElement -> branch.add(jsonElement.getAsLong()));
                return new SendMessage(bid, sender, value, rootHash, branch, inputSize);
            case EchoMessage.ECHO:
                inputSize = root.get("inputSize").getAsInt();
                int blockId = root.get("blockId").getAsInt();
                value = Base64.getDecoder().decode(root.get("value").getAsString().getBytes());
                root.get("branch").getAsJsonArray().forEach(jsonElement -> branch.add(jsonElement.getAsLong()));
                return new EchoMessage(bid, sender, value, rootHash, branch, blockId, inputSize);
            case ReadyMessage.READY:
                return new ReadyMessage(bid, sender, rootHash);
            default:
                return null; // FIXME throw exception
        }
    }
}
