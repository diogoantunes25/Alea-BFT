package pt.tecnico.ulisboa.hbbft.abc.alea;

import pt.tecnico.ulisboa.hbbft.NetworkInfo;
import pt.tecnico.ulisboa.hbbft.ProtocolMessage;
import pt.tecnico.ulisboa.hbbft.Step;
import pt.tecnico.ulisboa.hbbft.Transaction;
import pt.tecnico.ulisboa.hbbft.abc.Block;
import pt.tecnico.ulisboa.hbbft.abc.alea.benchmark.ExecutionLog;
import pt.tecnico.ulisboa.hbbft.abc.alea.messages.FillGapMessage;
import pt.tecnico.ulisboa.hbbft.abc.alea.messages.FillerMessage;
import pt.tecnico.ulisboa.hbbft.abc.alea.queue.Slot;
import pt.tecnico.ulisboa.hbbft.abc.alea.state.AgreementState;
import pt.tecnico.ulisboa.hbbft.abc.alea.state.ProposingState;
import pt.tecnico.ulisboa.hbbft.binaryagreement.BinaryAgreementMessage;
import pt.tecnico.ulisboa.hbbft.binaryagreement.IBinaryAgreement;
import pt.tecnico.ulisboa.hbbft.abc.alea.queue.PriorityQueue;
import pt.tecnico.ulisboa.hbbft.binaryagreement.moustefaoui.MoustefaouiBinaryAgreement;
import pt.tecnico.ulisboa.hbbft.utils.threshsig.Signature;
import pt.tecnico.ulisboa.hbbft.utils.threshsig.ThreshSigUtils;
import pt.tecnico.ulisboa.hbbft.vbroadcast.IVBroadcast;
import pt.tecnico.ulisboa.hbbft.vbroadcast.VBroadcastMessage;
import pt.tecnico.ulisboa.hbbft.vbroadcast.VOutput;
import pt.tecnico.ulisboa.hbbft.vbroadcast.echo.EchoVBroadcast2;
import pt.ulisboa.tecnico.utils.Utils;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class Alea implements IAlea {

    // Our ID.
    public final Integer replicaId;

    // The network configuration.
    private final NetworkInfo networkInfo;

    // Parameters controlling Alea's behavior
    private final Params params;

    // Threshold signature utils.
    private final ThreshSigUtils sigUtils;

    // The local priority value
    volatile private AtomicLong priority = new AtomicLong();

    // Queue of pending requests (ignored when batching is inactive)
    volatile private PendingContainer<byte[]> pending = Utils.TXT_SHUFFLE ? new ShuffledPendingContainer<>() : new VanillaPendingContainer();

    // The priority queues (one per replica)
    volatile private Map<Integer, PriorityQueue> queues = new HashMap<>();

    // A map of validated consistent broadcast instances by pid
    volatile private Map<BcPid, IVBroadcast> bcInstances = new ConcurrentHashMap<>();

    // A map of binary agreement instances by pid
    volatile private Map<BaPid, IBinaryAgreement> baInstances = new ConcurrentHashMap<>();

    // The current number of active broadcast instances
    volatile private AtomicInteger broadcastState;

    // The current agreement state
    volatile private AgreementState agreementState;

    // The set of executed commands
    volatile private Set<Transaction> executed = new HashSet<>();

    // History of VCBCs and their result
    private Map<BcPid, ExecutionLog<byte[]>> bcLog = new ConcurrentHashMap<>();

    // History of ABAs and their result
    private Map<BaPid, ExecutionLog<Boolean>> baLog = new ConcurrentHashMap<>();

    private ReentrantLock agreementStateLock = new ReentrantLock();
    private AtomicInteger waitingForLock = new AtomicInteger(0);

    private Map<BcPid, Long> bcStarts = new ConcurrentHashMap<>();
    private Map<BaPid, Long> baStarts = new ConcurrentHashMap<>();

    private Double vcbcAverageDuration = 0D;
    private Double baAverageDuration = 0D;

    public Alea(Integer replicaId, NetworkInfo networkInfo, Params params) {
        this.replicaId = replicaId;
        this.networkInfo = networkInfo;
        this.params = params;

        this.sigUtils = networkInfo.getSigUtils();

        // Initialize the priority queues
        for (int id=0; id<networkInfo.getN(); id++) this.queues.put(id, new PriorityQueue(id));

        this.broadcastState = new AtomicInteger(0);
        this.agreementState = new ProposingState(this, 0L);
    }

    @Override
    public Integer getReplicaId() {
        return replicaId;
    }

    @Override
    public NetworkInfo getNetworkInfo() {
        return networkInfo;
    }

    public Params getParams() {
        return params;
    }

    @Override
    public Long getPriority() {
        return priority.get();
    }

    @Override
    public Map<Integer, PriorityQueue> getQueues() {
        return queues;
    }

    @Override
    public IVBroadcast getVBroadcastInstance(BcPid bcPid) {
        this.bcLog.computeIfAbsent(bcPid, p -> new ExecutionLog<>(p.toString()));
        this.bcStarts.computeIfAbsent(bcPid,p -> {
            Utils.bcStart(bcPid.toString());
            return System.nanoTime();
        });

        return this.bcInstances.computeIfAbsent(bcPid,
                p -> new EchoVBroadcast2(
                    p.toString(),
                    replicaId,
                    networkInfo,
                    p.getQueueId()
        ));
    }

    @Override
    public IBinaryAgreement getBinaryAgreementInstance(BaPid baPid) {
        this.baLog.computeIfAbsent(baPid, p -> new ExecutionLog<>(p.toString()));
        this.baStarts.computeIfAbsent(baPid, p -> {
            Utils.baStart(baPid.toString());
            return System.nanoTime();
        });
        return this.baInstances.computeIfAbsent(baPid,
                p -> new MoustefaouiBinaryAgreement(
                        p.toString(),
                        replicaId,
                        networkInfo
                )
        );
    }

    @Override
    public Set<Transaction> getExecuted() {
        return executed;
    }

    public void addExecuted(byte[] value) {
        executed.add(new Transaction(value));
    }

    public Step<Block> setAgreementState(AgreementState agreementState) {
        this.agreementState = agreementState;
        Step<Block> step = this.agreementState.tryProgress();
        return step;
    }


    @Override
    public Step<Block> handleInput(byte[] input) {
        // add input to queue of pending requests
        this.pending.put(input);

        Step<Block> step = this.tryPropose();
        return step;
    }

    @Override
    public Step<Block> handleMessage(ProtocolMessage message) {
        if (params.getFault(replicaId) == Params.Fault.CRASH) return new Step<>();

        Step<Block> step;

        if (message instanceof VBroadcastMessage) {
            // Route a verifiable broadcast message
            step = this.handleVBroadcastMessage((VBroadcastMessage) message);
        } else if (message instanceof BinaryAgreementMessage) {
            // Route a binary agreement message
            step = this.handleBinaryAgreementMessage((BinaryAgreementMessage) message);
        } else {
            // Route sub-protocol messages
            step = this.handleProtocolMessage(message);
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

    @Override
    public Step<Block> handleVBroadcastMessage(VBroadcastMessage message) {
        BcPid bcPid = new BcPid(message.getPid());
        IVBroadcast instance = this.getVBroadcastInstance(bcPid);
        Step<VOutput> bsStep = instance.handleMessage(message);
        Step<Block> step = this.handleVBroadcastStep(bsStep, bcPid);
        return step;
    }

    @Override
    public Step<Block> handleBinaryAgreementMessage(BinaryAgreementMessage message){
        BaPid baPid = new BaPid(message.getPid());
        IBinaryAgreement instance = this.getBinaryAgreementInstance(baPid);
        Step<Boolean> baStep = instance.handleMessage(message);
        return this.handleBinaryAgreementStep(baStep, baPid);
    }

    @Override
    public Step<Block> handleProtocolMessage(ProtocolMessage message) {
        switch (message.getType()) {
            case FillGapMessage.FILL_GAP: {
                return handleFillGapMessage((FillGapMessage) message);
            }
            case FillerMessage.FILLER: {
                return handleFillerMessage((FillerMessage) message);
            }
            default: {
                return new Step<>();
            }
        }
    }

    @Override
    public Step<Block> handleFillGapMessage(FillGapMessage fillGapMessage) {
        Step<Block> step = new Step<>();

        final Integer senderId = fillGapMessage.getSender();
        final Integer queueId = fillGapMessage.getQueue();
        final Long slotId = fillGapMessage.getSlot();

        PriorityQueue queue = this.getQueues().get(queueId);
        Optional<Slot> optionalSlot = queue.get(slotId);

        if (optionalSlot.isPresent()) {
            Slot slot = optionalSlot.get();
            FillerMessage fillerMessage = new FillerMessage(
                    "ABC", replicaId, queueId, slotId, slot.getValue(), slot.getProof());
            step.add(fillerMessage, senderId);
        }

        return step;
    }

    @Override
    public Step<Block> handleFillerMessage(FillerMessage fillerMessage) {
        Step<Block> step = new Step<>();

        final Integer queueId = fillerMessage.getQueue();
        final Long slotId = fillerMessage.getSlot();
        final byte[] value = fillerMessage.getValue();
        final byte[] proof = fillerMessage.getProof();

        // Check if already contains the value
        PriorityQueue queue = this.getQueues().get(queueId);
        if (queue.get(slotId).isPresent()) {
            step.addFault("ABC", "UNEXPECTED FILLER MESSAGE");
            return step;
        }

        // Verify the threshold signature proof
        String toVerify = String.format("BC-%d-%d-%s", queueId, slotId, Base64.getEncoder().encodeToString(value));
        boolean valid = sigUtils.verifyFull(toVerify.getBytes(), Signature.fromBytes(proof));
        if (!params.isBenchmark() && !valid) {
            step.addFault("ABC", "INVALID PROOF");

            return step;
        }

        // Place the (value, proof) pair in the corresponding queue slot
        queue.enqueue(slotId, value, proof);

        step = this.tryProgress();
        return step;
    }

    @Override
    public Step<Block> handleVBroadcastStep(Step<VOutput> bcStep, BcPid bcPid) {

        Step<Block> step = new Step<>(bcStep.getMessages());
        // If broadcast produced no output, then don't move on
        if (bcStep.getOutput().isEmpty()) {
            return step;
        }

        // add pre-ordered request to the corresponding priority queue
        PriorityQueue queue = queues.get(bcPid.getQueueId());
        VOutput output = bcStep.getOutput().firstElement();
        queue.enqueue(bcPid.getSlotId(), output.getValue(), output.getSignature());

        this.bcLog.get(bcPid).setResult(output.getValue());

        synchronized (this.queues) {
            if (this.executed.contains(new Transaction(output.getValue()))) {
                queue.dequeue(bcPid.getSlotId());
            }
        }

        // remove VCBC instance
        this.bcInstances.remove(bcPid);
        long start = this.bcStarts.remove(bcPid);
        updateBcAverage(System.nanoTime() - start);
        Utils.bcEnd(bcPid.toString(), String.format("p-%s-%s", bcPid.getQueueId(), bcPid.getSlotId()));

        if (bcPid.getQueueId().equals(replicaId)) {
            // decrease the number of active broadcast instances
            int bcActive = this.broadcastState.decrementAndGet();
            if (bcActive < this.params.getMaxConcurrentBroadcasts()) {
                step.add(this.tryPropose());
            }
        }

        // Move to ABA
        step.add(this.tryProgress());

        return step;
    }

    @Override
    public Step<Block> handleBinaryAgreementStep(Step<Boolean> baStep, BaPid baPid) {
        Step<Block> step = new Step<>(baStep.getMessages());

        // set finish time for duration benchmark
        if (!baStep.getOutput().isEmpty()) {
            Long l = baStarts.remove(baPid);
            if (l != null) {
                updateBaAverage(System.nanoTime() - l);
                this.baLog.get(baPid).setResult(baStep.getOutput().firstElement());
            }
        }

        step.add(this.tryProgress());

        return step;
    }

    /**
     * Try to propose a value to be included in PriorityQueues
     * @return
     */
    private Step<Block> tryPropose() {

        List<byte[]> entries;

        synchronized (this.pending) {
            // do not propose if there is nothing to propose
            if (this.pending.isEmpty()) {
                return new Step<>();
            }

            // do not propose if the number of concurrent VCBC instances exceeds the maximum value in params
            if (this.broadcastState.get() >= this.params.getMaxConcurrentBroadcasts()) {
                return new Step<>();
            }

            // do not propose if the pipeline offset exceed the maximum value in the params
            final long pipelineOffset = this.priority.get() - this.getQueues().get(replicaId).getHead();
            if (pipelineOffset >= params.getMaxPipelineOffset()) {
                return new Step<>();
            }

            // group pending entries into a byte encoded batch
            entries = new ArrayList<>();

            // Estimate of when the aba with current replica as the leader happens = now + missing rounds * average duration
            int missing = (replicaId - agreementState.getLeader()) % networkInfo.getN() - 1;
            double timeToNextBA = missing * getAverageBADuration();
            // if I'm not past the deadline I'll keep collecting to the batch, otherwise, go as it is
            boolean pastDeadline = timeToNextBA <= getAverageVCBCDuration() * 2;

            // if DELAY_ABA is deactivated, I'm always past the deadline
            pastDeadline = pastDeadline || !Utils.DELAY_ABA;

            // select entries from pending queue
            // if (this.pending.size() < params.getBatchSize() && !pastDeadline ) {
            if (this.pending.size() < params.getBatchSize()) {
                return new Step<>();
            }

            for (int i=0; i < params.getBatchSize() && this.pending.size() > 0; i++) {
                byte[] input = this.pending.get();
                entries.add(input);
            }

            // System.out.printf("The batch size is %s\n", entries.size());

            if (entries.isEmpty()) {
                return new Step<>();
            }

            // increment the concurrent VCBC instances counter
            broadcastState.incrementAndGet();
        }

        byte[] batch = encodeBatchEntries(entries);

        // input batch to VCBC
        BcPid bcPid = new BcPid("VCBC", replicaId, priority.getAndIncrement());
        Utils.batchAllocate(bcPid.toString(), entries);
        IVBroadcast instance = this.getVBroadcastInstance(bcPid);
        Step<VOutput> bcStep = instance.handleInput(batch);

        Step<Block> step = this.handleVBroadcastStep(bcStep, bcPid);
        return step;
    }

    private Step<Block> tryProgress() {
        Step<Block> step = new Step<>();

        if (!agreementState.canProgress()) {
            return new Step<>();
        }

        /**
         * O call agreementState.tryProgress I must have a lock. Having only the lock works, but it's not very efficient.
         * All threads waiting for the lock will do the same thing, so if there's a thread waiting for the lock I don't
         * really need to do it to. I can't just do tryLock, because having someone running the tryProgress it's not the
         * same as me running it from the beginning. As far as I know, not primitive exists that checks this, so the
         * waitingForLock variable it's a hack that should do the trick most of the time.
         */
        if (waitingForLock.get() == 0) {
            waitingForLock.incrementAndGet();
            agreementStateLock.lock();
            waitingForLock.decrementAndGet();
            if (waitingForLock.get() == 0) {
                step = this.agreementState.tryProgress();
            }
            agreementStateLock.unlock();
        }

        if (!step.getOutput().isEmpty()) {
            step.add(this.tryPropose().getMessages());
        }

        return step;
    }

    // Parameters controlling Alea's behavior and performance.
    public static class Params {

        public enum Fault {
            FREE,
            CRASH,
            BYZANTINE
        }

        // batch size
        private Integer batchSize;

        // maximum number of VCBC concurrent instances
        private Integer maxConcurrentBroadcasts;

        // maximum offset between broadcast and agreement components
        private Integer maxPipelineOffset;

        private Map<Integer, Fault> faults;

        private boolean benchmark;

        private Integer maxPayloadSize;

        public static class Builder {
            private int batchSize = 8;
            private int maxConcurrentBroadcasts = 3;
            // private int maxConcurrentBroadcasts = 1;
            private int maxPipelineOffset = 5;
            private Map<Integer, Fault> faults = new HashMap<>();
            private boolean benchmark = false;
            private int maxPayloadSize = 250;

            public Builder() {}

            public Builder batchSize(int batchSize) {
                this.batchSize = batchSize;
                return this;
            }

            public Builder maxConcurrentBroadcasts(int maxConcurrentBroadcasts) {
                this.maxConcurrentBroadcasts = maxConcurrentBroadcasts;
                return this;
            }

            public Builder maxPipelineOffset(int maxPipelineOffset) {
                this.maxPipelineOffset = maxPipelineOffset;
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
                params.maxConcurrentBroadcasts = this.maxConcurrentBroadcasts;
                params.maxPipelineOffset = this.maxPipelineOffset;
                params.faults = this.faults;
                params.benchmark = this.benchmark;
                params.maxPayloadSize = this.maxPayloadSize;

                return params;
            }
        }

        private Params() {
        }

        public Integer getBatchSize() {
            return batchSize;
        }

        public Integer getMaxConcurrentBroadcasts() {
            return maxConcurrentBroadcasts;
        }

        public Integer getMaxPipelineOffset() {
            return maxPipelineOffset;
        }

        public Integer getMaxPayloadSize() {
            return maxPayloadSize;
        }

        public Fault getFault(Integer replicaId) {
            return faults.getOrDefault(replicaId, Fault.FREE);
        }

        public boolean isBenchmark() {
            return this.benchmark;
        }
    }

    public static byte[] encodeBatchEntries(Collection<byte[]> entries) {
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


        byte[] encoded = baos.toByteArray();

        return encoded;
    }

    public static Collection<byte[]> decodeBatchEntries(byte[] encoded) {
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

    public String printState() {
        StringBuilder sb = new StringBuilder();

        // add queue headers
        for (Integer replicaId: this.networkInfo.getAllIds()) {
            sb.append(String.format("| Q_%d |", replicaId));
        }
        sb.append("\n");

        Collection<Long> priorities = this.getQueues().values().stream().map(PriorityQueue::getHead).collect(Collectors.toList());
        long minPriority = Collections.min(priorities);
        // long minPriority = Math.max(Collections.min(priorities)-3, 0L);
        // long maxPriority = Collections.max(priorities);
        long maxPriority = Math.max(Collections.max(priorities)+3, minPriority+3);;

        // add queue bodies
        for (long i=0; i<(maxPriority - minPriority); i++) {
            final long p = minPriority + i;
            for (Integer replicaId: this.networkInfo.getAllIds()) {
                PriorityQueue pq = this.getQueues().get(replicaId);
                Optional<Slot> slot = pq.get(p);
                sb.append(p == pq.getHead() ? "|>" : "| ");
                sb.append(slot.isPresent() ? "[X] |" : "[ ] |");

            }
            sb.append(String.format(" (%d) \n", p));
        }

        // add round pointer
        for (long i=0; i<this.agreementState.getQueue().getId(); i++) sb.append("       ");
        sb.append("   *\n\n");

        return sb.toString();
    }

    public void reset() {
        broadcastState = new AtomicInteger(0);
        agreementState = new ProposingState(this, 0L);
        priority = new AtomicLong();
        pending = Utils.TXT_SHUFFLE ? new ShuffledPendingContainer<>() : new VanillaPendingContainer();
        bcInstances = new ConcurrentHashMap<>();
        baInstances = new ConcurrentHashMap<>();
        executed = new HashSet<>();
        bcLog = new ConcurrentHashMap<>();
        baLog = new ConcurrentHashMap<>();
        bcStarts = new ConcurrentHashMap<>();
        baStarts = new ConcurrentHashMap<>();

        for (int id=0; id<networkInfo.getN(); id++) {
            this.queues.put(id, new PriorityQueue(id));
        }
    }

    public void stop() {
//        this.summaryLogger.halt();
    }

    public List<String> getNonEmptyQueues() {
        return getQueues().values().stream().filter(q -> q.peek().isPresent()).map(q -> q.getId().toString()).collect(Collectors.toList());
    }

    public boolean broadcastOngoing(int queueId, long slotId) {
        BcPid bcPid = new BcPid("VCBC", queueId, slotId);
        return bcInstances.containsKey(bcPid);
    }

    public synchronized void updateBcAverage(long duration) {
        double alpha = 0.2;
        vcbcAverageDuration = vcbcAverageDuration * (1-alpha) + duration * alpha;
    }

    public synchronized void updateBaAverage(long duration) {
        double alpha = 0.2;
        baAverageDuration = baAverageDuration * (1-alpha) + duration * alpha;
    }

    public synchronized long getAverageVCBCDuration() {
        return Math.round(vcbcAverageDuration);
    }

    public synchronized long getAverageBADuration() {
        return Math.round(baAverageDuration);
    }
}
