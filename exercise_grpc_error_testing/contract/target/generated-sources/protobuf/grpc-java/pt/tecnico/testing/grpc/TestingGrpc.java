package pt.tecnico.testing.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.28.0)",
    comments = "Source: Testing.proto")
public final class TestingGrpc {

  private TestingGrpc() {}

  public static final String SERVICE_NAME = "pt.tecnico.testing.grpc.Testing";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<pt.tecnico.testing.grpc.TestingOuterClass.PingRequest,
      pt.tecnico.testing.grpc.TestingOuterClass.PingResponse> getCtrlPingMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ctrl_ping",
      requestType = pt.tecnico.testing.grpc.TestingOuterClass.PingRequest.class,
      responseType = pt.tecnico.testing.grpc.TestingOuterClass.PingResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<pt.tecnico.testing.grpc.TestingOuterClass.PingRequest,
      pt.tecnico.testing.grpc.TestingOuterClass.PingResponse> getCtrlPingMethod() {
    io.grpc.MethodDescriptor<pt.tecnico.testing.grpc.TestingOuterClass.PingRequest, pt.tecnico.testing.grpc.TestingOuterClass.PingResponse> getCtrlPingMethod;
    if ((getCtrlPingMethod = TestingGrpc.getCtrlPingMethod) == null) {
      synchronized (TestingGrpc.class) {
        if ((getCtrlPingMethod = TestingGrpc.getCtrlPingMethod) == null) {
          TestingGrpc.getCtrlPingMethod = getCtrlPingMethod =
              io.grpc.MethodDescriptor.<pt.tecnico.testing.grpc.TestingOuterClass.PingRequest, pt.tecnico.testing.grpc.TestingOuterClass.PingResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ctrl_ping"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.tecnico.testing.grpc.TestingOuterClass.PingRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.tecnico.testing.grpc.TestingOuterClass.PingResponse.getDefaultInstance()))
              .setSchemaDescriptor(new TestingMethodDescriptorSupplier("ctrl_ping"))
              .build();
        }
      }
    }
    return getCtrlPingMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static TestingStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<TestingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<TestingStub>() {
        @java.lang.Override
        public TestingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new TestingStub(channel, callOptions);
        }
      };
    return TestingStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static TestingBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<TestingBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<TestingBlockingStub>() {
        @java.lang.Override
        public TestingBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new TestingBlockingStub(channel, callOptions);
        }
      };
    return TestingBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static TestingFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<TestingFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<TestingFutureStub>() {
        @java.lang.Override
        public TestingFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new TestingFutureStub(channel, callOptions);
        }
      };
    return TestingFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class TestingImplBase implements io.grpc.BindableService {

    /**
     */
    public void ctrlPing(pt.tecnico.testing.grpc.TestingOuterClass.PingRequest request,
        io.grpc.stub.StreamObserver<pt.tecnico.testing.grpc.TestingOuterClass.PingResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getCtrlPingMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getCtrlPingMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                pt.tecnico.testing.grpc.TestingOuterClass.PingRequest,
                pt.tecnico.testing.grpc.TestingOuterClass.PingResponse>(
                  this, METHODID_CTRL_PING)))
          .build();
    }
  }

  /**
   */
  public static final class TestingStub extends io.grpc.stub.AbstractAsyncStub<TestingStub> {
    private TestingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected TestingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new TestingStub(channel, callOptions);
    }

    /**
     */
    public void ctrlPing(pt.tecnico.testing.grpc.TestingOuterClass.PingRequest request,
        io.grpc.stub.StreamObserver<pt.tecnico.testing.grpc.TestingOuterClass.PingResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getCtrlPingMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class TestingBlockingStub extends io.grpc.stub.AbstractBlockingStub<TestingBlockingStub> {
    private TestingBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected TestingBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new TestingBlockingStub(channel, callOptions);
    }

    /**
     */
    public pt.tecnico.testing.grpc.TestingOuterClass.PingResponse ctrlPing(pt.tecnico.testing.grpc.TestingOuterClass.PingRequest request) {
      return blockingUnaryCall(
          getChannel(), getCtrlPingMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class TestingFutureStub extends io.grpc.stub.AbstractFutureStub<TestingFutureStub> {
    private TestingFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected TestingFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new TestingFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<pt.tecnico.testing.grpc.TestingOuterClass.PingResponse> ctrlPing(
        pt.tecnico.testing.grpc.TestingOuterClass.PingRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getCtrlPingMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_CTRL_PING = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final TestingImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(TestingImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_CTRL_PING:
          serviceImpl.ctrlPing((pt.tecnico.testing.grpc.TestingOuterClass.PingRequest) request,
              (io.grpc.stub.StreamObserver<pt.tecnico.testing.grpc.TestingOuterClass.PingResponse>) responseObserver);
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

  private static abstract class TestingBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    TestingBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return pt.tecnico.testing.grpc.TestingOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("Testing");
    }
  }

  private static final class TestingFileDescriptorSupplier
      extends TestingBaseDescriptorSupplier {
    TestingFileDescriptorSupplier() {}
  }

  private static final class TestingMethodDescriptorSupplier
      extends TestingBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    TestingMethodDescriptorSupplier(String methodName) {
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
      synchronized (TestingGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new TestingFileDescriptorSupplier())
              .addMethod(getCtrlPingMethod())
              .build();
        }
      }
    }
    return result;
  }
}
