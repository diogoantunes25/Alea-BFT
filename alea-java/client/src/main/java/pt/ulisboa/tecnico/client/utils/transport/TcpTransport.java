package pt.ulisboa.tecnico.client.utils.transport;

import jdk.net.ExtendedSocketOptions;
import pt.tecnico.ulisboa.hbbft.Transport;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Generalization of TcpTransport available in the replica project
 * Responsible is the class to which the message handling is delegated.
 */
public class TcpTransport<R extends Responsible> implements Transport<String> {

    private R me;

    private final List<InetAddress> replicas;
    private final Map<Integer, Connection<R>> connections = new HashMap<>();

    public TcpTransport(R me, List<InetAddress> replicas, Integer numChannels, int port) {
        this.replicas = replicas;
        try {
            init(me, numChannels, port);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void init(R me, Integer numChannels, int port) throws IOException, InterruptedException {
        this.me = me;
        ServerSocket serverSocket = new ServerSocket(port);
        serverSocket.setOption(ExtendedSocketOptions.TCP_QUICKACK, true);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < replicas.size(); i++) {
            if (i == me.getId()) continue;
            for (int cid=0; cid < numChannels; cid++) {
                int key = cid * replicas.size() + i;
                connections.put(key, new Connection<R>(cid, me, i, replicas.get(i).getHostAddress()));
            }
        }

        long missing = connections.values().stream().filter(connection -> !connection.shouldConnect()).count();

        while (missing > 0) {
            try {
                Socket socket = serverSocket.accept();
                socket.setTcpNoDelay(true);

                int remoteId = new DataInputStream(socket.getInputStream()).readInt();
                int cid = new DataInputStream(socket.getInputStream()).readInt();
                int key = cid * replicas.size() + remoteId;

                if (connections.containsKey(key)) connections.get(key).connect(socket);

                missing--;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Collection<Connection<R>> getConnections() {
        return this.connections.values();
    }

    @Override
    public int countKnownReplicas() {
        return this.replicas.size();
    }

    public int getMyId() {
        return this.me.getId();
    }

    @Override
    public Collection<Integer> knownReplicaIds() {
        return IntStream.range(0, replicas.size()).boxed().collect(Collectors.toSet());
    }

    @Override
    public void sendToReplica(int replicaId, String data) {
        this.sendToReplica(replicaId, 0, data);
    }

    public void sendToReplica(int replica, int cid, String data) {
        int key = cid*countKnownReplicas() + replica;
        try {
            this.connections.get(key).send(data.getBytes(StandardCharsets.UTF_8));
        } catch (NullPointerException e) {
            System.out.printf("Key: %d%n", key);
            System.out.printf("CID: %d%n", cid);
            System.out.printf("Replica: %d%n", replica);
            System.out.println(connections.keySet());
            System.exit(1);
        }
    }

    @Override
    public void sendToClient(int i, String s) {
        // NOP
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
