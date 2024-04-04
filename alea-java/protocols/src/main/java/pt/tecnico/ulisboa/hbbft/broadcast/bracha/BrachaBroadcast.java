package pt.tecnico.ulisboa.hbbft.broadcast.bracha;

import pt.tecnico.ulisboa.hbbft.NetworkInfo;
import pt.tecnico.ulisboa.hbbft.Step;
import pt.tecnico.ulisboa.hbbft.broadcast.BroadcastMessage;
import pt.tecnico.ulisboa.hbbft.broadcast.IBroadcast;
import pt.tecnico.ulisboa.hbbft.broadcast.bracha.messages.EchoMessage;
import pt.tecnico.ulisboa.hbbft.broadcast.bracha.messages.ReadyMessage;
import pt.tecnico.ulisboa.hbbft.broadcast.bracha.messages.SendMessage;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class BrachaBroadcast implements IBroadcast {

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

    // The value to output when ready.
    private byte[] decidedValue;

    private final pt.tecnico.ulisboa.hbbft.broadcast.bracha.BrachaBroadcastMessageFactory messageFactory;

    public BrachaBroadcast(
            String pid,
            Integer replicaId,
            NetworkInfo networkInfo,
            Integer proposerId
    ) {
        this.pid = pid;
        this.replicaId = replicaId;
        this.networkInfo = networkInfo;
        this.proposerId = proposerId;
        this.messageFactory = new pt.tecnico.ulisboa.hbbft.broadcast.bracha.BrachaBroadcastMessageFactory(pid, replicaId);
    }

    @Override
    public String getPid() {
        return this.pid;
    }

    @Override
    public Step<byte[]> handleInput(byte[] input) {
        Step<byte[]> step = new Step<>();
        if (!this.replicaId.equals(proposerId) || this.sendSent.getAndSet(true)) {
            return step;
        }

        SendMessage sendMessage = messageFactory.createSendMessage(input);
        return this.sendMessage(sendMessage);
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

        EchoMessage echoMessage = messageFactory.createEchoMessage(sendMessage.getValue());
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
        final byte[] value = echoMessage.getValue();

        Step<byte[]> step = new Step<>();
        if (this.echos.containsKey(senderId)) {
            return step;
        }

        final long validEchosCount;
        synchronized (this.echos) {
            // Save the `Echo` message
            this.echos.put(senderId, echoMessage);
            validEchosCount = this.echos.values().stream()
                    .filter(e -> Arrays.equals(e.getValue(), value)).collect(Collectors.toList()).size();
        }

        int quorum = (int) Math.ceil((double) (networkInfo.getN() + networkInfo.getF() + 1) / 2);

        if (validEchosCount == quorum) {
            if (!this.echoSent.getAndSet(true)) {
                EchoMessage message = messageFactory.createEchoMessage(echoMessage.getValue());
                step.add(this.sendMessage(message));
            }

            if (!this.readySent.getAndSet(true)) {
                ReadyMessage readyMessage = messageFactory.createReadyMessage(value);
                step.add(this.sendMessage(readyMessage));
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
        final int senderId = readyMessage.getSender();
        final byte[] value = readyMessage.getValue();

        Step<byte[]> step = new Step<>();
        if (this.readies.containsKey(senderId)) {
            return step;
        }

        final long validReadiesCount;

        synchronized (this.readies) {
            // Save the `Ready` message
            this.readies.put(senderId, readyMessage);
            validReadiesCount = this.readies.values().stream()
                    .filter(m -> Arrays.equals(m.getValue(), value)).collect(Collectors.toList()).size();
        }

        // Upon receiving `f + 1` `Ready` messages and not having sent a `Ready` message
        final int quorum1 = networkInfo.getF() + 1;
        if (validReadiesCount == quorum1) {
            if (!this.echoSent.getAndSet(true)) {
                EchoMessage message = messageFactory.createEchoMessage(readyMessage.getValue());
                step.add(this.sendMessage(message));
            }

            if (!this.readySent.getAndSet(true)) {
                ReadyMessage message = messageFactory.createReadyMessage(value);
                step.add(this.sendMessage(message));
            }
        }

        // Upon receiving `2*f + 1` `Ready` messages
        final int quorum2 = 2*networkInfo.getF() + 1;
        if (validReadiesCount == quorum2) {
            // Deliver
            step.add(this.deliver(value));
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
