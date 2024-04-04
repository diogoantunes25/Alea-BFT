package pt.tecnico.ulisboa.hbbft.subset;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Subset {

    private final Map<Integer, byte[]> entries = new TreeMap<>();

    public Subset() {}

    public Map<Integer, byte[]> getEntries() {
        return entries;
    }

    public void addEntry(Integer instance, byte[] value) {
        this.entries.putIfAbsent(instance, value);
    }

    @Override
    public String toString() {
        return "Subset{" +
                "entries=" + entries.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> new String(e.getValue()))) +
                '}';
    }
}
