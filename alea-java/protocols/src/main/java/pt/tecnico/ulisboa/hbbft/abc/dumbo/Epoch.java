package pt.tecnico.ulisboa.hbbft.abc.dumbo;

import pt.tecnico.ulisboa.hbbft.IProtocol;
import pt.tecnico.ulisboa.hbbft.ProtocolMessage;
import pt.tecnico.ulisboa.hbbft.Step;
import pt.tecnico.ulisboa.hbbft.TargetedMessage;
import pt.tecnico.ulisboa.hbbft.subset.IAsynchronousCommonSubset;
import pt.tecnico.ulisboa.hbbft.subset.Subset;
import pt.tecnico.ulisboa.hbbft.subset.SubsetMessage;
import pt.tecnico.ulisboa.hbbft.subset.dumbo.DumboSubset;

import java.util.Optional;
import java.util.Vector;

// The sub-algorithms and their intermediate results for a single epoch.
public class Epoch implements IProtocol<byte[], Subset, ProtocolMessage> {

    // Our epoch number.
    private final Long epochId;

    // The the subset algorithm.
    // TODO hack to support dumbo2
    // private final DumboSubset subset;
    private final IAsynchronousCommonSubset subset;

    // Whether contributions should be encrypted in this epoch.
    private final Boolean requireEncryption;

    // The value to output when finished.
    private Subset decidedValue;

    public Epoch(
            Long epochId,
            IAsynchronousCommonSubset subset,
            Boolean requireEncryption
    ) {
        this.epochId = epochId;
        this.subset = subset;
        this.requireEncryption = requireEncryption;
    }

    @Override
    public Step<Subset> handleInput(byte[] input) {
        if (this.requireEncryption) {
            // TODO encrypt proposal
        }
        Step<Subset> subsetStep = this.subset.handleInput(input);
        return this.handleSubsetStep(subsetStep);
    }

    @Override
    public Step<Subset> handleMessage(ProtocolMessage message) {
        // TODO handle decryption shares
        if (message instanceof SubsetMessage) {
            Step<Subset> subsetStep = this.subset.handleMessage((SubsetMessage) message);
            return this.handleSubsetStep(subsetStep);
        }
        return new Step<>();
    }

    @Override
    public boolean hasTerminated() {
        return this.decidedValue != null;
    }

    @Override
    public Optional<Subset> deliver() {
        return Optional.ofNullable(this.decidedValue);
    }

    public Long getEpochId() {
        return epochId;
    }

    public Step<Subset> handleSubsetStep(Step<Subset> subsetStep) {
        Step<Subset> step = this.convertStep(subsetStep);

        // TODO collect decrypted outputs and add them to batch

        if (!subsetStep.getOutput().isEmpty()) {
            Subset output = subsetStep.getOutput().firstElement();
            step.add(output);
            decidedValue = output;
        }

        return step;
    }

    private Step<Subset> convertStep(Step<?> step) {
        Vector<TargetedMessage> messages = new Vector<>();
        for (TargetedMessage tm : step.getMessages()) {
            ProtocolMessage content = tm.getContent();
            DumboMessage message = new DumboMessage(content.getPid(), 0, content.getSender(), epochId, content);
            messages.add(new TargetedMessage(message, tm.getTargets()));
        }
        return new Step<>(messages);
    }
}
