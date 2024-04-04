package pt.tecnico.ulisboa.hbbft.utils.threshsig.dummy;

import pt.tecnico.ulisboa.hbbft.utils.threshsig.SigShare;

import java.util.Random;

public class DummySigShare extends SigShare {

    @Override
    public byte[] _toBytes() {
        byte[] serialized = new byte[10];
        serialized[1] = 1;
        return serialized;
    }

    @Override
    protected byte getNum() {
        return SigShareType.DUMMY.getFirst();
    }

    public static DummySigShare fromBytes(byte[] payload) {
        return new DummySigShare();
    }
}