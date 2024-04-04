package pt.tecnico.ulisboa.hbbft.abc.alea.state;

import pt.tecnico.ulisboa.hbbft.Step;
import pt.tecnico.ulisboa.hbbft.abc.Block;
import pt.tecnico.ulisboa.hbbft.abc.alea.Alea;
import pt.tecnico.ulisboa.hbbft.abc.alea.queue.PriorityQueue;
import pt.tecnico.ulisboa.hbbft.abc.alea.queue.Slot;
import pt.tecnico.ulisboa.hbbft.binaryagreement.IBinaryAgreement;
import pt.ulisboa.tecnico.utils.Utils;

import java.util.Optional;

/**
 * Ready to propose state
 */
public class ProposingState extends AgreementState {

    public ProposingState(Alea alea, Long epoch) {
        super(alea, epoch);
    }

    @Override
    public Step<Block> tryProgress() {
        Step<Block> step = new Step<>();

        if (!this.canProgress()) {
            return step;
        }

        PriorityQueue queue = this.getQueue();
        Optional<Slot> slot = queue.peek();

        IBinaryAgreement instance = this.getBaInstance();

        if (Utils.DELAY_BC) {
            if (alea.broadcastOngoing(queue.getId(), queue.getHead()) && !slot.isPresent()) {
                return step;
            }
        }

        Utils.baAllocate(getBaInstance().getPid(), String.format("p-%s-%s", queue.getId(), queue.getHead()));
        Step<Boolean> baStep = instance.handleInput(slot.isPresent());
        step.add(baStep.getMessages());

        AgreementState nextState = new OngoingState(alea, epoch);

        step.add(this.alea.setAgreementState(nextState));

        return step;
    }

    @Override
    public boolean canProgress() {
        return this.alea.getQueues().values().stream()
                .anyMatch(queue -> queue.peek().isPresent());
    }

    public String toString() {
        return "ProposingState";
    }
}
