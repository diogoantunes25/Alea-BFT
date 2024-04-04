package pt.tecnico.ulisboa.hbbft;

import pt.tecnico.ulisboa.hbbft.utils.threshsig.ThreshSigUtils;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class NetworkInfo {

    // This node's ID.
    private final Integer ourId;

    // Whether this node is a validator.
    private final Boolean validator;

    // The indices in the list of sorted validator IDs.
    private final ValidatorSet validatorSet;

    private final ThreshSigUtils sigUtils;

    public NetworkInfo(Integer ourId, ValidatorSet validatorSet) {
        this(ourId, validatorSet, null);
    }

    public NetworkInfo(Integer ourId, ValidatorSet validatorSet, ThreshSigUtils sigUtils) {
        this.ourId = ourId;
        this.validator = validatorSet.contains(ourId);
        this.validatorSet = validatorSet;
        this.sigUtils = sigUtils;
    }

    // The ID of the node the algorithm runs on.
    public Integer getOurId() {
        return this.ourId;
    }

    // ID of all nodes in the network.
    public Collection<Integer> getAllIds() {
        return this.validatorSet.getAllIds();
    }

    // ID of all nodes in the network except this one.
    public Collection<Integer> getOtherIds() {
        return this.getAllIds().stream().filter(id -> !ourId.equals(id)).collect(Collectors.toSet());
    }

    // The total number _N_ of nodes.
    public Integer getN() {
        return this.validatorSet.getN();
    }

    // The maximum number _f_ of faulty, Byzantine nodes up to which Alea is guaranteed to be correct.
    public Integer getF() {
        return this.validatorSet.getF();
    }

    // The minimum number _N - f_ of correct nodes with which Alea is guaranteed to be correct.
    public Integer getNumCorrect() {
        return this.validatorSet.getC();
    }

    // Returns `true` if this node takes part in the consensus itself. If not, it is only an observer.
    public Boolean isValidator() {
        return this.validator;
    }

    // Returns `true` if the given node takes part in the consensus itself. If not, it is only an observer.
    public boolean isValidator(Integer id) {
        return this.validatorSet.contains(id);
    }

    // Returns the set of validator IDs.
    public ValidatorSet getValidatorSet() {
        return this.validatorSet;
    }

    public ThreshSigUtils getSigUtils() {
        return sigUtils;
    }

    public static class ValidatorSet {
        private final Set<Integer> indices;
        private final int numFaulty;

        public ValidatorSet(Set<Integer> indices, int numFaulty) {
            this.indices = indices;
            this.numFaulty = numFaulty;
        }

        // Returns `true` if the given ID belongs to a known validator.
        public Boolean contains(Integer id) {
            return this.indices.contains(id);
        }

        // The total number N of validators.
        public Integer getN() {
            return this.indices.size();
        }

        // The maximum number f of faulty, Byzantine validators up to which Alea is guaranteed to be correct.
        public Integer getF() {
            return this.numFaulty;
        }

        // The minimum number N - f of correct validators with which Alea is guaranteed to be correct.
        public Integer getC() {
            return this.getN() - this.getF();
        }

        // IDs of all validators in the network.
        public Collection<Integer> getAllIds() {
            return this.indices;
        }
    }
}
