package pt.tecnico.staysafe.dgs.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import com.google.protobuf.Timestamp;

import pt.tecnico.staysafe.dgs.grpc.DgsGrpc;
import pt.tecnico.staysafe.dgs.grpc.*;
import pt.ulisboa.tecnico.sdis.zk.*;

public class DgsFrontend {
	private DgsGrpc.DgsBlockingStub _stub;
	private long[] _prevTs;
	private LastView _lastView;
	private long[] _possibleRead;

	public DgsFrontend(String zooHost, String zooPort, String path) {
		try {
			_prevTs = new long[] {0,0};
			_possibleRead = new long[] {0,0};
			ZKNaming zkNaming = new ZKNaming(zooHost,zooPort);
			// lookup
			ZKRecord record = zkNaming.lookup(path);
			String target = record.getURI();
			
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
		String[] splitJoin = response.toString().split(" - ", 4);
		_prevTs[0] = Long.parseLong(splitJoin[1]);
		_prevTs[1] = Long.parseLong(splitJoin[2]);
		return response;
	}

	public SnifferInfoResponse sniffer_info(SnifferInfoRequest request) {
		SnifferInfoResponse response;
		response = _stub.snifferInfo(request);
		String[] splitInfo = response.toString().split(" - ", 4);
		_prevTs[0] = Long.parseLong(splitInfo[1]);
		_prevTs[1] = Long.parseLong(splitInfo[2]);
		return response;
	}

	public ReportResponse report(ReportRequest request) {
		ReportResponse response;
		response = _stub.report(request);
		String[] splitReport = response.toString().split(" - ", 4);
		_prevTs[0] = Long.parseLong(splitReport[1]);
		_prevTs[1] = Long.parseLong(splitReport[2]);
		return response;
	}

	public PingResponse ctrl_ping(PingRequest request) {
		PingResponse response;
		response = _stub.ctrlPing(request);
		String[] splitPing= response.toString().split(" - ", 4);
		_prevTs[0] = Long.parseLong(splitPing[1]);
		_prevTs[1] = Long.parseLong(splitPing[2]);
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
		String[] splitClear = response.toString().split(" - ", 4);
		_prevTs[0] = Long.parseLong(splitClear[1]);
		_prevTs[1] = Long.parseLong(splitClear[2]);
		return response;
	}

	public IndividualProbResponse individual_infection_probability(IndividualProbRequest request) {
		IndividualProbResponse response;
		long replicaId = request.getReplicaId()-1;
		
		response = _stub.individualInfectionProbability(request);
		String convResponse = response.toString();
		String[] splited1 = convResponse.split(" ", 2);
		String[] splited2 = splited1[1].split("\n", 2);
		String prob = splited2[0].toString();

		String[] splited4 =	splited2[1].split(" ", 2);	
		String[] splited5 =	splited4[1].split("\n", 2);	
		String ts1 = splited5[0].toString();

		String[] splited6 =	splited5[1].split(" ", 2);
		String ts2 = splited6[1].toString();
		
		
		_possibleRead[0] = (long) Float.parseFloat(ts1);
		_possibleRead[1] = (long) Float.parseFloat(ts2);


		if (_possibleRead[0] < _prevTs[0] || _possibleRead[1] < _prevTs[1]){
			// if it is a different read, i need to rely on my backup read
			return _lastView.getSingleProb();
		}
		//if it is a same  read, i can update my backup read
		_lastView.setSingleProb(response);
		
		return response;
	}

	public AggregateProbResponse aggregate_infection_probability(AggregateProbRequest request) {
		AggregateProbResponse response;
		String command = request.getCommand();
		
		response = _stub.aggregateInfectionProbability(request);
		
		if (command.equals("mean_dev")){
			String convResponse = response.toString();
			String[] splited1 = convResponse.split(" ", 2);
			String[] splited2 = splited1[1].split("\n", 2);
			String prob1 = splited2[0].toString();

			String[] splited4 =	splited2[1].split(" ", 2);	
			String[] splited5 =	splited4[1].split("\n", 2);	
			String prob2 = splited5[0].toString();

			String[] splited7 =	splited5[1].split(" ", 2);
			String[] splited8 =	splited7[1].split("\n", 2);
			String ts1 = splited8[0].toString();

			String[] splited9 =	splited8[1].split(" ", 2);
			String ts2 = splited9[1].toString();
		
			_possibleRead[0] = (long) Float.parseFloat(ts1);
			_possibleRead[1] = (long) Float.parseFloat(ts2);

			if (_possibleRead[0] < _prevTs[0] || _possibleRead[1] < _prevTs[1]){
				// if it is a different read, i need to rely on my backup read
				return _lastView.getMeanDev();
			}
			//if it is a same  read, i can update my backup read
			_lastView.setMeanDev(response);
			
			return response;

		} else{
			String convResponse = response.toString();
			String[] splited1 = convResponse.split(" ", 2);
			String[] splited2 = splited1[1].split("\n", 2);
			String prob1 = splited2[0].toString();

			String[] splited4 =	splited2[1].split(" ", 2);	
			String[] splited5 =	splited4[1].split("\n", 2);	
			String prob2 = splited5[0].toString();

			String[] splited7 =	splited5[1].split(" ", 2);
			String[] splited8 =	splited7[1].split("\n", 2);
			String prob3 =	splited8[0].toString();

			String[] splited9 =	splited8[1].split(" ", 2);
			String[] splited10 = splited9[1].split("\n", 2);
			String ts1 = splited10[0].toString();

			String[] splited11 = splited10[1].split(" ", 2);
			String ts2 = splited11[1].toString();

			_possibleRead[0] = (long) Float.parseFloat(ts1);
			_possibleRead[1] = (long) Float.parseFloat(ts2);

			if (_possibleRead[0] < _prevTs[0] || _possibleRead[1] < _prevTs[1]){
				// if it is a different read, i need to rely on my backup read
				return _lastView.getPercentiles();
			}
			//if it is a same  read, i can update my backup read
			_lastView.setPercentiles(response);
			
			return response;
		}
		
	}
}
