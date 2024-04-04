package pt.tecnico.ulisboa.hbbft.abc.honeybadger;

import pt.tecnico.ulisboa.hbbft.*;
import pt.tecnico.ulisboa.hbbft.abc.honeybadger.messages.DecryptionMessage;
import pt.tecnico.ulisboa.hbbft.subset.hbbft.HoneyBadgerSubset;
import pt.tecnico.ulisboa.hbbft.subset.Subset;
import pt.tecnico.ulisboa.hbbft.subset.SubsetMessage;

import java.util.*;
import java.util.stream.Collectors;

// The sub-algorithms and their intermediate results for a single epoch.
public class Epoch implements IProtocol<byte[], Batch, ProtocolMessage> {

    // Our epoch number.
    private final Long epochId;

    // Our ID.
    private final Integer replicaId;

    // Shared network data.
    private final NetworkInfo networkInfo;

    // The the subset algorithm.
    private final HoneyBadgerSubset subset;

    // The status of threshold decryption, by proposer.
    private final Map<Integer, byte[]> decryptionShares = new HashMap<>();

    // Whether contributions should be encrypted in this epoch.
    private final Boolean requireEncryption;

    // The value to output when finished.
    private Batch decidedValue;

    // Creates a new `Epoch` instance.
    public Epoch(
            Long epochId,
            Integer replicaId,
            NetworkInfo networkInfo,
            HoneyBadgerSubset subset,
            Boolean requireEncryption
    ) {
        this.epochId = epochId;
        this.replicaId = replicaId;
        this.networkInfo = networkInfo;
        this.subset = subset;
        this.requireEncryption = requireEncryption;
    }

    @Override
    public Step<Batch> handleInput(byte[] proposal) {
        if (this.requireEncryption) {
            // TODO encrypt proposal
        }

        Step<Subset> subsetStep = this.subset.handleInput(proposal);
        return this.handleSubsetStep(subsetStep);
    }

    public Step<Batch> handleMessage(ProtocolMessage message) {

        // TODO handle decryption shares
        if (message instanceof SubsetMessage) {
            Step<Subset> acsStep = this.subset.handleMessage((SubsetMessage) message);
            return this.handleSubsetStep(acsStep);

        } else if (message instanceof DecryptionMessage) {
            return this.handleDecryptionMessage((DecryptionMessage) message);
        }

        return new Step<>();
    }

    @Override
    public boolean hasTerminated() {
        return this.decidedValue != null;
    }

    @Override
    public Optional<Batch> deliver() {
        return Optional.ofNullable(this.decidedValue);
    }

    public Long getEpochId() {
        return epochId;
    }

    private Step<Batch> handleDecryptionMessage(DecryptionMessage message) {
        this.decryptionShares.put(message.getSender(), message.getShare());
        return this.tryOutput();
    }

    public Step<Batch> handleSubsetStep(Step<Subset> subsetStep) {
        Step<Batch> step = this.convertStep(subsetStep);
        if (subsetStep.getOutput().isEmpty()) return step;

        // send decryption share
        if (this.requireEncryption) {
            Random rd = new Random();
            byte[] decryptionShare = new byte[250];
            rd.nextBytes(decryptionShare);
            DecryptionMessage decryptionMessage = new DecryptionMessage(String.format("HB-%d", epochId), this.replicaId, decryptionShare);
            step.add(this.handleMessage(decryptionMessage));
            step.add(decryptionMessage, this.networkInfo.getValidatorSet().getAllIds().stream()
                    .filter(id -> !id.equals(this.replicaId)).collect(Collectors.toList()));
        }

        step.add(this.tryOutput());
        return step;
    }

    private Step<Batch> tryOutput() {
        Step<Batch> step = new Step<>();

        if (!this.subset.hasTerminated()) return step;

        final int quorum = networkInfo.getF() + 1;
        if (!this.requireEncryption || this.decryptionShares.size() < quorum) return step;

        // recompute batch from shares and output
        Batch batch = new Batch(this.epochId, this.subset.deliver().orElseThrow().getEntries());
        this.decidedValue = batch;
        step.add(batch);

        return step;
    }

    private Step<Batch> convertStep(Step<?> step) {
        Vector<TargetedMessage> messages = new Vector<>();
        for (TargetedMessage tm : step.getMessages()) {
            ProtocolMessage content = tm.getContent();
            HoneyBadgerMessage message = new HoneyBadgerMessage("HB-" + epochId, 0, content.getSender(), epochId, content);
            messages.add(new TargetedMessage(message, tm.getTarget()));
        }
        return new Step<>(messages);
    }
}
