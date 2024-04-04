package pt.tecnico.ulisboa.hbbft.abc.alea.queue;

import java.util.Arrays;

public class Slot {

    private final long id;

    private final byte[] value;
    private final byte[] proof;

    private boolean removed = false;

    public Slot(long id, byte[] value, byte[] proof) {
        this.id = id;
        this.value = value;
        this.proof = proof;
    }

    public long getId() {
        return id;
    }

    public byte[] getValue() {
        return value;
    }

    public byte[] getProof() {
        return proof;
    }

    synchronized public boolean isRemoved() {
        return removed;
    }

    synchronized public void setRemoved() {
        this.removed = true;
    }

    public String toString() {
        return "Slot(" + id + ": " + Arrays.toString(this.value) + ")";
    }
}
