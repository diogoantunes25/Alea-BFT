package pt.ulisboa.tecnico.contract;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.40.1)",
    comments = "Source: InformationCollectorService.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class InformationCollectorServiceGrpc {

  private InformationCollectorServiceGrpc() {}

  public static final String SERVICE_NAME = "pt.ulisboa.tecnico.contract.InformationCollectorService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<pt.ulisboa.tecnico.contract.InformationCollectorServiceOuterClass.ReplicaRequest,
      pt.ulisboa.tecnico.contract.InformationCollectorServiceOuterClass.ReplicaResponse> getReplicaUpdateMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "replicaUpdate",
      requestType = pt.ulisboa.tecnico.contract.InformationCollectorServiceOuterClass.ReplicaRequest.class,
      responseType = pt.ulisboa.tecnico.contract.InformationCollectorServiceOuterClass.ReplicaResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<pt.ulisboa.tecnico.contract.InformationCollectorServiceOuterClass.ReplicaRequest,
      pt.ulisboa.tecnico.contract.InformationCollectorServiceOuterClass.ReplicaResponse> getReplicaUpdateMethod() {
    io.grpc.MethodDescriptor<pt.ulisboa.tecnico.contract.InformationCollectorServiceOuterClass.ReplicaRequest, pt.ulisboa.tecnico.contract.InformationCollectorServiceOuterClass.ReplicaResponse> getReplicaUpdateMethod;
    if ((getReplicaUpdateMethod = InformationCollectorServiceGrpc.getReplicaUpdateMethod) == null) {
      synchronized (InformationCollectorServiceGrpc.class) {
        if ((getReplicaUpdateMethod = InformationCollectorServiceGrpc.getReplicaUpdateMethod) == null) {
          InformationCollectorServiceGrpc.getReplicaUpdateMethod = getReplicaUpdateMethod =
              io.grpc.MethodDescriptor.<pt.ulisboa.tecnico.contract.InformationCollectorServiceOuterClass.ReplicaRequest, pt.ulisboa.tecnico.contract.InformationCollectorServiceOuterClass.ReplicaResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "replicaUpdate"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.ulisboa.tecnico.contract.InformationCollectorServiceOuterClass.ReplicaRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.ulisboa.tecnico.contract.InformationCollectorServiceOuterClass.ReplicaResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InformationCollectorServiceMethodDescriptorSupplier("replicaUpdate"))
              .build();
        }
      }
    }
    return getReplicaUpdateMethod;
  }

  private static volatile io.grpc.MethodDescriptor<pt.ulisboa.tecnico.contract.InformationCollectorServiceOuterClass.ClientRequest,
      pt.ulisboa.tecnico.contract.InformationCollectorServiceOuterClass.ClientResponse> getClientUpdateMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "clientUpdate",
      requestType = pt.ulisboa.tecnico.contract.InformationCollectorServiceOuterClass.ClientRequest.class,
      responseType = pt.ulisboa.tecnico.contract.InformationCollectorServiceOuterClass.ClientResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<pt.ulisboa.tecnico.contract.InformationCollectorServiceOuterClass.ClientRequest,
      pt.ulisboa.tecnico.contract.InformationCollectorServiceOuterClass.ClientResponse> getClientUpdateMethod() {
    io.grpc.MethodDescriptor<pt.ulisboa.tecnico.contract.InformationCollectorServiceOuterClass.ClientRequest, pt.ulisboa.tecnico.contract.InformationCollectorServiceOuterClass.ClientResponse> getClientUpdateMethod;
    if ((getClientUpdateMethod = InformationCollectorServiceGrpc.getClientUpdateMethod) == null) {
      synchronized (InformationCollectorServiceGrpc.class) {
        if ((getClientUpdateMethod = InformationCollectorServiceGrpc.getClientUpdateMethod) == null) {
          InformationCollectorServiceGrpc.getClientUpdateMethod = getClientUpdateMethod =
              io.grpc.MethodDescriptor.<pt.ulisboa.tecnico.contract.InformationCollectorServiceOuterClass.ClientRequest, pt.ulisboa.tecnico.contract.InformationCollectorServiceOuterClass.ClientResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "clientUpdate"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.ulisboa.tecnico.contract.InformationCollectorServiceOuterClass.ClientRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.ulisboa.tecnico.contract.InformationCollectorServiceOuterClass.ClientResponse.getDefaultInstance()))
              .setSchemaDescriptor(new InformationCollectorServiceMethodDescriptorSupplier("clientUpdate"))
              .build();
        }
      }
    }
    return getClientUpdateMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static InformationCollectorServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<InformationCollectorServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<InformationCollectorServiceStub>() {
        @java.lang.Override
        public InformationCollectorServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new InformationCollectorServiceStub(channel, callOptions);
        }
      };
    return InformationCollectorServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static InformationCollectorServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<InformationCollectorServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<InformationCollectorServiceBlockingStub>() {
        @java.lang.Override
        public InformationCollectorServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new InformationCollectorServiceBlockingStub(channel, callOptions);
        }
      };
    return InformationCollectorServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static InformationCollectorServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<InformationCollectorServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<InformationCollectorServiceFutureStub>() {
        @java.lang.Override
        public InformationCollectorServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new InformationCollectorServiceFutureStub(channel, callOptions);
        }
      };
    return InformationCollectorServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class InformationCollectorServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void replicaUpdate(pt.ulisboa.tecnico.contract.InformationCollectorServiceOuterClass.ReplicaRequest request,
        io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.InformationCollectorServiceOuterClass.ReplicaResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getReplicaUpdateMethod(), responseObserver);
    }

    /**
     */
    public void clientUpdate(pt.ulisboa.tecnico.contract.InformationCollectorServiceOuterClass.ClientRequest request,
        io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.InformationCollectorServiceOuterClass.ClientResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getClientUpdateMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getReplicaUpdateMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                pt.ulisboa.tecnico.contract.InformationCollectorServiceOuterClass.ReplicaRequest,
                pt.ulisboa.tecnico.contract.InformationCollectorServiceOuterClass.ReplicaResponse>(
                  this, METHODID_REPLICA_UPDATE)))
          .addMethod(
            getClientUpdateMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                pt.ulisboa.tecnico.contract.InformationCollectorServiceOuterClass.ClientRequest,
                pt.ulisboa.tecnico.contract.InformationCollectorServiceOuterClass.ClientResponse>(
                  this, METHODID_CLIENT_UPDATE)))
          .build();
    }
  }

  /**
   */
  public static final class InformationCollectorServiceStub extends io.grpc.stub.AbstractAsyncStub<InformationCollectorServiceStub> {
    private InformationCollectorServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected InformationCollectorServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new InformationCollectorServiceStub(channel, callOptions);
    }

    /**
     */
    public void replicaUpdate(pt.ulisboa.tecnico.contract.InformationCollectorServiceOuterClass.ReplicaRequest request,
        io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.InformationCollectorServiceOuterClass.ReplicaResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getReplicaUpdateMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void clientUpdate(pt.ulisboa.tecnico.contract.InformationCollectorServiceOuterClass.ClientRequest request,
        io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.InformationCollectorServiceOuterClass.ClientResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getClientUpdateMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class InformationCollectorServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<InformationCollectorServiceBlockingStub> {
    private InformationCollectorServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected InformationCollectorServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new InformationCollectorServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public pt.ulisboa.tecnico.contract.InformationCollectorServiceOuterClass.ReplicaResponse replicaUpdate(pt.ulisboa.tecnico.contract.InformationCollectorServiceOuterClass.ReplicaRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getReplicaUpdateMethod(), getCallOptions(), request);
    }

    /**
     */
    public pt.ulisboa.tecnico.contract.InformationCollectorServiceOuterClass.ClientResponse clientUpdate(pt.ulisboa.tecnico.contract.InformationCollectorServiceOuterClass.ClientRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getClientUpdateMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class InformationCollectorServiceFutureStub extends io.grpc.stub.AbstractFutureStub<InformationCollectorServiceFutureStub> {
    private InformationCollectorServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected InformationCollectorServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new InformationCollectorServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<pt.ulisboa.tecnico.contract.InformationCollectorServiceOuterClass.ReplicaResponse> replicaUpdate(
        pt.ulisboa.tecnico.contract.InformationCollectorServiceOuterClass.ReplicaRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getReplicaUpdateMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<pt.ulisboa.tecnico.contract.InformationCollectorServiceOuterClass.ClientResponse> clientUpdate(
        pt.ulisboa.tecnico.contract.InformationCollectorServiceOuterClass.ClientRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getClientUpdateMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_REPLICA_UPDATE = 0;
  private static final int METHODID_CLIENT_UPDATE = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final InformationCollectorServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(InformationCollectorServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_REPLICA_UPDATE:
          serviceImpl.replicaUpdate((pt.ulisboa.tecnico.contract.InformationCollectorServiceOuterClass.ReplicaRequest) request,
              (io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.InformationCollectorServiceOuterClass.ReplicaResponse>) responseObserver);
          break;
        case METHODID_CLIENT_UPDATE:
          serviceImpl.clientUpdate((pt.ulisboa.tecnico.contract.InformationCollectorServiceOuterClass.ClientRequest) request,
              (io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.InformationCollectorServiceOuterClass.ClientResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class InformationCollectorServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    InformationCollectorServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return pt.ulisboa.tecnico.contract.InformationCollectorServiceOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("InformationCollectorService");
    }
  }

  private static final class InformationCollectorServiceFileDescriptorSupplier
      extends InformationCollectorServiceBaseDescriptorSupplier {
    InformationCollectorServiceFileDescriptorSupplier() {}
  }

  private static final class InformationCollectorServiceMethodDescriptorSupplier
      extends InformationCollectorServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    InformationCollectorServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (InformationCollectorServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new InformationCollectorServiceFileDescriptorSupplier())
              .addMethod(getReplicaUpdateMethod())
              .addMethod(getClientUpdateMethod())
              .build();
        }
      }
    }
    return result;
  }
}
