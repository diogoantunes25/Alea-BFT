package pt.tecnico.ulisboa.hbbft.abc.dumbo;

import pt.tecnico.ulisboa.hbbft.abc.honeybadger.crypto.EncryptionSchedule;

// Parameters controlling Dumbo's behavior and performance.
public class Params {

    // The maximum number of future epochs for which we handle messages simultaneously.
    private final Long maxFutureEpochs;

    // Schedule for adding threshold encryption to some percentage of rounds
    private final EncryptionSchedule encryptionSchedule;

    private final Integer batchSize;

    private final Integer k;

    public Params(
            Long maxFutureEpochs,
            EncryptionSchedule encryptionSchedule,
            Integer batchSize,
            Integer k
    ) {
        this.maxFutureEpochs = maxFutureEpochs;
        this.encryptionSchedule = encryptionSchedule;
        this.batchSize = batchSize;
        this.k = k;
    }

    public Long getMaxFutureEpochs() {
        return maxFutureEpochs;
    }

    public EncryptionSchedule getEncryptionSchedule() {
        return encryptionSchedule;
    }

    public Integer getBatchSize() {
        return batchSize;
    }

    public Integer getK() {
        return k;
    }
}
