package pt.tecnico.ulisboa.hbbft.abc.alea;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class VanillaPendingContainer<T> implements PendingContainer<T> {
    private Queue<T> q;

    public VanillaPendingContainer() {
        q = new LinkedBlockingQueue<>();
    }

    public synchronized void put(T t) {
        q.add(t);
    }

    public synchronized T get() {
        if (isEmpty()) return null;
        return q.remove();
    }

    public synchronized boolean isEmpty() {
        return q.isEmpty();
    }

    public synchronized long size() {
        return q.size();
    }
}
