package pt.tecnico.ulisboa.hbbft.example.broadcast;

import pt.tecnico.ulisboa.hbbft.MessageEncoder;
import pt.tecnico.ulisboa.hbbft.NetworkInfo;
import pt.tecnico.ulisboa.hbbft.Transport;
import pt.tecnico.ulisboa.hbbft.example.RedisTransport;
import pt.tecnico.ulisboa.hbbft.example.broadcast.avid.AvidBroadcastMessageEncoder;
import pt.tecnico.ulisboa.hbbft.example.broadcast.avid.AvidBroadcastReplica;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

public class Main2 {

    private static final int TOLERANCE = 1;
    private static final int NUM_REPLICAS = 3 * TOLERANCE + 1;

    public static void main(String[] args) throws InterruptedException {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");

        if (args.length < 1) {
            System.out.println("Use: java Main <replicaId>");
            System.exit(-1);
        }
        int replicaId = Integer.parseInt(args[0]);

        try (JedisPool pool = new JedisPool("192.168.1.93")) {
            // Wait for PubSub listeners to setup
            Thread.sleep(1000);

            AvidBroadcastReplica replica = setupReplica(replicaId, pool);
            if (replicaId == 0) {
                replica.propose("Hello World".getBytes());
            }
        }
    }

    private static AvidBroadcastReplica setupReplica(Integer replicaId, JedisPool pool) {
        Set<Integer> validators = new TreeSet<>(Arrays.asList(0, 1, 2, 3));
        NetworkInfo.ValidatorSet validatorSet = new NetworkInfo.ValidatorSet(validators, TOLERANCE);
        NetworkInfo networkInfo = new NetworkInfo(replicaId, validatorSet);

        MessageEncoder<String> encoder = new AvidBroadcastMessageEncoder();
        Transport<String> transport = new RedisTransport(pool, NUM_REPLICAS);

        AvidBroadcastReplica replica = new AvidBroadcastReplica(
                replicaId, networkInfo, encoder, transport);

        Thread listenerThread = new Thread(() -> {
            try (Jedis jedis = pool.getResource()) {
                String channel = "replica-" + replica.getId();
                JedisPubSub listener = new JedisPubSub() {
                    @Override
                    public void onMessage(String channel, String message) {
                        replica.handleMessage(message);
                    }
                };
                jedis.subscribe(listener, channel);
            }
        });
        listenerThread.setDaemon(true);
        listenerThread.start();

        return replica;
    }
}
