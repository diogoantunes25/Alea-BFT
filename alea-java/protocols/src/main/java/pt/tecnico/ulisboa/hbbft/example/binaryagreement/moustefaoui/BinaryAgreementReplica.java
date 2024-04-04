package pt.tecnico.ulisboa.hbbft.example.binaryagreement.moustefaoui;

import pt.tecnico.ulisboa.hbbft.*;
import pt.tecnico.ulisboa.hbbft.binaryagreement.BinaryAgreementMessage;
import pt.tecnico.ulisboa.hbbft.binaryagreement.moustefaoui.MoustefaouiBinaryAgreement;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class BinaryAgreementReplica {

    private final Integer replicaId;
    private final NetworkInfo networkInfo;

    private MessageEncoder<String> encoder;
    private Transport<String> transport;

    private AtomicLong count = new AtomicLong();
    private Map<String, MoustefaouiBinaryAgreement> instances = new HashMap<>();

    private final AtomicLong sentCount = new AtomicLong();
    private final AtomicLong recvCount = new AtomicLong();

    public BinaryAgreementReplica(
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

    public void propose(boolean value) {
        // Create binary agreement instance
        String pid = "ABA-" + count.getAndIncrement();
        MoustefaouiBinaryAgreement instance = instances.computeIfAbsent(pid, this::createInstance);

        // Propose value
        Step<Boolean> step = instance.handleInput(value);
        this.handleStep(step);
    }

    public synchronized void handleMessage(String data) {
        this.recvCount.incrementAndGet();
        //System.out.println(String.format("RECV: %d -> %d: %s", senderId, replicaId, data));

        BinaryAgreementMessage message = (BinaryAgreementMessage) encoder.decode(data);
        final String pid = message.getPid();

        MoustefaouiBinaryAgreement instance = instances.computeIfAbsent(pid, this::createInstance);
        Step<Boolean> step = instance.handleMessage(message);
        this.handleStep(step);
    }

    private void handleStep(Step<Boolean> step) {
        for (TargetedMessage message : step.getMessages()) {
            this.sentCount.incrementAndGet();
            String encoded = this.encoder.encode(message.getContent());
            this.transport.sendToReplica(message.getTarget(), encoded);
        }

        // Output if terminated
        for (Boolean output : step.getOutput()) {
            System.out.printf("(%d) Decided: %b%n", replicaId, output);
            System.out.printf("(%d) Rounds: %b%n", replicaId, output);
            System.out.printf("(%d) Sent: %d%n", replicaId, sentCount.get());
            System.out.printf("(%d) Received: %d%n", replicaId, recvCount.get());
        }

    }

    private MoustefaouiBinaryAgreement createInstance(String pid) {
        return new MoustefaouiBinaryAgreement(pid, replicaId, networkInfo);
    }
}
