package pt.ulisboa.tecnico.contract;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.40.1)",
    comments = "Source: RegisterService.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class RegisterServiceGrpc {

  private RegisterServiceGrpc() {}

  public static final String SERVICE_NAME = "pt.ulisboa.tecnico.contract.RegisterService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<pt.ulisboa.tecnico.contract.RegisterServiceOuterClass.RegisterRequest,
      pt.ulisboa.tecnico.contract.RegisterServiceOuterClass.RegisterResponse> getRegisterMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "register",
      requestType = pt.ulisboa.tecnico.contract.RegisterServiceOuterClass.RegisterRequest.class,
      responseType = pt.ulisboa.tecnico.contract.RegisterServiceOuterClass.RegisterResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<pt.ulisboa.tecnico.contract.RegisterServiceOuterClass.RegisterRequest,
      pt.ulisboa.tecnico.contract.RegisterServiceOuterClass.RegisterResponse> getRegisterMethod() {
    io.grpc.MethodDescriptor<pt.ulisboa.tecnico.contract.RegisterServiceOuterClass.RegisterRequest, pt.ulisboa.tecnico.contract.RegisterServiceOuterClass.RegisterResponse> getRegisterMethod;
    if ((getRegisterMethod = RegisterServiceGrpc.getRegisterMethod) == null) {
      synchronized (RegisterServiceGrpc.class) {
        if ((getRegisterMethod = RegisterServiceGrpc.getRegisterMethod) == null) {
          RegisterServiceGrpc.getRegisterMethod = getRegisterMethod =
              io.grpc.MethodDescriptor.<pt.ulisboa.tecnico.contract.RegisterServiceOuterClass.RegisterRequest, pt.ulisboa.tecnico.contract.RegisterServiceOuterClass.RegisterResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "register"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.ulisboa.tecnico.contract.RegisterServiceOuterClass.RegisterRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.ulisboa.tecnico.contract.RegisterServiceOuterClass.RegisterResponse.getDefaultInstance()))
              .setSchemaDescriptor(new RegisterServiceMethodDescriptorSupplier("register"))
              .build();
        }
      }
    }
    return getRegisterMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static RegisterServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<RegisterServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<RegisterServiceStub>() {
        @java.lang.Override
        public RegisterServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new RegisterServiceStub(channel, callOptions);
        }
      };
    return RegisterServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static RegisterServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<RegisterServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<RegisterServiceBlockingStub>() {
        @java.lang.Override
        public RegisterServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new RegisterServiceBlockingStub(channel, callOptions);
        }
      };
    return RegisterServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static RegisterServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<RegisterServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<RegisterServiceFutureStub>() {
        @java.lang.Override
        public RegisterServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new RegisterServiceFutureStub(channel, callOptions);
        }
      };
    return RegisterServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class RegisterServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void register(pt.ulisboa.tecnico.contract.RegisterServiceOuterClass.RegisterRequest request,
        io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.RegisterServiceOuterClass.RegisterResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getRegisterMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getRegisterMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                pt.ulisboa.tecnico.contract.RegisterServiceOuterClass.RegisterRequest,
                pt.ulisboa.tecnico.contract.RegisterServiceOuterClass.RegisterResponse>(
                  this, METHODID_REGISTER)))
          .build();
    }
  }

  /**
   */
  public static final class RegisterServiceStub extends io.grpc.stub.AbstractAsyncStub<RegisterServiceStub> {
    private RegisterServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RegisterServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new RegisterServiceStub(channel, callOptions);
    }

    /**
     */
    public void register(pt.ulisboa.tecnico.contract.RegisterServiceOuterClass.RegisterRequest request,
        io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.RegisterServiceOuterClass.RegisterResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getRegisterMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class RegisterServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<RegisterServiceBlockingStub> {
    private RegisterServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RegisterServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new RegisterServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public pt.ulisboa.tecnico.contract.RegisterServiceOuterClass.RegisterResponse register(pt.ulisboa.tecnico.contract.RegisterServiceOuterClass.RegisterRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getRegisterMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class RegisterServiceFutureStub extends io.grpc.stub.AbstractFutureStub<RegisterServiceFutureStub> {
    private RegisterServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RegisterServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new RegisterServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<pt.ulisboa.tecnico.contract.RegisterServiceOuterClass.RegisterResponse> register(
        pt.ulisboa.tecnico.contract.RegisterServiceOuterClass.RegisterRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getRegisterMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_REGISTER = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final RegisterServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(RegisterServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_REGISTER:
          serviceImpl.register((pt.ulisboa.tecnico.contract.RegisterServiceOuterClass.RegisterRequest) request,
              (io.grpc.stub.StreamObserver<pt.ulisboa.tecnico.contract.RegisterServiceOuterClass.RegisterResponse>) responseObserver);
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

  private static abstract class RegisterServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    RegisterServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return pt.ulisboa.tecnico.contract.RegisterServiceOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("RegisterService");
    }
  }

  private static final class RegisterServiceFileDescriptorSupplier
      extends RegisterServiceBaseDescriptorSupplier {
    RegisterServiceFileDescriptorSupplier() {}
  }

  private static final class RegisterServiceMethodDescriptorSupplier
      extends RegisterServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    RegisterServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (RegisterServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new RegisterServiceFileDescriptorSupplier())
              .addMethod(getRegisterMethod())
              .build();
        }
      }
    }
    return result;
  }
}
