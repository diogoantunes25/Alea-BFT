package pt.tecnico.ulisboa.hbbft.example.agreement.mvba;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import pt.tecnico.ulisboa.hbbft.MessageEncoder;
import pt.tecnico.ulisboa.hbbft.ProtocolMessage;
import pt.tecnico.ulisboa.hbbft.agreement.mvba.messages.VoteMessage;
import pt.tecnico.ulisboa.hbbft.agreement.vba.ValidatedBinaryAgreementMessage;
import pt.tecnico.ulisboa.hbbft.vbroadcast.VBroadcastMessage;

import java.util.Base64;

public class MvbaMessageEncoder implements MessageEncoder<String> {

    private final Gson gson = new Gson();

    private final MessageEncoder<String> vcbcMessageEncoder;
    private final MessageEncoder<String> vbaMessageEncoder;

    public MvbaMessageEncoder(MessageEncoder<String> vcbcMessageEncoder, MessageEncoder<String> vbaMessageEncoder) {
        this.vcbcMessageEncoder = vcbcMessageEncoder;
        this.vbaMessageEncoder = vbaMessageEncoder;
    }

    @Override
    public String encode(ProtocolMessage message) {
        if (message instanceof VoteMessage) {
            return this.encodeVoteMessage((VoteMessage) message);

        } else if (message instanceof VBroadcastMessage) {
            return this.vcbcMessageEncoder.encode(message);

            //} else if (message instanceof ValidatedBinaryAgreementMessage) {
            //    return this.vbaMessageEncoder.encode(message);
            //}
        } else {
            return this.vbaMessageEncoder.encode(message);
        }
    }

    private String encodeVoteMessage(VoteMessage message) {
        JsonObject root = new JsonObject();
        root.addProperty("pid", message.getPid());
        root.addProperty("type", message.getType());
        root.addProperty("sender", message.getSender());
        root.addProperty("candidate", message.getCandidate());
        root.addProperty("vote", message.getVote());
        root.addProperty("proof", Base64.getEncoder().encodeToString(message.getProof()));
        return root.toString();
    }


    @Override
    public ProtocolMessage decode(String data) {
        JsonObject root = gson.fromJson(data, JsonObject.class);

        final String pid = root.get("pid").getAsString();
        String[] subIds = pid.split("\\|");
        if (subIds.length == 1) {
            final int type = root.get("type").getAsInt();
            final int sender = root.get("sender").getAsInt();
            final int candidate = root.get("candidate").getAsInt();
            if (type == VoteMessage.VOTE) {
                final boolean vote = root.get("vote").getAsBoolean();
                final byte[] proof = Base64.getDecoder().decode(root.get("proof").getAsString().getBytes());
                return new VoteMessage(pid, sender, candidate, vote, proof);
            }

        } else {
            // Decode sub-protocol message
            if (subIds[1].startsWith("VCBC")) return this.vcbcMessageEncoder.decode(data);
            else if (subIds[1].startsWith("VBA")) return this.vbaMessageEncoder.decode(data);
        }

        return null;
    }
}
