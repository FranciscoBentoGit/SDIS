package pt.tecnico.staysafe.dgs.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import pt.tecnico.staysafe.dgs.grpc.DgsGrpc;
import pt.tecnico.staysafe.dgs.grpc.*;

public class DgsFrontend {
	private DgsGrpc.DgsBlockingStub _stub;

	public DgsFrontend(String host, int port) {
		final String target = host + ":" + port;
		final ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
		DgsGrpc.DgsBlockingStub stub = DgsGrpc.newBlockingStub(channel);
		_stub = stub;
	}

	public SnifferJoinResponse sniffer_join(SnifferJoinRequest request) {
		SnifferJoinResponse response;

		response = _stub.snifferJoin(request);

		return response;
	}

	public SnifferInfoResponse sniffer_info(SnifferInfoRequest request) {
		SnifferInfoResponse response;

		response = _stub.snifferInfo(request);

		return response;
	}

	public ReportResponse report(ReportRequest request) {
		ReportResponse response;

		response = _stub.report(request);

		return response;
	}
}