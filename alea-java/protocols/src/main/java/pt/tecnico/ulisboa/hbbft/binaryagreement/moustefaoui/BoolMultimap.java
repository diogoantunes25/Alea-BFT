package pt.tecnico.ulisboa.hbbft.binaryagreement.moustefaoui;

import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class BoolMultimap {

    private Vector<Set<Integer>> values = new Vector<>(2);

    public BoolMultimap() {
        this.values.add(0, ConcurrentHashMap.newKeySet());
        this.values.add(1, ConcurrentHashMap.newKeySet());
    }

    /**
     * Get set for boolean b
     */
    public Set<Integer> getIndex(Boolean b) {
        return this.values.elementAt(b ? 1 : 0);
    }

    public Vector<Set<Integer>> getValues() {
        return this.values;
    }
}
