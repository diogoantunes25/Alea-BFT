package pt.tecnico.ulisboa.hbbft.binaryagreement.moustefaoui;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BoolSet {

    // The empty set of boolean values.
    //public static BoolSet NONE = new BoolSet(new HashSet<>());
    public static BoolSet NONE() {
        return new BoolSet(ConcurrentHashMap.newKeySet());
    }

    // The set containing only `false`.
    //public static BoolSet FALSE = new BoolSet(Collections.singleton(false));

    // The set containing only `true`.
    //public static BoolSet TRUE = new BoolSet(Collections.singleton(true));

    // The set of both boolean values, `false` and `true`.
    //public static BoolSet BOTH = new BoolSet(Stream.of(true, false).collect(Collectors.toCollection(HashSet::new)));
    public static BoolSet BOTH() {
        return new BoolSet(Stream.of(true, false).collect(Collectors.toCollection(HashSet::new)));
    }

    private Set<Boolean> values;

    public BoolSet(Set<Boolean> values) {
        this.values = values;
    }

    // Inserts a boolean value into the `BoolSet` and returns `true` iff the `BoolSet` has
    // changed as a result.
    public Boolean insert(Boolean b) {
        return this.values.add(b);
    }

    // Removes a value from the set.
    public void remove(Boolean b) {
        this.values.remove(b);
    }

    // Returns `true` if the set contains the value `b`.
    public Boolean contains(Boolean b) {
        return this.values.contains(b);
    }

    public Boolean isSubset(BoolSet other) {
        return other.getValues().containsAll(this.values);
    }

    public Set<Boolean> getValues() {
        return values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BoolSet boolSet = (BoolSet) o;

        return Objects.equals(values, boolSet.values);
    }

    @Override
    public int hashCode() {
        return values != null ? values.hashCode() : 0;
    }
}
