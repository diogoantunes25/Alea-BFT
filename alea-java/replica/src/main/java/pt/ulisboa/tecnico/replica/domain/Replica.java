package pt.ulisboa.tecnico.replica.domain;

import io.grpc.BindableService;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import pt.tecnico.ulisboa.hbbft.*;
import pt.tecnico.ulisboa.hbbft.abc.Block;
import pt.tecnico.ulisboa.hbbft.abc.IAtomicBroadcast;
import pt.tecnico.ulisboa.hbbft.abc.acs.AcsAtomicBroadcast;
import pt.tecnico.ulisboa.hbbft.abc.alea.Alea;
import pt.tecnico.ulisboa.hbbft.abc.honeybadger.HoneyBadger;
import pt.tecnico.ulisboa.hbbft.abc.honeybadger.Params;
import pt.tecnico.ulisboa.hbbft.abc.honeybadger.crypto.EncryptionSchedule;
import pt.tecnico.ulisboa.hbbft.abc.honeybadger.crypto.NeverEncrypt;
import pt.tecnico.ulisboa.hbbft.binaryagreement.BinaryAgreementFactory;
import pt.tecnico.ulisboa.hbbft.binaryagreement.BinaryAgreementMessage;
import pt.tecnico.ulisboa.hbbft.binaryagreement.moustefaoui.MoustefaouiBinaryAgreementFactory;
import pt.tecnico.ulisboa.hbbft.broadcast.BroadcastFactory;
import pt.tecnico.ulisboa.hbbft.broadcast.bracha.BrachaBroadcastFactory;
import pt.tecnico.ulisboa.hbbft.broadcast.bracha2.BrachaBroadcast2Factory;
import pt.tecnico.ulisboa.hbbft.example.abc.acs.AcsAtomicBroadcastMessageEncoder;
import pt.tecnico.ulisboa.hbbft.example.abc.alea.AleaMessageEncoder;
import pt.tecnico.ulisboa.hbbft.example.abc.byzness.EchoVBroadcastMessageEncoder;
import pt.tecnico.ulisboa.hbbft.example.abc.honeybadger.HoneyBadgerMessageEncoder;
import pt.tecnico.ulisboa.hbbft.example.binaryagreement.moustefaoui.MoustefaouiBinaryAgreementMessageEncoder;
import pt.tecnico.ulisboa.hbbft.example.broadcast.bracha.BrachaBroadcastMessageEncoder;
import pt.tecnico.ulisboa.hbbft.example.broadcast.bracha2.BrachaBroadcast2MessageEncoder;
import pt.tecnico.ulisboa.hbbft.example.subset.hbbft.HoneyBadgerSubsetMessageEncoder;
import pt.tecnico.ulisboa.hbbft.subset.hbbft.HoneyBadgerSubsetFactory;
import pt.ulisboa.tecnico.contract.ConfirmationServiceGrpc;
import pt.ulisboa.tecnico.contract.Setup6Service;
import pt.ulisboa.tecnico.contract.StateMachineServiceGrpc;
import pt.ulisboa.tecnico.contract.ClientService.ClientAliveRequest;
import pt.ulisboa.tecnico.replica.domain.grpc.StateMachineServiceImpl;
import pt.ulisboa.tecnico.replica.utils.*;
import pt.ulisboa.tecnico.replica.utils.transport.*;

import pt.ulisboa.tecnico.utils.Utils;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

// public class Replica extends Host implements Responsible {
public class Replica implements Responsible {

    public static final int BASE_REPLICATION_PORT = 30000;
    public static final int BASE_SUBMISSION_PORT = 31000;
    public static final int BASE_CONFIRMATION_PORT = 32000;

    public static final int MAX_BATCH_PENDING = 20;
    public int maxPending;

    // Generic protocol parameters2Id,...,txnId
    public static final int DEFAULT_BATCH_SIZE = 1;

    // Alea parameters
    private static final int CONCURRENT_BRODCASTS = 3;
    private static final int MAX_PIPELINE_OFFSET = 5;
    private AtomicBoolean running = new AtomicBoolean(false);

    // Honeybadger parameters

    // replication port = base replication port + replicaId
    private List<InetAddress> clients;
    private List<InetAddress> replicas;
    private int replicationPort;
    // submission port = base submission port + replicaId
    private int submissionPort;
    private int replicaId;
    private TcpTransport<Replica> transport;
    private MessageEncoder<String> encoder;
    private IAtomicBroadcast instance;
    private List<InetAddress> addresses;
    private List<ExecutorService> senders = new ArrayList<>();
    private NetworkInfo networkInfo;
    private StateMachineServiceImpl service;
    private Function<byte[], Integer> confirmationCallback;
    private Server server;

    public Replica(int replicaId, NetworkInfo networkInfo, List<InetAddress> addresses, Protocol protocol, ReplicaConfig config, List<InetAddress> clientAddresses)
            throws IOException {

        Utils.init();
        BindableService service = new StateMachineServiceImpl(this, clientAddresses);
        this.replicaId = replicaId;
        this.maxPending = config.batchSize() * MAX_BATCH_PENDING;
        this.addresses = addresses;
        this.networkInfo = networkInfo;
        this.replicationPort = BASE_REPLICATION_PORT + replicaId;
        this.submissionPort = BASE_SUBMISSION_PORT + replicaId;
        this.clients = clientAddresses;
        try {
            setupInstance(protocol, config);

            setupTransport(addresses);

            server = ServerBuilder
                        .forPort(getSubmissionPort())
                        .addService(service)
                        .build();
            server.start();
        } catch (Exception e) {
            e.printStackTrace(System.out);
            throw e;
        }
    }

    public void setCallback(Function<byte[], Integer> callback) {
        this.confirmationCallback = callback;
    }

    private void setupTransport(List<InetAddress> addresses) {
        System.out.print("Setting up transport channels...");
        System.out.flush();

        this.transport = new TcpTransport<Replica>(this, addresses, 2, this.getReplicationPort());

        for (Connection<Replica> connection: this.transport.getConnections()) {
            connection.setListener(this);
        }

        for (int i = 0; i < networkInfo.getN(); i++) senders.add(Executors.newFixedThreadPool(2));

        System.out.println("Done");
    }

    private void setupInstance(Protocol protocol, ReplicaConfig config) {
        switch (protocol) {
            case ALEA:
                this.encoder = new AleaMessageEncoder(new EchoVBroadcastMessageEncoder(), new MoustefaouiBinaryAgreementMessageEncoder());
                Alea.Params aleaParams = new Alea.Params.Builder()
                        .batchSize(config.batchSize())
                        .maxConcurrentBroadcasts(CONCURRENT_BRODCASTS)
                        .maxPipelineOffset(MAX_PIPELINE_OFFSET)
                        .build();

                instance = new Alea(this.replicaId, networkInfo, aleaParams);
                break;
            case HB:
                this.encoder = new AcsAtomicBroadcastMessageEncoder(new HoneyBadgerSubsetMessageEncoder(
                        new BrachaBroadcast2MessageEncoder(),
                        new MoustefaouiBinaryAgreementMessageEncoder()
                ));

                BroadcastFactory bcFactory = new BrachaBroadcast2Factory(replicaId, networkInfo);
                BinaryAgreementFactory baFactory = new MoustefaouiBinaryAgreementFactory(replicaId, networkInfo);
                HoneyBadgerSubsetFactory acsFactory = new HoneyBadgerSubsetFactory(this.replicaId,
                        networkInfo, bcFactory, baFactory);

                AcsAtomicBroadcast.Params params = new AcsAtomicBroadcast.Params.Builder()
                        .batchSize(config.batchSize())
                        .encryptionSchedule(new NeverEncrypt()) // FIXME: change this
                        .committeeSize(networkInfo.getF()+1)
                        .build();

                instance = new AcsAtomicBroadcast(replicaId, networkInfo, params, acsFactory);
                break;

            case DUMBO:
                // TODO
                break;
        }
    }

    public int getId() {
        return replicaId;
    }

    public int getPort() {
        return getReplicationPort();
    }

    public String getAddress() {
        return addresses.get(replicaId).toString();
    }

    public int getReplicationPort() {
        return BASE_REPLICATION_PORT + replicaId;
    }

    public int getSubmissionPort() {
        return BASE_SUBMISSION_PORT + replicaId;
    }

    @Override
    public void handleMessage(String data) {
        if (Utils.CRASHED) return;

        ProtocolMessage message = this.encoder.decode(data);

        if (message != null) {
            Step<Block> step = this.instance.handleMessage(message);
            this.handleStep(step);
        }
    }

    public void submit(Transaction t) {
        Utils.aleaStart(t.content());
        Step<Block> step = instance.handleInput(t.content());
        handleStep(step);
    }

    public void deliver(Block block) {
        for (byte[] entry: block.getEntries()) {
            Transaction t = new Transaction(entry);
            Utils.aleaEnd(t.content());
            confirmationCallback.apply(t.content());
        }
    }

    public void handleStep(Step<Block> step) {
        if (Utils.CRASHED) return;

        for (TargetedMessage message: step.getMessages()) {
            ExecutorService sender = senders.get(message.getTarget());
            sender.submit(() -> {
                String encoded = this.encoder.encode(message.getContent());

                // Channel id -> channel 1 for ABA and 0 for VCBC
                final int cid = (message.getContent() instanceof BinaryAgreementMessage) ? 1 : 0;
                for (Integer target: message.getTargets()) {
                    this.transport.sendToReplica(target, cid, encoded);
                }
            });
        }

        // handle step outputs
        for (Block block: step.getOutput()) {
            this.deliver(block);
        }
    }

    /**
     * Starts replica - starts responding to requests and processing transactions. Blocking call
     */
    public void start() throws IOException, InterruptedException {
        running.set(true);

        if (Utils.BYZ) {
            Thread fakeClientd = new Thread(() -> {
                while (running.get()) {
                    submit(new Transaction(new byte[256]));
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            fakeClientd.start();
        }

        server.awaitTermination();
    }

    /**
     * Blocks until all replicas respond to ping message
     */
    public void waitClientsUp(List<InetAddress> clients) throws InterruptedException {
        System.out.print("Waiting for clients to come up...");
        System.out.flush();

        CountDownLatch latch = new CountDownLatch(clients.size());

        for (int i = 0; i < clients.size(); i++) {
            final int clientId = i;
            Thread t = new Thread(() -> {
                while (true) {
                    ManagedChannel channel = null;
                    try {
                        String target = String.format("%s:%s", clients.get(clientId).getHostAddress(), Replica.BASE_CONFIRMATION_PORT + clientId);
                        channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
                        ConfirmationServiceGrpc.ConfirmationServiceBlockingStub stub = ConfirmationServiceGrpc.newBlockingStub(channel);
                        stub.check(ClientAliveRequest.getDefaultInstance());
                        latch.countDown();
                        return;
                    } catch (Exception e) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                    } finally {
                        channel.shutdown();
                    }
                }
            });
            t.start();
        }

        latch.await();
        System.out.println("Done");
    }


    /**
     * Blocks until all replicas respond to ping message
     */
    public void waitReplicasUp(List<InetAddress> replicas) throws InterruptedException {
        System.out.print("Waiting for replicas to come up...");
        System.out.flush();

        CountDownLatch latch = new CountDownLatch(replicas.size() - 1);

        for (int i = 0; i < replicas.size(); i++) {
            if (i == replicaId) continue;
            final int replicaId = i;
            Thread t = new Thread(() -> {
                while (true) {
                    ManagedChannel channel = null;
                    try {
                        String target = String.format("%s:%s", replicas.get(replicaId).getHostAddress(), Replica.BASE_SUBMISSION_PORT + replicaId);
                        channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
                        StateMachineServiceGrpc.StateMachineServiceBlockingStub stub = StateMachineServiceGrpc.newBlockingStub(channel);
                        stub.check(Setup6Service.AliveRequest.getDefaultInstance());
                        latch.countDown();
                        return;
                    } catch (Exception e) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                    } finally {
                        channel.shutdown();
                    }
                }
            });
            t.start();
        }

        latch.await();
        System.out.println("Done");
    }

    public void exit() {
        running.set(false);
        for (Connection<Replica> connection: transport.getConnections()) connection.disconnect();
    }

    public enum Protocol {
        ALEA, HB, DUMBO
    }

    public static class ReplicaConfig {
        private int _batchSize;
        private ReplicaConfig(int batchSize) {
            _batchSize = batchSize;
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public int batchSize() {
            return _batchSize;
        }

        public static class Builder {
            private int _batchSize;
            public Builder() {}

            public Builder setBatchsize(int batchsize) {
                this._batchSize = batchsize;
                return this;
            }

            public ReplicaConfig build() {
                return new ReplicaConfig(_batchSize);
            }
        }
    }
}
