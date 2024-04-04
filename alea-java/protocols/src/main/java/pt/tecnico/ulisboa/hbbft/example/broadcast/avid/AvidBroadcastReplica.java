package pt.tecnico.ulisboa.hbbft.example.broadcast.avid;

import pt.tecnico.ulisboa.hbbft.*;
import pt.tecnico.ulisboa.hbbft.broadcast.BroadcastMessage;
import pt.tecnico.ulisboa.hbbft.broadcast.avid.AvidBroadcast;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class AvidBroadcastReplica {

    private final Integer replicaId;
    private final NetworkInfo networkInfo;

    private MessageEncoder<String> encoder;
    private Transport<String> transport;

    private AtomicLong count = new AtomicLong();
    private Map<String, AvidBroadcast> instances = new HashMap<>();

    public AvidBroadcastReplica(
            Integer replicaId,
            NetworkInfo networkInfo,
            MessageEncoder<String> encoder,
            Transport<String> transport
    ) {
        this.replicaId = replicaId;
        this.networkInfo = networkInfo;
        this.encoder = encoder;
        this.transport = transport;
    }

    public Integer getId() {
        return this.replicaId;
    }

    public void propose(byte[] value) {
        // Create broadcast instance
        String bid = "BC-" + replicaId + "-" + count.getAndIncrement();
        AvidBroadcast instance = createInstance(bid, replicaId);
        instances.put(bid, instance);

        // Propose value
        Step<byte[]> step = instance.handleInput(value);
        this.handleStep(step);
    }

    public synchronized void handleMessage(String data) {
        BroadcastMessage message = (BroadcastMessage) encoder.decode(data);
        final String pid = message.getPid();
        final Integer senderId = message.getSender();

        AvidBroadcast instance = instances.computeIfAbsent(pid, id -> createInstance(id, senderId));
        Step<byte[]> step = instance.handleMessage(message);
        this.handleStep(step);
    }

    private void handleStep(Step<byte[]> step) {
        for (TargetedMessage message : step.getMessages()) {
            String encoded = this.encoder.encode(message.getContent());
            this.transport.sendToReplica(message.getTarget(), encoded);
        }

        // Output if terminated
        for (byte[] output : step.getOutput())
            System.out.println(String.format("(%d) Terminated: %s", replicaId, new String(output, StandardCharsets.UTF_8)));
    }

    private AvidBroadcast createInstance(String bid, Integer proposerId) {
        return new AvidBroadcast(bid, replicaId, networkInfo, proposerId);
    }
}
