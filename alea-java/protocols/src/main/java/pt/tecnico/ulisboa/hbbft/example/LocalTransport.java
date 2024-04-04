package pt.tecnico.ulisboa.hbbft.example;

import pt.tecnico.ulisboa.hbbft.Transport;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

public class LocalTransport implements Transport<String> {

    private final Map<Integer, BlockingQueue<String>> messageQueue;

    public LocalTransport(Map<Integer, BlockingQueue<String>> messageQueue) {
        this.messageQueue = messageQueue;
    }

    @Override
    public int countKnownReplicas() {
        return messageQueue.size();
    }

    @Override
    public Collection<Integer> knownReplicaIds() {
        return messageQueue.keySet();
    }

    @Override
    public void sendToReplica(int replicaId, String data) {
        messageQueue.get(replicaId).add(data);
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

        for (int i = 0; i < this.countKnownReplicas(); i++) {
            if (!ignored.contains(i)) {
                this.sendToReplica(i, data);
            }
        }
    }
}
