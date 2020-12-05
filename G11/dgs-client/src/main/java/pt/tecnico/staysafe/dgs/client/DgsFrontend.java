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
	private long[] _possibleRead;
	private IndividualProbResponse _singleProb;
    private AggregateProbResponse _meanDev;
    private AggregateProbResponse _percentiles;
	

	public DgsFrontend(String zooHost, String zooPort, String path) {
		try {
			_prevTs = new long[] {0,0,0};
			_possibleRead = new long[] {0,0,0};
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
		_prevTs[0] = response.getTs(0);
		_prevTs[1] = response.getTs(1);
		_prevTs[2] = response.getTs(2);
		System.out.printf("Frontend received answer with TS{%d,%d,%d}%n",_prevTs[0],_prevTs[1],_prevTs[2]);
		return response;
	}

	public SnifferInfoResponse sniffer_info(SnifferInfoRequest request) {
		SnifferInfoResponse response;
		response = _stub.snifferInfo(request);
		_prevTs[0] = response.getTs(0);
		_prevTs[1] = response.getTs(1);
		_prevTs[2] = response.getTs(2);
		System.out.printf("Frontend received answer with TS{%d,%d,%d}%n",_prevTs[0],_prevTs[1],_prevTs[2]);
		return response;
	}

	public ReportResponse report(ReportRequest request) {
		ReportResponse response;
		response = _stub.report(request);
		_prevTs[0] = response.getTs(0);
		_prevTs[1] = response.getTs(1);
		_prevTs[2] = response.getTs(2);
		System.out.printf("Frontend received answer with TS{%d,%d,%d}%n",_prevTs[0],_prevTs[1],_prevTs[2]);
		return response;
	}

	public PingResponse ctrl_ping(PingRequest request) {
		PingResponse response;
		response = _stub.ctrlPing(request);
		_prevTs[0] = response.getTs(0);
		_prevTs[1] = response.getTs(1);
		_prevTs[2] = response.getTs(2);
		System.out.printf("Frontend received answer with TS{%d,%d,%d}%n",_prevTs[0],_prevTs[1],_prevTs[2]);
		return response;
	}

	public ClearResponse ctrl_clear(ClearRequest request) {
		ClearResponse response;
		response = _stub.ctrlClear(request);
		_prevTs[0] = response.getTs(0);
		_prevTs[1] = response.getTs(1);
		_prevTs[2] = response.getTs(2);
		System.out.printf("Frontend received answer with TS{%d,%d,%d}%n",_prevTs[0],_prevTs[1],_prevTs[2]);
		return response;
	}

	public IndividualProbResponse individual_infection_probability(IndividualProbRequest request) {
		IndividualProbResponse response;
		
		response = _stub.individualInfectionProbability(request);
		
		//This is the timestamp returned by the function, we need to evaluate if it is updated or not
		_possibleRead[0] = response.getTs(0);
		_possibleRead[1] = response.getTs(1);
		_possibleRead[2] = response.getTs(2);


		// If it is a different read, we need to rely on the backup read
		if (_possibleRead[0] < _prevTs[0] || _possibleRead[1] < _prevTs[1] || _possibleRead[2] < _prevTs[2]){
			System.out.printf("Frontend received answer with TS{%d,%d,%d}%n",_prevTs[0],_prevTs[1],_prevTs[2]);
			return _singleProb;
		}

		//If it is a same  read, we can update the backup read
		_prevTs[0] = _possibleRead[0];
		_prevTs[1] = _possibleRead[1];
		_prevTs[2] = _possibleRead[2];
		System.out.printf("Frontend received answer with TS{%d,%d,%d}%n",_prevTs[0],_prevTs[1],_prevTs[2]);
		_singleProb = response;
		
		return response;
	}

	public AggregateProbResponse aggregate_infection_probability(AggregateProbRequest request) {
		AggregateProbResponse response;
		String command = request.getCommand();
		
		response = _stub.aggregateInfectionProbability(request);
		
		//This is the timestamp returned by the function, we need to evaluate if it is updated or not
		if (command.equals("mean_dev")){
			_possibleRead[0] = response.getTs(0);
			_possibleRead[1] = response.getTs(1);
			_possibleRead[2] = response.getTs(2);
			
			if (_possibleRead[0] < _prevTs[0] || _possibleRead[1] < _prevTs[1] || _possibleRead[2] < _prevTs[2]){
				System.out.printf("Frontend received answer with TS{%d,%d,%d}%n",_prevTs[0],_prevTs[1],_prevTs[2]);
				return _meanDev;
			}

			//If it is a same  read, we can update the backup read
			_prevTs[0] = _possibleRead[0];
			_prevTs[1] = _possibleRead[1];
			_prevTs[2] = _possibleRead[2];
			System.out.printf("Frontend received answer with TS{%d,%d,%d}%n",_prevTs[0],_prevTs[1],_prevTs[2]);
			_meanDev = response;
			
			return response;

		} else{
			//This is the timestamp returned by the function, we need to evaluate if it is updated or not
			_possibleRead[0] = response.getTs(0);
			_possibleRead[1] = response.getTs(1);
			_possibleRead[2] = response.getTs(2);


			// If it is a different read, we need to rely on the backup read
			if (_possibleRead[0] < _prevTs[0] || _possibleRead[1] < _prevTs[1] || _possibleRead[2] < _prevTs[2]){
				System.out.printf("Frontend received answer with TS{%d,%d,%d}%n",_prevTs[0],_prevTs[1],_prevTs[2]);
				return _percentiles;
			}

			//If it is a same  read, we can update the backup read
			_prevTs[0] = _possibleRead[0];
			_prevTs[1] = _possibleRead[1];
			_prevTs[2] = _possibleRead[2];
			System.out.printf("Frontend received answer with TS{%d,%d,%d}%n",_prevTs[0],_prevTs[1],_prevTs[2]);			
			_percentiles = response;
			
			return response;
		}
		
	}
}
