package pt.tecnico.ulisboa.hbbft.abc.alea.messages;

import pt.tecnico.ulisboa.hbbft.abc.alea.AleaMessage;

public class FillGapMessage extends AleaMessage {

    public static final int FILL_GAP = 431;

    private final Integer queue;
    private final Long slot;

    public FillGapMessage(String pid, Integer sender, Integer queue, Long slot) {
        super(pid, FILL_GAP, sender);
        this.queue = queue;
        this.slot = slot;
    }

    public Integer getQueue() {
        return queue;
    }

    public Long getSlot() {
        return slot;
    }
}
