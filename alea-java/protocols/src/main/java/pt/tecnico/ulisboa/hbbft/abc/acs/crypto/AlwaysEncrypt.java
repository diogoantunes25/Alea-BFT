package pt.tecnico.ulisboa.hbbft.abc.acs.crypto;

// Always encrypt. All contributions are encrypted in every epoch.
public class AlwaysEncrypt implements EncryptionSchedule {
    @Override
    public boolean encryptOnEpoch(long epoch) {
        return true;
    }
}
