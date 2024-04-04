package pt.ulisboa.tecnico.client.domain;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.common.util.concurrent.RateLimiter;
import com.google.protobuf.ByteString;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import pt.ulisboa.tecnico.client.domain.grpc.ConfirmationService;
import pt.ulisboa.tecnico.contract.Setup6Service;
import pt.ulisboa.tecnico.contract.Setup6Service.ExitRequest;
import pt.ulisboa.tecnico.contract.Setup6Service.ReplicateRequest;
import pt.ulisboa.tecnico.contract.Setup6Service.ReplicateResponse;
import pt.ulisboa.tecnico.contract.StateMachineServiceGrpc;
import pt.ulisboa.tecnico.contract.ConfirmationServiceGrpc.ConfirmationServiceImplBase;
import pt.ulisboa.tecnico.replica.domain.Replica;
import pt.ulisboa.tecnico.utils.Utils;

public class Client {
    private final static boolean REPORTER = true;
    private final static boolean WARMUP = false;
    private final static int REPORT_PERIOD = 1; // seconds
    private final static int DEFAULT_DURATION = 120; // seconds
    private final static int WARMUP_DURATION = 15; // seconds
    private static final int PAYLOAD_SIZE = 256;
    private static final int BASE_CONFIRMATION_PORT = 20000;
    private int clientId;

    private static byte[] baseTx = new byte[PAYLOAD_SIZE];
	private static int txI = 0;

	private int duration = DEFAULT_DURATION;
    // How many replicas should the client contact
    private int oksRequested;

    // How many confirmation needed until can send confirmation "upwards"
    private int oksRequired = 1; // Optimistic (timeout needed in real implementation)

    // Channels opened to replicas
    private Map<Integer, ManagedChannel> channels = new ConcurrentHashMap<>();

    // Number of transactions to request replication for
    protected Map<Long, Integer> pendingTransactions = new ConcurrentHashMap();

    private AtomicLong recentlyConfirmed = new AtomicLong(0);
    private AtomicBoolean reporterRunning = new AtomicBoolean(false);

    // File to which latencies registered are logged
    private Map<Integer, StateMachineServiceGrpc.StateMachineServiceStub> stubs = new HashMap<>();
    private Map<Integer, StreamObserver<ReplicateRequest>> streams = new HashMap<>();
    private AtomicLong submitted = new AtomicLong(0L);
    private List<InetAddress> replicasAddresses;
    private int load;

    public Client(List<InetAddress> addresses, int clientId, int oksRequested, int load) {
        Utils.init();
        replicasAddresses = addresses;
        this.clientId = clientId;
        this.oksRequested = oksRequested;
        setLoad(load);
        launchService();
    }

    private void launchService() {
        int port = Replica.BASE_CONFIRMATION_PORT + clientId;
        System.out.printf("Creating server at port %d...", port);
        Server server = ServerBuilder
                .forPort(port)
                .addService(new ConfirmationService(this))
                .build();

        try {
            server.start();
            System.out.println("Done");
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(2);
        }
    }

    public List<InetAddress> getReplicasAddresses() {
        return replicasAddresses;
    }

    public int getOksRequired() {
        return oksRequired;
    }

    public Map<Integer, ManagedChannel> getChannels() {
        return channels;
    }

    public ManagedChannel getChannel(int replicaId) {
        return channels.get(replicaId);
    }

    public static byte[] generateTransaction() {
		baseTx[txI]++;
		txI = (txI+1) % PAYLOAD_SIZE;
        return baseTx;
    }
 
    public void registerConfirmation(long tid) {
        Utils.clientConfirm(tid);
        recentlyConfirmed.incrementAndGet();
        confirmed++;
    }
    static StreamObserver<ReplicateResponse> defaultStreamObserver = new StreamObserver<Setup6Service.ReplicateResponse>() {
            @Override
            public void onNext(ReplicateResponse request) {
                return;
            }

            public void onCompleted() {
                return;
            }

            public void onError(Throwable t) {
                System.out.println("Error on replication.");
                t.printStackTrace();
            }
    };

    public void tryConfirm(long tid) {
        int remaining;
        synchronized (this) {
            if (!pendingTransactions.containsKey(tid))
                return;

            remaining = pendingTransactions.get(tid);
            remaining--;

            if (remaining == 0) {
                pendingTransactions.remove(tid);
            } else {
                pendingTransactions.put(tid, remaining);
            }
        }

        // Register confirmation
        if (oksRequested - remaining == oksRequired) {
            registerConfirmation(tid);
        }
    }

    private void setupConnections(List<InetAddress> replicas) {
        for (int i = 0; i < replicas.size(); i++) {
            setupConnection(replicas, i); 
        }
    }

    private void setupConnection(List<InetAddress> replicas, int i) {
            ManagedChannel channel = channels.get(i);
            if (channel != null) {
               channel.shutdown(); 
            }

            channel = ManagedChannelBuilder.forAddress(replicas.get(i).getHostAddress(), Replica.BASE_SUBMISSION_PORT + i)
                            .usePlaintext()
                            .build();
            channels.put(i, channel);
            try {
                StateMachineServiceGrpc.StateMachineServiceStub stub = StateMachineServiceGrpc.newStub(channel);
                stubs.put(i, stub);
                streams.put(i, stub.replicate(defaultStreamObserver));
            } catch (Exception e) {
                channel.shutdown();
                throw e;
            }
    }

    /**
     * Non-blocking submits transction into stream
     */
    protected void submit() {
        long count = submitted.getAndIncrement();

        // Get probable leader (in Alea)
        int leader = (int) (count % replicasAddresses.size());

        // Skip faulty leader
        while (leader < replicasAddresses.size()/3 && (Utils.BYZ || Utils.CRASHED)) {
            leader++;
        }

        // Pick oksRequested random replicas to submit to
        List<Integer> shuffledReplicas = IntStream.range(0, oksRequested).boxed().collect(Collectors.toList());
        Collections.shuffle(shuffledReplicas);
        Set<Integer> chosenReplicas = new HashSet<>(shuffledReplicas.subList(0, oksRequested - 1));
        if (chosenReplicas.contains(leader)) chosenReplicas.add(shuffledReplicas.get(oksRequested - 1));
        else chosenReplicas.add(leader);

        // Submit the same request to selected replicas
        ReplicateRequest request = ReplicateRequest.newBuilder()
                .setTid(count)
                .setPayload(ByteString.copyFrom(generateTransaction()))
                .setClientId(clientId)
                .build();

        pendingTransactions.put(count, oksRequested);
        Utils.clientSubmit(count);
        
        for (int replicaId: chosenReplicas) {
            // stubs.get(replicaId).replicate(request, so);
            streams.get(replicaId).onNext(request);
        }
    }

    /**
     * Blocks until all replicas respond to ping message
     */
    public void waitReplicasUp(List<InetAddress> replicas) throws InterruptedException {

        System.out.print("Waiting for replicas to come up...");
        CountDownLatch latch = new CountDownLatch(replicas.size());

        for (int i = 0; i < replicas.size(); i++) {
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
        if (REPORTER) stopReporter();
        System.out.printf("ending at client\n");
        Utils.end();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (clientId == 0) {
            for (int i = 0; i < replicasAddresses.size(); i++) {
                ManagedChannel channel = ManagedChannelBuilder
                        .forAddress(replicasAddresses.get(i).getHostAddress(), Replica.BASE_SUBMISSION_PORT + i)
                        .usePlaintext()
                        .build();
                try {
                    StateMachineServiceGrpc.StateMachineServiceBlockingStub stub = StateMachineServiceGrpc.newBlockingStub(channel);
                    stub.exit(ExitRequest.newBuilder().getDefaultInstanceForType());
                    System.out.printf("[%s] killed replica %s\n", clientId, i);
                } catch (Exception e) {
                }
                finally {
                    channel.shutdown();
                }
            }

            for (StateMachineServiceGrpc.StateMachineServiceStub stub: stubs.values()) {
                ((ManagedChannel) stub.getChannel()).shutdown();
            }
        }
    }

    public void warmUp(int load) {
        // Warm up
        System.out.print("Warming up system...");
		long start = System.currentTimeMillis();
        RateLimiter rateLimiter = RateLimiter.create(load);
         while (System.currentTimeMillis() - start < WARMUP_DURATION * 1000) {
            rateLimiter.acquire();
            submit();
        }
        System.out.println("Done");

        // Reset stats
        confirmed = 0;
        recentlyConfirmed.set(0);
        submitted.set(0);
    }

    public void setLoad(int load) {
        this.load = load;
    }

    /**
     * Starts submitting transactions (blocking call)
     */
    public void start() {
        (new Random()).nextBytes(baseTx); // randomize base transaction

        setupConnections(getReplicasAddresses());

        if (WARMUP) warmUp(load);

        if (REPORTER) startReporter();

        RateLimiter rateLimiter = RateLimiter.create(load);
        System.out.println("Starting stress tests.");
		long start = System.currentTimeMillis();
         while (System.currentTimeMillis() - start < duration * 1000) {
            rateLimiter.acquire();
            submit();
        }

        double duration = (System.currentTimeMillis() - start) / 1000.0;
        System.out.printf("End summary - duration: %f, txs: %d, avg throughput: %f\n",
            duration, confirmed, confirmed/duration);

        exit();
    }

    // Start reporter daemon on background
    public void startReporter() {
        reporterRunning.set(true);

        Thread reporter = new Thread() {
            public void run() {
                long now, before;
                before = System.nanoTime();
                while (reporterRunning.get()) {
                    try {
                        Thread.sleep(REPORT_PERIOD * 1000);
                    } catch (InterruptedException e) {
                        return;
                    }

                    now = System.nanoTime();
                    long txs = recentlyConfirmed.getAndSet(0);
                    double delta = (now - before) / 1000000000.0;
                    System.out.printf("Client report - recently confirmed: %d, avg throughput: %f\n", txs, txs/delta);

                    before = System.nanoTime();
                }
            }
        };

        reporter.setDaemon(true);
        reporter.start();
    }

    // Stop reporter daemon on background
    public void stopReporter() {
        reporterRunning.set(false);
    }
}
