package pt.tecnico.ulisboa.hbbft.abc.alea;

/**
 * Thread-safe container for pending transactions. Constant time insertion
 * and removal. Removal is pseudo random.
 */
public interface PendingContainer<T> {
    public void put(T t);
    public T get();
    public boolean isEmpty();
    public long size();
}
