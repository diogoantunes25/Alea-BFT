package pt.tecnico.ulisboa.hbbft.example.vbroadcast;

import pt.tecnico.ulisboa.hbbft.*;
import pt.tecnico.ulisboa.hbbft.vbroadcast.IVBroadcast;
import pt.tecnico.ulisboa.hbbft.vbroadcast.VBroadcastMessage;
import pt.tecnico.ulisboa.hbbft.vbroadcast.VOutput;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class ValidatedBroadcastReplica {

    private final Integer replicaId;

    private final MessageEncoder<String> encoder;
    private final Transport<String> transport;

    // private final VBroadcastFactory factory;
    private final Map<String, IVBroadcast> instances = new HashMap<>();

    private final AtomicLong counter = new AtomicLong();

    public ValidatedBroadcastReplica(
            Integer replicaId,
            MessageEncoder<String> encoder,
            Transport<String> transport
    ) {
        this.replicaId = replicaId;
        this.encoder = encoder;
        this.transport = transport;
    }

    public synchronized void broadcast(byte[] value) {
        // Create validated broadcast
        String pid = String.format("VBC-%d-%d", replicaId, counter.getAndIncrement());
        IVBroadcast instance = this.getInstance(pid);

        // Broadcast value
        Step<VOutput> step = instance.handleInput(value);
        this.handleStep(step);
    }

    public synchronized void handleMessage(String data) {
        VBroadcastMessage message = (VBroadcastMessage) this.encoder.decode(data);
        IVBroadcast instance = this.getInstance(message.getPid());
        Step<VOutput> step = instance.handleMessage(message);
        this.handleStep(step);
    }

    private void handleStep(Step<VOutput> step) {
        for (TargetedMessage message : step.getMessages()) {
            String encoded = this.encoder.encode(message.getContent());
            this.transport.sendToReplica(message.getTarget(), encoded);
        }

        // Output if terminated
        for (VOutput output : step.getOutput())
            System.out.println(output);
    }

    private synchronized IVBroadcast getInstance(String pid) {
        return null;
        //return instances.computeIfAbsent(pid, id -> factory.create(pid));
    }
}
