package pt.tecnico.ulisboa.hbbft.abc.alea.queue;

import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.logging.Logger;

public class PriorityQueue {

    private final Integer id;

    private Long head;

    private final Map<Long, Slot> slots = new TreeMap<>();

    private Long count; // Number of elements in queue

    public PriorityQueue(Integer id) {
        this.id = id;
        this.head = 0L;
        this.count = 0L;
    }

    public Integer getId() {
        return id;
    }

    synchronized public Long getHead() {
        return this.head;
    }

    /**
     * Insert an element in the queue according to a given priority.
     *
     * @param priority the priority value.
     * @param element the element to insert in the queue.
     * @param proof a cryptographic proof binding the element to this queue slot.
     */
    synchronized public void enqueue(long priority, byte[] element, byte[] proof) {
        Slot slot = new Slot(priority, element, proof);
        this.slots.putIfAbsent(priority, slot);
        this.count++;
    }

    /**
     * Remove an element from the queue according to a given priority.
     *
     * @param priority the priority value of the element to remove.
     * @return the queue slot at the position mapped by the priority value.
     */
    synchronized public Optional<Slot> dequeue(long priority) {
        Slot slot = this.slots.get(priority);
        slot.setRemoved();
        while (this.peek().isPresent() && this.peek().get().isRemoved()) {
            this.head += 1;
            this.count--;
        }

        return Optional.empty();
    }

    synchronized public Optional<Slot> dequeue(byte[] element) {
        for (Map.Entry<Long, Slot> entry : slots.entrySet()) {
            if (Arrays.equals(element, entry.getValue().getValue())) {
                entry.getValue().setRemoved();

                if (entry.getKey().equals(this.head)) {
                    while (this.peek().isPresent() && this.peek().get().isRemoved())
                        this.head += 1;
                        this.count--;
                }
                return Optional.of(entry.getValue());
            }
        }
        return Optional.empty();
    }

    /**
     * Retrieve an element from the queue according to a given priority.
     *
     * @param priority the priority value of the element to retrieve.
     * @return the queue slot at the position mapped by the priority value.
     */
    synchronized public Optional<Slot> get(long priority) {
        return Optional.ofNullable(this.slots.get(priority));
    }

    /**
     * Retrieve the element at the head of the queue without removing it.
     *
     * @return the slot at the head of the queue.
     */
    synchronized public Optional<Slot> peek() {
        return this.get(head);
    }

    // Whether a value exists in the queue.
    synchronized public Boolean contains(byte[] input) {
        return this.slots.values().stream()
                .anyMatch(slot -> Arrays.equals(slot.getValue(), input));
    }

    @Override
    public String toString() {
        return "PriorityQueue{" +
                "id=" + id +
                ", head=" + head +
                ", slots=" + slots +
                ", count=" + count +
                '}';
    }

    public long count() { return this.count; }
}
