package pt.tecnico.ulisboa.hbbft.abc.acs;

import pt.tecnico.ulisboa.hbbft.IProtocol;
import pt.tecnico.ulisboa.hbbft.NetworkInfo;
import pt.tecnico.ulisboa.hbbft.ProtocolMessage;
import pt.tecnico.ulisboa.hbbft.Step;
import pt.tecnico.ulisboa.hbbft.abc.acs.messages.DecryptionMessage;
import pt.tecnico.ulisboa.hbbft.subset.IAsynchronousCommonSubset;
import pt.tecnico.ulisboa.hbbft.subset.Subset;
import pt.tecnico.ulisboa.hbbft.subset.SubsetMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

// The sub-protocols and their intermediate results for a single epoch.
public class Epoch implements IProtocol<byte[], Batch, ProtocolMessage> {

    // Our epoch number.
    private final Long epochId;

    // Our ID.
    private final Integer replicaId;

    // Shared network data.
    private final NetworkInfo networkInfo;

    // The the ACS protocol.
    private final IAsynchronousCommonSubset acs;

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
            IAsynchronousCommonSubset acs,
            Boolean requireEncryption
    ) {
        this.epochId = epochId;
        this.replicaId = replicaId;
        this.networkInfo = networkInfo;
        this.acs = acs;
        this.requireEncryption = requireEncryption;
    }

    @Override
    public Step<Batch> handleInput(byte[] input) {
        if (this.requireEncryption) {
            // TODO encrypt proposal
        }

        Step<Subset> acsStep = this.acs.handleInput(input);
        return this.handleAcsStep(acsStep);
    }

    @Override
    public Step<Batch> handleMessage(ProtocolMessage message) {
        if (message instanceof SubsetMessage) {
            Step<Subset> acsStep = this.acs.handleMessage((SubsetMessage) message);
            return this.handleAcsStep(acsStep);

        } else if (message instanceof DecryptionMessage) {
            return this.handleDecryptionMessage((DecryptionMessage) message);
        } else {
            return new Step<>();
        }
    }

    @Override
    public boolean hasTerminated() {
        return decidedValue != null;
    }

    @Override
    public Optional<Batch> deliver() {
        return Optional.ofNullable(decidedValue);
    }

    private Step<Batch> handleDecryptionMessage(DecryptionMessage message) {
        // save sender decryption share
        this.decryptionShares.putIfAbsent(message.getSender(), message.getShare());

        // output if ACS has terminated and enough shares have been collected
        return this.tryOutput();
    }

    private Step<Batch> handleAcsStep(Step<Subset> acsStep) {
        Step<Batch> step = new Step<>(acsStep.getMessages());
        if (acsStep.getOutput().isEmpty()) return step;

        // start decryption stage
        if (this.requireEncryption) {
            // generate simulated decryption share
            // TODO: Generate proper share
            Random rd = new Random();
            byte[] decryptionShare = new byte[250];
            rd.nextBytes(decryptionShare);

            // broadcast decryption share for batch
            DecryptionMessage decryptionMessage = new DecryptionMessage(
                    this.acs.getPid(), this.replicaId, decryptionShare);
            step.add(this.handleMessage(decryptionMessage));
            step.add(decryptionMessage, this.networkInfo.getValidatorSet().getAllIds().stream()
                    .filter(id -> !id.equals(this.replicaId)).collect(Collectors.toList()));
        }

        // output if ACS has terminated and enough shares have been collected
        step.add(this.tryOutput());

        return step;
    }

    private Step<Batch> tryOutput() {

        Step<Batch> step = new Step<>();

        // ignore if ACS hasn't terminated
        if (!this.acs.hasTerminated()) return step;

        // ignore if not enough decryption shares have been collected
        final int quorum = networkInfo.getF() + 1;
        if (this.requireEncryption && this.decryptionShares.size() < quorum) return step;

        // reconstruct batch from the ACS output and decryption shares
        Batch batch = new Batch(this.epochId, this.acs.deliver().orElseThrow().getEntries());

        // output batch
        this.decidedValue = batch;
        step.add(batch);

        return step;
    }
}
