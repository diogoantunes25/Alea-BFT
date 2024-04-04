package pt.tecnico.ulisboa.hbbft.utils.threshsig.dummy;

import pt.tecnico.ulisboa.hbbft.utils.threshsig.Signature;

public class DummySignature extends Signature {
    @Override
    public byte[] _toBytes() {
        byte[] serialized = new byte[10];
        serialized[1] = 1;
        return serialized;
    }

    @Override
    protected byte getNum() {
        return SignatureType.DUMMY.getFirst();
    }

    public static DummySignature fromBytes(byte[] payload) {
        return new DummySignature();
    }
}
