package pt.tecnico.ulisboa.hbbft.abc.acs.crypto;

// How frequently Threshold Encryption should be used.
public interface EncryptionSchedule {
    // Returns `true` if the contributions in the `epoch` should be encrypted.
    boolean encryptOnEpoch(long epoch);
}
