package pt.tecnico.ulisboa.hbbft.example.abc.alea;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import pt.tecnico.ulisboa.hbbft.MessageEncoder;
import pt.tecnico.ulisboa.hbbft.ProtocolMessage;
import pt.tecnico.ulisboa.hbbft.abc.alea.AleaMessage;
import pt.tecnico.ulisboa.hbbft.abc.alea.messages.FillGapMessage;
import pt.tecnico.ulisboa.hbbft.abc.alea.messages.FillerMessage;
import pt.tecnico.ulisboa.hbbft.binaryagreement.BinaryAgreementMessage;
import pt.tecnico.ulisboa.hbbft.vbroadcast.VBroadcastMessage;

import java.util.Base64;

public class AleaMessageEncoder implements MessageEncoder<String> {

    // sub protocol message encoders
    private final MessageEncoder<String> vcbcEncoder;
    private final MessageEncoder<String> baEncoder;

    private final Gson gson = new Gson();

    public AleaMessageEncoder(
            MessageEncoder<String> vcbcEncoder,
            MessageEncoder<String> baEncoder
    ) {
        this.vcbcEncoder = vcbcEncoder;
        this.baEncoder = baEncoder;
    }

    @Override
    public String encode(ProtocolMessage message) {
        // route message to the corresponding encoder
        if (message instanceof VBroadcastMessage) {
            return vcbcEncoder.encode(message);

        } else if (message instanceof BinaryAgreementMessage) {
            return baEncoder.encode(message);

        } else if (message instanceof AleaMessage) {
            switch (message.getType()) {
                case FillGapMessage.FILL_GAP: {
                    return this.encodeFillerMessage((FillerMessage) message);
                }

                case FillerMessage.FILLER: {
                    return this.encodeFillGapMessage((FillGapMessage) message);
                }
            }
        }
        System.out.println("ERROR");
        return null; // TODO handle error
    }

    private JsonObject encodeAleaMessage(AleaMessage message) {
        JsonObject root = new JsonObject();
        root.addProperty("pid", message.getPid());
        root.addProperty("type", message.getType());
        root.addProperty("sender", message.getSender());
        return root;
    }

    private String encodeFillGapMessage(FillGapMessage message) {
        JsonObject root = encodeAleaMessage(message);
        root.addProperty("queue", message.getQueue());
        root.addProperty("slot", message.getSlot());
        return root.toString();
    }

    private String encodeFillerMessage(FillerMessage message) {
        JsonObject root = encodeAleaMessage(message);
        root.addProperty("queue", message.getQueue());
        root.addProperty("slot", message.getSlot());
        root.addProperty("value", Base64.getEncoder().encodeToString(message.getValue()));
        root.addProperty("proof", Base64.getEncoder().encodeToString(message.getProof()));
        return root.toString();
    }

    @Override
    public ProtocolMessage decode(String data) {
        JsonObject root = gson.fromJson(data, JsonObject.class);
        final String pid = root.get("pid").getAsString();

        // route message to the corresponding decoder
        switch (pid.split("-")[0]) {
            case "VCBC": {
                return vcbcEncoder.decode(data);
            }

            case "BA": {
                return baEncoder.decode(data);
            }

            case "1": {
                final int type = root.get("type").getAsInt();
                switch (type) {
                    case FillGapMessage.FILL_GAP: {
                        return this.decodeFillGapMessage(data);
                    }
                    case FillerMessage.FILLER: {
                        return this.decodeFillerMessage(data);
                    }
                }
            }
        }

        // TODO throw error
        return null;
    }

    private FillGapMessage decodeFillGapMessage(String data) {
        JsonObject root = gson.fromJson(data, JsonObject.class);

        String pid = root.get("pid").getAsString();
        int senderId = root.get("sender").getAsInt();

        int queue = root.get("queue").getAsInt();
        long slot = root.get("slot").getAsLong();

        return new FillGapMessage(pid, senderId, queue, slot);
    }

    private FillerMessage decodeFillerMessage(String data) {
        JsonObject root = gson.fromJson(data, JsonObject.class);

        String pid = root.get("pid").getAsString();
        int senderId = root.get("sender").getAsInt();

        int queue = root.get("queue").getAsInt();
        long slot = root.get("slot").getAsLong();
        byte[] value = Base64.getDecoder().decode(root.get("value").getAsString());
        byte[] proof = Base64.getDecoder().decode(root.get("proof").getAsString());

        return new FillerMessage(pid, senderId, queue, slot, value, proof);
    }
}
