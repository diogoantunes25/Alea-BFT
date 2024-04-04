package pt.ulisboa.tecnico.client.domain.grpc;

import io.grpc.stub.StreamObserver;
import pt.ulisboa.tecnico.client.domain.Client;
import pt.ulisboa.tecnico.contract.ClientService.*;
import pt.ulisboa.tecnico.contract.ConfirmationServiceGrpc.ConfirmationServiceImplBase;

public class ConfirmationService extends ConfirmationServiceImplBase {
    
    private Client client;

    public ConfirmationService(Client c) {
        this.client = c;
    }

    @Override
    public StreamObserver<ConfirmRequest> confirm(final StreamObserver<ConfirmResponse> responseStreamObserver) {
        return new StreamObserver<ConfirmRequest>() {

            @Override
            public void onNext(ConfirmRequest request) {
                client.tryConfirm(request.getTid());
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
                System.exit(2);
            }

            @Override
            public void onCompleted() {
                System.out.println("Confirmation stream closed");
            }
        };
    }

    @Override
    public void check(ClientAliveRequest request, StreamObserver<ClientAliveResponse> responseStreamObserver) {
        responseStreamObserver.onNext(ClientAliveResponse.getDefaultInstance());
        responseStreamObserver.onCompleted();
    }
}
