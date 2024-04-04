package pt.ulisboa.tecnico.contract;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.40.1)",
    comments = "Source: BenchmarkService.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class BenchmarkServiceGrpc {

  private BenchmarkServiceGrpc() {}

  public static final String SERVICE_NAME = "pt.ulisboa.tecnico.contract.BenchmarkService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.TopologyRequest,
      pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.TopologyResponse> getTopologyMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "topology",
      requestType = pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.TopologyRequest.class,
      responseType = pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.TopologyResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.TopologyRequest,
      pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.TopologyResponse> getTopologyMethod() {
    io.grpc.MethodDescriptor<pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.TopologyRequest, pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.TopologyResponse> getTopologyMethod;
    if ((getTopologyMethod = BenchmarkServiceGrpc.getTopologyMethod) == null) {
      synchronized (BenchmarkServiceGrpc.class) {
        if ((getTopologyMethod = BenchmarkServiceGrpc.getTopologyMethod) == null) {
          BenchmarkServiceGrpc.getTopologyMethod = getTopologyMethod =
              io.grpc.MethodDescriptor.<pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.TopologyRequest, pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.TopologyResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "topology"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.TopologyRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.TopologyResponse.getDefaultInstance()))
              .setSchemaDescriptor(new BenchmarkServiceMethodDescriptorSupplier("topology"))
              .build();
        }
      }
    }
    return getTopologyMethod;
  }

  private static volatile io.grpc.MethodDescriptor<pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.ProtocolRequest,
      pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.ProtocolResponse> getProtocolMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "protocol",
      requestType = pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.ProtocolRequest.class,
      responseType = pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.ProtocolResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.ProtocolRequest,
      pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.ProtocolResponse> getProtocolMethod() {
    io.grpc.MethodDescriptor<pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.ProtocolRequest, pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.ProtocolResponse> getProtocolMethod;
    if ((getProtocolMethod = BenchmarkServiceGrpc.getProtocolMethod) == null) {
      synchronized (BenchmarkServiceGrpc.class) {
        if ((getProtocolMethod = BenchmarkServiceGrpc.getProtocolMethod) == null) {
          BenchmarkServiceGrpc.getProtocolMethod = getProtocolMethod =
              io.grpc.MethodDescriptor.<pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.ProtocolRequest, pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.ProtocolResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "protocol"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.ProtocolRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.ProtocolResponse.getDefaultInstance()))
              .setSchemaDescriptor(new BenchmarkServiceMethodDescriptorSupplier("protocol"))
              .build();
        }
      }
    }
    return getProtocolMethod;
  }

  private static volatile io.grpc.MethodDescriptor<pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.StartBenchmarkRequest,
      pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.StartBenchmarkResponse> getStartMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "start",
      requestType = pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.StartBenchmarkRequest.class,
      responseType = pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.StartBenchmarkResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.StartBenchmarkRequest,
      pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.StartBenchmarkResponse> getStartMethod() {
    io.grpc.MethodDescriptor<pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.StartBenchmarkRequest, pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.StartBenchmarkResponse> getStartMethod;
    if ((getStartMethod = BenchmarkServiceGrpc.getStartMethod) == null) {
      synchronized (BenchmarkServiceGrpc.class) {
        if ((getStartMethod = BenchmarkServiceGrpc.getStartMethod) == null) {
          BenchmarkServiceGrpc.getStartMethod = getStartMethod =
              io.grpc.MethodDescriptor.<pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.StartBenchmarkRequest, pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.StartBenchmarkResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "start"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.StartBenchmarkRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.StartBenchmarkResponse.getDefaultInstance()))
              .setSchemaDescriptor(new BenchmarkServiceMethodDescriptorSupplier("start"))
              .build();
        }
      }
    }
    return getStartMethod;
  }

  private static volatile io.grpc.MethodDescriptor<pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.StopBenchmarkRequest,
      pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.StopBenchmarkResponse> getStopMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "stop",
      requestType = pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.StopBenchmarkRequest.class,
      responseType = pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.StopBenchmarkResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.StopBenchmarkRequest,
      pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.StopBenchmarkResponse> getStopMethod() {
    io.grpc.MethodDescriptor<pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.StopBenchmarkRequest, pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.StopBenchmarkResponse> getStopMethod;
    if ((getStopMethod = BenchmarkServiceGrpc.getStopMethod) == null) {
      synchronized (BenchmarkServiceGrpc.class) {
        if ((getStopMethod = BenchmarkServiceGrpc.getStopMethod) == null) {
          BenchmarkServiceGrpc.getStopMethod = getStopMethod =
              io.grpc.MethodDescriptor.<pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.StopBenchmarkRequest, pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.StopBenchmarkResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "stop"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.StopBenchmarkRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.StopBenchmarkResponse.getDefaultInstance()))
              .setSchemaDescriptor(new BenchmarkServiceMethodDescriptorSupplier("stop"))
              .build();
        }
      }
    }
    return getStopMethod;
  }

  private static volatile io.grpc.MethodDescriptor<pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.SubmitRequest,
      pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.SubmitResponse> getSubmitMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "submit",
      requestType = pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.SubmitRequest.class,
      responseType = pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.SubmitResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.SubmitRequest,
      pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.SubmitResponse> getSubmitMethod() {
    io.grpc.MethodDescriptor<pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.SubmitRequest, pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.SubmitResponse> getSubmitMethod;
    if ((getSubmitMethod = BenchmarkServiceGrpc.getSubmitMethod) == null) {
      synchronized (BenchmarkServiceGrpc.class) {
        if ((getSubmitMethod = BenchmarkServiceGrpc.getSubmitMethod) == null) {
          BenchmarkServiceGrpc.getSubmitMethod = getSubmitMethod =
              io.grpc.MethodDescriptor.<pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.SubmitRequest, pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.SubmitResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "submit"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.SubmitRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.SubmitResponse.getDefaultInstance()))
              .setSchemaDescriptor(new BenchmarkServiceMethodDescriptorSupplier("submit"))
              .build();
        }
      }
    }
    return getSubmitMethod;
  }

  private static volatile io.grpc.MethodDescriptor<pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.BenchmarkRequest,
      pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.BenchmarkResponse> getExecuteMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "execute",
      requestType = pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.BenchmarkRequest.class,
      responseType = pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.BenchmarkResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.BenchmarkRequest,
      pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.BenchmarkResponse> getExecuteMethod() {
    io.grpc.MethodDescriptor<pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.BenchmarkRequest, pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.BenchmarkResponse> getExecuteMethod;
    if ((getExecuteMethod = BenchmarkServiceGrpc.getExecuteMethod) == null) {
      synchronized (BenchmarkServiceGrpc.class) {
        if ((getExecuteMethod = BenchmarkServiceGrpc.getExecuteMethod) == null) {
          BenchmarkServiceGrpc.getExecuteMethod = getExecuteMethod =
              io.grpc.MethodDescriptor.<pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.BenchmarkRequest, pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.BenchmarkResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "execute"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.BenchmarkRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.BenchmarkResponse.getDefaultInstance()))
              .setSchemaDescriptor(new BenchmarkServiceMethodDescriptorSupplier("execute"))
              .build();
        }
      }
    }
    return getExecuteMethod;
  }

  private static volatile io.grpc.MethodDescriptor<pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.ShutdownRequest,
      pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.ShutdownResponse> getShutdownMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "shutdown",
      requestType = pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.ShutdownRequest.class,
      responseType = pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.ShutdownResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.ShutdownRequest,
      pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.ShutdownResponse> getShutdownMethod() {
    io.grpc.MethodDescriptor<pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.ShutdownRequest, pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.ShutdownResponse> getShutdownMethod;
    if ((getShutdownMethod = BenchmarkServiceGrpc.getShutdownMethod) == null) {
      synchronized (BenchmarkServiceGrpc.class) {
        if ((getShutdownMethod = BenchmarkServiceGrpc.getShutdownMethod) == null) {
          BenchmarkServiceGrpc.getShutdownMethod = getShutdownMethod =
              io.grpc.MethodDescriptor.<pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.ShutdownRequest, pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.ShutdownResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "shutdown"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.ShutdownRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.ShutdownResponse.getDefaultInstance()))
              .setSchemaDescriptor(new BenchmarkServiceMethodDescriptorSupplier("shutdown"))
              .build();
        }
      }
    }
    return getShutdownMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static BenchmarkServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<BenchmarkServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<BenchmarkServiceStub>() {
        @java.lang.Override
        public BenchmarkServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new BenchmarkServiceStub(channel, callOptions);
        }
      };
    return BenchmarkServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static BenchmarkServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<BenchmarkServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<BenchmarkServiceBlockingStub>() {
        @java.lang.Override
        public BenchmarkServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new BenchmarkServiceBlockingStub(channel, callOptions);
        }
      };
    return BenchmarkServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static BenchmarkServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<BenchmarkServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<BenchmarkServiceFutureStub>() {
        @java.lang.Override
        public BenchmarkServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new BenchmarkServiceFutureStub(channel, callOptions);
        }
      };
    return BenchmarkServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class BenchmarkServiceImplBase implements io.grpc.BindableService {

    /**
     * <pre>
     * benchmark config
     * </pre>
     */
    public void topology(pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.TopologyRequest request,
        io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.TopologyResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getTopologyMethod(), responseObserver);
    }

    /**
     */
    public void protocol(pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.ProtocolRequest request,
        io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.ProtocolResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getProtocolMethod(), responseObserver);
    }

    /**
     * <pre>
     * benchmark control
     * </pre>
     */
    public void start(pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.StartBenchmarkRequest request,
        io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.StartBenchmarkResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getStartMethod(), responseObserver);
    }

    /**
     */
    public void stop(pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.StopBenchmarkRequest request,
        io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.StopBenchmarkResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getStopMethod(), responseObserver);
    }

    /**
     * <pre>
     * transaction submission
     * </pre>
     */
    public void submit(pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.SubmitRequest request,
        io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.SubmitResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSubmitMethod(), responseObserver);
    }

    /**
     * <pre>
     * others... TODO delete?
     * </pre>
     */
    public void execute(pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.BenchmarkRequest request,
        io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.BenchmarkResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getExecuteMethod(), responseObserver);
    }

    /**
     */
    public void shutdown(pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.ShutdownRequest request,
        io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.ShutdownResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getShutdownMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getTopologyMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.TopologyRequest,
                pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.TopologyResponse>(
                  this, METHODID_TOPOLOGY)))
          .addMethod(
            getProtocolMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.ProtocolRequest,
                pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.ProtocolResponse>(
                  this, METHODID_PROTOCOL)))
          .addMethod(
            getStartMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.StartBenchmarkRequest,
                pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.StartBenchmarkResponse>(
                  this, METHODID_START)))
          .addMethod(
            getStopMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.StopBenchmarkRequest,
                pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.StopBenchmarkResponse>(
                  this, METHODID_STOP)))
          .addMethod(
            getSubmitMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.SubmitRequest,
                pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.SubmitResponse>(
                  this, METHODID_SUBMIT)))
          .addMethod(
            getExecuteMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.BenchmarkRequest,
                pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.BenchmarkResponse>(
                  this, METHODID_EXECUTE)))
          .addMethod(
            getShutdownMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.ShutdownRequest,
                pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.ShutdownResponse>(
                  this, METHODID_SHUTDOWN)))
          .build();
    }
  }

  /**
   */
  public static final class BenchmarkServiceStub extends io.grpc.stub.AbstractAsyncStub<BenchmarkServiceStub> {
    private BenchmarkServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected BenchmarkServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new BenchmarkServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * benchmark config
     * </pre>
     */
    public void topology(pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.TopologyRequest request,
        io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.TopologyResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getTopologyMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void protocol(pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.ProtocolRequest request,
        io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.ProtocolResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getProtocolMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * benchmark control
     * </pre>
     */
    public void start(pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.StartBenchmarkRequest request,
        io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.StartBenchmarkResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getStartMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void stop(pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.StopBenchmarkRequest request,
        io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.StopBenchmarkResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getStopMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * transaction submission
     * </pre>
     */
    public void submit(pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.SubmitRequest request,
        io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.SubmitResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getSubmitMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * others... TODO delete?
     * </pre>
     */
    public void execute(pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.BenchmarkRequest request,
        io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.BenchmarkResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getExecuteMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void shutdown(pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.ShutdownRequest request,
        io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.ShutdownResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getShutdownMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class BenchmarkServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<BenchmarkServiceBlockingStub> {
    private BenchmarkServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected BenchmarkServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new BenchmarkServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * benchmark config
     * </pre>
     */
    public pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.TopologyResponse topology(pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.TopologyRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getTopologyMethod(), getCallOptions(), request);
    }

    /**
     */
    public pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.ProtocolResponse protocol(pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.ProtocolRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getProtocolMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * benchmark control
     * </pre>
     */
    public pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.StartBenchmarkResponse start(pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.StartBenchmarkRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getStartMethod(), getCallOptions(), request);
    }

    /**
     */
    public pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.StopBenchmarkResponse stop(pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.StopBenchmarkRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getStopMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * transaction submission
     * </pre>
     */
    public pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.SubmitResponse submit(pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.SubmitRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getSubmitMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * others... TODO delete?
     * </pre>
     */
    public pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.BenchmarkResponse execute(pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.BenchmarkRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getExecuteMethod(), getCallOptions(), request);
    }

    /**
     */
    public pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.ShutdownResponse shutdown(pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.ShutdownRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getShutdownMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class BenchmarkServiceFutureStub extends io.grpc.stub.AbstractFutureStub<BenchmarkServiceFutureStub> {
    private BenchmarkServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected BenchmarkServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new BenchmarkServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * benchmark config
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.TopologyResponse> topology(
        pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.TopologyRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getTopologyMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.ProtocolResponse> protocol(
        pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.ProtocolRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getProtocolMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * benchmark control
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.StartBenchmarkResponse> start(
        pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.StartBenchmarkRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getStartMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.StopBenchmarkResponse> stop(
        pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.StopBenchmarkRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getStopMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * transaction submission
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.SubmitResponse> submit(
        pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.SubmitRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getSubmitMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * others... TODO delete?
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.BenchmarkResponse> execute(
        pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.BenchmarkRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getExecuteMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.ShutdownResponse> shutdown(
        pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.ShutdownRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getShutdownMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_TOPOLOGY = 0;
  private static final int METHODID_PROTOCOL = 1;
  private static final int METHODID_START = 2;
  private static final int METHODID_STOP = 3;
  private static final int METHODID_SUBMIT = 4;
  private static final int METHODID_EXECUTE = 5;
  private static final int METHODID_SHUTDOWN = 6;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final BenchmarkServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(BenchmarkServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_TOPOLOGY:
          serviceImpl.topology((pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.TopologyRequest) request,
              (io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.TopologyResponse>) responseObserver);
          break;
        case METHODID_PROTOCOL:
          serviceImpl.protocol((pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.ProtocolRequest) request,
              (io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.ProtocolResponse>) responseObserver);
          break;
        case METHODID_START:
          serviceImpl.start((pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.StartBenchmarkRequest) request,
              (io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.StartBenchmarkResponse>) responseObserver);
          break;
        case METHODID_STOP:
          serviceImpl.stop((pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.StopBenchmarkRequest) request,
              (io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.StopBenchmarkResponse>) responseObserver);
          break;
        case METHODID_SUBMIT:
          serviceImpl.submit((pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.SubmitRequest) request,
              (io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.SubmitResponse>) responseObserver);
          break;
        case METHODID_EXECUTE:
          serviceImpl.execute((pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.BenchmarkRequest) request,
              (io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.BenchmarkResponse>) responseObserver);
          break;
        case METHODID_SHUTDOWN:
          serviceImpl.shutdown((pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.ShutdownRequest) request,
              (io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.ShutdownResponse>) responseObserver);
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

  private static abstract class BenchmarkServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    BenchmarkServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return pt.ulisboa.tecnico.contract.BenchmarkServiceOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("BenchmarkService");
    }
  }

  private static final class BenchmarkServiceFileDescriptorSupplier
      extends BenchmarkServiceBaseDescriptorSupplier {
    BenchmarkServiceFileDescriptorSupplier() {}
  }

  private static final class BenchmarkServiceMethodDescriptorSupplier
      extends BenchmarkServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    BenchmarkServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (BenchmarkServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new BenchmarkServiceFileDescriptorSupplier())
              .addMethod(getTopologyMethod())
              .addMethod(getProtocolMethod())
              .addMethod(getStartMethod())
              .addMethod(getStopMethod())
              .addMethod(getSubmitMethod())
              .addMethod(getExecuteMethod())
              .addMethod(getShutdownMethod())
              .build();
        }
      }
    }
    return result;
  }
}
