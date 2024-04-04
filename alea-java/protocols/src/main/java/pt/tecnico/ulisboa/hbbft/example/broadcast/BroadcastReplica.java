package pt.tecnico.ulisboa.hbbft.example.broadcast;

import pt.tecnico.ulisboa.hbbft.*;
import pt.tecnico.ulisboa.hbbft.broadcast.BroadcastFactory;
import pt.tecnico.ulisboa.hbbft.broadcast.BroadcastMessage;
import pt.tecnico.ulisboa.hbbft.broadcast.IBroadcast;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class BroadcastReplica {

    private final Integer replicaId;

    private MessageEncoder<String> encoder;
    private Transport<String> transport;

    private final BroadcastFactory factory;
    private final Map<String, IBroadcast> instances = new HashMap<>();

    public BroadcastReplica(
            Integer replicaId,
            MessageEncoder<String> encoder,
            Transport<String> transport,
            BroadcastFactory factory
    ) {
        this.replicaId = replicaId;
        this.encoder = encoder;
        this.transport = transport;
        this.factory = factory;
    }

    public Integer getId() {
        return this.replicaId;
    }

    public void propose(byte[] value) {
        // Create broadcast instance
        IBroadcast instance = factory.create();
        instances.put(instance.getPid(), instance);

        // Propose value
        System.out.printf("(%d) Proposed: %s%n", replicaId, new String(value, StandardCharsets.UTF_8));
        Step<byte[]> step = instance.handleInput(value);
        this.handleStep(step);
    }

    public synchronized void handleMessage(String data) {
        BroadcastMessage message = (BroadcastMessage) encoder.decode(data);
        final String pid = message.getPid();
        final Integer sender = message.getSender();

        IBroadcast instance = instances.computeIfAbsent(pid, id -> factory.create(id, sender)); // FIXME buggy sender may not be proposer
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
            System.out.println(String.format("(%d) Delivered: %s", replicaId, new String(output, StandardCharsets.UTF_8)));
    }
}
