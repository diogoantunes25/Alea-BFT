package pt.tecnico.ulisboa.hbbft.utils.threshsig.bls;

import com.herumi.bls.PublicKey;
import pt.tecnico.ulisboa.hbbft.utils.threshsig.Signature;

public class BLSSignature extends Signature {
    private com.herumi.bls.Signature _sig;

    public BLSSignature(com.herumi.bls.Signature sig) {
        _sig = sig;
    }

    public boolean verify(PublicKey pk, byte[] payload) {
        return _sig.verify(pk, payload);
    }

    public static BLSSignature fromBytes(byte[] payload) {
        com.herumi.bls.Signature sig = new com.herumi.bls.Signature();
        sig.deserialize(payload);
        return new BLSSignature(sig);
    }

    public byte[] _toBytes() {

        return _sig.serialize();
    }

    @Override
    protected byte getNum() {
        return SignatureType.BLS.getFirst();
    }
}
