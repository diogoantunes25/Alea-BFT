package pt.tecnico.ulisboa.hbbft.example.abc;

import pt.tecnico.ulisboa.hbbft.MessageEncoder;
import pt.tecnico.ulisboa.hbbft.NetworkInfo;
import pt.tecnico.ulisboa.hbbft.Transport;
import pt.tecnico.ulisboa.hbbft.abc.IAtomicBroadcast;
import pt.tecnico.ulisboa.hbbft.abc.honeybadger.HoneyBadger;
import pt.tecnico.ulisboa.hbbft.abc.honeybadger.Params;
import pt.tecnico.ulisboa.hbbft.abc.honeybadger.crypto.EncryptionSchedule;
import pt.tecnico.ulisboa.hbbft.abc.honeybadger.crypto.NeverEncrypt;
import pt.tecnico.ulisboa.hbbft.binaryagreement.BinaryAgreementFactory;
import pt.tecnico.ulisboa.hbbft.binaryagreement.moustefaoui.MoustefaouiBinaryAgreementFactory;
import pt.tecnico.ulisboa.hbbft.broadcast.BroadcastFactory;
import pt.tecnico.ulisboa.hbbft.broadcast.bracha.BrachaBroadcastFactory;
import pt.tecnico.ulisboa.hbbft.example.TcpTransport;
import pt.tecnico.ulisboa.hbbft.example.abc.honeybadger.HoneyBadgerMessageEncoder;
import pt.tecnico.ulisboa.hbbft.example.binaryagreement.moustefaoui.MoustefaouiBinaryAgreementMessageEncoder;
import pt.tecnico.ulisboa.hbbft.example.broadcast.bracha.BrachaBroadcastMessageEncoder;
import pt.tecnico.ulisboa.hbbft.example.subset.hbbft.HoneyBadgerSubsetMessageEncoder;
import pt.tecnico.ulisboa.hbbft.subset.hbbft.HoneyBadgerSubsetFactory;
import pt.tecnico.ulisboa.hbbft.utils.ThreshsigUtil;
import pt.tecnico.ulisboa.hbbft.utils.threshsig.ThreshSigUtils;
import pt.tecnico.ulisboa.hbbft.utils.threshsig.dummy.DummyUtils;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MainTcp {

    private enum Protocol {
        HONEY_BADGER,
        DUMBO,
        ALEA_BFT
    }

    private static final int TOLERANCE = 1;
    private static final int NUM_REPLICAS = 3 * TOLERANCE + 1;
    private static final int BASE_PORT = 8081;

    public static void main(String[] args) {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");

        // Parse args
        Config config = Config.fromArgs(args);

        try {
            AtomicBroadcastReplica replica = setupReplica(config);

            // Wait for connections to stabilize
            Thread.sleep(5000);

            // String proposal = "r-" + replica.getId();
            // replica.propose(proposal.getBytes(StandardCharsets.UTF_8));

            while (true) {}

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static AtomicBroadcastReplica setupReplica(Config config) throws IOException, InterruptedException {
        Set<Integer> validators = IntStream.range(0, NUM_REPLICAS)
                .boxed().collect(Collectors.toSet());
        NetworkInfo.ValidatorSet validatorSet = new NetworkInfo.ValidatorSet(validators, TOLERANCE);

        // Initialize message encoders
        MessageEncoder<String> bcEncoder = new BrachaBroadcastMessageEncoder();
        MessageEncoder<String> baEncoder = new MoustefaouiBinaryAgreementMessageEncoder();
        MessageEncoder<String> subsetEncoder = new HoneyBadgerSubsetMessageEncoder(bcEncoder, baEncoder);
        MessageEncoder<String> encoder = new HoneyBadgerMessageEncoder(subsetEncoder);

        // Initialize communication layer
        ServerSocket serverSocket = new ServerSocket(BASE_PORT + config.getReplicaId());
        Map<Integer, TcpTransport.Connection> connections = new TreeMap<>();

        CountDownLatch connectedLatch = new CountDownLatch(config.getReplicaId());
        Thread connThread = new Thread(() -> {
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    int remoteId = new DataInputStream(socket.getInputStream()).readInt();
                    connections.get(remoteId).reconnect(socket);
                    // connectedLatch.countDown();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        connThread.setDaemon(true);
        connThread.start();

        for (int validator: validatorSet.getAllIds()) {
            if (validator == config.getReplicaId()) continue;
            connections.put(validator, new TcpTransport.Connection(config.getReplicaId(), validator, null));
        }

        connectedLatch.await();
        System.out.println("DONE");

        Transport<String> transport = new TcpTransport(connections, NUM_REPLICAS);

        // Load threshsig key and share
        String dir = "src/main/resources";
        ThreshsigUtil threshsigUtil = null;
        try {
            threshsigUtil = ThreshsigUtil.readFromFile(config.getReplicaId(), dir);
        } catch (Exception e) {
            System.err.println("Unable to load threshsig keys from " + dir);
            System.exit(-1);
        }

        ThreshSigUtils sigUtils = new DummyUtils(NUM_REPLICAS);

        // Config params
        EncryptionSchedule encryptionSchedule = new NeverEncrypt();
        Params params = new Params(100L, encryptionSchedule, 8);

        NetworkInfo networkInfo = new NetworkInfo(config.getReplicaId(), validatorSet, sigUtils);

        // Initialize building blocks
        BroadcastFactory bcFactory = new BrachaBroadcastFactory(config.getReplicaId(), networkInfo);
        BinaryAgreementFactory baFactory = new MoustefaouiBinaryAgreementFactory(config.getReplicaId(), networkInfo);
        HoneyBadgerSubsetFactory subsetFactory = new HoneyBadgerSubsetFactory(config.getReplicaId(), networkInfo, bcFactory, baFactory);

        // Initialize atomic broadcast instance
        IAtomicBroadcast instance = new HoneyBadger(config.getReplicaId(), networkInfo, params, subsetFactory);

        // Setup replica
        AtomicBroadcastReplica replica = new AtomicBroadcastReplica(config.getReplicaId(), instance, encoder, transport);
        for (TcpTransport.Connection conn: connections.values())
            conn.startListener(replica);

        return replica;
    }

    private static class Config {

        private final int replicaId;
        private final int numReplicas;
        private final Protocol protocol;

        public static Config fromArgs(String[] args) {
            if (args.length < 1) {
                System.out.println("Use: java MainTcp <replicaId>");
                System.exit(-1);
            }
            return new Config(Integer.parseInt(args[0]), NUM_REPLICAS, Protocol.HONEY_BADGER);
        }

        public Config(int replicaId, int numReplicas, Protocol protocol) {
            this.replicaId = replicaId;
            this.numReplicas = numReplicas;
            this.protocol = protocol;
        }

        public int getReplicaId() {
            return replicaId;
        }

        public int getNumReplicas() {
            return numReplicas;
        }

        public Protocol getProtocol() {
            return protocol;
        }
    }
}
