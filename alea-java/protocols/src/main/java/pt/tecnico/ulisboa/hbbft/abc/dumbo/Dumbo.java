package pt.tecnico.ulisboa.hbbft.abc.dumbo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.tecnico.ulisboa.hbbft.NetworkInfo;
import pt.tecnico.ulisboa.hbbft.ProtocolMessage;
import pt.tecnico.ulisboa.hbbft.Step;
import pt.tecnico.ulisboa.hbbft.abc.Block;
import pt.tecnico.ulisboa.hbbft.abc.honeybadger.TransactionQueue;
import pt.tecnico.ulisboa.hbbft.subset.Subset;
import pt.tecnico.ulisboa.hbbft.subset.SubsetFactory;
import pt.tecnico.ulisboa.hbbft.subset.dumbo.DumboSubsetFactory;

import java.io.*;
import java.util.*;

public class Dumbo implements IDumbo {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // Our ID.
    private final Integer replicaId;

    // The network configuration.
    private final NetworkInfo networkInfo;

    // Parameters controlling Dumbo's behavior and performance.
    private final Params params;

    // TODO hack to support dumbo2
    // private final DumboSubsetFactory acsFactory;
    private final SubsetFactory acsFactory;

    // The earliest epoch from which we have not yet received output.
    private Long epoch = 0L;

    // Whether we have already submitted a proposal for the current epoch.
    private Boolean hasInput = false;

    private Map<Long, Epoch> epochs = new TreeMap<>();

    private TransactionQueue txQueue;

    public Dumbo(
            Integer replicaId,
            NetworkInfo networkInfo,
            Params params,
            SubsetFactory acsFactory
    ) {
        this.replicaId = replicaId;
        this.networkInfo = networkInfo;
        this.params = params;
        this.acsFactory = acsFactory;

        int bSize = params.getBatchSize();
        int pSize = bSize/networkInfo.getN();
        this.txQueue = new TransactionQueue(bSize, pSize);
    }

    @Override
    public Step<Block> handleInput(byte[] input) {
        //logger.info("handleInput - input:{}", input);
        this.txQueue.add(input);
        return this.tryPropose();
    }

    @Override
    public Step<Block> handleMessage(ProtocolMessage message) {
        //logger.info("handleMessage - msg:{}", message);
        if (message instanceof DumboMessage) {
            return this.handleDumboMessage((DumboMessage) message);
        }
        return new Step<>();
    }

    @Override
    public boolean hasTerminated() {
        return false;
    }

    @Override
    public Optional<Block> deliver() {
        return Optional.empty();
    }

    public Step<Block> handleDumboMessage(DumboMessage message) {
        Step<Block> step = new Step<>();
        final long epochId = message.getEpoch();

        if (epochId < this.epoch) {
            // Ignore old messages
            step.addFault(message.getPid(), "OLD MESSAGE");
        }

        else if (epochId > this.epoch + this.params.getMaxFutureEpochs()) {
            // Ignore messages out of range
            step.addFault(message.getPid(), "MESSAGE OUT OF EPOCH RANGE");
        }

        else {
            Epoch epoch = this.getEpoch(epochId);
            Step<Subset> epochStep = epoch.handleMessage(message.getContent());
            step.add(this.handleEpochStep(epochStep));
        }

        return step;
    }

    private Step<Block> tryPropose() {
        if (this.hasInput) return new Step<>();
        this.hasInput = true;

        Collection<byte[]> proposal = this.txQueue.get();
        byte[] encoded = this.encodeBatchEntries(proposal);

        Epoch epoch = this.getEpoch(this.epoch);
        Step<Subset> epochStep = epoch.handleInput(encoded);

        return this.handleEpochStep(epochStep);
    }

    private Step<Block> handleEpochStep(Step<Subset> epochStep) {
        Step<Block> step = new Step<>(epochStep.getMessages());

        Epoch epoch = this.getEpoch(this.epoch);
        if (!epoch.hasTerminated()) {
            return step;
        }

        Subset subset = epoch.deliver().orElseThrow();

        Set<byte[]> blockContents = new HashSet<>();
        for (byte[] contribution: subset.getEntries().values())
            blockContents.addAll(decodeBatchEntries(contribution));
        this.txQueue.removeAll(blockContents);

        Block block = new Block(epoch.getEpochId(), blockContents);
        step.add(block);

        this.updateEpoch();
        step.add(this.tryPropose());

        step.add(this.handleEpochStep(new Step<>()));
        return step;
    }

    private void updateEpoch() {
        this.epochs.remove(epoch);
        this.epoch += 1;
        this.hasInput = false;
    }

    private Epoch getEpoch(Long epochId) {
        return epochs.computeIfAbsent(epochId,
                eid -> new Epoch(
                        eid,
                        acsFactory.create("ACS-" + eid),
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
        this.txQueue = new TransactionQueue(bSize, pSize);
    }
}
