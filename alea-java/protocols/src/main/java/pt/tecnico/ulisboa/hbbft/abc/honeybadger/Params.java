package pt.tecnico.ulisboa.hbbft.abc.honeybadger;

import pt.tecnico.ulisboa.hbbft.abc.honeybadger.crypto.EncryptionSchedule;

// Parameters controlling Honey Badger's behavior and performance.
public class Params {

    // The maximum number of future epochs for which we handle messages simultaneously.
    private final Long maxFutureEpochs;

    // Strategy used to handle the output of the `Subset` algorithm.
    //private final SubsetHandlingStrategy subsetHandlingStrategy;

    // Schedule for adding threshold encryption to some percentage of rounds
    private final EncryptionSchedule encryptionSchedule;

    private final Integer batchSize;

    public Params(
            Long maxFutureEpochs,
            //SubsetHandlingStrategy subsetHandlingStrategy,
            EncryptionSchedule encryptionSchedule,
            Integer batchSize
    ) {
        this.maxFutureEpochs = maxFutureEpochs;
        //this.subsetHandlingStrategy = subsetHandlingStrategy;
        this.encryptionSchedule = encryptionSchedule;
        this.batchSize = batchSize;
    }

    public Long getMaxFutureEpochs() {
        return maxFutureEpochs;
    }

    /*public SubsetHandlingStrategy getSubsetHandlingStrategy() {
        return subsetHandlingStrategy;
    }*/

    public EncryptionSchedule getEncryptionSchedule() {
        return encryptionSchedule;
    }

    public Integer getBatchSize() {
        return batchSize;
    }
}
