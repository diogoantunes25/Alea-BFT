package pt.tecnico.ulisboa.hbbft.example.election;

import pt.tecnico.ulisboa.hbbft.MessageEncoder;
import pt.tecnico.ulisboa.hbbft.NetworkInfo;
import pt.tecnico.ulisboa.hbbft.Transport;
import pt.tecnico.ulisboa.hbbft.example.RedisTransport;
import pt.tecnico.ulisboa.hbbft.utils.threshsig.ThreshSigUtils;
import pt.tecnico.ulisboa.hbbft.utils.threshsig.dummy.DummyUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

import java.util.*;
import java.util.concurrent.CountDownLatch;

public class Main {

    private static final int TOLERANCE = 1;
    private static final int NUM_REPLICAS = 3 * TOLERANCE + 1;
    private static final int KEY_SIZE = 256;
    private static final int COMMITTEE_SIZE = NUM_REPLICAS/2;

    public static void main(String[] args) throws InterruptedException {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info");
        try (JedisPool pool = new JedisPool("192.168.1.93")) {
            // Wait for PubSub listeners to setup
            Thread.sleep(1000);

            Map<Integer, CommitteeElectionReplica> replicas = setupReplicas(pool);
            for (CommitteeElectionReplica replica : replicas.values()) {
                replica.propose();
            }

            // Wait forever
            while (true) {}
        }
    }

    private static Map<Integer, CommitteeElectionReplica> setupReplicas(JedisPool pool) {
        CountDownLatch readyLatch = new CountDownLatch(NUM_REPLICAS - 1);

        Set<Integer> validators = new TreeSet<>(Arrays.asList(0, 1, 2, 3));
        NetworkInfo.ValidatorSet validatorSet = new NetworkInfo.ValidatorSet(validators, TOLERANCE);

        // Generate threshold keys and shares
        ThreshSigUtils sigUtils = new DummyUtils(NUM_REPLICAS);

        MessageEncoder<String> encoder = new CommitteeElectionMessageEncoder();
        Transport<String> transport = new RedisTransport(pool, NUM_REPLICAS);

        Map<Integer, CommitteeElectionReplica> replicas = new TreeMap<>();
        for (int i=0; i < NUM_REPLICAS; i++) {
            CommitteeElectionReplica replica = new CommitteeElectionReplica(
                    i, new NetworkInfo(i, validatorSet), sigUtils, COMMITTEE_SIZE, encoder, transport);
            replicas.put(replica.getId(), replica);

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
