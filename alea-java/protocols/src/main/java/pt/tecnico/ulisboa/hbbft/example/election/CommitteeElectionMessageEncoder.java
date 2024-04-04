package pt.tecnico.ulisboa.hbbft.example.election;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import pt.tecnico.ulisboa.hbbft.MessageEncoder;
import pt.tecnico.ulisboa.hbbft.ProtocolMessage;
import pt.tecnico.ulisboa.hbbft.election.CommitteeElectionMessage;
import pt.tecnico.ulisboa.hbbft.election.messages.ShareMessage;

import java.util.Base64;

public class CommitteeElectionMessageEncoder implements MessageEncoder<String> {

    private final Gson gson = new Gson();

    @Override
    public String encode(ProtocolMessage message) {
        if (message.getType() == ShareMessage.SHARE)
            return this.encodeShareMessage((ShareMessage) message);
        else return null; // FIXME throw exception
    }

    private JsonObject encodeCommitteeElectionMessage(CommitteeElectionMessage message) {
        JsonObject root = new JsonObject();
        root.addProperty("pid", message.getPid());
        root.addProperty("type", message.getType());
        root.addProperty("sender", message.getSender());
        return root;
    }

    private String encodeShareMessage(ShareMessage message) {
        JsonObject root = encodeCommitteeElectionMessage(message);
        root.addProperty("share", Base64.getEncoder().encodeToString(message.getShare()));
        return root.toString();
    }

    @Override
    public ProtocolMessage decode(String data) {
        JsonObject root = gson.fromJson(data, JsonObject.class);
        final String pid = root.get("pid").getAsString();
        final int type = root.get("type").getAsInt();
        final int sender = root.get("sender").getAsInt();

        if (type == ShareMessage.SHARE) {
            final byte[] share = Base64.getDecoder().decode(root.get("share").getAsString());
            return new ShareMessage(pid, sender, share);
        } else {
            return null;  // FIXME throw exception
        }
    }
}
