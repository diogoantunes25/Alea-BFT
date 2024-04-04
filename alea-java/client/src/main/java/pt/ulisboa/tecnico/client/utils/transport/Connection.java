package pt.ulisboa.tecnico.client.utils.transport;

import jdk.net.ExtendedSocketOptions;
import pt.ulisboa.tecnico.replica.domain.Replica;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Generalization of Connection available in the replica project
 * Responsible is the class to which the message handling is delegated.
 */
public class Connection<R extends Responsible> {

    private final Integer cid;
    private final R me;
    private final int other;

    private Socket socket;
    private DataOutputStream socketOutStream;
    private DataInputStream socketInStream;

    private final Lock connectLock = new ReentrantLock();
    private final Lock sendLock = new ReentrantLock();

    private Listener listenerThread;
    private R listener;
    private ExecutorService workers;

    private String otherAddress;

    public Connection(R me, int other, String otherAddress) {
        this(0, me, other, otherAddress);
    }

    public Connection(Integer cid, R me, int other, String otherAddress) {
        this.cid = cid;
        this.me = me;
        this.other = other;
        this.otherAddress = otherAddress;

        if (this.shouldConnect()) {
            connect();
        }
    }

    public Boolean shouldConnect() {
        return me.getId() < other;
    }

    public void setListener(R responsible) {

        this.listener = responsible;

        if (this.listenerThread == null || !this.listenerThread.isAlive()) {
            this.listenerThread = new Listener();
            this.listenerThread.setDaemon(true);
            this.listenerThread.start();

            this.workers =  Executors.newCachedThreadPool();
        }
    }

    private void connect() {
        boolean done = false;
        while (!done) {
            try {
                this.socket = new Socket(otherAddress, Replica.BASE_REPLICATION_PORT + other);
                this.socket.setTcpNoDelay(true);
                socket.setOption(ExtendedSocketOptions.TCP_QUICKACK, true);
                this.socketOutStream = new DataOutputStream(socket.getOutputStream());
                this.socketInStream = new DataInputStream(socket.getInputStream());
                this.socketOutStream.writeInt(me.getId());
                this.socketOutStream.writeInt(cid);
                done = true;
            } catch (IOException e) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        }
    }

    public void connect(Socket newSocket) {
        if (socket == null || !socket.isConnected()) {
            this.socket = newSocket;
            try {
                this.socketOutStream = new DataOutputStream(socket.getOutputStream());
                this.socketInStream = new DataInputStream(socket.getInputStream());
            } catch (IOException e) {
                System.out.println("Failed to authenticate to replica");
            }
        }
    }

    public void disconnect() {
        connectLock.lock();

        try {
            if (socket != null) {
                socketOutStream.flush();
                socketOutStream.close();
            }

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            connectLock.unlock();
        }
    }

    public void send(byte[] data) {
        sendLock.lock();
        try {
            this.socketOutStream.writeInt(data.length);
            this.socketOutStream.write(data);
            this.socketOutStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            disconnect();
        } finally {
            sendLock.unlock();
        }
    }

    private class Listener extends Thread {
        private AtomicBoolean done = new AtomicBoolean(false);

        public void run() {
            while (!done.get()) {
                try {
                    // read data length
                    int dataLength = socketInStream.readInt();
                    byte[] data = new byte[dataLength];

                    // read data
                    int read = 0;
                    do {
                        read += socketInStream.read(data, read, dataLength - read);
                    } while (read < dataLength);


                    workers.submit(() -> {
                        try {
                            listener.handleMessage(new String(data));
                        } catch (Throwable t) {
                            t.printStackTrace();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Closing socket.");
                    disconnect();
                    break;
                }
            }
            disconnect();
        }

        public void halt() {
            done.set(true);
        }
    }
}
