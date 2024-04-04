package pt.tecnico.ulisboa.hbbft.subset.dumbo;

import pt.tecnico.ulisboa.hbbft.NetworkInfo;
import pt.tecnico.ulisboa.hbbft.ProtocolMessage;
import pt.tecnico.ulisboa.hbbft.Step;
import pt.tecnico.ulisboa.hbbft.TargetedMessage;
import pt.tecnico.ulisboa.hbbft.agreement.mvba.IMultiValueAgreement;
import pt.tecnico.ulisboa.hbbft.agreement.mvba.MultiValueAgreement;
import pt.tecnico.ulisboa.hbbft.subset.IAsynchronousCommonSubset;
import pt.tecnico.ulisboa.hbbft.subset.Subset;
import pt.tecnico.ulisboa.hbbft.subset.SubsetMessage;
import pt.tecnico.ulisboa.hbbft.vbroadcast.VOutput;
import pt.tecnico.ulisboa.hbbft.vbroadcast.prbc.IProvableReliableBroadcast;
import pt.tecnico.ulisboa.hbbft.vbroadcast.prbc.ProvableReliableBroadcast;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Vector;
import java.util.stream.Collectors;

public class Dumbo2Subset implements IAsynchronousCommonSubset {

    private final String pid;

    private final Integer replicaId;

    private final NetworkInfo networkInfo;

    // provable reliable broadcast instances
    private final Map<Integer, IProvableReliableBroadcast> prbcInstances;

    // multi-value agreement instance
    private final IMultiValueAgreement mvba;

    private final Map<Integer, VOutput> prbcResults = new HashMap<>();

    private Map<Integer, byte[]> mvbaResult;

    private byte[] input;

    // The value decided by this instance
    private Subset output;

    public Dumbo2Subset(String pid, Integer replicaId, NetworkInfo networkInfo) {
        this.pid = pid;
        this.replicaId = replicaId;
        this.networkInfo = networkInfo;

        this.prbcInstances = new HashMap<>();
        this.mvba = new MultiValueAgreement("MVBA-0", replicaId, networkInfo);
    }

    @Override
    public String getPid() {
        return pid;
    }

    @Override
    public Step<Subset> handleInput(byte[] input) {
        // Pass the input into a provable reliable broadcast instance
        IProvableReliableBroadcast prbc = this.getPrbcInstance(replicaId);
        Step<VOutput> prbcStep = prbc.handleInput(input);
        this.input = input;
        return this.handlePrbcStep(prbcStep, replicaId);
    }

    @Override
    public Step<Subset> handleMessage(SubsetMessage message) {
        Step<Subset> step = new Step<>();

        if (!message.getPid().equals(pid)) return step;

        final int instance = message.getInstance();
        final ProtocolMessage content = message.getContent();

        if (message.getType() == 0) {
            IProvableReliableBroadcast prbc = this.getPrbcInstance(instance);
            Step<VOutput> prbcStep = prbc.handleMessage(content);
            return this.handlePrbcStep(prbcStep, instance);

        } else if (message.getType() == 1) {
            Step<byte[]> mvbaStep = mvba.handleMessage(content);
            return this.handleMvbaStep(mvbaStep);
        }

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

    private IProvableReliableBroadcast getPrbcInstance(Integer instance) {
        return this.prbcInstances.computeIfAbsent(instance,
                id -> new ProvableReliableBroadcast(String.format("PRBC-%d", instance), replicaId, networkInfo, instance));
    }

    private Step<Subset> handlePrbcStep(Step<VOutput> prbcStep, Integer instance) {
        Step<Subset> step = new Step<>(convertMessages(prbcStep.getMessages(), 0, instance));
        if (prbcStep.getOutput().isEmpty()) return step;

        this.prbcResults.putIfAbsent(instance, prbcStep.getOutput().firstElement());

        final int quorum = networkInfo.getN() - networkInfo.getF();
        if (this.prbcResults.size() == quorum) {
            Map<Integer, byte[]> proposals = this.prbcResults.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getSignature()));
            Step<byte[]> mvbaStep = mvba.handleInput(encodeProposals(proposals));
            step.add(this.handleMvbaStep(mvbaStep));
        }

        step.add(this.tryOutput());

        return step;
    }

    private Step<Subset> handleMvbaStep(Step<byte[]> mvbaStep) {
        Step<Subset> step = new Step<>(convertMessages(mvbaStep.getMessages(), 1, -1));
        if (mvbaStep.getOutput().isEmpty()) return step;

        byte[] mvbaOut = mvbaStep.getOutput().firstElement();
        this.mvbaResult = decodeProposals(mvbaOut.length > 0 ? mvbaOut : this.input);
        System.out.println(this.mvbaResult);

        step.add(this.tryOutput());

        return step;
    }

    private Step<Subset> tryOutput() {
        Step<Subset> step = new Step<>();

        // wait for MVBA to terminate
        if (mvbaResult == null) return step;

        // wait for all VRBC instances in the output of MVBA to terminate
        if (!prbcResults.keySet().containsAll(mvbaResult.keySet())) return step;

        Subset subset = new Subset();
        for (int i: mvbaResult.keySet())
            subset.addEntry(i, prbcResults.get(i).getValue());

        this.output = subset;
        step.add(output);

        return step;
    }

    private byte[] encodeProposals(Map<Integer, byte[]> proposals) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);
        try {
            // write the proposals count
            out.writeInt(proposals.size());

            // write the proposals
            for (Map.Entry<Integer, byte[]> entry: proposals.entrySet()) {
                out.writeInt(entry.getKey());
                out.writeInt(entry.getValue().length);
                out.write(entry.getValue());
            }

            // close the output stream
            baos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }

    private Map<Integer, byte[]> decodeProposals(byte[] encoded) {
        ByteArrayInputStream bais = new ByteArrayInputStream(encoded);
        DataInputStream in = new DataInputStream(bais);

        Map<Integer, byte[]> proposals = new HashMap<>();
        try {
            int size = in.readInt();
            for (int i=0; i<size; i++) {
                int replicaId = in.readInt();
                int sigSize = in.readInt();
                byte[] signature = in.readNBytes(sigSize);
                proposals.put(replicaId, signature);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return proposals;
    }

    private Vector<TargetedMessage> convertMessages(Vector<TargetedMessage> messages, Integer type, Integer instance) {
        Vector<TargetedMessage> converted = new Vector<>();
        for (TargetedMessage tm : messages) {
            ProtocolMessage content = tm.getContent();

            SubsetMessage subsetMessage = new SubsetMessage(pid, type, content.getSender(), instance, content);
            converted.add(new TargetedMessage(subsetMessage, tm.getTargets()));
        }
        return converted;
    }
}