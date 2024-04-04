package pt.tecnico.ulisboa.hbbft.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.tecnico.ulisboa.hbbft.Transport;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RedisTransport implements Transport<String> {

    private final Logger logger = LoggerFactory.getLogger("Transport");

    private final JedisPool pool;
    private final int replicas;

    private final Lock sendLock = new ReentrantLock();

    public RedisTransport(JedisPool pool, int replicas) {
        this.pool = pool;
        this.replicas = replicas;
    }

    @Override
    public int countKnownReplicas() {
        return this.replicas;
    }

    @Override
    public Collection<Integer> knownReplicaIds() {
        return Stream.iterate(0, n -> n + 1).limit(this.replicas).collect(Collectors.toList());
    }

    @Override
    public void sendToReplica(int replicaId, String data) {
        //logger.debug("SEND: REPLICA -> {}: {}", replicaId, data);

        String channel = "replica-" + replicaId;
        sendLock.lock();
        try (Jedis jedis = this.pool.getResource()) {
            jedis.publish(channel, data);
        } finally {
            sendLock.unlock();
        }
    }

    @Override
    public void sendToClient(int clientId, String data) {
        // TODO
    }

    @Override
    public void multicast(String data, int... ignoredReplicas) {
        Set<Integer> ignored = new HashSet<>(ignoredReplicas.length);
        for (int id : ignoredReplicas) {
            ignored.add(id);
        }

        for (int i = 0; i < this.replicas; i++) {
            if (!ignored.contains(i)) {
                this.sendToReplica(i, data);
            }
        }
    }
}
