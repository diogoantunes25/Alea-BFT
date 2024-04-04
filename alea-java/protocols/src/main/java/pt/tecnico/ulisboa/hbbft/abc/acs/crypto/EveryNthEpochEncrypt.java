package pt.tecnico.ulisboa.hbbft.abc.acs.crypto;

// Every _n_-th epoch uses encryption. In all other epochs, contributions are plaintext.
public class EveryNthEpochEncrypt implements EncryptionSchedule {

    private final long n;

    public EveryNthEpochEncrypt(long n) {
        this.n = n;
    }

    @Override
    public boolean encryptOnEpoch(long epoch) {
        return (epoch % n) == 0;
    }
}
