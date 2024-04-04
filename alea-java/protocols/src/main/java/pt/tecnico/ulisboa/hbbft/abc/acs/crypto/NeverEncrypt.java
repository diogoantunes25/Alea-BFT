package pt.tecnico.ulisboa.hbbft.abc.acs.crypto;

// Never encrypt. All contributions are plaintext in every epoch.
public class NeverEncrypt implements EncryptionSchedule {
    @Override
    public boolean encryptOnEpoch(long epoch) {
        return false;
    }
}
