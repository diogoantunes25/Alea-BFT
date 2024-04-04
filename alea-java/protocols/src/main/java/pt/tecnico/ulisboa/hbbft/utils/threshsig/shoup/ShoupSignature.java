package pt.tecnico.ulisboa.hbbft.utils.threshsig.shoup;

import java.io.*;
import java.math.BigInteger;

import pt.tecnico.ulisboa.hbbft.utils.threshsig.Signature;

public class ShoupSignature extends Signature implements Serializable {
    private BigInteger _sig;
    
    public ShoupSignature(BigInteger sig) {
        _sig = sig;
    }

    public BigInteger getSignature() {
        return _sig;
    }

    public static ShoupSignature getNull() {
        return new ShoupSignature(null); 
    }

    public static ShoupSignature fromBytes(byte[] payload) {
        ByteArrayInputStream bis = new ByteArrayInputStream(payload);
        ObjectInput ois = null;
        ShoupSignature sigShare;
        try {
            ois = new ObjectInputStream(bis);
            sigShare = (ShoupSignature) ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (ois != null) ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sigShare;
    }

    @Override
    protected byte getNum() {
        return SignatureType.SHOUP.getFirst();
    }

    @Override
    public byte[] _toBytes() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] serialized;
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(this);
            oos.flush();
            serialized = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return serialized;
    }
}
