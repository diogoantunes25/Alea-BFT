package pt.tecnico.ulisboa.hbbft.example.abc;

import pt.tecnico.ulisboa.hbbft.MessageEncoder;
import pt.tecnico.ulisboa.hbbft.NetworkInfo;
import pt.tecnico.ulisboa.hbbft.Transport;
import pt.tecnico.ulisboa.hbbft.abc.IAtomicBroadcast;
import pt.tecnico.ulisboa.hbbft.abc.acs.AcsAtomicBroadcast;
import pt.tecnico.ulisboa.hbbft.abc.alea.Alea;
import pt.tecnico.ulisboa.hbbft.abc.honeybadger.crypto.AlwaysEncrypt;
import pt.tecnico.ulisboa.hbbft.binaryagreement.moustefaoui.MoustefaouiBinaryAgreementFactory;
import pt.tecnico.ulisboa.hbbft.broadcast.bracha.BrachaBroadcastFactory;
import pt.tecnico.ulisboa.hbbft.example.RedisTransport;
import pt.tecnico.ulisboa.hbbft.example.abc.acs.AcsAtomicBroadcastMessageEncoder;
import pt.tecnico.ulisboa.hbbft.example.abc.alea.AleaMessageEncoder;
import pt.tecnico.ulisboa.hbbft.example.abc.byzness.EchoVBroadcastMessageEncoder;
import pt.tecnico.ulisboa.hbbft.example.binaryagreement.moustefaoui.MoustefaouiBinaryAgreementMessageEncoder;
import pt.tecnico.ulisboa.hbbft.example.broadcast.bracha.BrachaBroadcastMessageEncoder;
import pt.tecnico.ulisboa.hbbft.example.election.CommitteeElectionMessageEncoder;
import pt.tecnico.ulisboa.hbbft.example.subset.dumbo.DumboSubsetMessageEncoder;
import pt.tecnico.ulisboa.hbbft.example.subset.hbbft.HoneyBadgerSubsetMessageEncoder;
import pt.tecnico.ulisboa.hbbft.subset.SubsetFactory;
import pt.tecnico.ulisboa.hbbft.subset.dumbo.DumboSubsetFactory;
import pt.tecnico.ulisboa.hbbft.subset.hbbft.HoneyBadgerSubsetFactory;
import pt.tecnico.ulisboa.hbbft.utils.threshsig.ThreshSigUtils;
import pt.tecnico.ulisboa.hbbft.utils.threshsig.dummy.DummyUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {

    private enum Protocol {
        HONEY_BADGER,
        DUMBO,
        ALEA_BFT
    }

    private static final int TOLERANCE = 1;
    private static final int NUM_REPLICAS = 3 * TOLERANCE + 1;
    private static final int KEY_SIZE = 256;

    public static void main(String[] args) throws InterruptedException {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info");

        try (JedisPool pool = new JedisPool("192.168.1.103")) {
            // Wait for PubSub listeners to setup
            Thread.sleep(2000);

            // Initialize atomic broadcast replicas
            Map<Integer, AtomicBroadcastReplica> replicas = setupReplicas(pool, Protocol.ALEA_BFT);

            replicas.get(0).propose("A".getBytes(StandardCharsets.UTF_8));
            replicas.get(1).propose("B".getBytes(StandardCharsets.UTF_8));
            replicas.get(2).propose("C".getBytes(StandardCharsets.UTF_8));
            replicas.get(3).propose("D".getBytes(StandardCharsets.UTF_8));

            // Wait forever
            while (true) {}
        }
    }

    private static Map<Integer, AtomicBroadcastReplica> setupReplicas(JedisPool pool, Protocol protocol) {
        CountDownLatch readyLatch = new CountDownLatch(NUM_REPLICAS - 1);

        Set<Integer> validators = IntStream.range(0, NUM_REPLICAS).boxed().collect(Collectors.toSet());
        NetworkInfo.ValidatorSet validatorSet = new NetworkInfo.ValidatorSet(validators, TOLERANCE);

        // Initialize message encoder
        MessageEncoder<String> encoder;
        if (protocol == Protocol.ALEA_BFT) {
            encoder = new AleaMessageEncoder(
                    new EchoVBroadcastMessageEncoder(),
                    new MoustefaouiBinaryAgreementMessageEncoder()
            );
        } else {
            MessageEncoder<String> acsEncoder;
            switch (protocol) {
                case DUMBO:
                    acsEncoder = new DumboSubsetMessageEncoder(
                            new BrachaBroadcastMessageEncoder(),
                            new MoustefaouiBinaryAgreementMessageEncoder(),
                            new CommitteeElectionMessageEncoder()
                    ); break;
                case HONEY_BADGER:
                default:
                    acsEncoder = new HoneyBadgerSubsetMessageEncoder(
                            new BrachaBroadcastMessageEncoder(),
                            new MoustefaouiBinaryAgreementMessageEncoder()
                    ); break;

            }
            encoder = new AcsAtomicBroadcastMessageEncoder(acsEncoder);
        }

        // Initialize communication layer
        Transport<String> transport = new RedisTransport(pool, NUM_REPLICAS);

        // Generate threshold keys and shares
        ThreshSigUtils sigUtils = new DummyUtils(NUM_REPLICAS);

        Map<Integer, AtomicBroadcastReplica> replicas = new TreeMap<>();
        for (int replicaId: validatorSet.getAllIds()) {
            NetworkInfo networkInfo = new NetworkInfo(replicaId, validatorSet, sigUtils);

            IAtomicBroadcast instance;
            if (protocol == Protocol.ALEA_BFT) {
                Alea.Params params = new Alea.Params.Builder()
                        .batchSize(8)
                        .benchmark(true)
                        .build();
                instance = new Alea(replicaId, networkInfo, params);

            } else {
                SubsetFactory acsFactory;
                switch (protocol) {
                    case DUMBO:
                        acsFactory = new DumboSubsetFactory(
                                replicaId,
                                networkInfo,
                                new BrachaBroadcastFactory(replicaId, networkInfo),
                                new MoustefaouiBinaryAgreementFactory(replicaId, networkInfo)
                        );
                        break;

                    case HONEY_BADGER:
                    default:
                        acsFactory = new HoneyBadgerSubsetFactory(
                                replicaId,
                                networkInfo,
                                new BrachaBroadcastFactory(replicaId, networkInfo),
                                new MoustefaouiBinaryAgreementFactory(replicaId, networkInfo)
                        ); break;
                }

                AcsAtomicBroadcast.Params params = new AcsAtomicBroadcast.Params.Builder()
                        .batchSize(8)
                        .encryptionSchedule(new AlwaysEncrypt())
                        .committeeSize(networkInfo.getF() + 1)
                        .maxFutureEpochs(100L)
                        .faults(Map.of(1, AcsAtomicBroadcast.Params.Fault.BYZANTINE))
                        .benchmark(true)
                        .maxPayloadSize(4)
                        .build();
                instance = new AcsAtomicBroadcast(replicaId, networkInfo, params, acsFactory);
            }

            // setup replica
            AtomicBroadcastReplica replica = new AtomicBroadcastReplica(replicaId, instance, encoder, transport);
            replicas.put(replicaId, replica);

            // start message listener thread
            Thread listenerThread = new Thread(() -> {
                try (Jedis jedis = pool.getResource()) {
                    String channel = "replica-" + replica.getId();
                    JedisPubSub listener = new JedisPubSub() {
                        @Override
                        public void onMessage(String channel, String message) {
                            replica.handleMessage(message);
                        }
                    };
                    readyLatch.countDown();
                    jedis.subscribe(listener, channel);
                }
            });
            listenerThread.setDaemon(true);
            listenerThread.start();
        }

        try {
            readyLatch.await();
        } catch (InterruptedException ignored) {
        }
        return replicas;
    }
}
