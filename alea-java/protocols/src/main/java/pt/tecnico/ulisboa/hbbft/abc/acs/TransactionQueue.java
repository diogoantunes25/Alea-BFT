package pt.tecnico.ulisboa.hbbft.abc.acs;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TransactionQueue {

    private final Integer batchSize;
    private final Integer proposalSize;

    private final Set<Transaction> elements = ConcurrentHashMap.newKeySet();

    public TransactionQueue(Integer batchSize, Integer proposalSize) {
        assert batchSize >= proposalSize;
        this.batchSize = batchSize;
        this.proposalSize = proposalSize;
    }

    public Integer getProposalSize() {
        return proposalSize;
    }

    public boolean add(byte[] element) {
        return this.elements.add(new Transaction(element));
    }

    public Collection<byte[]> get() {
        List<byte[]> candidates = this.elements.stream().limit(batchSize).map(Transaction::getValue).collect(Collectors.toList());
        Collections.shuffle(candidates);
        return candidates.stream().limit(proposalSize).collect(Collectors.toList());
    }

    public void remove(byte[] element) {
        this.elements.remove(new Transaction(element));
    }

    public void removeAll(Set<byte[]> elements) {
        elements.stream().map(Transaction::new).collect(Collectors.toList()).forEach(this.elements::remove);
    }

    public String toString() {
        return "TransactionQueue(" + elements + ")";
    }

    private static class Transaction {
        public final byte[] value;

        private Transaction(byte[] value) {
            this.value = value;
        }

        public byte[] getValue() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Transaction that = (Transaction) o;

            return Arrays.equals(value, that.value);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(value);
        }


    }
}
