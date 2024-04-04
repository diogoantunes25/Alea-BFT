package pt.tecnico.ulisboa.hbbft.utils.threshsig.bls;

import com.herumi.bls.SecretKey;
import com.herumi.bls.Signature;
import pt.tecnico.ulisboa.hbbft.utils.threshsig.SigShare;

import java.io.*;

public class BLSSigShare extends SigShare {
    Signature _signature;
    SecretKey _id;
    public BLSSigShare(Signature signature, SecretKey id) {
        _signature = signature;
        _id = id;
    }    

    public Signature getSignature() {
        return _signature;
    }

    public SecretKey getId() {
        return _id;
    }

    public byte[] _toBytes() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] serialized;
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(new BLSSigShareSerializable(_signature.serialize(), _id.serialize()));
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

    public static BLSSigShare fromBytes(byte[] payload) {
        ByteArrayInputStream bis = new ByteArrayInputStream(payload);
        ObjectInput ois = null;
        BLSSigShare sigShare;
        try {
            ois = new ObjectInputStream(bis);
            BLSSigShareSerializable _tmp = (BLSSigShareSerializable) ois.readObject();
            com.herumi.bls.Signature sig = new Signature();
            sig.deserialize(_tmp.getSig());
            SecretKey id = new SecretKey();
            id.deserialize(_tmp.getId());
            sigShare = new BLSSigShare(sig, id);
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

    protected byte getNum() { return SigShareType.BLS.getFirst(); }

    private static class BLSSigShareSerializable implements Serializable {
        private byte[] _sig;
        private byte[] _id;
        public BLSSigShareSerializable(byte[] sig, byte[] id) {
            _sig = sig;
            _id = id;
        }

        public byte[] getSig() {
            return _sig;
        }

        public byte[] getId() {
            return _id;
        }
    }
}
