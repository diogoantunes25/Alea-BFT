package pt.tecnico.ulisboa.hbbft.broadcast.bracha2;

import pt.tecnico.ulisboa.hbbft.NetworkInfo;
import pt.tecnico.ulisboa.hbbft.Step;
import pt.tecnico.ulisboa.hbbft.broadcast.BroadcastMessage;
import pt.tecnico.ulisboa.hbbft.broadcast.IBroadcast;
import pt.tecnico.ulisboa.hbbft.broadcast.bracha2.erasureCodes.ErasureCodesUtils;
import pt.tecnico.ulisboa.hbbft.broadcast.bracha2.merkleTree.MerkleTree;
import pt.tecnico.ulisboa.hbbft.broadcast.bracha2.messages.EchoMessage;
import pt.tecnico.ulisboa.hbbft.broadcast.bracha2.messages.ReadyMessage;
import pt.tecnico.ulisboa.hbbft.broadcast.bracha2.messages.SendMessage;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class BrachaBroadcast2 implements IBroadcast {

    // The protocol instance identifier.
    private final String pid;

    // The replica id.
    private final Integer replicaId;

    // The network configuration.
    private final NetworkInfo networkInfo;

    // The ID of the sending node.
    private  final Integer proposerId;

    // If we are the proposer: whether we have already sent the `Send` message.
    volatile private AtomicBoolean sendSent = new AtomicBoolean(false);

    // Whether we have already multicast `Echo`.
    volatile private AtomicBoolean echoSent = new AtomicBoolean(false);

    // Whether we have already multicast `Ready`.
    volatile private AtomicBoolean readySent = new AtomicBoolean(false);

    // The `Echo` messages received, by sender ID.
    volatile private Map<Integer, EchoMessage> echos = new ConcurrentHashMap<>();

    // The `Ready` messages received, by sender ID.
    volatile private Map<Integer, ReadyMessage> readies = new ConcurrentHashMap<>();

    // Whether we have already output a value.
    volatile private AtomicBoolean decided = new AtomicBoolean(false);

    volatile private Map<Long, byte[]> knownHashes = new ConcurrentHashMap();
    volatile private Map<Long, Map<Integer, byte[]>> blocksReceived = new ConcurrentHashMap<>();

    // The value to output when ready.
    private byte[] decidedValue;

    private Map<Long, Integer> sizes = new ConcurrentHashMap<>();

    private final BrachaBroadcast2MessageFactory messageFactory;

    private ErasureCodesUtils erasureUtils = null;

    // TODO: actually not needed, just check if erasureUtils is not null
    private int inputSize = -1;

    private int n;
    private int f;
    public BrachaBroadcast2(
            String pid,
            Integer replicaId,
            NetworkInfo networkInfo,
            Integer proposerId
    ) {
        this.pid = pid;
        this.replicaId = replicaId;
        this.networkInfo = networkInfo;
        this.proposerId = proposerId;
        this.messageFactory = new BrachaBroadcast2MessageFactory(pid, replicaId);
        n = networkInfo.getN();
        f = networkInfo.getF();
    }

    @Override
    public String getPid() {
        return this.pid;
    }

    @Override
    public Step<byte[]> handleInput(byte[] input) {
            if (inputSize == -1) {
                this.erasureUtils = new ErasureCodesUtils(n - 2 * f, 2 * f, input.length);
                inputSize = input.length;
            }

        Step<byte[]> step = new Step<>();
        if (!this.replicaId.equals(proposerId) || this.sendSent.getAndSet(true)) {
            return step;
        }

        List<byte[]> stripes = erasureUtils.encode(input);

        MerkleTree mt = new MerkleTree(stripes);

        for (int i = 0 ; i < networkInfo.getN(); i++) {
            SendMessage sendMessage = messageFactory.createSendMessage(stripes.get(i), mt.getRootHash(), mt.getMerkleBranch(i), input.length);
            if (i != replicaId) step.add(sendMessage, i);
            else step.add(handleSendMessage(sendMessage));
        }

        return step;
    }

    @Override
    public Step<byte[]> handleMessage(BroadcastMessage message) {
        if (!message.getPid().equals(pid)) {
            return new Step<>();
        }

        final int type = message.getType();
        switch (type) {
            case SendMessage.SEND: {
                return handleSendMessage((SendMessage) message);
            }
            case EchoMessage.ECHO: {
                return handleEchoMessage((EchoMessage) message);
            }
            case ReadyMessage.READY: {
                return handleReadyMessage((ReadyMessage) message);
            }
            default: {
                return new Step<>();
            }
        }
    }

    @Override
    public boolean hasTerminated() {
        return this.decided.get();
    }

    @Override
    public Optional<byte[]> deliver() {
        return Optional.ofNullable(this.decidedValue);
    }

    /**
     * Called by the replica to indicate that
     * a {@link SendMessage} has been received.
     *
     * @param sendMessage the received message
     */
    private Step<byte[]> handleSendMessage(SendMessage sendMessage) {
        if (!sendMessage.getSender().equals(this.proposerId) || this.echoSent.getAndSet(true)) {
            return new Step<>();
        }

        EchoMessage echoMessage = messageFactory.createEchoMessage(sendMessage.getValue(), sendMessage.getRootHash(), sendMessage.getBranch(), replicaId, sendMessage.getInputSize());
        Step<byte[]> step = this.sendMessage(echoMessage);
        return step;
    }

    /**
     * Called by the replica to indicate that
     * a {@link EchoMessage} has been received.
     *
     * @param echoMessage the received message
     */
    private Step<byte[]> handleEchoMessage(EchoMessage echoMessage) {
        final int senderId = echoMessage.getSender();

        Step<byte[]> step = new Step<>();
        if (this.echos.containsKey(senderId)) {
            return step;
        }

        boolean valid = MerkleTree.merkleVerify(echoMessage.getValue(),
                echoMessage.getRootHash(), echoMessage.getBranch(), echoMessage.getBlockId());

        if (!valid) {
            return step;
        }

        // TODO: fix (having n-f echos of same block is not useful)
        final long validEchosCount;
        synchronized (this.echos) {
            // Save the `Echo` message
            // TODO: Check if they are all the same
            this.blocksReceived.putIfAbsent(echoMessage.getRootHash(), new ConcurrentHashMap<>());
            this.sizes.putIfAbsent(echoMessage.getRootHash(), echoMessage.getInputSize());
            Map<Integer, byte[]> blocks = this.blocksReceived.get(echoMessage.getRootHash());
            blocks.putIfAbsent(echoMessage.getBlockId(), echoMessage.getValue());
            this.echos.put(senderId, echoMessage);
            validEchosCount = blocks.size();
        }

        int quorum = networkInfo.getN() - networkInfo.getF();

        if (validEchosCount == quorum) {
            try {
                if (!this.readySent.getAndSet(true)) {
                    // recover message
                    if (inputSize == -1) {
                        this.erasureUtils = new ErasureCodesUtils(n - 2 * f, 2 * f, this.sizes.get(echoMessage.getRootHash()));
                        inputSize = this.sizes.get(echoMessage.getRootHash());
                    }

                    byte[] recovered = erasureUtils.decode(blocksReceived.get(echoMessage.getRootHash()));
                    List<byte[]> encoded = erasureUtils.encode(recovered);
                    MerkleTree mt = new MerkleTree(encoded);

                    if (mt.getRootHash() == echoMessage.getRootHash()) {
                        knownHashes.put(echoMessage.getRootHash(), recovered);
                        ReadyMessage readyMessage = messageFactory.createReadyMessage(echoMessage.getRootHash());
                        step.add(this.sendMessage(readyMessage));
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        step.add(tryOutput(echoMessage.getRootHash()));

        return step;
    }

    /**
     * Called by the replica to indicate that
     * a {@link ReadyMessage} has been received.
     *
     * @param readyMessage the received message
     */
    private Step<byte[]> handleReadyMessage(ReadyMessage readyMessage) {
        final int senderId = readyMessage.getSender();
        final byte[] value = knownHashes.get(readyMessage.getRootHash());

        Step<byte[]> step = new Step<>();

        if (this.readies.containsKey(senderId)) {
            return step;
        }

        this.readies.put(senderId, readyMessage);

        step.add(tryOutput(readyMessage.getRootHash()));

        return step;
    }

    private Step<byte[]> tryOutput(long rootHash) {
        Step<byte[]> step = new Step<>();

        if (knownHashes.get(rootHash) == null) {
            return step;
        }

        // Save the `Ready` message
        long validReadiesCount = this.readies.values().stream()
                .filter(m -> m.getRootHash() == rootHash).collect(Collectors.toList()).size();

        // Upon receiving `2*f + 1` `Ready` messages
        final int quorum2 = 2*networkInfo.getF() + 1;
        if (validReadiesCount >= quorum2) {
            step.add(this.deliver(knownHashes.get(rootHash)));
        }

        return step;
    }

    private Step<byte[]> sendMessage(BroadcastMessage message) {
        Step<byte[]> step = new Step<>();
        for (int id=0; id < this.networkInfo.getN(); id++) {
            if (id == this.replicaId) step.add(this.handleMessage(message));
            else step.add(message, id);
        }
        return step;
    }

    private Step<byte[]> deliver(byte[] value) {
        Step<byte[]> step = new Step<>();
        if (decided.getAndSet(true)) {
            return step;
        }
        this.decidedValue = value;
        step.add(value);

        return step;
    }
}
