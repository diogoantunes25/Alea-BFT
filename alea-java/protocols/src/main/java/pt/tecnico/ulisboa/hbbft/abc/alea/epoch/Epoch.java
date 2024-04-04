package pt.tecnico.ulisboa.hbbft.abc.alea.epoch;

import pt.tecnico.ulisboa.hbbft.Step;
import pt.tecnico.ulisboa.hbbft.abc.Block;

public class Epoch {

    private final Long number;
    private EpochState epochState;

    public Epoch(Long number) {
        this.number = number;
    }

    public void setEpochState(EpochState epochState) {
        this.epochState = epochState;
    }

    public Step<Block> tryProgress() {
        return new Step<>();
        //return this.epochState.tryProgress();
    }
}
