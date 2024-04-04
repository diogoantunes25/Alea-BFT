package pt.ulisboa.tecnico.contract;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.40.1)",
    comments = "Source: ProcessCreationService.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class ProcessCreationServiceGrpc {

  private ProcessCreationServiceGrpc() {}

  public static final String SERVICE_NAME = "pt.ulisboa.tecnico.contract.ProcessCreationService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<pt.ulisboa.tecnico.contract.ProcessCreationServiceOuterClass.CreateReplicaRequest,
      pt.ulisboa.tecnico.contract.ProcessCreationServiceOuterClass.CreateReplicaResponse> getReplicaMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "replica",
      requestType = pt.ulisboa.tecnico.contract.ProcessCreationServiceOuterClass.CreateReplicaRequest.class,
      responseType = pt.ulisboa.tecnico.contract.ProcessCreationServiceOuterClass.CreateReplicaResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<pt.ulisboa.tecnico.contract.ProcessCreationServiceOuterClass.CreateReplicaRequest,
      pt.ulisboa.tecnico.contract.ProcessCreationServiceOuterClass.CreateReplicaResponse> getReplicaMethod() {
    io.grpc.MethodDescriptor<pt.ulisboa.tecnico.contract.ProcessCreationServiceOuterClass.CreateReplicaRequest, pt.ulisboa.tecnico.contract.ProcessCreationServiceOuterClass.CreateReplicaResponse> getReplicaMethod;
    if ((getReplicaMethod = ProcessCreationServiceGrpc.getReplicaMethod) == null) {
      synchronized (ProcessCreationServiceGrpc.class) {
        if ((getReplicaMethod = ProcessCreationServiceGrpc.getReplicaMethod) == null) {
          ProcessCreationServiceGrpc.getReplicaMethod = getReplicaMethod =
              io.grpc.MethodDescriptor.<pt.ulisboa.tecnico.contract.ProcessCreationServiceOuterClass.CreateReplicaRequest, pt.ulisboa.tecnico.contract.ProcessCreationServiceOuterClass.CreateReplicaResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "replica"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.ulisboa.tecnico.contract.ProcessCreationServiceOuterClass.CreateReplicaRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.ulisboa.tecnico.contract.ProcessCreationServiceOuterClass.CreateReplicaResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ProcessCreationServiceMethodDescriptorSupplier("replica"))
              .build();
        }
      }
    }
    return getReplicaMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static ProcessCreationServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ProcessCreationServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ProcessCreationServiceStub>() {
        @java.lang.Override
        public ProcessCreationServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ProcessCreationServiceStub(channel, callOptions);
        }
      };
    return ProcessCreationServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static ProcessCreationServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ProcessCreationServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ProcessCreationServiceBlockingStub>() {
        @java.lang.Override
        public ProcessCreationServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ProcessCreationServiceBlockingStub(channel, callOptions);
        }
      };
    return ProcessCreationServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static ProcessCreationServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ProcessCreationServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ProcessCreationServiceFutureStub>() {
        @java.lang.Override
        public ProcessCreationServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ProcessCreationServiceFutureStub(channel, callOptions);
        }
      };
    return ProcessCreationServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class ProcessCreationServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void replica(pt.ulisboa.tecnico.contract.ProcessCreationServiceOuterClass.CreateReplicaRequest request,
        io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.ProcessCreationServiceOuterClass.CreateReplicaResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getReplicaMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getReplicaMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                pt.ulisboa.tecnico.contract.ProcessCreationServiceOuterClass.CreateReplicaRequest,
                pt.ulisboa.tecnico.contract.ProcessCreationServiceOuterClass.CreateReplicaResponse>(
                  this, METHODID_REPLICA)))
          .build();
    }
  }

  /**
   */
  public static final class ProcessCreationServiceStub extends io.grpc.stub.AbstractAsyncStub<ProcessCreationServiceStub> {
    private ProcessCreationServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ProcessCreationServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ProcessCreationServiceStub(channel, callOptions);
    }

    /**
     */
    public void replica(pt.ulisboa.tecnico.contract.ProcessCreationServiceOuterClass.CreateReplicaRequest request,
        io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.ProcessCreationServiceOuterClass.CreateReplicaResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getReplicaMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class ProcessCreationServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<ProcessCreationServiceBlockingStub> {
    private ProcessCreationServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ProcessCreationServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ProcessCreationServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public pt.ulisboa.tecnico.contract.ProcessCreationServiceOuterClass.CreateReplicaResponse replica(pt.ulisboa.tecnico.contract.ProcessCreationServiceOuterClass.CreateReplicaRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getReplicaMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class ProcessCreationServiceFutureStub extends io.grpc.stub.AbstractFutureStub<ProcessCreationServiceFutureStub> {
    private ProcessCreationServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ProcessCreationServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ProcessCreationServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<pt.ulisboa.tecnico.contract.ProcessCreationServiceOuterClass.CreateReplicaResponse> replica(
        pt.ulisboa.tecnico.contract.ProcessCreationServiceOuterClass.CreateReplicaRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getReplicaMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_REPLICA = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final ProcessCreationServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(ProcessCreationServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_REPLICA:
          serviceImpl.replica((pt.ulisboa.tecnico.contract.ProcessCreationServiceOuterClass.CreateReplicaRequest) request,
              (io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.ProcessCreationServiceOuterClass.CreateReplicaResponse>) responseObserver);
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

  private static abstract class ProcessCreationServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    ProcessCreationServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return pt.ulisboa.tecnico.contract.ProcessCreationServiceOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("ProcessCreationService");
    }
  }

  private static final class ProcessCreationServiceFileDescriptorSupplier
      extends ProcessCreationServiceBaseDescriptorSupplier {
    ProcessCreationServiceFileDescriptorSupplier() {}
  }

  private static final class ProcessCreationServiceMethodDescriptorSupplier
      extends ProcessCreationServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    ProcessCreationServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (ProcessCreationServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new ProcessCreationServiceFileDescriptorSupplier())
              .addMethod(getReplicaMethod())
              .build();
        }
      }
    }
    return result;
  }
}
