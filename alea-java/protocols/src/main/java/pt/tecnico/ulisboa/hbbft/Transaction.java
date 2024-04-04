package pt.tecnico.ulisboa.hbbft;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Wrapper on byte[]. byte[] can't be used in hashmap because hashmap first uses the hash and then equal to compare
 * arrays (which means that arrays will only match if they are the same object).
 */
public class Transaction {
    private byte[] contents;
    private static AtomicInteger count = new AtomicInteger(0);
    private final int id;

    public Transaction(byte[] contents) {
        this.contents = contents;
        this.id = count.getAndIncrement();
    }

    public byte[] content() {
        return contents;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof Transaction) {
            Transaction t = (Transaction) obj;
            byte[] otherContents = t.content();
            if (this.contents.length != otherContents.length) { return false; }

            for (int i = 0; i < this.contents.length; i++) {
                if (otherContents[i] != contents[i]) {
                    return false;
                }
            }

            return true;
        }
        return false;
    }

    public int getId() {
        return id;
    }

    public int hashCode() {
        return Arrays.hashCode(contents);
    }

    public String toString() {
        return contents.toString();
    }
}
