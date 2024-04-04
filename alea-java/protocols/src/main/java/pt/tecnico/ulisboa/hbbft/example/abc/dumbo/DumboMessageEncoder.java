package pt.tecnico.ulisboa.hbbft.example.abc.dumbo;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import pt.tecnico.ulisboa.hbbft.MessageEncoder;
import pt.tecnico.ulisboa.hbbft.ProtocolMessage;
import pt.tecnico.ulisboa.hbbft.abc.dumbo.DumboMessage;

public class DumboMessageEncoder implements MessageEncoder<String> {

    private final MessageEncoder<String> subsetEncoder;

    private final Gson gson = new Gson();

    public DumboMessageEncoder(MessageEncoder<String> subsetEncoder) {
        this.subsetEncoder = subsetEncoder;
    }

    @Override
    public String encode(ProtocolMessage message) {
        assert (message instanceof DumboMessage);
        return encodeDumboMessage((DumboMessage) message);
    }

    private String encodeDumboMessage(DumboMessage message) {
        JsonObject root = new JsonObject();
        root.addProperty("pid", message.getPid());
        root.addProperty("type", message.getType());
        root.addProperty("sender", message.getSender());
        root.addProperty("epoch", message.getEpoch());
        root.addProperty("content", subsetEncoder.encode(message.getContent()));
        return root.toString();
    }

    @Override
    public ProtocolMessage decode(String data) {
        JsonObject root = gson.fromJson(data, JsonObject.class);
        final String pid = root.get("pid").getAsString();
        final int type = root.get("type").getAsInt();
        final int senderId = root.get("sender").getAsInt();
        final long epoch = root.get("epoch").getAsLong();
        final ProtocolMessage content = subsetEncoder.decode(root.get("content").getAsString());
        return new DumboMessage(pid, type, senderId, epoch, content);
    }
}
