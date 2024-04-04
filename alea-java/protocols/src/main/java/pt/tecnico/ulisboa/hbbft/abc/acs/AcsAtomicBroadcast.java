package pt.tecnico.ulisboa.hbbft.abc.acs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.tecnico.ulisboa.hbbft.NetworkInfo;
import pt.tecnico.ulisboa.hbbft.ProtocolMessage;
import pt.tecnico.ulisboa.hbbft.Step;
import pt.tecnico.ulisboa.hbbft.abc.Block;
import pt.tecnico.ulisboa.hbbft.abc.IAtomicBroadcast;
import pt.tecnico.ulisboa.hbbft.abc.honeybadger.crypto.AlwaysEncrypt;
import pt.tecnico.ulisboa.hbbft.abc.honeybadger.crypto.EncryptionSchedule;
import pt.tecnico.ulisboa.hbbft.subset.SubsetFactory;
import pt.ulisboa.tecnico.utils.Utils;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class AcsAtomicBroadcast implements IAtomicBroadcast {

    // Our ID.
    public final Integer replicaId;

    // The network configuration.
    private final NetworkInfo networkInfo;

    // The earliest epoch from which we have not yet received output.
    private AtomicLong epoch = new AtomicLong(0);

    // Whether we have already submitted a proposal for the current epoch.
    private AtomicBoolean hasInput = new AtomicBoolean(false);

    // The sub-protocols for ongoing epochs.
    private Map<Long, Epoch> epochs = new ConcurrentSkipListMap<>();

    // Parameters controlling the ABC behavior and performance.
    private final Params params;

    private final SubsetFactory acsFactory;

    // Queue of pending transactions
    private TransactionQueue queue;

    public AcsAtomicBroadcast(
            Integer replicaId,
            NetworkInfo networkInfo,
            Params params,
            SubsetFactory acsFactory
    ) {
        this.replicaId = replicaId;
        this.networkInfo = networkInfo;
        this.params = params;
        this.acsFactory = acsFactory;

        final int bSize = params.getBatchSize();
        final int pSize = Math.max(bSize/networkInfo.getN(), 1);
        this.queue = new TransactionQueue(bSize, pSize);
    }

    public Epoch getEpoch(Long epochId) {
        return this.epochs.computeIfAbsent(epochId, (eid) -> {
                Utils.acsEpochStart(eid);
                return new Epoch(
                        eid,
                        replicaId,
                        networkInfo,
                        acsFactory.create(String.format("ABC-%d|ACS-%d", eid, eid)),
                        params.getEncryptionSchedule().encryptOnEpoch(eid)
                );
                }
        );
    }

    @Override
    public Step<Block> handleInput(byte[] input) {

        // add input to the queue of pending transactions
        this.queue.add(input);

        // try propose into a new epoch
        return this.tryPropose();
    }

    @Override
    public synchronized Step<Block> handleMessage(ProtocolMessage message) {
        // logger.info("handleMessage - msg:{}", message);
        long e = epoch.get();

        if (params.getFault(replicaId) == Params.Fault.CRASH) return new Step<>();

        final String pid = message.getPid().split("\\|")[0];
        final long epochId = Long.parseLong(pid.split("-")[1]);

        if (epochId < e) {
            // ignore previous epoch messages

        } else if (epochId > e + this.params.getMaxFutureEpochs()) {
            // ignore out of range message
            // logger.info("MESSAGE OUT OF EPOCH RANGE");

        } else {
            // route the message to the corresponding epoch
            Epoch epoch = this.getEpoch(epochId);
            Step<Batch> epochStep = epoch.handleMessage(message);
            return this.handleEpochStep(epochStep);
        }

        return new Step<>();
    }

    @Override
    public boolean hasTerminated() {
        // always false because ABC operates as a channel
        return false;
    }

    @Override
    public Optional<Block> deliver() {
        // always empty, blocks are output in steps
        return Optional.empty();
    }

    private Step<Block> tryPropose() {

        Collection<byte[]> proposal = this.queue.get();

        if (proposal.isEmpty())  {
            return new Step<>();
        }

        if (this.hasInput.getAndSet(true)) {
            return new Step<>();
        }

//        System.out.printf("actually proposing\n");
        byte[] encoded = this.encodeBatchEntries(proposal);

        Epoch epoch = this.getEpoch(this.epoch.get());
        Step<Batch> step = epoch.handleInput(encoded);

        return this.handleEpochStep(step);
    }

    public Step<Block> handleEpochStep(Step<Batch> epochStep) {
        Step<Block> step = new Step<>(epochStep.getMessages());

        // check is the current epoch has terminated - if not return (because epoch has not stepped yet)
        Epoch epoch = this.getEpoch(this.epoch.get());
        if (!epoch.hasTerminated()) {
            return step;
        }

        // retrieve the output batch for the current epoch
        Batch batch = epoch.deliver().orElseThrow();

        Set<byte[]> blockContents = new HashSet<>();
        for (Map.Entry<Integer, byte[]> contribution: batch.getContributions().entrySet()) {
            Collection<byte[]> entries = this.decodeBatchEntries(contribution.getValue());

            if (params.getFault(contribution.getKey()) != Params.Fault.BYZANTINE) {
                blockContents.addAll(entries);
            }

            // remove the block contents form the pending queue
            this.queue.removeAll(blockContents);
        }


        Block block = new Block(batch.getEpochId(), blockContents);
        block.setProposers(batch.getContributions().keySet());
        step.add(block);

        this.updateEpoch();
        step.add(this.tryPropose());

        step.add(this.handleEpochStep(new Step<>()));
        return step;
    }

    // Increments the epoch number and clears any state that is local to the finished epoch.
    private void updateEpoch() {
        // System.out.printf("updating epoch\n");
        Utils.acsEpochEnd(this.epoch.get());
        this.epochs.remove(this.epoch.getAndIncrement());
        this.hasInput.set(false);
    }

    // TODO move to static method in Batch
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

    // TODO move to static method in Batch
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
        int bSize = params.getBatchSize();
        int pSize = Math.max(bSize/networkInfo.getN(),1);
        epochs = new ConcurrentSkipListMap<>();
        this.queue = new TransactionQueue(bSize, pSize);
    }

    // Parameters controlling the protocol's behavior and performance.
    public static class Params {

        public enum Fault {
            FREE,
            CRASH,
            BYZANTINE
        }

        // The maximum number of future epochs for which we handle messages simultaneously.
        private Long maxFutureEpochs;

        // Schedule for adding threshold encryption to some percentage of rounds
        private EncryptionSchedule encryptionSchedule;

        private Integer batchSize;

        private Integer committeeSize;

        private Map<Integer, Fault> faults;

        private Boolean benchmark;

        private Integer maxPayloadSize;

        public static class Builder {

            private int batchSize = 8;
            private long maxFutureEpochs = 100L;
            private EncryptionSchedule encryptionSchedule = new AlwaysEncrypt();
            private int committeeSize = 2;
            private Map<Integer, Fault> faults = new HashMap<>();
            private boolean benchmark = false;
            private int maxPayloadSize = 250;

            public Builder() {}

            public Builder batchSize(int batchSize) {
                this.batchSize = batchSize;
                return this;
            }

            public Builder maxFutureEpochs(long maxFutureEpochs) {
                this.maxFutureEpochs = maxFutureEpochs;
                return this;
            }

            public Builder encryptionSchedule(EncryptionSchedule encryptionSchedule) {
                this.encryptionSchedule = encryptionSchedule;
                return this;
            }

            public Builder committeeSize(int committeeSize) {
                this.committeeSize = committeeSize;
                return this;
            }

            public Builder faults(Map<Integer, Fault> faults) {
                this.faults = faults;
                return this;
            }

            public Builder benchmark(boolean benchmark) {
                this.benchmark = benchmark;
                return this;
            }

            public Builder maxPayloadSize(int maxPayloadSize) {
                this.maxPayloadSize = maxPayloadSize;
                return this;
            }

            public Params build() {
                Params params = new Params();

                params.batchSize = this.batchSize;
                params.maxFutureEpochs = this.maxFutureEpochs;
                params.encryptionSchedule = this.encryptionSchedule;
                params.committeeSize = this.committeeSize;
                params.faults = this.faults;
                params.benchmark = this.benchmark;
                params.maxPayloadSize = this.maxPayloadSize;

                return params;
            }
        }

        private Params() {}

        public Long getMaxFutureEpochs() {
            return maxFutureEpochs;
        }

        public EncryptionSchedule getEncryptionSchedule() {
            return encryptionSchedule;
        }

        public Integer getBatchSize() {
            return batchSize;
        }

        public Integer getK() {
            return committeeSize;
        }

        public Fault getFault(Integer replicaId) {
            return faults.getOrDefault(replicaId, Fault.FREE);
        }

        public Boolean isBenchmark() {
            return this.benchmark;
        }

        public Integer getMaxPayloadSize() {
            return maxPayloadSize;
        }


    }
}
