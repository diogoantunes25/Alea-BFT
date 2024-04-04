package pt.tecnico.ulisboa.hbbft.utils.threshsig.bls;

import com.herumi.bls.*;

import pt.tecnico.ulisboa.hbbft.utils.threshsig.AbstractThreshSigFactory;
import pt.tecnico.ulisboa.hbbft.utils.threshsig.ThreshSigUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BLSSigFactory implements AbstractThreshSigFactory {

    static {
        System.loadLibrary("blsjava");
        Bls.init(Bls.BN254);
    }

    private int _n;
    private int _f;
    PublicKeyVec _pks = new PublicKeyVec();
    SecretKeyVec _sks = new SecretKeyVec();
    SecretKeyVec _ids = new SecretKeyVec();
    PublicKey _mpk;

    public BLSSigFactory() {}

    public BLSSigFactory(int n, int f, List<PublicKey> pks, List<SecretKey> sks, List<SecretKey> ids, PublicKey mpk) {
        _n = n;
        _f = f;

        _ids.reserve(n);
        _sks.reserve(n);
        _pks.reserve(n);
        _mpk = mpk;

        for (int i = 0; i < n; i++) {
            _ids.add(ids.get(i));
            _sks.add(sks.get(i));
            _pks.add(pks.get(i));
        }
    }

    @Override
    public void init(int n) {

        _n = n;
        _f = (int) Math.floorDiv(n-1, 3);

        SecretKeyVec msk = new SecretKeyVec();
        PublicKeyVec mpk = new PublicKeyVec();

		for (int i = 0; i < 2*_f+1; i++) {
            SecretKey sec = new SecretKey();
			sec.setByCSPRNG();
			msk.add(sec);
			PublicKey pub = sec.getPublicKey();
			mpk.add(pub);
		}

        _mpk = mpk.get(0);
        _ids.reserve(n);
        _sks.reserve(n);
		_pks.reserve(n);

        for (int i = 0; i < n; i++) {
			SecretKey id = new SecretKey();
			id.setByCSPRNG();
			_ids.add(id);
            _sks.add(Bls.share(msk, _ids.get(i)));
            _pks.add(Bls.share(mpk, _ids.get(i)));
		}
    }


    @Override
    public int getN() {
        return _n;
    }

    @Override
    public ThreshSigUtils get(int id) {
        return new BLSUtils(_n, id, _ids.get(id), _sks.get(id), _pks, _mpk);
    }


    public byte[] serialize() {
        SerializableFactory toSerialize = getSerializable();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] serialized;
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(toSerialize);
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

    public SerializableFactory getSerializable() {
        List<byte[]> pks = new ArrayList<>();
        List<byte[]> sks = new ArrayList<>();
        List<byte[]> ids = new ArrayList<>();

        for (int i = 0; i < _n; i++) {
            pks.add(_pks.get(i).serialize());
            sks.add(_sks.get(i).serialize());
            ids.add(_ids.get(i).serialize());
        }

        return new SerializableFactory(_n, _f, pks, sks, ids, _mpk.serialize());
    }

    public static BLSSigFactory deserialize(byte[] buffer) {
        ByteArrayInputStream bis = new ByteArrayInputStream(buffer);
        ObjectInput ois = null;
        SerializableFactory factory;
        try {
            ois = new ObjectInputStream(bis);
            factory = (SerializableFactory) ois.readObject();
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

        return factory.toFactory();
    }

    public static class SerializableFactory implements Serializable {
        int _n;
        int _f;
        private List<byte[]> _pks;
        private List<byte[]> _sks;
        private List<byte[]> _ids;
        private byte[] _mpk;

        public SerializableFactory(int n, int f, List<byte[]> pks, List<byte[]> sks, List<byte[]> ids, byte[] mpk) {
            _n = n;
            _f = f;
            _pks = pks;
            _sks = sks;
            _ids = ids;
            _mpk = mpk;
        }

        public int getN() {
            return _n;
        }

        public int getF() {
            return _f;
        }

        public List<byte[]> getPks() {
            return _pks;
        }

        public List<byte[]> getSks() {
            return _sks;
        }

        public List<byte[]> getIds() {
            return _ids;
        }

        public byte[] getMpk() {
            return _mpk;
        }

        public BLSSigFactory toFactory() {
            List<SecretKey> sks = _sks.stream().map(s -> {
                SecretKey sk = new SecretKey();
                sk.deserialize(s);
                return sk;
            }).collect(Collectors.toList());

            List<PublicKey> pks = _pks.stream().map(p -> {
                PublicKey pk = new PublicKey();
                pk.deserialize(p);
                return pk;
            }).collect(Collectors.toList());

            List<SecretKey> ids = _ids.stream().map(i -> {
                SecretKey sk = new SecretKey();
                sk.deserialize(i);
                return sk;
            }).collect(Collectors.toList());

            PublicKey mpk = new PublicKey();
            mpk.deserialize(_mpk);

            return new BLSSigFactory(_n, _f, pks, sks, ids, mpk);
        }
    }
}
