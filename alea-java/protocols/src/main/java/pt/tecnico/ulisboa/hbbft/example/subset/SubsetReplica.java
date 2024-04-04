package pt.tecnico.ulisboa.hbbft.example.subset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.tecnico.ulisboa.hbbft.*;
import pt.tecnico.ulisboa.hbbft.subset.IAsynchronousCommonSubset;
import pt.tecnico.ulisboa.hbbft.subset.SubsetFactory;
import pt.tecnico.ulisboa.hbbft.subset.Subset;
import pt.tecnico.ulisboa.hbbft.subset.SubsetMessage;
import pt.tecnico.ulisboa.hbbft.utils.ExecutionLogger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class SubsetReplica {

    private final Logger logger = LoggerFactory.getLogger(SubsetReplica.class);

    private final Integer replicaId;

    private final MessageEncoder<String> encoder;
    private final Transport<String> transport;

    private final SubsetFactory factory;
    private final Map<String, IAsynchronousCommonSubset> instances = new HashMap<>();

    private final AtomicLong counter = new AtomicLong();

    private final ExecutionLogger execLogger = new ExecutionLogger();

    public SubsetReplica(
            Integer replicaId,
            MessageEncoder<String> encoder,
            Transport<String> transport,
            SubsetFactory factory
    ) {
        this.replicaId = replicaId;
        this.encoder = encoder;
        this.transport = transport;
        this.factory = factory;
    }

    public Integer getId() {
        return this.replicaId;
    }

    public synchronized void propose(byte[] value) {
        // Create subset instance
        String pid = "ACS-" + this.counter.getAndIncrement();
        IAsynchronousCommonSubset instance = this.getInstance(pid);

        logger.info("Proposed to {}: {}", pid, new String(value));

        // Propose value
        Step<Subset> step = instance.handleInput(value);
        this.handleStep(step);
    }

    public synchronized void handleMessage(String data) {
        SubsetMessage message = (SubsetMessage) this.encoder.decode(data);
        //if (replicaId == 1) logger.info("Recv: {} -> {}: {}", message.getSender(), replicaId, data);

        final String pid = message.getPid();

        IAsynchronousCommonSubset instance = this.getInstance(pid);
        Step<Subset> step = instance.handleMessage(message);

        /*if (replicaId == 3) {
            execLogger.logEvent(message, step);
            execLogger.printLog();
            System.out.println();
        }*/
        //if (replicaId == 3 && message.getContent() instanceof DoneMessage) System.out.println(message.getTarget());

        this.handleStep(step);

    }

    private void handleStep(Step<Subset> step) {
        for (TargetedMessage message : step.getMessages()) {
            String encoded = this.encoder.encode(message.getContent());
            for (int target: message.getTargets()) {
                if (replicaId == 0) logger.info("Send: {} -> {}: msg={}", replicaId, target, encoded);
                this.transport.sendToReplica(target, encoded);
            }
        }

        // Output if terminated
        for (Subset output : step.getOutput())
            System.out.println(String.format("%d: %s", replicaId, output.toString()));
    }

    private synchronized IAsynchronousCommonSubset getInstance(String pid) {
        return instances.computeIfAbsent(pid, id -> factory.create(pid));
    }
}
