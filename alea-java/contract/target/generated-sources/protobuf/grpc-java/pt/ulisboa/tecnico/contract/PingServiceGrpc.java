package pt.ulisboa.tecnico.contract;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.40.1)",
    comments = "Source: PingService.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class PingServiceGrpc {

  private PingServiceGrpc() {}

  public static final String SERVICE_NAME = "pt.ulisboa.tecnico.contract.PingService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<pt.ulisboa.tecnico.contract.PingServiceOuterClass.Ping,
      pt.ulisboa.tecnico.contract.PingServiceOuterClass.Pong> getPingMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ping",
      requestType = pt.ulisboa.tecnico.contract.PingServiceOuterClass.Ping.class,
      responseType = pt.ulisboa.tecnico.contract.PingServiceOuterClass.Pong.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<pt.ulisboa.tecnico.contract.PingServiceOuterClass.Ping,
      pt.ulisboa.tecnico.contract.PingServiceOuterClass.Pong> getPingMethod() {
    io.grpc.MethodDescriptor<pt.ulisboa.tecnico.contract.PingServiceOuterClass.Ping, pt.ulisboa.tecnico.contract.PingServiceOuterClass.Pong> getPingMethod;
    if ((getPingMethod = PingServiceGrpc.getPingMethod) == null) {
      synchronized (PingServiceGrpc.class) {
        if ((getPingMethod = PingServiceGrpc.getPingMethod) == null) {
          PingServiceGrpc.getPingMethod = getPingMethod =
              io.grpc.MethodDescriptor.<pt.ulisboa.tecnico.contract.PingServiceOuterClass.Ping, pt.ulisboa.tecnico.contract.PingServiceOuterClass.Pong>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ping"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.ulisboa.tecnico.contract.PingServiceOuterClass.Ping.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.ulisboa.tecnico.contract.PingServiceOuterClass.Pong.getDefaultInstance()))
              .setSchemaDescriptor(new PingServiceMethodDescriptorSupplier("ping"))
              .build();
        }
      }
    }
    return getPingMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static PingServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<PingServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<PingServiceStub>() {
        @java.lang.Override
        public PingServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new PingServiceStub(channel, callOptions);
        }
      };
    return PingServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static PingServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<PingServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<PingServiceBlockingStub>() {
        @java.lang.Override
        public PingServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new PingServiceBlockingStub(channel, callOptions);
        }
      };
    return PingServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static PingServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<PingServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<PingServiceFutureStub>() {
        @java.lang.Override
        public PingServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new PingServiceFutureStub(channel, callOptions);
        }
      };
    return PingServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class PingServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void ping(pt.ulisboa.tecnico.contract.PingServiceOuterClass.Ping request,
        io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.PingServiceOuterClass.Pong> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getPingMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getPingMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                pt.ulisboa.tecnico.contract.PingServiceOuterClass.Ping,
                pt.ulisboa.tecnico.contract.PingServiceOuterClass.Pong>(
                  this, METHODID_PING)))
          .build();
    }
  }

  /**
   */
  public static final class PingServiceStub extends io.grpc.stub.AbstractAsyncStub<PingServiceStub> {
    private PingServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PingServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new PingServiceStub(channel, callOptions);
    }

    /**
     */
    public void ping(pt.ulisboa.tecnico.contract.PingServiceOuterClass.Ping request,
        io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.PingServiceOuterClass.Pong> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getPingMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class PingServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<PingServiceBlockingStub> {
    private PingServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PingServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new PingServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public pt.ulisboa.tecnico.contract.PingServiceOuterClass.Pong ping(pt.ulisboa.tecnico.contract.PingServiceOuterClass.Ping request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getPingMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class PingServiceFutureStub extends io.grpc.stub.AbstractFutureStub<PingServiceFutureStub> {
    private PingServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PingServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new PingServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<pt.ulisboa.tecnico.contract.PingServiceOuterClass.Pong> ping(
        pt.ulisboa.tecnico.contract.PingServiceOuterClass.Ping request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getPingMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_PING = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final PingServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(PingServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_PING:
          serviceImpl.ping((pt.ulisboa.tecnico.contract.PingServiceOuterClass.Ping) request,
              (io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.PingServiceOuterClass.Pong>) responseObserver);
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

  private static abstract class PingServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    PingServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return pt.ulisboa.tecnico.contract.PingServiceOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("PingService");
    }
  }

  private static final class PingServiceFileDescriptorSupplier
      extends PingServiceBaseDescriptorSupplier {
    PingServiceFileDescriptorSupplier() {}
  }

  private static final class PingServiceMethodDescriptorSupplier
      extends PingServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    PingServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (PingServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new PingServiceFileDescriptorSupplier())
              .addMethod(getPingMethod())
              .build();
        }
      }
    }
    return result;
  }
}
