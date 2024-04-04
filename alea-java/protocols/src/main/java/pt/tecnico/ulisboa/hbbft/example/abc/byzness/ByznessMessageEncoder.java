package pt.tecnico.ulisboa.hbbft.example.abc.byzness;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import pt.tecnico.ulisboa.hbbft.MessageEncoder;
import pt.tecnico.ulisboa.hbbft.ProtocolMessage;
import pt.tecnico.ulisboa.hbbft.binaryagreement.BinaryAgreementMessage;
import pt.tecnico.ulisboa.hbbft.vbroadcast.VBroadcastMessage;

public class ByznessMessageEncoder implements MessageEncoder<String> {

    private final MessageEncoder<String> vbcEncoder;
    private final MessageEncoder<String> baEncoder;

    private final Gson gson = new Gson();

    public ByznessMessageEncoder(
            MessageEncoder<String> vbcEncoder,
            MessageEncoder<String> baEncoder
    ) {
        this.vbcEncoder = vbcEncoder;
        this.baEncoder = baEncoder;
    }

    @Override
    public String encode(ProtocolMessage message) {
       if (message instanceof VBroadcastMessage) {
           return vbcEncoder.encode(message);
       } else if (message instanceof BinaryAgreementMessage) {
           return baEncoder.encode(message);
       } else {
           System.out.println("ERROR");
           return "null"; // TODO handle error
       }
    }

    @Override
    public ProtocolMessage decode(String data) {
        JsonObject root = gson.fromJson(data, JsonObject.class);
        final String pid = root.get("pid").getAsString();
        switch (pid.split("-")[0]) {
            case "vCBC": {
                return vbcEncoder.decode(data);
            }
            case "BA": {
                return baEncoder.decode(data);
            }
            case "BIZ": {
                // TODO
                /*final int type = root.get("type").getAsInt();
                final int senderId = root.get("sender").getAsInt();
                switch (type) {
                    case FillGapMessage.FILL_GAP: {
                        final
                    }
                    case FillerMessage.FILLER: {

                    }
                }*/
            }
            default: {
                return null; // TODO throw error
            }
        }
    }
}
