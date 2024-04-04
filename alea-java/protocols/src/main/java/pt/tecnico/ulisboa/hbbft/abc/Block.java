package pt.tecnico.ulisboa.hbbft.abc;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

public class Block {

    private final Long number;
    private final byte[] content;
    private final Collection<byte[]> entries;

    private Collection<Integer> proposers;

    public Block(Long number, byte[] content) {
        this.number = number;
        this.content = content;
        this.entries = new HashSet<>();
        this.proposers = new HashSet<>();
    }

    public Block(Long number, Collection<byte[]> entries) {
        this.number = number;
        this.content = new byte[0];
        this.entries = entries;
        this.proposers = new HashSet<>();
    }

    public Long getNumber() {
        return number;
    }

    public byte[] getContent() {
        return content;
    }

    public Collection<byte[]> getEntries() {
        return entries;
    }

    public void setProposers(Collection<Integer> proposers) {
        this.proposers = proposers;
    }

    public Collection<Integer> getProposers() {
        return proposers;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(String.format("Block #%d (", number));
        s.append(proposers).append(") = {");
        // s.append(entries.size());
        s.append(entries);
        s.append("}");
        return s.toString();
    }
}
