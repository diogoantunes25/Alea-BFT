package pt.tecnico.ulisboa.hbbft.example.abc;

import pt.tecnico.ulisboa.hbbft.*;
import pt.tecnico.ulisboa.hbbft.abc.Block;
import pt.tecnico.ulisboa.hbbft.abc.IAtomicBroadcast;

public class AtomicBroadcastReplica {

    // Our ID.
    private final Integer replicaId;

    // The atomic broadcast protocol instance.
    private final IAtomicBroadcast instance;

    private final MessageEncoder<String> encoder;

    private final Transport<String> transport;

    public AtomicBroadcastReplica(
            Integer replicaId,
            IAtomicBroadcast instance,
            MessageEncoder<String> encoder,
            Transport<String> transport
    ) {
        this.replicaId = replicaId;
        this.instance = instance;
        this.encoder = encoder;

        this.transport = transport;
    }

    public Integer getId() {
        return this.replicaId;
    }

    public synchronized void propose(byte[] value) {
        Step<Block> step = instance.handleInput(value);
        this.handleStep(step);
    }

    public synchronized void handleMessage(String data) {
        // if (replicaId == 1) System.out.println(data);
        ProtocolMessage message = this.encoder.decode(data);
        Step<Block> step = instance.handleMessage(message);
        this.handleStep(step);
    }

    private void handleStep(Step<Block> step) {
        // Send messages generated during this step
        for (TargetedMessage message : step.getMessages()) {
            String encoded = this.encoder.encode(message.getContent());
            for (int target: message.getTargets()) {
                this.transport.sendToReplica(target, encoded);
            }
        }

        // Output batches delivered for each step
        for (Block block : step.getOutput()) deliver(block);
    }

    public void deliver(Block block) {
        System.out.println(block);
        //System.out.println(String.format("Block #%d", block.getNumber()));
    }
}
