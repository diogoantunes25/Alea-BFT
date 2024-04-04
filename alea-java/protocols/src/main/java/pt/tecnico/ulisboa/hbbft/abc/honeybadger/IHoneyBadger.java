package pt.tecnico.ulisboa.hbbft.abc.honeybadger;

import pt.tecnico.ulisboa.hbbft.Step;
import pt.tecnico.ulisboa.hbbft.abc.Block;
import pt.tecnico.ulisboa.hbbft.abc.IAtomicBroadcast;

public interface IHoneyBadger extends IAtomicBroadcast {

    /**
     * Returns epoch of HoneyBadger protocol given its id
     * @param epochId
     * @return epoch
     */
    Epoch getEpoch(Long epochId);

    /**
     * Handles the input of a batch of entries (the step of another algorithm).
     * @param epochStep
     * @return step resulting of handling the previous step (that retrived the batch)
     */
    Step<Block> handleEpochStep(Step<Batch> epochStep);
}
