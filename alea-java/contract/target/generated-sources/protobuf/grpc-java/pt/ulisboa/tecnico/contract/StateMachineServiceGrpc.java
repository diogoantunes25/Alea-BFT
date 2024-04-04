package pt.ulisboa.tecnico.contract;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.40.1)",
    comments = "Source: Setup6Service.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class StateMachineServiceGrpc {

  private StateMachineServiceGrpc() {}

  public static final String SERVICE_NAME = "pt.ulisboa.tecnico.contract.StateMachineService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<pt.ulisboa.tecnico.contract.Setup6Service.ReplicateRequest,
      pt.ulisboa.tecnico.contract.Setup6Service.ReplicateResponse> getReplicateMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "replicate",
      requestType = pt.ulisboa.tecnico.contract.Setup6Service.ReplicateRequest.class,
      responseType = pt.ulisboa.tecnico.contract.Setup6Service.ReplicateResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING)
  public static io.grpc.MethodDescriptor<pt.ulisboa.tecnico.contract.Setup6Service.ReplicateRequest,
      pt.ulisboa.tecnico.contract.Setup6Service.ReplicateResponse> getReplicateMethod() {
    io.grpc.MethodDescriptor<pt.ulisboa.tecnico.contract.Setup6Service.ReplicateRequest, pt.ulisboa.tecnico.contract.Setup6Service.ReplicateResponse> getReplicateMethod;
    if ((getReplicateMethod = StateMachineServiceGrpc.getReplicateMethod) == null) {
      synchronized (StateMachineServiceGrpc.class) {
        if ((getReplicateMethod = StateMachineServiceGrpc.getReplicateMethod) == null) {
          StateMachineServiceGrpc.getReplicateMethod = getReplicateMethod =
              io.grpc.MethodDescriptor.<pt.ulisboa.tecnico.contract.Setup6Service.ReplicateRequest, pt.ulisboa.tecnico.contract.Setup6Service.ReplicateResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "replicate"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.ulisboa.tecnico.contract.Setup6Service.ReplicateRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.ulisboa.tecnico.contract.Setup6Service.ReplicateResponse.getDefaultInstance()))
              .setSchemaDescriptor(new StateMachineServiceMethodDescriptorSupplier("replicate"))
              .build();
        }
      }
    }
    return getReplicateMethod;
  }

  private static volatile io.grpc.MethodDescriptor<pt.ulisboa.tecnico.contract.Setup6Service.AliveRequest,
      pt.ulisboa.tecnico.contract.Setup6Service.AliveResponse> getCheckMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "check",
      requestType = pt.ulisboa.tecnico.contract.Setup6Service.AliveRequest.class,
      responseType = pt.ulisboa.tecnico.contract.Setup6Service.AliveResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<pt.ulisboa.tecnico.contract.Setup6Service.AliveRequest,
      pt.ulisboa.tecnico.contract.Setup6Service.AliveResponse> getCheckMethod() {
    io.grpc.MethodDescriptor<pt.ulisboa.tecnico.contract.Setup6Service.AliveRequest, pt.ulisboa.tecnico.contract.Setup6Service.AliveResponse> getCheckMethod;
    if ((getCheckMethod = StateMachineServiceGrpc.getCheckMethod) == null) {
      synchronized (StateMachineServiceGrpc.class) {
        if ((getCheckMethod = StateMachineServiceGrpc.getCheckMethod) == null) {
          StateMachineServiceGrpc.getCheckMethod = getCheckMethod =
              io.grpc.MethodDescriptor.<pt.ulisboa.tecnico.contract.Setup6Service.AliveRequest, pt.ulisboa.tecnico.contract.Setup6Service.AliveResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "check"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.ulisboa.tecnico.contract.Setup6Service.AliveRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.ulisboa.tecnico.contract.Setup6Service.AliveResponse.getDefaultInstance()))
              .setSchemaDescriptor(new StateMachineServiceMethodDescriptorSupplier("check"))
              .build();
        }
      }
    }
    return getCheckMethod;
  }

  private static volatile io.grpc.MethodDescriptor<pt.ulisboa.tecnico.contract.Setup6Service.ExitRequest,
      pt.ulisboa.tecnico.contract.Setup6Service.ExitResponse> getExitMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "exit",
      requestType = pt.ulisboa.tecnico.contract.Setup6Service.ExitRequest.class,
      responseType = pt.ulisboa.tecnico.contract.Setup6Service.ExitResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<pt.ulisboa.tecnico.contract.Setup6Service.ExitRequest,
      pt.ulisboa.tecnico.contract.Setup6Service.ExitResponse> getExitMethod() {
    io.grpc.MethodDescriptor<pt.ulisboa.tecnico.contract.Setup6Service.ExitRequest, pt.ulisboa.tecnico.contract.Setup6Service.ExitResponse> getExitMethod;
    if ((getExitMethod = StateMachineServiceGrpc.getExitMethod) == null) {
      synchronized (StateMachineServiceGrpc.class) {
        if ((getExitMethod = StateMachineServiceGrpc.getExitMethod) == null) {
          StateMachineServiceGrpc.getExitMethod = getExitMethod =
              io.grpc.MethodDescriptor.<pt.ulisboa.tecnico.contract.Setup6Service.ExitRequest, pt.ulisboa.tecnico.contract.Setup6Service.ExitResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "exit"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.ulisboa.tecnico.contract.Setup6Service.ExitRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.ulisboa.tecnico.contract.Setup6Service.ExitResponse.getDefaultInstance()))
              .setSchemaDescriptor(new StateMachineServiceMethodDescriptorSupplier("exit"))
              .build();
        }
      }
    }
    return getExitMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static StateMachineServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<StateMachineServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<StateMachineServiceStub>() {
        @java.lang.Override
        public StateMachineServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new StateMachineServiceStub(channel, callOptions);
        }
      };
    return StateMachineServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static StateMachineServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<StateMachineServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<StateMachineServiceBlockingStub>() {
        @java.lang.Override
        public StateMachineServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new StateMachineServiceBlockingStub(channel, callOptions);
        }
      };
    return StateMachineServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static StateMachineServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<StateMachineServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<StateMachineServiceFutureStub>() {
        @java.lang.Override
        public StateMachineServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new StateMachineServiceFutureStub(channel, callOptions);
        }
      };
    return StateMachineServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class StateMachineServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.Setup6Service.ReplicateRequest> replicate(
        io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.Setup6Service.ReplicateResponse> responseObserver) {
      return io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall(getReplicateMethod(), responseObserver);
    }

    /**
     */
    public void check(pt.ulisboa.tecnico.contract.Setup6Service.AliveRequest request,
        io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.Setup6Service.AliveResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCheckMethod(), responseObserver);
    }

    /**
     */
    public void exit(pt.ulisboa.tecnico.contract.Setup6Service.ExitRequest request,
        io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.Setup6Service.ExitResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getExitMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getReplicateMethod(),
            io.grpc.stub.ServerCalls.asyncClientStreamingCall(
              new MethodHandlers<
                pt.ulisboa.tecnico.contract.Setup6Service.ReplicateRequest,
                pt.ulisboa.tecnico.contract.Setup6Service.ReplicateResponse>(
                  this, METHODID_REPLICATE)))
          .addMethod(
            getCheckMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                pt.ulisboa.tecnico.contract.Setup6Service.AliveRequest,
                pt.ulisboa.tecnico.contract.Setup6Service.AliveResponse>(
                  this, METHODID_CHECK)))
          .addMethod(
            getExitMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                pt.ulisboa.tecnico.contract.Setup6Service.ExitRequest,
                pt.ulisboa.tecnico.contract.Setup6Service.ExitResponse>(
                  this, METHODID_EXIT)))
          .build();
    }
  }

  /**
   */
  public static final class StateMachineServiceStub extends io.grpc.stub.AbstractAsyncStub<StateMachineServiceStub> {
    private StateMachineServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected StateMachineServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new StateMachineServiceStub(channel, callOptions);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.Setup6Service.ReplicateRequest> replicate(
        io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.Setup6Service.ReplicateResponse> responseObserver) {
      return io.grpc.stub.ClientCalls.asyncClientStreamingCall(
          getChannel().newCall(getReplicateMethod(), getCallOptions()), responseObserver);
    }

    /**
     */
    public void check(pt.ulisboa.tecnico.contract.Setup6Service.AliveRequest request,
        io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.Setup6Service.AliveResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCheckMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void exit(pt.ulisboa.tecnico.contract.Setup6Service.ExitRequest request,
        io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.Setup6Service.ExitResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getExitMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class StateMachineServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<StateMachineServiceBlockingStub> {
    private StateMachineServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected StateMachineServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new StateMachineServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public pt.ulisboa.tecnico.contract.Setup6Service.AliveResponse check(pt.ulisboa.tecnico.contract.Setup6Service.AliveRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCheckMethod(), getCallOptions(), request);
    }

    /**
     */
    public pt.ulisboa.tecnico.contract.Setup6Service.ExitResponse exit(pt.ulisboa.tecnico.contract.Setup6Service.ExitRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getExitMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class StateMachineServiceFutureStub extends io.grpc.stub.AbstractFutureStub<StateMachineServiceFutureStub> {
    private StateMachineServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected StateMachineServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new StateMachineServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<pt.ulisboa.tecnico.contract.Setup6Service.AliveResponse> check(
        pt.ulisboa.tecnico.contract.Setup6Service.AliveRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCheckMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<pt.ulisboa.tecnico.contract.Setup6Service.ExitResponse> exit(
        pt.ulisboa.tecnico.contract.Setup6Service.ExitRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getExitMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_CHECK = 0;
  private static final int METHODID_EXIT = 1;
  private static final int METHODID_REPLICATE = 2;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final StateMachineServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(StateMachineServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_CHECK:
          serviceImpl.check((pt.ulisboa.tecnico.contract.Setup6Service.AliveRequest) request,
              (io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.Setup6Service.AliveResponse>) responseObserver);
          break;
        case METHODID_EXIT:
          serviceImpl.exit((pt.ulisboa.tecnico.contract.Setup6Service.ExitRequest) request,
              (io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.Setup6Service.ExitResponse>) responseObserver);
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
        case METHODID_REPLICATE:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.replicate(
              (io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.Setup6Service.ReplicateResponse>) responseObserver);
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class StateMachineServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    StateMachineServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return pt.ulisboa.tecnico.contract.Setup6Service.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("StateMachineService");
    }
  }

  private static final class StateMachineServiceFileDescriptorSupplier
      extends StateMachineServiceBaseDescriptorSupplier {
    StateMachineServiceFileDescriptorSupplier() {}
  }

  private static final class StateMachineServiceMethodDescriptorSupplier
      extends StateMachineServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    StateMachineServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (StateMachineServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new StateMachineServiceFileDescriptorSupplier())
              .addMethod(getReplicateMethod())
              .addMethod(getCheckMethod())
              .addMethod(getExitMethod())
              .build();
        }
      }
    }
    return result;
  }
}
