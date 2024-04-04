package pt.tecnico.ulisboa.hbbft.example.broadcast;

import pt.tecnico.ulisboa.hbbft.MessageEncoder;
import pt.tecnico.ulisboa.hbbft.NetworkInfo;
import pt.tecnico.ulisboa.hbbft.Transport;
import pt.tecnico.ulisboa.hbbft.broadcast.BroadcastFactory;
//import pt.tecnico.ulisboa.hbbft.broadcast.avid.AvidBroadcastFactory;
import pt.tecnico.ulisboa.hbbft.broadcast.avid.AvidBroadcastFactory;
import pt.tecnico.ulisboa.hbbft.broadcast.bracha.BrachaBroadcastFactory;
import pt.tecnico.ulisboa.hbbft.broadcast.echo.EchoBroadcastFactory;
import pt.tecnico.ulisboa.hbbft.broadcast.echo.utils.SignatureUtils;
import pt.tecnico.ulisboa.hbbft.example.RedisTransport;
//import pt.tecnico.ulisboa.hbbft.example.broadcast.avid.AvidBroadcastMessageEncoder;
import pt.tecnico.ulisboa.hbbft.example.broadcast.avid.AvidBroadcastMessageEncoder;
import pt.tecnico.ulisboa.hbbft.example.broadcast.bracha.BrachaBroadcastMessageEncoder;
import pt.tecnico.ulisboa.hbbft.example.broadcast.echo.EchoBroadcastMessageEncoder;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

public class Main {

    private enum Protocol {
        AVID,
        BRACHA,
        ECHO
    }

    private static final int TOLERANCE = 1;
    private static final int NUM_REPLICAS = 3 * TOLERANCE + 1;

    public static void main(String[] args) throws InterruptedException {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info");
        try (JedisPool pool = new JedisPool("192.168.1.117")) {
            // Wait for PubSub listeners to setup
            Thread.sleep(1000);

            Map<Integer, BroadcastReplica> replicas = setupReplicas(pool, Protocol.AVID);
            replicas.get(0).propose("Hello World".getBytes());

            while (true) {
                // Wait forever
            }
        }
    }

    private static Map<Integer, BroadcastReplica> setupReplicas(JedisPool pool, Protocol protocol) {
        CountDownLatch readyLatch = new CountDownLatch(NUM_REPLICAS - 1);

        Set<Integer> validators = new TreeSet<>(Arrays.asList(0, 1, 2, 3));
        NetworkInfo.ValidatorSet validatorSet = new NetworkInfo.ValidatorSet(validators, TOLERANCE);

        // Generate key pairs
        Map<Integer, KeyPair> keyPairs = new TreeMap<>();
        try {
            for (int i=0; i < NUM_REPLICAS; i++)
                keyPairs.put(i, SignatureUtils.generateKeyPair());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        // Initialize message encoder
        MessageEncoder<String> encoder;
        switch (protocol) {
            case AVID: {
                encoder = new AvidBroadcastMessageEncoder();
                break;
            }
            case BRACHA: {
                encoder = new BrachaBroadcastMessageEncoder();
                break;
            }
            case ECHO: {
                encoder = new EchoBroadcastMessageEncoder();
                break;
            }
            default: {
                encoder = new AvidBroadcastMessageEncoder();
            }
        }

        // Initialize transport layer
        Transport<String> transport = new RedisTransport(pool, NUM_REPLICAS);

        Map<Integer, BroadcastReplica> replicas = new TreeMap<>();
        for (int i=0; i < NUM_REPLICAS; i++) {
            NetworkInfo networkInfo = new NetworkInfo(i, validatorSet);

            PrivateKey privateKey = keyPairs.get(i).getPrivate();
            Map<Integer, PublicKey> publicKeys = keyPairs.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getPublic()));

            // Initialize the broadcast factory
            BroadcastFactory factory;
            switch (protocol) {
                case AVID: {
                    factory = new AvidBroadcastFactory(i, networkInfo);
                    break;
                }
                case BRACHA: {
                    factory = new BrachaBroadcastFactory(i, networkInfo);
                    break;
                }
                case ECHO: {
                    factory = new EchoBroadcastFactory(i, networkInfo, privateKey, publicKeys);
                    break;
                }
                default: {
                    factory = new AvidBroadcastFactory(i, networkInfo);
                }
            }

            BroadcastReplica replica = new BroadcastReplica(i, encoder, transport, factory);
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
