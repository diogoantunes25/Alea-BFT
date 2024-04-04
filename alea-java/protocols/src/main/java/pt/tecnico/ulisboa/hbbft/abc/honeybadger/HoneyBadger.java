package pt.tecnico.ulisboa.hbbft.abc.honeybadger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.tecnico.ulisboa.hbbft.NetworkInfo;
import pt.tecnico.ulisboa.hbbft.ProtocolMessage;
import pt.tecnico.ulisboa.hbbft.Step;
import pt.tecnico.ulisboa.hbbft.abc.Block;
import pt.tecnico.ulisboa.hbbft.subset.hbbft.HoneyBadgerSubsetFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class HoneyBadger implements IHoneyBadger {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // Our ID.
    public final Integer replicaId;

    // The network configuration.
    private final NetworkInfo networkInfo;

    // The earliest epoch from which we have not yet received output.
    private Long epoch = 0L;

    // Whether we have already submitted a proposal for the current epoch.
    private Boolean hasInput = false;

    // The sub-algorithms for ongoing epochs.
    private Map<Long, Epoch> epochs = new ConcurrentHashMap<>();

    // Parameters controlling HoneyBadger's behavior and performance.
    private final Params params;

    private final HoneyBadgerSubsetFactory acsFactory;

    private TransactionQueue queue;

    public HoneyBadger(
            Integer replicaId,
            NetworkInfo networkInfo,
            Params params,
            HoneyBadgerSubsetFactory acsFactory
    ) {
        this.replicaId = replicaId;
        this.networkInfo = networkInfo;
        this.params = params;
        this.acsFactory = acsFactory;

        final int bSize = params.getBatchSize();
        final int pSize = Math.max(bSize/networkInfo.getN(), 1);
        this.queue = new TransactionQueue(bSize, pSize);
    }

    @Override
    public Step<Block> handleInput(byte[] input) {
        this.queue.add(input);
        return this.tryPropose();
    }

    @Override
    public Step<Block> handleMessage(ProtocolMessage message) {

        Step<Block> step = new Step<>();

        final String pid = message.getPid().split("\\|")[0];
        final long epochId = Long.parseLong(pid.split("-")[1]);

        if (epochId < this.epoch) {
            System.out.println("OLD MESSAGE");

        } else if (epochId > this.epoch + this.params.getMaxFutureEpochs()) {
            System.out.println("MESSAGE OUT OF EPOCH RANGE");

        } else {
            Epoch epoch = getEpoch(epochId);
            Step<Batch> epochStep = epoch.handleMessage(message);
            step.add(this.handleEpochStep(epochStep));
        }

        return step;
    }

    @Override
    public boolean hasTerminated() {
        return false;
    }

    @Override
    public Optional<Block> deliver() {
        return Optional.empty();
    }

    private Step<Block> tryPropose() {
        if (this.hasInput) {
            return new Step<>();
        }

        Epoch epoch = getEpoch(this.epoch);
        Collection<byte[]> proposal = this.queue.get();
        if (proposal.isEmpty()) {
            return new Step<>();
        }


        byte[] encoded = encodeBatchEntries(proposal);
        Step<Batch> step = epoch.handleInput(encoded);
        this.hasInput = true;

        return this.handleEpochStep(step);
    }

    @Override
    public Step<Block> handleEpochStep(Step<Batch> epochStep) {
        Step<Block> step = new Step<>(epochStep.getMessages());

        Epoch epoch = this.getEpoch(this.epoch);
        if (!epoch.hasTerminated()) {
            return step;
        }

        Batch batch = epoch.deliver().orElseThrow();

        Set<byte[]> blockContents = new HashSet<>();
        for (byte[] contribution: batch.getContributions().values())
            blockContents.addAll(decodeBatchEntries(contribution));
        this.queue.removeAll(blockContents);

        Block block = new Block(batch.getEpochId(), blockContents);
        step.add(block);

        this.updateEpoch();
        step.add(this.tryPropose());

        step.add(this.handleEpochStep(new Step<>()));
        return step;
    }

    // Increments the epoch number and clears any state that is local to the finished epoch.
    private void updateEpoch() {
        epochs.remove(epoch);
        epoch += 1;
        this.hasInput = false;
    }

    public Epoch getEpoch(Long epochId) {
        return epochs.computeIfAbsent(epochId,
                eid -> new Epoch(
                        eid,
                        replicaId,
                        networkInfo,
                        acsFactory.create(),
                        params.getEncryptionSchedule().encryptOnEpoch(eid)
                )
        );
    }

    private byte[] encodeBatchEntries(Collection<byte[]> entries) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);

        try {
            out.writeInt(entries.size());
            for (byte[] entry : entries) {
                out.writeInt(entry.length);
                out.write(entry);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }

    private Collection<byte[]> decodeBatchEntries(byte[] encoded) {
        ByteArrayInputStream bais = new ByteArrayInputStream(encoded);
        DataInputStream in = new DataInputStream(bais);

        Set<byte[]> entries = new HashSet<>();
        try {
            int count = in.readInt();
            for (int i=0; i < count; i++) {
                int size = in.readInt();
                entries.add(in.readNBytes(size));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return entries;
    }

    public void reset() {
        epochs = new TreeMap<>();
        int bSize = params.getBatchSize();
        int pSize = bSize/networkInfo.getN();
        this.queue = new TransactionQueue(bSize, pSize);
    }
}
