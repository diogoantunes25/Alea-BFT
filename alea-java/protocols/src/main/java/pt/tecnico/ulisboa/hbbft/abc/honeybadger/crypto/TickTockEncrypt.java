package pt.tecnico.ulisboa.hbbft.abc.honeybadger.crypto;

// With `TickTock(n, m)`, `n` epochs use encryption, followed by `m` epochs that don't.
// `m` out of `n + m` epochs will use plaintext contributions.
public class TickTockEncrypt implements EncryptionSchedule {

    private final long on;
    private final long off;

    public TickTockEncrypt(long on, long off) {
        this.on = on;
        this.off = off;
    }

    @Override
    public boolean encryptOnEpoch(long epoch) {
        return epoch % (on + off) <= on;
    }
}
