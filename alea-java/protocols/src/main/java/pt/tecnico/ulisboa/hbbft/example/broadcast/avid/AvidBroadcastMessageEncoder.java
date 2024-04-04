package pt.tecnico.ulisboa.hbbft.example.broadcast.avid;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import pt.tecnico.ulisboa.hbbft.MessageEncoder;
import pt.tecnico.ulisboa.hbbft.ProtocolMessage;
import pt.tecnico.ulisboa.hbbft.broadcast.BroadcastMessage;
import pt.tecnico.ulisboa.hbbft.broadcast.avid.messages.EchoMessage;
import pt.tecnico.ulisboa.hbbft.broadcast.avid.messages.ReadyMessage;
import pt.tecnico.ulisboa.hbbft.broadcast.avid.messages.ValueMessage;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class AvidBroadcastMessageEncoder implements MessageEncoder<String> {

    private Gson gson = new Gson();

    @Override
    public String encode(ProtocolMessage message) {
        switch (message.getType()) {
            case ValueMessage.VALUE:
                return this.encodeValueMessage((ValueMessage) message);
            case EchoMessage.ECHO:
                return this.encodeEchoMessage((EchoMessage) message);
            case ReadyMessage.READY:
                return this.encodeReadyMessage((ReadyMessage) message);
            default:
                return null; // TODO throw exception
        }
    }

    private JsonObject encodeBroadcastMessage(BroadcastMessage message) {
        JsonObject root = new JsonObject();
        root.addProperty("pid", message.getPid());
        root.addProperty("type", message.getType());
        root.addProperty("sender", message.getSender());
        return root;
    }

    private String encodeValueMessage(ValueMessage message) {
        JsonObject root = this.encodeBroadcastMessage(message);
        root.addProperty("root", Base64.getEncoder().encodeToString(message.getRoot()));
        JsonArray branch = new JsonArray();
        for (byte[] v: message.getBranch())
            branch.add(Base64.getEncoder().encodeToString(v));
        root.add("branch", branch);
        root.addProperty("value", Base64.getEncoder().encodeToString(message.getValue()));
        return root.toString();
    }

    private String encodeEchoMessage(EchoMessage message) {
        JsonObject root = this.encodeBroadcastMessage(message);
        root.addProperty("root", Base64.getEncoder().encodeToString(message.getRoot()));
        JsonArray branch = new JsonArray();
        for (byte[] v: message.getBranch())
            branch.add(Base64.getEncoder().encodeToString(v));
        root.add("branch", branch);
        root.addProperty("value", Base64.getEncoder().encodeToString(message.getValue()));
        return root.toString();
    }

    private String encodeReadyMessage(ReadyMessage message) {
        JsonObject root = this.encodeBroadcastMessage(message);
        root.addProperty("root", Base64.getEncoder().encodeToString(message.getRoot()));
        return root.toString();
    }

    @Override
    public BroadcastMessage decode(String data) {
        JsonObject root = gson.fromJson(data, JsonObject.class);
        int type = root.get("type").getAsInt();
        switch (type) {
            case ValueMessage.VALUE:
                return this.decodeValueMessage(data);
            case EchoMessage.ECHO:
                return this.decodeEchoMessage(data);
            case ReadyMessage.READY:
                return this.decodeReadyMessage(data);
            default:
                return null; // TODO throw exception
        }
    }

    private ValueMessage decodeValueMessage(String data) {
        JsonObject root = gson.fromJson(data, JsonObject.class);
        String bid = root.get("pid").getAsString();
        int senderId = root.get("sender").getAsInt();

        byte[] rootHash = Base64.getDecoder().decode(root.get("root").getAsString().getBytes());
        List<byte[]> branch = new ArrayList<>();
        for (JsonElement element: root.getAsJsonArray("branch"))
            branch.add( Base64.getDecoder().decode(element.getAsString()));
        byte[] value = Base64.getDecoder().decode(root.get("value").getAsString().getBytes());

        return new ValueMessage(bid, senderId, rootHash, branch, value);
    }

    private EchoMessage decodeEchoMessage(String data) {
        JsonObject root = gson.fromJson(data, JsonObject.class);
        String bid = root.get("pid").getAsString();
        int senderId = root.get("sender").getAsInt();

        byte[] rootHash = Base64.getDecoder().decode(root.get("root").getAsString().getBytes());
        List<byte[]> branch = new ArrayList<>();
        for (JsonElement element: root.getAsJsonArray("branch"))
            branch.add( Base64.getDecoder().decode(element.getAsString()));
        byte[] value = Base64.getDecoder().decode(root.get("value").getAsString().getBytes());

        return new EchoMessage(bid, senderId, rootHash, branch, value);
    }

    private ReadyMessage decodeReadyMessage(String data) {
        JsonObject root = gson.fromJson(data, JsonObject.class);
        String bid = root.get("pid").getAsString();
        int senderId = root.get("sender").getAsInt();

        byte[] rootHash = Base64.getDecoder().decode(root.get("root").getAsString().getBytes());

        return new ReadyMessage(bid, senderId, rootHash);
    }
}
