package pt.tecnico.ulisboa.hbbft.example.subset;

import pt.tecnico.ulisboa.hbbft.MessageEncoder;
import pt.tecnico.ulisboa.hbbft.NetworkInfo;
import pt.tecnico.ulisboa.hbbft.Transport;
import pt.tecnico.ulisboa.hbbft.binaryagreement.moustefaoui.MoustefaouiBinaryAgreementFactory;
import pt.tecnico.ulisboa.hbbft.broadcast.bracha.BrachaBroadcastFactory;
import pt.tecnico.ulisboa.hbbft.example.RedisTransport;
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
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {

    public enum Mode {
        HONEY_BADGER,
        DUMBO
    }

    private static final int TOLERANCE = 1;
    private static final int NUM_REPLICAS = 4;
    private static final int KEY_SIZE = 256;

    public static void main(String[] args) throws InterruptedException {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info");

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxWaitMillis(2000);
        poolConfig.setMaxTotal(30);
        try (JedisPool pool = new JedisPool(poolConfig, "192.168.1.103")) {
            // Wait for PubSub listeners to setup
            Thread.sleep(1000);

            Map<Integer, SubsetReplica> replicas = setupReplicas(pool, Mode.HONEY_BADGER);

            replicas.get(0).propose("Hello".getBytes());
            replicas.get(1).propose("World".getBytes());
            replicas.get(3).propose("Bar".getBytes());
            replicas.get(2).propose("Foo".getBytes());
            //replicas.get(5).propose("Alea".getBytes());
            //replicas.get(4).propose("HoneyBadger".getBytes());
            //replicas.get(6).propose("Dumbo2".getBytes());

            while (true) {
                // Wait forever
            }
        }

        /*Map<Integer, SubsetReplica> replicas = setupReplicas(pool, Mode.DUMBO_2);

        replicas.get(0).propose("Hello".getBytes());
        //replicas.get(1).propose("World".getBytes());
        Thread.sleep(3000);
        replicas.get(3).propose("Bar".getBytes());
        replicas.get(2).propose("Foo".getBytes());

        // Wait forever
        while (true) {
        }*/
    }

    private static Map<Integer, SubsetReplica> setupReplicas(JedisPool pool, Mode protocol) {
        CountDownLatch readyLatch = new CountDownLatch(NUM_REPLICAS - 1);

        Set<Integer> validators = IntStream.range(0, NUM_REPLICAS).boxed().collect(Collectors.toSet());
        NetworkInfo.ValidatorSet validatorSet = new NetworkInfo.ValidatorSet(validators, TOLERANCE);

        // Initialize message encoder
        MessageEncoder<String> encoder;
        switch (protocol) {
            case HONEY_BADGER:
                encoder = new HoneyBadgerSubsetMessageEncoder(
                        new BrachaBroadcastMessageEncoder(),
                        //new AvidBroadcastMessageEncoder(),
                        new MoustefaouiBinaryAgreementMessageEncoder()
                ); break;
            case DUMBO:
            default:
                encoder = new DumboSubsetMessageEncoder(
                        new BrachaBroadcastMessageEncoder(),
                        new MoustefaouiBinaryAgreementMessageEncoder(),
                        new CommitteeElectionMessageEncoder()
                ); break;
        }

        // TODO Redis Transport
        Transport<String> transport = new RedisTransport(pool, NUM_REPLICAS);

        //Map<Integer, BlockingQueue<String>> messageQueues = new ConcurrentHashMap<>();
        //Transport<String> transport = new LocalTransport(messageQueues);

        // Generate threshold keys and shares
        ThreshSigUtils sigUtils = new DummyUtils(NUM_REPLICAS);

        Map<Integer, SubsetReplica> replicas = new TreeMap<>();
        for (int replicaId: validatorSet.getAllIds()) {
            NetworkInfo networkInfo = new NetworkInfo(replicaId, validatorSet, sigUtils);

            // Initialize asynchronous common subset factory
            SubsetFactory subsetFactory;
            switch (protocol) {
                case HONEY_BADGER:
                    subsetFactory = new HoneyBadgerSubsetFactory(
                            replicaId,
                            networkInfo,
                            //new AvidBroadcastFactory(replicaId, networkInfo),
                            new BrachaBroadcastFactory(replicaId, networkInfo),
                            new MoustefaouiBinaryAgreementFactory(replicaId, networkInfo)
                    ); break;
                case DUMBO:
                default:
                    subsetFactory = new DumboSubsetFactory(
                            replicaId,
                            networkInfo,
                            new BrachaBroadcastFactory(replicaId, networkInfo),
                            new MoustefaouiBinaryAgreementFactory(replicaId, networkInfo)
                    ); break;
            }

            // Setup replica
            SubsetReplica replica = new SubsetReplica(replicaId, encoder, transport, subsetFactory);
            replicas.put(replica.getId(), replica);

            // Start message listener thread
            /*BlockingQueue<String> queue = messageQueues.computeIfAbsent(replicaId, id -> new LinkedBlockingQueue<>());
            Thread listenerThread = new Thread(() -> {
                try {
                    replica.handleMessage(queue.take());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            readyLatch.countDown();
            listenerThread.setDaemon(true);
            listenerThread.start();*/

            // Start message listener thread
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
