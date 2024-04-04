package pt.tecnico.ulisboa.hbbft.abc.alea;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Thread-safe container for pending transactions. Constant time insertion
 * and removal. Removal is pseudo random.
 */
public class ShuffledPendingContainer<T> implements PendingContainer<T> {

    private Queue<T>[] queues;
    private int QUEUES_COUNT = 50;
    private long count = 0;

    public ShuffledPendingContainer() {
        queues = new Queue[QUEUES_COUNT];
        for (int i = 0; i < QUEUES_COUNT; i++) {
            queues[i] = new LinkedBlockingQueue<>();
        }
    }

    public synchronized void put(T t) {
        queues[(int) count % QUEUES_COUNT].add(t);
        count++;
    }

    public synchronized T get() {
        if (isEmpty()) return null;
        else {
            T ans = null;
            int startingId = (new Random()).nextInt(QUEUES_COUNT);
            for (int i = startingId; i != startingId + QUEUES_COUNT && ans == null; i++) {
                int qid = i % QUEUES_COUNT;
                ans = queues[qid].poll();
            }

            count--;
            return ans;
        }
    }

    public synchronized boolean isEmpty() {
        return count == 0;
    }

    public synchronized long size() {
        return count;
    }
}