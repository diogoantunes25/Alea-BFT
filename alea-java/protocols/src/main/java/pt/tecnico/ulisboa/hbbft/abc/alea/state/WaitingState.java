package pt.tecnico.ulisboa.hbbft.abc.alea.state;

import pt.tecnico.ulisboa.hbbft.ProtocolMessage;
import pt.tecnico.ulisboa.hbbft.Step;
import pt.tecnico.ulisboa.hbbft.abc.Block;
import pt.tecnico.ulisboa.hbbft.abc.alea.Alea;
import pt.tecnico.ulisboa.hbbft.abc.alea.messages.FillGapMessage;
import pt.tecnico.ulisboa.hbbft.abc.alea.queue.PriorityQueue;
import pt.tecnico.ulisboa.hbbft.abc.alea.queue.Slot;
import pt.ulisboa.tecnico.utils.Utils;

import java.util.Collection;
import java.util.Collections;

/**
 * Binary agreement ended with True (node needs to get the slot)
 */
public class WaitingState extends AgreementState {

    public WaitingState(Alea alea, Long epoch) {
        super(alea, epoch);
    }

    @Override
    public Step<Block> tryProgress() {
        Step<Block> step = new Step<>();

        // Wait until I know of the value that was agreed upon
        if (this.canProgress()) {

            Slot slot = this.getQueue().peek().orElseThrow();
            Collection<byte[]> blockContents;
            blockContents = Alea.decodeBatchEntries(slot.getValue());

            // Assemble block
            Block block = new Block(epoch, blockContents);
            block.setProposers(Collections.singleton(this.getQueue().getId()));
            step.add(block);

            // Remove decided value from all queues
//            if (blockContents.size() > 1) {
//                // FIXME: if batch has more than 1 tx i should remove all from queues also
//                this.getQueue().dequeue(slot.getId());
//            } else {
//                for (PriorityQueue q : this.alea.getQueues().values()) {
//                    q.dequeue(slot.getValue());
//                }
//            }

            synchronized (this.alea.getQueues()) {
                this.alea.addExecuted(slot.getValue());
                for (PriorityQueue q: this.alea.getQueues().values()) {
                    q.dequeue(slot.getValue());
                }
            }

            Utils.baEnd(getBaInstance().getPid(), getBaInstance().deliver().orElseThrow());
            AgreementState nextState = new ProposingState(alea, epoch+1);
            step.add(this.alea.setAgreementState(nextState));
        } else {
            ProtocolMessage fillMessage = new FillGapMessage("ALEA", alea.replicaId, this.getQueue().getId(), this.getQueue().getHead());
            for (int id=0; id < this.alea.getNetworkInfo().getN(); id++) {
                step.add(fillMessage, id);
            }
        }

        return step;
    }

    @Override
    public boolean canProgress() {
        return this.getQueue().peek().isPresent();
    }

    public String toString() {
        return "WaitingState";
    }

}
