package pt.tecnico.ulisboa.hbbft.agreement.vba;

public class ValidatedBoolean {

    private final Boolean value;
    private final byte[] proof;

    public ValidatedBoolean(Boolean value, byte[] proof) {
        this.value = value;
        this.proof = proof;
    }

    public Boolean getValue() {
        return value;
    }

    public byte[] getProof() {
        return proof;
    }
}
