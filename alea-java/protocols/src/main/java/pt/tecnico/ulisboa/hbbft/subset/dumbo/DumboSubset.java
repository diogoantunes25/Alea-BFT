package pt.tecnico.ulisboa.hbbft.subset.dumbo;

import pt.tecnico.ulisboa.hbbft.*;
import pt.tecnico.ulisboa.hbbft.binaryagreement.BinaryAgreementFactory;
import pt.tecnico.ulisboa.hbbft.binaryagreement.BinaryAgreementMessage;
import pt.tecnico.ulisboa.hbbft.broadcast.BroadcastFactory;
import pt.tecnico.ulisboa.hbbft.broadcast.BroadcastMessage;
import pt.tecnico.ulisboa.hbbft.broadcast.IBroadcast;
import pt.tecnico.ulisboa.hbbft.election.CommitteeElection;
import pt.tecnico.ulisboa.hbbft.election.CommitteeElectionMessage;
import pt.tecnico.ulisboa.hbbft.subset.IAsynchronousCommonSubset;
import pt.tecnico.ulisboa.hbbft.subset.Proposal;
import pt.tecnico.ulisboa.hbbft.subset.Subset;
import pt.tecnico.ulisboa.hbbft.subset.SubsetMessage;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DumboSubset implements IAsynchronousCommonSubset {

    private final String pid;

    private final Integer replicaId;

    private final NetworkInfo networkInfo;

    private final BroadcastFactory bcFactory;
    private final BinaryAgreementFactory baFactory;

    // The value broadcast instances
    private final Map<Integer, IBroadcast> vBroadcasts = new ConcurrentHashMap<>();

    // The proposed value by replica
    private final Map<Integer, byte[]> values = new ConcurrentHashMap<>();

    // A committee election protocol instance
    private final CommitteeElection ce;

    // The elected committee
    private Set<Integer> committee;

    private final Map<Integer, Proposal> proposals = new ConcurrentHashMap<>();
    private Set<Integer> indices;

    // The value decided by this instance
    private Subset decision;

    public DumboSubset(
            String pid,
            Integer replicaId,
            NetworkInfo networkInfo,
            BroadcastFactory bcFactory,
            BinaryAgreementFactory baFactory
    ) {
        this.pid = pid;
        this.replicaId = replicaId;
        this.networkInfo = networkInfo;

        this.bcFactory = bcFactory;
        this.baFactory = baFactory;

        final int committeeSize = networkInfo.getF() + 1;
        this.ce = new CommitteeElection(pid, replicaId, networkInfo, committeeSize);
    }

    @Override
    public String getPid() {
        return pid;
    }

    @Override
    public Step<Subset> handleInput(byte[] input) {
        Step<Subset> step = new Step<>();

        // Input a value to the broadcast protocol
        IBroadcast broadcast = this.getValueBroadcastInstance(replicaId);
        Step<byte[]> vbcStep = broadcast.handleInput(input);
        step.add(this.handleValueBroadcastStep(vbcStep, replicaId));

        // Invoke the committee election protocol
        Step<Set<Integer>> ceStep = ce.handleInput(true);
        step.add(this.handleCommitteeElectionStep(ceStep));

        return step;
    }

    @Override
    public Step<Subset> handleMessage(SubsetMessage message) {
        Step<Subset> step = new Step<>();

        final int instance = message.getInstance();
        final ProtocolMessage content = message.getContent();

        if (content instanceof BroadcastMessage && content.getPid().endsWith("v")) {
            IBroadcast broadcast = this.getValueBroadcastInstance(instance);
            Step<byte[]> vbcStep = broadcast.handleMessage((BroadcastMessage) content);
            step.add(this.handleValueBroadcastStep(vbcStep, instance));
        }

       else if (content instanceof CommitteeElectionMessage) {
            Step<Set<Integer>> ceStep = ce.handleMessage((CommitteeElectionMessage) content);
            step.add(this.handleCommitteeElectionStep(ceStep));
        }

       else {
            // Check if instance was elected by the committee election protocol
            if (ce.hasTerminated() && !committee.contains(instance)) {
                step.addFault(pid, "NOT IN COMMITTEE");
                return step;
            }

            if (instance == replicaId && !this.proposals.containsKey(replicaId)) {
                step.addFault(pid, "HAS NOT PROPOSED YET");
                return step;
            }

            // Retrieve the corresponding proposal
            Proposal proposal = this.getProposal(instance);
            Step<byte[]> proposalStep = proposal.handleMessage(content);
            if (this.indices == null && proposal.complete() && proposal.accepted()) {
                byte[] encoded = proposal.getResult();
                this.indices = this.decodeIndexSet(encoded);
            }
            step.add(this.convertMessages(proposalStep.getMessages(), instance));
        }

        // Try output
        step.add(this.tryOutput());

        return step;
    }

    @Override
    public boolean hasTerminated() {
        return this.deliver().isPresent();
    }

    @Override
    public Optional<Subset> deliver() {
        return Optional.ofNullable(this.decision);
    }

    public Step<Subset> handleValueBroadcastStep(Step<byte[]> vbcStep, Integer instance) {
        Step<Subset> step = new Step<>(this.convertMessages(vbcStep.getMessages(), instance));
        Vector<byte[]> output = vbcStep.getOutput();
        if (!output.isEmpty()) {
            // Store the delivered value
            this.values.putIfAbsent(instance, output.firstElement());
            step.add(this.tryProposeIndexSet());
        }
        return step;
    }

    public Step<Subset> handleCommitteeElectionStep(Step<Set<Integer>> ceStep) {
        Step<Subset> step = new Step<>(this.convertMessages(ceStep.getMessages(), -1));
        if (!ceStep.getOutput().isEmpty()) {
            this.committee = ceStep.getOutput().firstElement();
            if (committee.contains(replicaId)) {
                step.add(this.tryProposeIndexSet());
            }
        }
        return step;
    }

    private Step<Subset> tryProposeIndexSet() {
        Step<Subset> step = new Step<>();
        if (!this.ce.hasTerminated() || !this.committee.contains(replicaId)) return step;

        Set<Integer> indexes = this.values.keySet();

        final int threshold = 2*this.networkInfo.getF() + 1;
        if (indexes.size() >= threshold) {
            Proposal proposal = this.getProposal(replicaId);
            byte[] encoded = this.encodeIndexSet(indexes);
            Step<byte[]> proposalStep = proposal.propose(encoded);
            if (this.indices == null && !proposalStep.getOutput().isEmpty()) {
                this.indices = this.decodeIndexSet(proposalStep.getOutput().firstElement());
            }
            step.add(this.convertMessages(proposalStep.getMessages(), replicaId));
        }

        return step;
    }

    private Step<Subset> tryOutput() {
        Step<Subset> step = new Step<>();
        if (this.decision != null || this.indices == null) {
            return step;
        }

        // Check if all broadcast instances contained in the index set have terminated
        if (this.indices.stream().allMatch(i -> this.getValueBroadcastInstance(i).hasTerminated())) {
            Subset output = new Subset();
            for (int index : this.indices) {
                byte[] value = this.getValueBroadcastInstance(index).deliver().orElseThrow();
                output.addEntry(index, value);
            }
            step.add(output);
            this.decision = output;
        }

        return step;
    }

    private IBroadcast getValueBroadcastInstance(Integer instance) {
        // System.out.println(this.vBroadcasts.getClass().getSimpleName());
        return this.vBroadcasts.computeIfAbsent(instance,
                id -> this.bcFactory.create(String.format("%s-%d-v", pid, id), id)
        );
    }

    public Proposal getProposal(Integer instance) {
        return this.proposals.computeIfAbsent(instance,
                id -> new Proposal(
                        id,
                        bcFactory.create(String.format("%s-%d-i", pid, id), id),
                        baFactory.create(String.format("%s-%d-a", pid, id))
                )
        );
    }

    private byte[] encodeIndexSet(Set<Integer> indexSet) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);
        try {
            // Write the set size
            out.writeInt(indexSet.size());

            // Write the set elements
            for (int index: indexSet)
                out.writeInt(index);

            // Close the output stream
            baos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }

    private Set<Integer> decodeIndexSet(byte[] encoded) {
        ByteArrayInputStream bais = new ByteArrayInputStream(encoded);
        DataInputStream in = new DataInputStream(bais);

        Set<Integer> indexSet = new HashSet<>();
        try {
            int size = in.readInt();
            for (int i=0; i < size; i++) {
                indexSet.add(in.readInt());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return indexSet;
    }

    private Vector<TargetedMessage> convertMessages(Vector<TargetedMessage> messages, Integer instance) {
        Vector<TargetedMessage> converted = new Vector<>();
        for (TargetedMessage tm : messages) {
            ProtocolMessage content = tm.getContent();

            SubsetMessage subsetMessage;
            if (content instanceof BroadcastMessage)
                subsetMessage = new SubsetMessage(pid, 0, content.getSender(), instance, content);
            else if (content instanceof BinaryAgreementMessage)
                subsetMessage = new SubsetMessage(pid, 1, content.getSender(), instance, content);
            else if (content instanceof CommitteeElectionMessage)
                subsetMessage = new SubsetMessage(pid, 2, content.getSender(), instance, content);
            else
                continue;
            converted.add(new TargetedMessage(subsetMessage, tm.getTarget()));
        }return converted;
    }
}
