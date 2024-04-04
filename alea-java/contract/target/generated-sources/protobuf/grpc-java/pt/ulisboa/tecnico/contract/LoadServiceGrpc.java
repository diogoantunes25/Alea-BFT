package pt.ulisboa.tecnico.contract;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.40.1)",
    comments = "Source: LoadService.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class LoadServiceGrpc {

  private LoadServiceGrpc() {}

  public static final String SERVICE_NAME = "pt.ulisboa.tecnico.contract.LoadService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<pt.ulisboa.tecnico.contract.LoadServiceOuterClass.StartRequest,
      pt.ulisboa.tecnico.contract.LoadServiceOuterClass.StartResponse> getStartMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "start",
      requestType = pt.ulisboa.tecnico.contract.LoadServiceOuterClass.StartRequest.class,
      responseType = pt.ulisboa.tecnico.contract.LoadServiceOuterClass.StartResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<pt.ulisboa.tecnico.contract.LoadServiceOuterClass.StartRequest,
      pt.ulisboa.tecnico.contract.LoadServiceOuterClass.StartResponse> getStartMethod() {
    io.grpc.MethodDescriptor<pt.ulisboa.tecnico.contract.LoadServiceOuterClass.StartRequest, pt.ulisboa.tecnico.contract.LoadServiceOuterClass.StartResponse> getStartMethod;
    if ((getStartMethod = LoadServiceGrpc.getStartMethod) == null) {
      synchronized (LoadServiceGrpc.class) {
        if ((getStartMethod = LoadServiceGrpc.getStartMethod) == null) {
          LoadServiceGrpc.getStartMethod = getStartMethod =
              io.grpc.MethodDescriptor.<pt.ulisboa.tecnico.contract.LoadServiceOuterClass.StartRequest, pt.ulisboa.tecnico.contract.LoadServiceOuterClass.StartResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "start"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.ulisboa.tecnico.contract.LoadServiceOuterClass.StartRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.ulisboa.tecnico.contract.LoadServiceOuterClass.StartResponse.getDefaultInstance()))
              .setSchemaDescriptor(new LoadServiceMethodDescriptorSupplier("start"))
              .build();
        }
      }
    }
    return getStartMethod;
  }

  private static volatile io.grpc.MethodDescriptor<pt.ulisboa.tecnico.contract.LoadServiceOuterClass.StopRequest,
      pt.ulisboa.tecnico.contract.LoadServiceOuterClass.StopResponse> getStopMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "stop",
      requestType = pt.ulisboa.tecnico.contract.LoadServiceOuterClass.StopRequest.class,
      responseType = pt.ulisboa.tecnico.contract.LoadServiceOuterClass.StopResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<pt.ulisboa.tecnico.contract.LoadServiceOuterClass.StopRequest,
      pt.ulisboa.tecnico.contract.LoadServiceOuterClass.StopResponse> getStopMethod() {
    io.grpc.MethodDescriptor<pt.ulisboa.tecnico.contract.LoadServiceOuterClass.StopRequest, pt.ulisboa.tecnico.contract.LoadServiceOuterClass.StopResponse> getStopMethod;
    if ((getStopMethod = LoadServiceGrpc.getStopMethod) == null) {
      synchronized (LoadServiceGrpc.class) {
        if ((getStopMethod = LoadServiceGrpc.getStopMethod) == null) {
          LoadServiceGrpc.getStopMethod = getStopMethod =
              io.grpc.MethodDescriptor.<pt.ulisboa.tecnico.contract.LoadServiceOuterClass.StopRequest, pt.ulisboa.tecnico.contract.LoadServiceOuterClass.StopResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "stop"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.ulisboa.tecnico.contract.LoadServiceOuterClass.StopRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.ulisboa.tecnico.contract.LoadServiceOuterClass.StopResponse.getDefaultInstance()))
              .setSchemaDescriptor(new LoadServiceMethodDescriptorSupplier("stop"))
              .build();
        }
      }
    }
    return getStopMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static LoadServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<LoadServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<LoadServiceStub>() {
        @java.lang.Override
        public LoadServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new LoadServiceStub(channel, callOptions);
        }
      };
    return LoadServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static LoadServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<LoadServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<LoadServiceBlockingStub>() {
        @java.lang.Override
        public LoadServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new LoadServiceBlockingStub(channel, callOptions);
        }
      };
    return LoadServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static LoadServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<LoadServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<LoadServiceFutureStub>() {
        @java.lang.Override
        public LoadServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new LoadServiceFutureStub(channel, callOptions);
        }
      };
    return LoadServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class LoadServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void start(pt.ulisboa.tecnico.contract.LoadServiceOuterClass.StartRequest request,
        io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.LoadServiceOuterClass.StartResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getStartMethod(), responseObserver);
    }

    /**
     */
    public void stop(pt.ulisboa.tecnico.contract.LoadServiceOuterClass.StopRequest request,
        io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.LoadServiceOuterClass.StopResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getStopMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getStartMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                pt.ulisboa.tecnico.contract.LoadServiceOuterClass.StartRequest,
                pt.ulisboa.tecnico.contract.LoadServiceOuterClass.StartResponse>(
                  this, METHODID_START)))
          .addMethod(
            getStopMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                pt.ulisboa.tecnico.contract.LoadServiceOuterClass.StopRequest,
                pt.ulisboa.tecnico.contract.LoadServiceOuterClass.StopResponse>(
                  this, METHODID_STOP)))
          .build();
    }
  }

  /**
   */
  public static final class LoadServiceStub extends io.grpc.stub.AbstractAsyncStub<LoadServiceStub> {
    private LoadServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected LoadServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new LoadServiceStub(channel, callOptions);
    }

    /**
     */
    public void start(pt.ulisboa.tecnico.contract.LoadServiceOuterClass.StartRequest request,
        io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.LoadServiceOuterClass.StartResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getStartMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void stop(pt.ulisboa.tecnico.contract.LoadServiceOuterClass.StopRequest request,
        io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.LoadServiceOuterClass.StopResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getStopMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class LoadServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<LoadServiceBlockingStub> {
    private LoadServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected LoadServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new LoadServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public pt.ulisboa.tecnico.contract.LoadServiceOuterClass.StartResponse start(pt.ulisboa.tecnico.contract.LoadServiceOuterClass.StartRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getStartMethod(), getCallOptions(), request);
    }

    /**
     */
    public pt.ulisboa.tecnico.contract.LoadServiceOuterClass.StopResponse stop(pt.ulisboa.tecnico.contract.LoadServiceOuterClass.StopRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getStopMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class LoadServiceFutureStub extends io.grpc.stub.AbstractFutureStub<LoadServiceFutureStub> {
    private LoadServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected LoadServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new LoadServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<pt.ulisboa.tecnico.contract.LoadServiceOuterClass.StartResponse> start(
        pt.ulisboa.tecnico.contract.LoadServiceOuterClass.StartRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getStartMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<pt.ulisboa.tecnico.contract.LoadServiceOuterClass.StopResponse> stop(
        pt.ulisboa.tecnico.contract.LoadServiceOuterClass.StopRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getStopMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_START = 0;
  private static final int METHODID_STOP = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final LoadServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(LoadServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_START:
          serviceImpl.start((pt.ulisboa.tecnico.contract.LoadServiceOuterClass.StartRequest) request,
              (io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.LoadServiceOuterClass.StartResponse>) responseObserver);
          break;
        case METHODID_STOP:
          serviceImpl.stop((pt.ulisboa.tecnico.contract.LoadServiceOuterClass.StopRequest) request,
              (io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.LoadServiceOuterClass.StopResponse>) responseObserver);
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

  private static abstract class LoadServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    LoadServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return pt.ulisboa.tecnico.contract.LoadServiceOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("LoadService");
    }
  }

  private static final class LoadServiceFileDescriptorSupplier
      extends LoadServiceBaseDescriptorSupplier {
    LoadServiceFileDescriptorSupplier() {}
  }

  private static final class LoadServiceMethodDescriptorSupplier
      extends LoadServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    LoadServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (LoadServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new LoadServiceFileDescriptorSupplier())
              .addMethod(getStartMethod())
              .addMethod(getStopMethod())
              .build();
        }
      }
    }
    return result;
  }
}
