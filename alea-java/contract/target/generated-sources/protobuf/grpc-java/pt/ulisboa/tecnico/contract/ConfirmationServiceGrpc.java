package pt.ulisboa.tecnico.contract;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.40.1)",
    comments = "Source: ClientService.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class ConfirmationServiceGrpc {

  private ConfirmationServiceGrpc() {}

  public static final String SERVICE_NAME = "pt.ulisboa.tecnico.contract.ConfirmationService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<pt.ulisboa.tecnico.contract.ClientService.ConfirmRequest,
      pt.ulisboa.tecnico.contract.ClientService.ConfirmResponse> getConfirmMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "confirm",
      requestType = pt.ulisboa.tecnico.contract.ClientService.ConfirmRequest.class,
      responseType = pt.ulisboa.tecnico.contract.ClientService.ConfirmResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING)
  public static io.grpc.MethodDescriptor<pt.ulisboa.tecnico.contract.ClientService.ConfirmRequest,
      pt.ulisboa.tecnico.contract.ClientService.ConfirmResponse> getConfirmMethod() {
    io.grpc.MethodDescriptor<pt.ulisboa.tecnico.contract.ClientService.ConfirmRequest, pt.ulisboa.tecnico.contract.ClientService.ConfirmResponse> getConfirmMethod;
    if ((getConfirmMethod = ConfirmationServiceGrpc.getConfirmMethod) == null) {
      synchronized (ConfirmationServiceGrpc.class) {
        if ((getConfirmMethod = ConfirmationServiceGrpc.getConfirmMethod) == null) {
          ConfirmationServiceGrpc.getConfirmMethod = getConfirmMethod =
              io.grpc.MethodDescriptor.<pt.ulisboa.tecnico.contract.ClientService.ConfirmRequest, pt.ulisboa.tecnico.contract.ClientService.ConfirmResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "confirm"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.ulisboa.tecnico.contract.ClientService.ConfirmRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.ulisboa.tecnico.contract.ClientService.ConfirmResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ConfirmationServiceMethodDescriptorSupplier("confirm"))
              .build();
        }
      }
    }
    return getConfirmMethod;
  }

  private static volatile io.grpc.MethodDescriptor<pt.ulisboa.tecnico.contract.ClientService.ClientAliveRequest,
      pt.ulisboa.tecnico.contract.ClientService.ClientAliveResponse> getCheckMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "check",
      requestType = pt.ulisboa.tecnico.contract.ClientService.ClientAliveRequest.class,
      responseType = pt.ulisboa.tecnico.contract.ClientService.ClientAliveResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<pt.ulisboa.tecnico.contract.ClientService.ClientAliveRequest,
      pt.ulisboa.tecnico.contract.ClientService.ClientAliveResponse> getCheckMethod() {
    io.grpc.MethodDescriptor<pt.ulisboa.tecnico.contract.ClientService.ClientAliveRequest, pt.ulisboa.tecnico.contract.ClientService.ClientAliveResponse> getCheckMethod;
    if ((getCheckMethod = ConfirmationServiceGrpc.getCheckMethod) == null) {
      synchronized (ConfirmationServiceGrpc.class) {
        if ((getCheckMethod = ConfirmationServiceGrpc.getCheckMethod) == null) {
          ConfirmationServiceGrpc.getCheckMethod = getCheckMethod =
              io.grpc.MethodDescriptor.<pt.ulisboa.tecnico.contract.ClientService.ClientAliveRequest, pt.ulisboa.tecnico.contract.ClientService.ClientAliveResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "check"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.ulisboa.tecnico.contract.ClientService.ClientAliveRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.ulisboa.tecnico.contract.ClientService.ClientAliveResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ConfirmationServiceMethodDescriptorSupplier("check"))
              .build();
        }
      }
    }
    return getCheckMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static ConfirmationServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ConfirmationServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ConfirmationServiceStub>() {
        @java.lang.Override
        public ConfirmationServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ConfirmationServiceStub(channel, callOptions);
        }
      };
    return ConfirmationServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static ConfirmationServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ConfirmationServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ConfirmationServiceBlockingStub>() {
        @java.lang.Override
        public ConfirmationServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ConfirmationServiceBlockingStub(channel, callOptions);
        }
      };
    return ConfirmationServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static ConfirmationServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ConfirmationServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ConfirmationServiceFutureStub>() {
        @java.lang.Override
        public ConfirmationServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ConfirmationServiceFutureStub(channel, callOptions);
        }
      };
    return ConfirmationServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class ConfirmationServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.ClientService.ConfirmRequest> confirm(
        io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.ClientService.ConfirmResponse> responseObserver) {
      return io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall(getConfirmMethod(), responseObserver);
    }

    /**
     */
    public void check(pt.ulisboa.tecnico.contract.ClientService.ClientAliveRequest request,
        io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.ClientService.ClientAliveResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCheckMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getConfirmMethod(),
            io.grpc.stub.ServerCalls.asyncClientStreamingCall(
              new MethodHandlers<
                pt.ulisboa.tecnico.contract.ClientService.ConfirmRequest,
                pt.ulisboa.tecnico.contract.ClientService.ConfirmResponse>(
                  this, METHODID_CONFIRM)))
          .addMethod(
            getCheckMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                pt.ulisboa.tecnico.contract.ClientService.ClientAliveRequest,
                pt.ulisboa.tecnico.contract.ClientService.ClientAliveResponse>(
                  this, METHODID_CHECK)))
          .build();
    }
  }

  /**
   */
  public static final class ConfirmationServiceStub extends io.grpc.stub.AbstractAsyncStub<ConfirmationServiceStub> {
    private ConfirmationServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ConfirmationServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ConfirmationServiceStub(channel, callOptions);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.ClientService.ConfirmRequest> confirm(
        io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.ClientService.ConfirmResponse> responseObserver) {
      return io.grpc.stub.ClientCalls.asyncClientStreamingCall(
          getChannel().newCall(getConfirmMethod(), getCallOptions()), responseObserver);
    }

    /**
     */
    public void check(pt.ulisboa.tecnico.contract.ClientService.ClientAliveRequest request,
        io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.ClientService.ClientAliveResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCheckMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class ConfirmationServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<ConfirmationServiceBlockingStub> {
    private ConfirmationServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ConfirmationServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ConfirmationServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public pt.ulisboa.tecnico.contract.ClientService.ClientAliveResponse check(pt.ulisboa.tecnico.contract.ClientService.ClientAliveRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCheckMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class ConfirmationServiceFutureStub extends io.grpc.stub.AbstractFutureStub<ConfirmationServiceFutureStub> {
    private ConfirmationServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ConfirmationServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ConfirmationServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<pt.ulisboa.tecnico.contract.ClientService.ClientAliveResponse> check(
        pt.ulisboa.tecnico.contract.ClientService.ClientAliveRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCheckMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_CHECK = 0;
  private static final int METHODID_CONFIRM = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final ConfirmationServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(ConfirmationServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_CHECK:
          serviceImpl.check((pt.ulisboa.tecnico.contract.ClientService.ClientAliveRequest) request,
              (io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.ClientService.ClientAliveResponse>) responseObserver);
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
        case METHODID_CONFIRM:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.confirm(
              (io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.ClientService.ConfirmResponse>) responseObserver);
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class ConfirmationServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    ConfirmationServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return pt.ulisboa.tecnico.contract.ClientService.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("ConfirmationService");
    }
  }

  private static final class ConfirmationServiceFileDescriptorSupplier
      extends ConfirmationServiceBaseDescriptorSupplier {
    ConfirmationServiceFileDescriptorSupplier() {}
  }

  private static final class ConfirmationServiceMethodDescriptorSupplier
      extends ConfirmationServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    ConfirmationServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (ConfirmationServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new ConfirmationServiceFileDescriptorSupplier())
              .addMethod(getConfirmMethod())
              .addMethod(getCheckMethod())
              .build();
        }
      }
    }
    return result;
  }
}
