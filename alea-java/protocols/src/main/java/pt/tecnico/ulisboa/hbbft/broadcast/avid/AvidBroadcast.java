package pt.tecnico.ulisboa.hbbft.broadcast.avid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.tecnico.ulisboa.hbbft.NetworkInfo;
import pt.tecnico.ulisboa.hbbft.Step;
import pt.tecnico.ulisboa.hbbft.broadcast.BroadcastMessage;
import pt.tecnico.ulisboa.hbbft.broadcast.IBroadcast;
import pt.tecnico.ulisboa.hbbft.broadcast.avid.crypto.merkle.MerkleTree;
import pt.tecnico.ulisboa.hbbft.broadcast.avid.crypto.merkle.Proof;
import pt.tecnico.ulisboa.hbbft.broadcast.avid.crypto.reedsolomon.ReedSolomon;
import pt.tecnico.ulisboa.hbbft.broadcast.avid.messages.*;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.Collectors;

public class    AvidBroadcast implements IBroadcast {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // The protocol instance identifier.
    private final String pid;

    // The replica id.
    private final Integer replicaId;

    // The network configuration.
    private final NetworkInfo networkInfo;

    // The ID of the sending node.
    private  final Integer proposerId;

    // The Reed-Solomon erasure coding configuration.
    private Coding coding;

    // If we are the proposer: whether we have already sent the `Value` messages with the shards.
    private Boolean valueSent = false;

    // Whether we have already sent `Echo` to all nodes who haven't sent `CanDecode`.
    private Boolean echoSent = false;

    // Whether we have already multicast `Ready`.
    private Boolean readySent = false;

    // The `Echo` messages received, by sender ID.
    private Map<Integer, EchoMessage> echos = new TreeMap<>();

    // The `Ready` messages received, by sender ID.
    private Map<Integer, ReadyMessage> readies = new TreeMap<>();

    // Whether we have already output a value.
    private Boolean decided = false;

    // The value to output when ready.
    private byte[] decidedValue;

    private final AvidBroadcastMessageFactory messageFactory;

    public AvidBroadcast(
            String pid,
            Integer replicaId,
            NetworkInfo networkInfo,
            Integer proposerId
    ) {
        this.pid = pid;
        this.replicaId = replicaId;
        this.proposerId = proposerId;
        this.networkInfo = networkInfo;

        int parityShardCount = 2*networkInfo.getF();
        int dataShardCount = networkInfo.getN() - parityShardCount;
        this.coding = new Coding(parityShardCount, dataShardCount);

        this.messageFactory = new AvidBroadcastMessageFactory(pid, replicaId);
    }

    @Override
    public String getPid() {
        return this.pid;
    }

    @Override
    public Step<byte[]> handleInput(byte[] input) {
        Step<byte[]> step = new Step<>();
        if (!this.replicaId.equals(this.proposerId) || this.valueSent) {
            return step;
        }
        this.valueSent = true;

        // Split the value into chunks/shards, encode them with erasure codes.
        byte[][] shards = getComputeShards(input);
        // Assemble a Merkle tree from data and parity shards.
        MerkleTree merkleTree = new MerkleTree(Arrays.asList(shards));
        // Take all proofs from this tree and send them, each to its own node
        for (int id=0; id < this.networkInfo.getN(); id++) {
            Proof proof = merkleTree.getProof(id).orElseThrow();
            ValueMessage valueMessage = messageFactory.createValueMessage(proof);
            step.add(this.sendMessage(valueMessage, id));
        }
        return step;
    }

    @Override
    public Step<byte[]> handleMessage(BroadcastMessage message) {
        // TODO check if BID matches this instance
        Integer type = message.getType();
        switch (type) {
            case ValueMessage.VALUE: {
                return handleValueMessage((ValueMessage) message);
            }
            case EchoMessage.ECHO: {
                return handleEchoMessage((EchoMessage) message);
            }
            case ReadyMessage.READY: {
                return handleReadyMessage((ReadyMessage) message);
            }
            default:
                return new Step<>();
                //throw new IllegalStateException("Unexpected value: " + type);
        }
    }

    @Override
    public boolean hasTerminated() {
        return this.decided;
    }

    @Override
    public Optional<byte[]> deliver() {
        if (this.decided) return Optional.of(this.decidedValue);
        else return Optional.empty();
    }

    /**
     * Called by the replica to indicate that
     * a {@link ValueMessage} has been received.
     *
     * @param valueMessage the received message
     */
    private Step<byte[]> handleValueMessage(ValueMessage valueMessage) {
        Step<byte[]> step = new Step<>();
        if (!valueMessage.getSender().equals(this.proposerId)) {
            System.out.println("NOT PROPOSER");
            return step;
        }
        if (this.echoSent) {
            System.out.println("ECHO ALREADY SENT");
            return step;
        }

        // If the proof is invalid, log the faulty node behavior and ignore.
        final byte[] value = valueMessage.getValue();
        final List<byte[]> branch = valueMessage.getBranch();
        final byte[] root = valueMessage.getRoot();
        Proof proof = new Proof(value, this.replicaId, branch, root);
        if (!(proof.getIndex().equals(this.replicaId) && proof.validate(this.networkInfo.getN()))) {
            System.out.println("INVALID PROOF");
            return step;
        }

        // Send the proof in an `Echo` message to all nodes.
        EchoMessage echoMessage = messageFactory.createEchoMessage(proof);
        this.echoSent = true;
        step.add(this.sendMessage(echoMessage));
        return step;

    }

    private Step<byte[]> sendMessage(ValueMessage valueMessage, Integer targetId) {
        Step<byte[]> step = new Step<>();
        if (targetId.equals(this.replicaId)) {
            step.add(this.handleValueMessage(valueMessage));
        } else {
            step.add(valueMessage, targetId);
        }
        return step;
    }

    /**
     * Called by the replica to indicate that
     * a {@link EchoMessage} has been received.
     *
     * @param echoMessage the received message
     */
    private Step<byte[]> handleEchoMessage(EchoMessage echoMessage) {
        Step<byte[]> step = new Step<>();
        // If the sender has already sent `Echo`, ignore.
        final int senderId = echoMessage.getSender();
        if (this.echos.containsKey(senderId)) {
            System.out.println(String.format("%s - ECHO ALREADY RECEIVED", pid));
            return step;
        }

        // If the proof is invalid, log the faulty-node behavior, and ignore.
        final byte[] value = echoMessage.getValue();
        final List<byte[]> branch = echoMessage.getBranch();
        final byte[] root = echoMessage.getRoot();
        Proof proof = new Proof(value, senderId, branch, root);
        if (!(proof.getIndex().equals(senderId) && proof.validate(this.networkInfo.getN()))) {
            System.out.println("INVALID PROOF");
            return step;
        }

        // Save the proof for reconstructing the tree later.
        this.echos.put(senderId, echoMessage);

        // Upon receiving `N - 2f` `Echo`s with the same root hash
        final List<EchoMessage> validEchos = this.echos.values().stream()
                .filter(e -> Arrays.equals(e.getRoot(), root)).collect(Collectors.toList());
        if (validEchos.size() == (networkInfo.getN() - 2*networkInfo.getF())) {
            // Interpolate the shards from the leaves received
            final int totalNumShards = coding.getDataShardCount() + coding.getParityShardCount();
            byte[][] shards = new byte[totalNumShards][];
            for (EchoMessage em: validEchos) shards[em.getSender()] = em.getValue();
            shards = recomputeShards(shards);

            // Recompute the merkle tree
            MerkleTree merkleTree = new MerkleTree(Arrays.asList(shards));

            // Check if the roots match
            if (!Arrays.equals(merkleTree.getRootHash(), root)) {
                System.out.println(String.format("%d - INVALID MERKLE TREE", replicaId));
                return step;
            }

            // If `Ready` has not yet been sent, multicast Ready(h).
            if (!this.readySent) {
                this.readySent = true;
                ReadyMessage readyMessage = this.messageFactory.createReadyMessage(proof);
                step.add(this.sendMessage(readyMessage));
            }
        }

        step.add(this.tryOutput(root));
        return step;
    }

    private Step<byte[]> sendMessage(EchoMessage echoMessage) {
        Step<byte[]> step = new Step<>();
        for (int id=0; id<this.networkInfo.getN(); id++) {
            if (id == this.replicaId) {
                step.add(this.handleEchoMessage(echoMessage));
            } else {
                step.add(echoMessage, id);
            }
        }
        return step;
    }

    /**
     * Called by the replica to indicate that
     * a {@link ReadyMessage} has been received.
     *
     * @param readyMessage the received message
     */
    private Step<byte[]> handleReadyMessage(ReadyMessage readyMessage) {
        Step<byte[]> step = new Step<>();
        // If the sender has already sent a `Ready` before, ignore.
        final int senderId = readyMessage.getSender();
        if (this.readies.containsKey(senderId)) {
            return step;
        }

        // Save the message received
        this.readies.put(senderId, readyMessage);

        // Collect all `Ready` messages matching the received root hash
        final byte[] root = readyMessage.getRoot();
        final List<ReadyMessage> validReadies = this.readies.values().stream()
                .filter(e -> Arrays.equals(e.getRoot(), root)).collect(Collectors.toList());

        // Upon receiving `f + 1` matching `Ready` messages
        if (validReadies.size() == (networkInfo.getF() + 1)) {
            // If `Ready` has not yet been sent, multicast Ready(h).
            if (!this.readySent) {
                this.readySent = true;
                step.add(this.sendMessage(this.messageFactory.createReadyMessage(root)));
            }
        }

        step.add(this.tryOutput(root));
        return step;
    }

    private Step<byte[]> sendMessage(ReadyMessage readyMessage) {
        Step<byte[]> step = new Step<>();
        for (int id=0; id<this.networkInfo.getN(); id++) {
            if (id == this.replicaId) {
                step.add(this.handleReadyMessage(readyMessage));
            } else {
                step.add(readyMessage, id);
            }
        }
        return step;
    }

    private Step<byte[]> tryOutput(byte[] root) {
        Step<byte[]> step = new Step<>();
        if (decided) {
            return step;
        }

        final List<ReadyMessage> validReadies = this.readies.values().stream()
                .filter(e -> Arrays.equals(e.getRoot(), root)).collect(Collectors.toList());
        if (validReadies.size() < (networkInfo.getF() + 1)) {
            return step;
        }

        // Upon receiving `N - 2f` `Echo`s with the same root hash
        final List<EchoMessage> validEchos = this.echos.values().stream()
                .filter(e -> Arrays.equals(e.getRoot(), root)).collect(Collectors.toList());
        if (validEchos.size() >= (networkInfo.getN() - 2*networkInfo.getF())) {
            // Try to decode the broadcast value
            final int totalNumShards = coding.getDataShardCount() + coding.getParityShardCount();
            final int shardSize = validEchos.get(0).getValue().length;
            byte[][] shards = new byte[totalNumShards][];
            for (EchoMessage em: validEchos) shards[em.getSender()] = em.getValue();
            shards = recomputeShards(shards);

            // Combine the data shards into one buffer for convenience.
            // (This is not efficient, but it is convenient.)
            byte[] allBytes = new byte [shardSize * coding.getDataShardCount()];
            for (int i = 0; i < coding.getDataShardCount(); i++) {
                System.arraycopy(shards[i], 0, allBytes, shardSize * i, shardSize);
            }

            this.decided = true;
            this.decidedValue = allBytes;
            step.add(decidedValue);
        }
        return step;
    }

    public byte[][] getComputeShards(byte[] value) {
        final int dataShardCount = this.coding.getDataShardCount();
        final int parityShardCount = this.coding.getParityShardCount();

        // Get the size of the input value
        final int valueSize = value.length;

        // Calculate the size of each shard
        final int payloadSize = Integer.BYTES + valueSize;
        final int shardSize = (payloadSize + dataShardCount - 1) / dataShardCount;

        // Create a buffer holding the input size followed by the contents
        final int bufferSize = shardSize * dataShardCount;
        final byte[] allBytes = new byte[bufferSize];
        ByteBuffer.wrap(allBytes).putInt(valueSize).put(value);

        // Make the buffers to hold the shards.
        byte[][] shards = new byte[dataShardCount + parityShardCount][shardSize];

        // Fill in the data shards
        for (int i = 0; i < dataShardCount; i++) {
            System.arraycopy(allBytes, i * shardSize, shards[i], 0, shardSize);
        }

        // Use Reed-Solomon to calculate the parity.
        this.coding.encode(shards);
        return shards;
    }

    public byte[][] recomputeShards(byte[][] shards) {
        final int dataShardCount = this.coding.getDataShardCount();
        final int parityShardCount = this.coding.getParityShardCount();
        final int totalShards = dataShardCount + parityShardCount;

        final boolean[] shardPresent = new boolean[totalShards];
        int shardSize = 0;
        int shardCount = 0;
        for (int i = 0; i < totalShards; i++) {
            if (shards[i] != null) {
                shardSize = shards[i].length;
                shardPresent[i] = true;
                shardCount += 1;
            }
        }

        // We need at least DATA_SHARDS to be able to reconstruct the file.
        if (shardCount < dataShardCount) {
            System.out.println("Not enough shards present");
            return null; // FIXME
        }

        // Make empty buffers for the missing shards.
        for (int i = 0; i < totalShards; i++) {
            if (!shardPresent[i]) {
                shards[i] = new byte [shardSize];
            }
        }

        // Use Reed-Solomon to fill in the missing shards
        ReedSolomon reedSolomon = ReedSolomon.create(dataShardCount, parityShardCount);
        reedSolomon.decodeMissing(shards, shardPresent, 0, shardSize);
        return shards;
    }
}
