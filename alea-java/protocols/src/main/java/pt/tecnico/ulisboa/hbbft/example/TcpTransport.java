package pt.tecnico.ulisboa.hbbft.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.tecnico.ulisboa.hbbft.Transport;
import pt.tecnico.ulisboa.hbbft.example.abc.AtomicBroadcastReplica;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TcpTransport implements Transport<String> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Map<Integer, Connection> connections;
    private final Integer replicas;

    public TcpTransport(
            Map<Integer, Connection> connections,
            Integer replicas
    ) {
        this.connections = connections;
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
        Connection connection = this.connections.get(replicaId);
        connection.send(data.getBytes(StandardCharsets.UTF_8));
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

    public static class Connection {

        private final Logger logger = LoggerFactory.getLogger(this.getClass());

        private final Integer replicaId;
        private final Integer remoteId;

        private Socket socket;
        private DataOutputStream socketOutStream;
        private DataInputStream socketInStream;

        private final Lock connectLock = new ReentrantLock();
        private final Lock sendLock = new ReentrantLock();

        public Connection(Integer replicaId, Integer remoteId, Socket socket) {
            this.replicaId = replicaId;
            this.remoteId = remoteId;
            this.socket = socket;

            // Connect to the remote process or wait for the connection
            if (isToConnect()) connect();

            if (this.socket != null) {
                try {
                    this.socketOutStream = new DataOutputStream(this.socket.getOutputStream());
                    this.socketInStream = new DataInputStream(this.socket.getInputStream());
                } catch (IOException e) {
                    logger.error("Error creating connection to " + remoteId, e);
                }
            }
        }

        public boolean isToConnect() {
            return replicaId < remoteId;
        }

        public void connect() {
            try {
                this.socket = new Socket("127.0.0.1", 8081 + remoteId);
                new DataOutputStream(this.socket.getOutputStream()).writeInt(replicaId);
                logger.info("Connected to " + remoteId);
            } catch (IOException e) {
                // logger.error("Unable to connect to " + remoteId);
                this.waitAndConnect();
            }
        }

        public void send(byte[] data) {
            sendLock.lock();

            boolean abort = false;
            do {
                // If there is a need to reconnect, abort this method
                if (abort) break;

                if (socket != null && socketOutStream != null) {
                    try {
                        socketOutStream.writeInt(data.length);
                        socketOutStream.write(data);
                        break;

                    } catch (IOException e) {
                        closeSocket();
                        waitAndConnect();
                        abort = true;
                    }

                } else {
                    System.out.println("CANT SEND TO " + remoteId);
                    waitAndConnect();
                    abort = true;
                }
            } while (true);

            sendLock.unlock();
        }

        private void waitAndConnect() {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
            reconnect(null);
        }

        public void reconnect(Socket newSocket) {
            connectLock.lock();

            if (socket == null || !socket.isConnected()) {
                if (isToConnect()) connect();
                else socket = newSocket;

                if (socket != null) {
                    try {
                        socketOutStream = new DataOutputStream(socket.getOutputStream());
                        socketInStream = new DataInputStream(socket.getInputStream());
                    } catch (IOException e) {
                        logger.error("Failed to authenticate to replica", e);
                    }
                }
            }

            connectLock.unlock();
        }

        private void closeSocket() {
            connectLock.lock();

            if (socket != null) {
                try {
                    socketOutStream.flush();
                    socketOutStream.close();
                } catch (IOException e) {
                    logger.error("Error closing socket to " + remoteId);
                } catch (NullPointerException e) {
                    logger.error("Socket already closed");
                }
            }

            socket = null;
            socketOutStream = null;
            socketInStream = null;

            connectLock.unlock();
        }

        public void startListener(AtomicBroadcastReplica replica) {
            Thread receiverThread = new Thread(() -> {
                while (true) {
                    if (socket != null && socketInStream != null) {
                        try {
                            // read data length
                            int dataLength = socketInStream.readInt();
                            byte[] data = new byte[dataLength];

                            // read data
                            int read = 0;
                            do {
                                read += socketInStream.read(data, read, dataLength - read);
                            } while (read < dataLength);

                            // Pass message to replica
                            replica.handleMessage(new String(data));

                        } catch (IOException ex) {
                            logger.debug("Closing socket and reconnecting");
                            closeSocket();
                            waitAndConnect();
                        }
                    }
                }
            });
            receiverThread.setDaemon(true);
            receiverThread.start();
        }
    }
}
