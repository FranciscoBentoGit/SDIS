package pt.tecnico.staysafe.dgs.grpc;

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
    comments = "Source: dgs.proto")
public final class DgsGrpc {

  private DgsGrpc() {}

  public static final String SERVICE_NAME = "pt.tecnico.staysafe.dgs.grpc.Dgs";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<pt.tecnico.staysafe.dgs.grpc.SnifferJoinRequest,
      pt.tecnico.staysafe.dgs.grpc.SnifferJoinResponse> getSnifferJoinMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "sniffer_join",
      requestType = pt.tecnico.staysafe.dgs.grpc.SnifferJoinRequest.class,
      responseType = pt.tecnico.staysafe.dgs.grpc.SnifferJoinResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<pt.tecnico.staysafe.dgs.grpc.SnifferJoinRequest,
      pt.tecnico.staysafe.dgs.grpc.SnifferJoinResponse> getSnifferJoinMethod() {
    io.grpc.MethodDescriptor<pt.tecnico.staysafe.dgs.grpc.SnifferJoinRequest, pt.tecnico.staysafe.dgs.grpc.SnifferJoinResponse> getSnifferJoinMethod;
    if ((getSnifferJoinMethod = DgsGrpc.getSnifferJoinMethod) == null) {
      synchronized (DgsGrpc.class) {
        if ((getSnifferJoinMethod = DgsGrpc.getSnifferJoinMethod) == null) {
          DgsGrpc.getSnifferJoinMethod = getSnifferJoinMethod =
              io.grpc.MethodDescriptor.<pt.tecnico.staysafe.dgs.grpc.SnifferJoinRequest, pt.tecnico.staysafe.dgs.grpc.SnifferJoinResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "sniffer_join"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.tecnico.staysafe.dgs.grpc.SnifferJoinRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.tecnico.staysafe.dgs.grpc.SnifferJoinResponse.getDefaultInstance()))
              .setSchemaDescriptor(new DgsMethodDescriptorSupplier("sniffer_join"))
              .build();
        }
      }
    }
    return getSnifferJoinMethod;
  }

  private static volatile io.grpc.MethodDescriptor<pt.tecnico.staysafe.dgs.grpc.SnifferInfoRequest,
      pt.tecnico.staysafe.dgs.grpc.SnifferInfoResponse> getSnifferInfoMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "sniffer_info",
      requestType = pt.tecnico.staysafe.dgs.grpc.SnifferInfoRequest.class,
      responseType = pt.tecnico.staysafe.dgs.grpc.SnifferInfoResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<pt.tecnico.staysafe.dgs.grpc.SnifferInfoRequest,
      pt.tecnico.staysafe.dgs.grpc.SnifferInfoResponse> getSnifferInfoMethod() {
    io.grpc.MethodDescriptor<pt.tecnico.staysafe.dgs.grpc.SnifferInfoRequest, pt.tecnico.staysafe.dgs.grpc.SnifferInfoResponse> getSnifferInfoMethod;
    if ((getSnifferInfoMethod = DgsGrpc.getSnifferInfoMethod) == null) {
      synchronized (DgsGrpc.class) {
        if ((getSnifferInfoMethod = DgsGrpc.getSnifferInfoMethod) == null) {
          DgsGrpc.getSnifferInfoMethod = getSnifferInfoMethod =
              io.grpc.MethodDescriptor.<pt.tecnico.staysafe.dgs.grpc.SnifferInfoRequest, pt.tecnico.staysafe.dgs.grpc.SnifferInfoResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "sniffer_info"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.tecnico.staysafe.dgs.grpc.SnifferInfoRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.tecnico.staysafe.dgs.grpc.SnifferInfoResponse.getDefaultInstance()))
              .setSchemaDescriptor(new DgsMethodDescriptorSupplier("sniffer_info"))
              .build();
        }
      }
    }
    return getSnifferInfoMethod;
  }

  private static volatile io.grpc.MethodDescriptor<pt.tecnico.staysafe.dgs.grpc.ReportRequest,
      pt.tecnico.staysafe.dgs.grpc.ReportResponse> getReportMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "report",
      requestType = pt.tecnico.staysafe.dgs.grpc.ReportRequest.class,
      responseType = pt.tecnico.staysafe.dgs.grpc.ReportResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<pt.tecnico.staysafe.dgs.grpc.ReportRequest,
      pt.tecnico.staysafe.dgs.grpc.ReportResponse> getReportMethod() {
    io.grpc.MethodDescriptor<pt.tecnico.staysafe.dgs.grpc.ReportRequest, pt.tecnico.staysafe.dgs.grpc.ReportResponse> getReportMethod;
    if ((getReportMethod = DgsGrpc.getReportMethod) == null) {
      synchronized (DgsGrpc.class) {
        if ((getReportMethod = DgsGrpc.getReportMethod) == null) {
          DgsGrpc.getReportMethod = getReportMethod =
              io.grpc.MethodDescriptor.<pt.tecnico.staysafe.dgs.grpc.ReportRequest, pt.tecnico.staysafe.dgs.grpc.ReportResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "report"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.tecnico.staysafe.dgs.grpc.ReportRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.tecnico.staysafe.dgs.grpc.ReportResponse.getDefaultInstance()))
              .setSchemaDescriptor(new DgsMethodDescriptorSupplier("report"))
              .build();
        }
      }
    }
    return getReportMethod;
  }

  private static volatile io.grpc.MethodDescriptor<pt.tecnico.staysafe.dgs.grpc.PingRequest,
      pt.tecnico.staysafe.dgs.grpc.PingResponse> getCtrlPingMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ctrl_ping",
      requestType = pt.tecnico.staysafe.dgs.grpc.PingRequest.class,
      responseType = pt.tecnico.staysafe.dgs.grpc.PingResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<pt.tecnico.staysafe.dgs.grpc.PingRequest,
      pt.tecnico.staysafe.dgs.grpc.PingResponse> getCtrlPingMethod() {
    io.grpc.MethodDescriptor<pt.tecnico.staysafe.dgs.grpc.PingRequest, pt.tecnico.staysafe.dgs.grpc.PingResponse> getCtrlPingMethod;
    if ((getCtrlPingMethod = DgsGrpc.getCtrlPingMethod) == null) {
      synchronized (DgsGrpc.class) {
        if ((getCtrlPingMethod = DgsGrpc.getCtrlPingMethod) == null) {
          DgsGrpc.getCtrlPingMethod = getCtrlPingMethod =
              io.grpc.MethodDescriptor.<pt.tecnico.staysafe.dgs.grpc.PingRequest, pt.tecnico.staysafe.dgs.grpc.PingResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ctrl_ping"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.tecnico.staysafe.dgs.grpc.PingRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.tecnico.staysafe.dgs.grpc.PingResponse.getDefaultInstance()))
              .setSchemaDescriptor(new DgsMethodDescriptorSupplier("ctrl_ping"))
              .build();
        }
      }
    }
    return getCtrlPingMethod;
  }

  private static volatile io.grpc.MethodDescriptor<pt.tecnico.staysafe.dgs.grpc.InitRequest,
      pt.tecnico.staysafe.dgs.grpc.InitResponse> getCtrlInitMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ctrl_init",
      requestType = pt.tecnico.staysafe.dgs.grpc.InitRequest.class,
      responseType = pt.tecnico.staysafe.dgs.grpc.InitResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<pt.tecnico.staysafe.dgs.grpc.InitRequest,
      pt.tecnico.staysafe.dgs.grpc.InitResponse> getCtrlInitMethod() {
    io.grpc.MethodDescriptor<pt.tecnico.staysafe.dgs.grpc.InitRequest, pt.tecnico.staysafe.dgs.grpc.InitResponse> getCtrlInitMethod;
    if ((getCtrlInitMethod = DgsGrpc.getCtrlInitMethod) == null) {
      synchronized (DgsGrpc.class) {
        if ((getCtrlInitMethod = DgsGrpc.getCtrlInitMethod) == null) {
          DgsGrpc.getCtrlInitMethod = getCtrlInitMethod =
              io.grpc.MethodDescriptor.<pt.tecnico.staysafe.dgs.grpc.InitRequest, pt.tecnico.staysafe.dgs.grpc.InitResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ctrl_init"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.tecnico.staysafe.dgs.grpc.InitRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.tecnico.staysafe.dgs.grpc.InitResponse.getDefaultInstance()))
              .setSchemaDescriptor(new DgsMethodDescriptorSupplier("ctrl_init"))
              .build();
        }
      }
    }
    return getCtrlInitMethod;
  }

  private static volatile io.grpc.MethodDescriptor<pt.tecnico.staysafe.dgs.grpc.ClearRequest,
      pt.tecnico.staysafe.dgs.grpc.ClearResponse> getCtrlClearMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ctrl_clear",
      requestType = pt.tecnico.staysafe.dgs.grpc.ClearRequest.class,
      responseType = pt.tecnico.staysafe.dgs.grpc.ClearResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<pt.tecnico.staysafe.dgs.grpc.ClearRequest,
      pt.tecnico.staysafe.dgs.grpc.ClearResponse> getCtrlClearMethod() {
    io.grpc.MethodDescriptor<pt.tecnico.staysafe.dgs.grpc.ClearRequest, pt.tecnico.staysafe.dgs.grpc.ClearResponse> getCtrlClearMethod;
    if ((getCtrlClearMethod = DgsGrpc.getCtrlClearMethod) == null) {
      synchronized (DgsGrpc.class) {
        if ((getCtrlClearMethod = DgsGrpc.getCtrlClearMethod) == null) {
          DgsGrpc.getCtrlClearMethod = getCtrlClearMethod =
              io.grpc.MethodDescriptor.<pt.tecnico.staysafe.dgs.grpc.ClearRequest, pt.tecnico.staysafe.dgs.grpc.ClearResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ctrl_clear"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.tecnico.staysafe.dgs.grpc.ClearRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pt.tecnico.staysafe.dgs.grpc.ClearResponse.getDefaultInstance()))
              .setSchemaDescriptor(new DgsMethodDescriptorSupplier("ctrl_clear"))
              .build();
        }
      }
    }
    return getCtrlClearMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static DgsStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<DgsStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<DgsStub>() {
        @java.lang.Override
        public DgsStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new DgsStub(channel, callOptions);
        }
      };
    return DgsStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static DgsBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<DgsBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<DgsBlockingStub>() {
        @java.lang.Override
        public DgsBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new DgsBlockingStub(channel, callOptions);
        }
      };
    return DgsBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static DgsFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<DgsFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<DgsFutureStub>() {
        @java.lang.Override
        public DgsFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new DgsFutureStub(channel, callOptions);
        }
      };
    return DgsFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class DgsImplBase implements io.grpc.BindableService {

    /**
     */
    public void snifferJoin(pt.tecnico.staysafe.dgs.grpc.SnifferJoinRequest request,
        io.grpc.stub.StreamObserver<pt.tecnico.staysafe.dgs.grpc.SnifferJoinResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getSnifferJoinMethod(), responseObserver);
    }

    /**
     */
    public void snifferInfo(pt.tecnico.staysafe.dgs.grpc.SnifferInfoRequest request,
        io.grpc.stub.StreamObserver<pt.tecnico.staysafe.dgs.grpc.SnifferInfoResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getSnifferInfoMethod(), responseObserver);
    }

    /**
     */
    public void report(pt.tecnico.staysafe.dgs.grpc.ReportRequest request,
        io.grpc.stub.StreamObserver<pt.tecnico.staysafe.dgs.grpc.ReportResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getReportMethod(), responseObserver);
    }

    /**
     */
    public void ctrlPing(pt.tecnico.staysafe.dgs.grpc.PingRequest request,
        io.grpc.stub.StreamObserver<pt.tecnico.staysafe.dgs.grpc.PingResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getCtrlPingMethod(), responseObserver);
    }

    /**
     */
    public void ctrlInit(pt.tecnico.staysafe.dgs.grpc.InitRequest request,
        io.grpc.stub.StreamObserver<pt.tecnico.staysafe.dgs.grpc.InitResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getCtrlInitMethod(), responseObserver);
    }

    /**
     */
    public void ctrlClear(pt.tecnico.staysafe.dgs.grpc.ClearRequest request,
        io.grpc.stub.StreamObserver<pt.tecnico.staysafe.dgs.grpc.ClearResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getCtrlClearMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getSnifferJoinMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                pt.tecnico.staysafe.dgs.grpc.SnifferJoinRequest,
                pt.tecnico.staysafe.dgs.grpc.SnifferJoinResponse>(
                  this, METHODID_SNIFFER_JOIN)))
          .addMethod(
            getSnifferInfoMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                pt.tecnico.staysafe.dgs.grpc.SnifferInfoRequest,
                pt.tecnico.staysafe.dgs.grpc.SnifferInfoResponse>(
                  this, METHODID_SNIFFER_INFO)))
          .addMethod(
            getReportMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                pt.tecnico.staysafe.dgs.grpc.ReportRequest,
                pt.tecnico.staysafe.dgs.grpc.ReportResponse>(
                  this, METHODID_REPORT)))
          .addMethod(
            getCtrlPingMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                pt.tecnico.staysafe.dgs.grpc.PingRequest,
                pt.tecnico.staysafe.dgs.grpc.PingResponse>(
                  this, METHODID_CTRL_PING)))
          .addMethod(
            getCtrlInitMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                pt.tecnico.staysafe.dgs.grpc.InitRequest,
                pt.tecnico.staysafe.dgs.grpc.InitResponse>(
                  this, METHODID_CTRL_INIT)))
          .addMethod(
            getCtrlClearMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                pt.tecnico.staysafe.dgs.grpc.ClearRequest,
                pt.tecnico.staysafe.dgs.grpc.ClearResponse>(
                  this, METHODID_CTRL_CLEAR)))
          .build();
    }
  }

  /**
   */
  public static final class DgsStub extends io.grpc.stub.AbstractAsyncStub<DgsStub> {
    private DgsStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DgsStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new DgsStub(channel, callOptions);
    }

    /**
     */
    public void snifferJoin(pt.tecnico.staysafe.dgs.grpc.SnifferJoinRequest request,
        io.grpc.stub.StreamObserver<pt.tecnico.staysafe.dgs.grpc.SnifferJoinResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getSnifferJoinMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void snifferInfo(pt.tecnico.staysafe.dgs.grpc.SnifferInfoRequest request,
        io.grpc.stub.StreamObserver<pt.tecnico.staysafe.dgs.grpc.SnifferInfoResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getSnifferInfoMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void report(pt.tecnico.staysafe.dgs.grpc.ReportRequest request,
        io.grpc.stub.StreamObserver<pt.tecnico.staysafe.dgs.grpc.ReportResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getReportMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void ctrlPing(pt.tecnico.staysafe.dgs.grpc.PingRequest request,
        io.grpc.stub.StreamObserver<pt.tecnico.staysafe.dgs.grpc.PingResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getCtrlPingMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void ctrlInit(pt.tecnico.staysafe.dgs.grpc.InitRequest request,
        io.grpc.stub.StreamObserver<pt.tecnico.staysafe.dgs.grpc.InitResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getCtrlInitMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void ctrlClear(pt.tecnico.staysafe.dgs.grpc.ClearRequest request,
        io.grpc.stub.StreamObserver<pt.tecnico.staysafe.dgs.grpc.ClearResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getCtrlClearMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class DgsBlockingStub extends io.grpc.stub.AbstractBlockingStub<DgsBlockingStub> {
    private DgsBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DgsBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new DgsBlockingStub(channel, callOptions);
    }

    /**
     */
    public pt.tecnico.staysafe.dgs.grpc.SnifferJoinResponse snifferJoin(pt.tecnico.staysafe.dgs.grpc.SnifferJoinRequest request) {
      return blockingUnaryCall(
          getChannel(), getSnifferJoinMethod(), getCallOptions(), request);
    }

    /**
     */
    public pt.tecnico.staysafe.dgs.grpc.SnifferInfoResponse snifferInfo(pt.tecnico.staysafe.dgs.grpc.SnifferInfoRequest request) {
      return blockingUnaryCall(
          getChannel(), getSnifferInfoMethod(), getCallOptions(), request);
    }

    /**
     */
    public pt.tecnico.staysafe.dgs.grpc.ReportResponse report(pt.tecnico.staysafe.dgs.grpc.ReportRequest request) {
      return blockingUnaryCall(
          getChannel(), getReportMethod(), getCallOptions(), request);
    }

    /**
     */
    public pt.tecnico.staysafe.dgs.grpc.PingResponse ctrlPing(pt.tecnico.staysafe.dgs.grpc.PingRequest request) {
      return blockingUnaryCall(
          getChannel(), getCtrlPingMethod(), getCallOptions(), request);
    }

    /**
     */
    public pt.tecnico.staysafe.dgs.grpc.InitResponse ctrlInit(pt.tecnico.staysafe.dgs.grpc.InitRequest request) {
      return blockingUnaryCall(
          getChannel(), getCtrlInitMethod(), getCallOptions(), request);
    }

    /**
     */
    public pt.tecnico.staysafe.dgs.grpc.ClearResponse ctrlClear(pt.tecnico.staysafe.dgs.grpc.ClearRequest request) {
      return blockingUnaryCall(
          getChannel(), getCtrlClearMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class DgsFutureStub extends io.grpc.stub.AbstractFutureStub<DgsFutureStub> {
    private DgsFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DgsFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new DgsFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<pt.tecnico.staysafe.dgs.grpc.SnifferJoinResponse> snifferJoin(
        pt.tecnico.staysafe.dgs.grpc.SnifferJoinRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getSnifferJoinMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<pt.tecnico.staysafe.dgs.grpc.SnifferInfoResponse> snifferInfo(
        pt.tecnico.staysafe.dgs.grpc.SnifferInfoRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getSnifferInfoMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<pt.tecnico.staysafe.dgs.grpc.ReportResponse> report(
        pt.tecnico.staysafe.dgs.grpc.ReportRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getReportMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<pt.tecnico.staysafe.dgs.grpc.PingResponse> ctrlPing(
        pt.tecnico.staysafe.dgs.grpc.PingRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getCtrlPingMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<pt.tecnico.staysafe.dgs.grpc.InitResponse> ctrlInit(
        pt.tecnico.staysafe.dgs.grpc.InitRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getCtrlInitMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<pt.tecnico.staysafe.dgs.grpc.ClearResponse> ctrlClear(
        pt.tecnico.staysafe.dgs.grpc.ClearRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getCtrlClearMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_SNIFFER_JOIN = 0;
  private static final int METHODID_SNIFFER_INFO = 1;
  private static final int METHODID_REPORT = 2;
  private static final int METHODID_CTRL_PING = 3;
  private static final int METHODID_CTRL_INIT = 4;
  private static final int METHODID_CTRL_CLEAR = 5;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final DgsImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(DgsImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_SNIFFER_JOIN:
          serviceImpl.snifferJoin((pt.tecnico.staysafe.dgs.grpc.SnifferJoinRequest) request,
              (io.grpc.stub.StreamObserver<pt.tecnico.staysafe.dgs.grpc.SnifferJoinResponse>) responseObserver);
          break;
        case METHODID_SNIFFER_INFO:
          serviceImpl.snifferInfo((pt.tecnico.staysafe.dgs.grpc.SnifferInfoRequest) request,
              (io.grpc.stub.StreamObserver<pt.tecnico.staysafe.dgs.grpc.SnifferInfoResponse>) responseObserver);
          break;
        case METHODID_REPORT:
          serviceImpl.report((pt.tecnico.staysafe.dgs.grpc.ReportRequest) request,
              (io.grpc.stub.StreamObserver<pt.tecnico.staysafe.dgs.grpc.ReportResponse>) responseObserver);
          break;
        case METHODID_CTRL_PING:
          serviceImpl.ctrlPing((pt.tecnico.staysafe.dgs.grpc.PingRequest) request,
              (io.grpc.stub.StreamObserver<pt.tecnico.staysafe.dgs.grpc.PingResponse>) responseObserver);
          break;
        case METHODID_CTRL_INIT:
          serviceImpl.ctrlInit((pt.tecnico.staysafe.dgs.grpc.InitRequest) request,
              (io.grpc.stub.StreamObserver<pt.tecnico.staysafe.dgs.grpc.InitResponse>) responseObserver);
          break;
        case METHODID_CTRL_CLEAR:
          serviceImpl.ctrlClear((pt.tecnico.staysafe.dgs.grpc.ClearRequest) request,
              (io.grpc.stub.StreamObserver<pt.tecnico.staysafe.dgs.grpc.ClearResponse>) responseObserver);
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

  private static abstract class DgsBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    DgsBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return pt.tecnico.staysafe.dgs.grpc.DgsOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("Dgs");
    }
  }

  private static final class DgsFileDescriptorSupplier
      extends DgsBaseDescriptorSupplier {
    DgsFileDescriptorSupplier() {}
  }

  private static final class DgsMethodDescriptorSupplier
      extends DgsBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    DgsMethodDescriptorSupplier(String methodName) {
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
      synchronized (DgsGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new DgsFileDescriptorSupplier())
              .addMethod(getSnifferJoinMethod())
              .addMethod(getSnifferInfoMethod())
              .addMethod(getReportMethod())
              .addMethod(getCtrlPingMethod())
              .addMethod(getCtrlInitMethod())
              .addMethod(getCtrlClearMethod())
              .build();
        }
      }
    }
    return result;
  }
}
