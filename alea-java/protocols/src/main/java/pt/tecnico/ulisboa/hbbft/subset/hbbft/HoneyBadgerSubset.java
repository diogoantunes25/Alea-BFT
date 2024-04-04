package pt.tecnico.ulisboa.hbbft.subset.hbbft;

import pt.tecnico.ulisboa.hbbft.*;
import pt.tecnico.ulisboa.hbbft.binaryagreement.BinaryAgreementFactory;
import pt.tecnico.ulisboa.hbbft.broadcast.BroadcastFactory;
import pt.tecnico.ulisboa.hbbft.broadcast.BroadcastMessage;
import pt.tecnico.ulisboa.hbbft.subset.IAsynchronousCommonSubset;
import pt.tecnico.ulisboa.hbbft.subset.Proposal;
import pt.tecnico.ulisboa.hbbft.subset.Subset;
import pt.tecnico.ulisboa.hbbft.subset.SubsetMessage;

import java.util.*;
import java.util.stream.Collectors;

public class HoneyBadgerSubset implements IAsynchronousCommonSubset {

    // The ACS instance identifier
    private final String pid;

    private final Integer replicaId;
    private final NetworkInfo networkInfo;

    // A map assigning each validator to the progress of their contribution.
    private final Map<Integer, Proposal> proposals = new TreeMap<>();

    // The ACS output value
    private Subset output;

    public HoneyBadgerSubset(
            String pid,
            Integer replicaId,
            NetworkInfo networkInfo,
            BroadcastFactory bcFactory,
            BinaryAgreementFactory baFactory
    ) {
        this.pid = pid;
        this.replicaId = replicaId;
        this.networkInfo = networkInfo;

        for (int id=0; id < networkInfo.getN(); id++) {
            Proposal proposal = new Proposal(
                    id,
                    bcFactory.create(String.format("RBC-%d", id), id),
                    baFactory.create(String.format("BA-%d", id))
            );

            proposals.put(id, proposal);
        }
    }

    @Override
    public String getPid() {
        return pid;
    }

    @Override
    public Step<Subset> handleInput(byte[] input) {

        Proposal proposal = proposals.get(replicaId);
        Step<Subset> step = this.convertStep(replicaId, proposal.propose(input));

        step.add(this.tryOutput());

        return step;
    }

    @Override
    public Step<Subset> handleMessage(SubsetMessage message) {
        final int instance = message.getInstance();
        Proposal proposal = proposals.get(instance);
        Step<byte[]> proposalStep = proposal.handleMessage(message.getContent());

        Step<Subset> step = this.convertStep(instance, proposalStep);
        step.add(this.tryOutput());
        return step;
    }

    @Override
    public boolean hasTerminated() {
        return output != null;
    }

    @Override
    public Optional<Subset> deliver() {
        return Optional.ofNullable(output);
    }

    // Checks the voting and termination conditions: If enough proposals have been accepted, votes
    // "no" for the remaining ones. If all proposals have been decided, outputs `Done`.
    private Step<Subset> tryOutput() {
        Step<Subset> step = new Step<>();

        if (this.hasTerminated() || this.countAccepted() < networkInfo.getNumCorrect()) {
            // System.out.printf("Returning from tryOutput\n");
            return step;
        }

        // Vote false for all remaining binary agreement instances
        if (this.countAccepted() == networkInfo.getNumCorrect()) {
            for (Map.Entry<Integer, Proposal> entry : proposals.entrySet()) {
                step.add(this.convertStep(entry.getKey(), entry.getValue().vote(false)));
            }
        }

        if (proposals.values().stream().allMatch(Proposal::complete)) {
            Subset output = new Subset();
            for (Proposal proposal : proposals.values().stream().filter(Proposal::accepted).collect(Collectors.toList())) {
                output.addEntry(proposal.getInstance(), proposal.getResult());
            }
            step.add(output);
            this.output = output;
        }


        return step;
    }

    // Returns the number of validators from which we have already received a proposal.
    public Integer getReceivedProposals() {
        return (int) proposals.values().stream().filter(Proposal::received).count();
    }

    // Returns the number of Binary Agreement instances that have decided "yes".
    private int countAccepted() {
        return (int) proposals.values().stream().filter(Proposal::accepted).count();
    }

    private Step<Subset> convertStep(Integer instance, Step<?> step) {

        // Convert broadcast/binary agreement messages into subset messages
        Vector<TargetedMessage> messages = new Vector<>();
        for (TargetedMessage tm : step.getMessages()) {
            ProtocolMessage content = tm.getContent();
            int type = (content instanceof BroadcastMessage) ? 0 : 1;
            SubsetMessage subsetMessage = new SubsetMessage(pid, type, content.getSender(), instance, content);
            messages.add(new TargetedMessage(subsetMessage, tm.getTarget()));
        }
        return new Step<>(messages);
    }
}
