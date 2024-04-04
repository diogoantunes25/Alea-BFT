package pt.ulisboa.tecnico.replica.domain.grpc;

import java.net.InetAddress;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import pt.tecnico.ulisboa.hbbft.Transaction;
import pt.ulisboa.tecnico.contract.ConfirmationServiceGrpc;
import pt.ulisboa.tecnico.contract.StateMachineServiceGrpc;
import pt.ulisboa.tecnico.contract.ClientService.ConfirmRequest;
import pt.ulisboa.tecnico.contract.ClientService.ConfirmResponse;
import pt.ulisboa.tecnico.contract.ConfirmationServiceGrpc.ConfirmationServiceStub;
import pt.ulisboa.tecnico.contract.Setup6Service.*;
import pt.ulisboa.tecnico.replica.domain.Replica;
import pt.ulisboa.tecnico.utils.Utils;
import pt.ulisboa.tecnico.contract.ConfirmationServiceGrpc.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class StateMachineServiceImpl extends StateMachineServiceGrpc.StateMachineServiceImplBase {

    private Replica replica;
    private AtomicBoolean failing = new AtomicBoolean(false);

    // Maps clients to stream observer their transactions must be confirmed to
    private Map<Long, StreamObserver<ConfirmRequest>> confirmationStreamObservers = new ConcurrentHashMap<>();

    // Maps transactions to their ids
    private Map<Transaction, long[]> pending = new ConcurrentHashMap<>();

    private Map<Long, ManagedChannel> clientChannels = new ConcurrentHashMap<>();

    private List<InetAddress> clients;

    private static StreamObserver<ConfirmResponse> defaultStreamObserver = new StreamObserver<ConfirmResponse>() {
            @Override
            public void onNext(ConfirmResponse request) {
                return;
            }

            public void onCompleted() {
                return;
            }

            public void onError(Throwable t) {
                System.out.println("Error on confirmation.");
                t.printStackTrace();
            }
        };


    public StateMachineServiceImpl(Replica me, List<InetAddress> clients) {
        this.clients = clients;
        replica = me;
        replica.setCallback(this::confirm);
        setupConnections(clients);
    }

    public void setupConnections(List<InetAddress> clients) {
        for (int i = 0; i < clients.size(); i++) {
            setupConnection(clients, i);
        }
    }

    private void setupConnection(List<InetAddress> clients, int i) {
            ManagedChannel channel = clientChannels.get(i);
            if (channel != null) {
               channel.shutdown(); 
            }

            channel = ManagedChannelBuilder.forAddress(clients.get(i).getHostAddress(), Replica.BASE_CONFIRMATION_PORT + i)
                            .usePlaintext()
                            .build();

            if (channel == null) {
                System.out.printf("Failed to create channel to client %d\n.", i);
                System.exit(2);
            }

            clientChannels.put((long) i, channel);
    }

    private StreamObserver<ConfirmRequest> getSo(long cid) {
        StreamObserver<ConfirmRequest> so;

        so = confirmationStreamObservers.get(cid);

        if (so == null) {
            so = ConfirmationServiceGrpc.newStub(clientChannels.get(cid)).confirm(defaultStreamObserver);
            confirmationStreamObservers.put((long) cid, so);
        }

        return so;
    }

    // Function that protocol calls when transaction is confirmed
    public Integer confirm(byte[] payload) {
        Transaction t = new Transaction(payload);
        long[] info = pending.remove(t);
        if (info == null) return 0;

        long tid = info[0];
        long cid = info[1];
        Utils.clientConfirm(tid);
        
        // Not confirming to client

        return 0;
    }

    private void handleRequest(ReplicateRequest request) {
        if (pending.size() > replica.maxPending) return; // defensive strategy (drop when getting behind)

        byte[] payload = request.getPayload().toByteArray();
        long[] info = {request.getTid(), request.getClientId()};
        Transaction t = new Transaction(payload);
        Utils.clientSubmit(request.getTid());

        replica.submit(t);
        pending.put(t, info);
    }
    
    @Override
    public StreamObserver<ReplicateRequest> replicate(final StreamObserver<ReplicateResponse> responseStreamObserver) {
        return new StreamObserver<ReplicateRequest>() {

            @Override
            public void onNext(ReplicateRequest request) {
                handleRequest(request);
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
                System.exit(2);
            }

            @Override
            public void onCompleted() {
                System.out.println("Replication request stream closed.");
            }
        };
    }

    @Override
    public void check(AliveRequest request, StreamObserver<AliveResponse> responseStreamObserver) {
        responseStreamObserver.onNext(AliveResponse.getDefaultInstance());
        responseStreamObserver.onCompleted();
    }

    @Override
    public void exit(ExitRequest request, StreamObserver<ExitResponse> responseStreamObserver) {

        System.out.printf("[%s] exit request\n", replica.getId());

        try {
            replica.exit();
            responseStreamObserver.onNext(ExitResponse.getDefaultInstance());
            responseStreamObserver.onCompleted();
        } finally {
            System.exit(0);
        }
    }
}
