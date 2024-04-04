package pt.tecnico.ulisboa.hbbft.vbroadcast;

public class VOutput {

    private final byte[] value;
    private final byte[] signature;

    public VOutput(byte[] value, byte[] signature) {
        this.value = value;
        this.signature = signature;
    }

    public byte[] getValue() {
        return value;
    }

    public byte[] getSignature() {
        return signature;
    }
}
