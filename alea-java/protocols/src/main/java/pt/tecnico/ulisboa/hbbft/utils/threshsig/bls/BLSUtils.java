package pt.tecnico.ulisboa.hbbft.utils.threshsig.bls;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.herumi.bls.Bls;
import com.herumi.bls.PublicKey;
import com.herumi.bls.SecretKey;
import com.herumi.bls.SecretKeyVec;
import com.herumi.bls.SignatureVec;
import pt.tecnico.ulisboa.hbbft.utils.threshsig.SigShare;
import pt.tecnico.ulisboa.hbbft.utils.threshsig.Signature;
import pt.tecnico.ulisboa.hbbft.utils.threshsig.ThreshSigUtils;

public class BLSUtils extends ThreshSigUtils {

    int _id;
    SecretKey _sid;
    SecretKey _sec;
    List<PublicKey> _pks;
    PublicKey _mpk;

    public BLSUtils(int n, int id, SecretKey sid, SecretKey sec, List<PublicKey> pks, PublicKey mpk) {
        super(n);
        _id = id;
        _sid = sid;
        _sec = sec;
        _pks = pks;
        _mpk = mpk;
    }
    
    @Override
    public SigShare sign(byte[] payload) {
        BLSSigShare sigShare = new BLSSigShare(_sec.sign(payload), _sid);
        return sigShare;
    }

    @Override
    public Signature combine(byte[] value, Collection<SigShare> shares) {
        SignatureVec sigs = new SignatureVec(0, new com.herumi.bls.Signature());
        SecretKeyVec ids = new SecretKeyVec(0, new SecretKey());

        for (SigShare share: shares) {
            sigs.add(((BLSSigShare) share).getSignature());
            ids.add(((BLSSigShare) share).getId());
        }

        return new BLSSignature(Bls.recover(sigs, ids));
    }

    @Override
    public boolean verifyFull(byte[] payload, Signature s) {
        BLSSignature sig = (BLSSignature) s;
        return sig.verify(_mpk, payload);
    }

    /**
     * Verifies if share is the signature of payload by pk[i]
     */
    @Override
    public boolean verifyShare(byte[] payload, SigShare s, int id) {
        BLSSigShare share = (BLSSigShare) s;
        PublicKey pk = _pks.get(id);
        return share.getSignature().verify(pk, payload);
    }
    
}
