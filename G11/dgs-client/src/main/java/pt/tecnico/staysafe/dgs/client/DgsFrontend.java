package pt.tecnico.staysafe.dgs.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import com.google.protobuf.Timestamp;

import pt.tecnico.staysafe.dgs.grpc.DgsGrpc;
import pt.tecnico.staysafe.dgs.grpc.*;
import pt.ulisboa.tecnico.sdis.zk.*;

public class DgsFrontend {
	private DgsGrpc.DgsBlockingStub _stub;

	public DgsFrontend(String zooHost, String zooPort, String path) {
		try {
			ZKNaming zkNaming = new ZKNaming(zooHost,zooPort);
		// lookup
		ZKRecord record = zkNaming.lookup(path);
		String target = record.getURI();
		
		//final String target = host + ":" + port;
		final ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
		
		DgsGrpc.DgsBlockingStub stub = DgsGrpc.newBlockingStub(channel);
		_stub = stub;
		} catch (ZKNamingException e) {
			System.out.println("Caught exception with description: " + e.getMessage());
		}
		
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

	public PingResponse ctrl_ping(PingRequest request) {
		PingResponse response;
		response = _stub.ctrlPing(request);
		return response;
	}

	public InitResponse ctrl_init(InitRequest request) {
		InitResponse response;
		response = _stub.ctrlInit(request);
		return response;
	}

	public ClearResponse ctrl_clear(ClearRequest request) {
		ClearResponse response;
		response = _stub.ctrlClear(request);
		return response;
	}

	public IndividualProbResponse individual_infection_probability(IndividualProbRequest request) {
		IndividualProbResponse response;
		response = _stub.individualInfectionProbability(request);
		return response;
	}

	public AggregateProbResponse aggregate_infection_probability(AggregateProbRequest request) {
		AggregateProbResponse response;
		response = _stub.aggregateInfectionProbability(request);
		return response;
	}
}
