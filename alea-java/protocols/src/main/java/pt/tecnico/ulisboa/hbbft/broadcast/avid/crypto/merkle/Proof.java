package pt.tecnico.ulisboa.hbbft.broadcast.avid.crypto.merkle;

import java.util.List;

public class Proof {

    private final byte[] value;
    private final Integer index;
    private final List<byte[]> digests;
    private final byte[] rootHash;

    public Proof(byte[] value, Integer index, List<byte[]> digests, byte[] rootHash) {
        this.value = value;
        this.index = index;
        this.digests = digests;
        this.rootHash = rootHash;
    }

    public byte[] getValue() {
        return value;
    }

    public Integer getIndex() {
        return index;
    }

    public List<byte[]> getDigests() {
        return digests;
    }

    public byte[] getRootHash() {
        return rootHash;
    }

    public Boolean validate(Integer n) {
        //Integer levelI = this.index;
        //Integer levelN = n;
        //while (levelN > 1) {
        //    if ((levelI ^ 1) < levelN) {
        //
        //    }
        //}
        return true;
    }
}
