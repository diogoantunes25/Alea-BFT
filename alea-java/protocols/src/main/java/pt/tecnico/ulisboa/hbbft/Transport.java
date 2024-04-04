package pt.tecnico.ulisboa.hbbft;

import java.util.Collection;
import java.util.stream.IntStream;

/**
 * The transport used to control messaging between replicas
 * as well as clients.
 *
 * @param <T> the encoded message type
 */
public interface Transport<T> {
    /**
     * Obtains the number of replicas known by the
     * transport layer.
     *
     * @return the known number of replicas
     */
    int countKnownReplicas();

    /**
     * Obtains an {@link Collection} populated with the
     * known replica ID numbers.
     *
     * @return a stream of known replica IDs
     */
    Collection<Integer> knownReplicaIds();

    /**
     * Sends a message to the replica with the given
     * replica ID number.
     *
     * @param replicaId the replica to send the message
     * @param data the encoded message to send
     */
    void sendToReplica(int replicaId, T data);

    /**
     * Sends a message to the client with the given
     * client ID number.
     *
     * @param clientId the client to send the message
     * @param data the encoded message to send
     */
    void sendToClient(int clientId, T data);

    /**
     * Multicasts a message to all known replicas.
     *
     * @param data the encoded message to multicast
     * @param ignoredReplicas the replicas to ignored in
     *                        the multicast operation
     */
    void multicast(T data, int... ignoredReplicas);
}
