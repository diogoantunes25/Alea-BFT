package pt.tecnico.ulisboa.hbbft.utils.threshsig;


import pt.tecnico.ulisboa.hbbft.utils.threshsig.bls.BLSSignature;
import pt.tecnico.ulisboa.hbbft.utils.threshsig.dummy.DummySignature;
import pt.tecnico.ulisboa.hbbft.utils.threshsig.shoup.ShoupSignature;

import java.util.Arrays;

public abstract class Signature {
    public static enum SignatureType {
        DUMMY(0), SHOUP(1), BLS(2);

        private final byte first;
        SignatureType(int first) { this.first = (byte) first; }

        public byte getFirst() {
            return first;
        }
    }

    protected abstract byte[] _toBytes();
    protected abstract byte getNum();

    public static Signature fromBytes(byte[] serialized) {
        byte[] actualPayload = Arrays.copyOfRange(serialized, 1, serialized.length);

        if (serialized[0] == SignatureType.DUMMY.first) {
            return DummySignature.fromBytes(actualPayload);
        } else if (serialized[0] == SignatureType.SHOUP.first) {
            return ShoupSignature.fromBytes(actualPayload);
        } else if (serialized[0] == SignatureType.BLS.first) {
            return BLSSignature.fromBytes(actualPayload);
        }

        return null;
    }

    public byte[] toBytes() {
        byte[] serialized = _toBytes();
        byte[] answer = new byte[serialized.length+1];
        answer[0] = getNum();
        System.arraycopy(serialized, 0, answer, 1, serialized.length);
        return answer;
    }

    @Override
    public String toString() {
        return Arrays.toString(toBytes());
    }
}
