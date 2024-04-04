package pt.tecnico.ulisboa.hbbft.abc.alea.state;

import pt.tecnico.ulisboa.hbbft.Step;
import pt.tecnico.ulisboa.hbbft.abc.Block;
import pt.tecnico.ulisboa.hbbft.abc.alea.Alea;
import pt.tecnico.ulisboa.hbbft.binaryagreement.IBinaryAgreement;

/**
 * Binary agreement ongoing state
 */
public class OngoingState extends AgreementState {

    public OngoingState(Alea alea, Long epoch) {
        super(alea, epoch);
    }

    @Override
    public Step<Block> tryProgress() {
        Step<Block> step = new Step<>();

        if (!this.canProgress()) {
            return step;
        }

        IBinaryAgreement instance = this.getBaInstance();
        Boolean decision = instance.deliver().orElseThrow();


        AgreementState nextState;

        if (decision) {
            nextState = new WaitingState(alea, epoch);
        } else {
            nextState = new ProposingState(alea, epoch + 1);
        }

        step.add(this.alea.setAgreementState(nextState));

        return step;
    }

    @Override
    public boolean canProgress() {
        return this.getBaInstance().hasTerminated();
    }

    public String toString() {
        return "OnGoingState";
    }
}
