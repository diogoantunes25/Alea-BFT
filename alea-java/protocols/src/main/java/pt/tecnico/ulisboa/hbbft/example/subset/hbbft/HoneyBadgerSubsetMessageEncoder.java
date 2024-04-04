package pt.tecnico.ulisboa.hbbft.example.subset.hbbft;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import pt.tecnico.ulisboa.hbbft.MessageEncoder;
import pt.tecnico.ulisboa.hbbft.ProtocolMessage;
import pt.tecnico.ulisboa.hbbft.subset.SubsetMessage;

public class HoneyBadgerSubsetMessageEncoder implements MessageEncoder<String> {

    private final Gson gson = new Gson();

    private final MessageEncoder<String> bcEncoder;
    private final MessageEncoder<String> baEncoder;

    public HoneyBadgerSubsetMessageEncoder(
            MessageEncoder<String> bcEncoder,
            MessageEncoder<String> baEncoder
    ) {
        this.bcEncoder = bcEncoder;
        this.baEncoder = baEncoder;
    }

    @Override
    public String encode(ProtocolMessage message) {
        assert (message instanceof SubsetMessage);
        return encodeSubsetMessage((SubsetMessage) message);
    }

    private String encodeSubsetMessage(SubsetMessage message) {
        JsonObject root = new JsonObject();
        root.addProperty("pid", message.getPid());
        root.addProperty("type", message.getType());
        root.addProperty("sender", message.getSender());
        root.addProperty("instance", message.getInstance());

        if (message.getType() == 0)
            root.addProperty("content", bcEncoder.encode(message.getContent()));
        else if (message.getType() == 1)
            root.addProperty("content", baEncoder.encode(message.getContent()));

        return root.toString();
    }

    @Override
    public ProtocolMessage decode(String data) {
        JsonObject root = gson.fromJson(data, JsonObject.class);
        String pid = root.get("pid").getAsString();
        final int type = root.get("type").getAsInt();
        final int senderId = root.get("sender").getAsInt();
        final int instance = root.get("instance").getAsInt();

        ProtocolMessage content;
        switch (type) {
            case 0:
                content = bcEncoder.decode(root.get("content").getAsString());
                break;
            case 1:
                content = baEncoder.decode(root.get("content").getAsString());
                break;
            default:
                return null;
        }

        return new SubsetMessage(pid, type, senderId, instance, content);
    }
}
