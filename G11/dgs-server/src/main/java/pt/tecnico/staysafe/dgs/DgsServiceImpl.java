package pt.tecnico.staysafe.dgs;

import io.grpc.stub.StreamObserver;
import pt.tecnico.staysafe.dgs.grpc.DgsGrpc;
import pt.tecnico.staysafe.dgs.grpc.*;
import com.google.protobuf.Timestamp;
import java.util.regex.*;
import java.util.concurrent.CopyOnWriteArrayList;

import static io.grpc.Status.INVALID_ARGUMENT;
import java.lang.IllegalStateException;


public class DgsServiceImpl extends DgsGrpc.DgsImplBase {

	private DgsServices dService = new DgsServices();
	private long[] _valueTs = {0,0,0};
	private CopyOnWriteArrayList<Operation> _executedList = new CopyOnWriteArrayList<Operation>();
    private Operation _newOperation;
    private long _identifier = 0;

	@Override
	public void snifferJoin(SnifferJoinRequest request, StreamObserver<SnifferJoinResponse> responseObserver) {
		try {
			int replicaId = request.getReplicaId();

			String name = request.getName();
			if (name == null || name.isBlank()) {
				responseObserver.onError(INVALID_ARGUMENT.withDescription("Name: input cannot be empty!").asRuntimeException());
			}
			if (name.length() < 5 || name.length() > 30) {
				responseObserver.onError(INVALID_ARGUMENT.withDescription("Name: invalid name, should contain 5 to 30 characters!").asRuntimeException());
			}
			
			//In order to filter if a name is alphanumeric(cotains both letters and numbers)
			String regex = "^(?=.*[a-zA-Z])(?=.*[0-9])[A-Za-z0-9]+$";
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(name);
			if (!(m.matches())) {
				responseObserver.onError(INVALID_ARGUMENT.withDescription("Name: invalid name, should be alpha numeric!").asRuntimeException());
			}

			String address = request.getAddress();
			if (address == null || address.isBlank()) {
				responseObserver.onError(INVALID_ARGUMENT.withDescription("Address: input cannot be empty!").asRuntimeException());
			}

			else {
				String success = dService.sniffer_join(name, address);
				if (success.equals("Success to join sniffer.")) {
					_identifier = _identifier + (long) 1;
					_valueTs[replicaId - 1]++;
					_newOperation = new Operation(_identifier,"join",request,null);
					_executedList.add(_newOperation);
				}
				SnifferJoinResponse response = SnifferJoinResponse.newBuilder().setSuccess(success).addTs(_valueTs[0]).addTs(_valueTs[1]).addTs(_valueTs[2]).build();
				responseObserver.onNext(response);
				responseObserver.onCompleted();
			}
		} catch (IllegalStateException ise) {
			System.out.println("Exception caught!");
		}
		
	}

	@Override
	public void snifferInfo(SnifferInfoRequest request, StreamObserver<SnifferInfoResponse> responseObserver) {
		try {
			int replicaId = request.getReplicaId();

			String name = request.getName();
			if (name == null || name.isBlank()) {
				responseObserver.onError(INVALID_ARGUMENT.withDescription("Name: input cannot be empty!").asRuntimeException());
			}

			else {
				SnifferInfoResponse response = SnifferInfoResponse.newBuilder().setNameAddress(dService.sniffer_info(name)).addTs(_valueTs[0]).addTs(_valueTs[1]).addTs(_valueTs[2]).build();
				responseObserver.onNext(response);
				responseObserver.onCompleted();
			}  
		} catch (IllegalStateException ise) {
			System.out.println("Exception caught!");
		}
		
	}

	@Override
	public void report(ReportRequest request, StreamObserver<ReportResponse> responseObserver) {
		try {
			int replicaId = request.getReplicaId();

			String name = request.getName();
			if (name == null || name.isBlank()) {
				responseObserver.onError(INVALID_ARGUMENT.withDescription("Name: input cannot be empty!").asRuntimeException());
			}

			String infection = request.getInfection();
			if (infection == null || infection.isBlank()) {
				responseObserver.onError(INVALID_ARGUMENT.withDescription("Infection: input cannot be empty!").asRuntimeException());
			}

			long id = request.getId();
			if (String.valueOf(id).length() != 9) {
				responseObserver.onError(INVALID_ARGUMENT.withDescription("Id: invalid input id - must have 9 digits!").asRuntimeException());
			}

			com.google.protobuf.Timestamp timeIn = request.getTimeIn();
			if (!(timeIn.isInitialized())) {
				responseObserver.onError(INVALID_ARGUMENT.withDescription("TimeIn: invalid input time!").asRuntimeException());
			}

			com.google.protobuf.Timestamp timeOut = request.getTimeOut();
			if (!(timeOut.isInitialized())) {
				responseObserver.onError(INVALID_ARGUMENT.withDescription("TimeOut: invalid input time!").asRuntimeException());
			}

			else {
				String success = dService.report(name,infection,id,timeIn,timeOut);
				if (success.equals("Success to report.")) {
					_valueTs[replicaId - 1]++;
					_identifier = _identifier + (long) 1;
					_newOperation = new Operation(_identifier,"report",null,request);
					_executedList.add(_newOperation);
				}

				ReportResponse response = ReportResponse.newBuilder().setSuccess(success).addTs(_valueTs[0]).addTs(_valueTs[1]).addTs(_valueTs[2]).build();
				responseObserver.onNext(response);
				responseObserver.onCompleted();
			}
		} catch (IllegalStateException ise) {
			System.out.println("Exception caught!");
		}
		   
	}

	@Override
	public void individualInfectionProbability(IndividualProbRequest request, StreamObserver<IndividualProbResponse> responseObserver) {
		System.out.println("entrei - ind");
		try {
			System.out.println("entrei - try");
			int replicaId = request.getReplicaId();

			long id = request.getId();
			if (String.valueOf(id).length() != 9) {
				responseObserver.onError(INVALID_ARGUMENT.withDescription("Id: invalid input id - must have 9 digits!").asRuntimeException());
			}
			
			else {
				System.out.println("entrei - construir");
				IndividualProbResponse response = IndividualProbResponse.newBuilder().setProb(dService.individual_infection_probability(id)).addTs(_valueTs[0]).addTs(_valueTs[1]).addTs(_valueTs[2]).build();
				System.out.println("entrei - passei");
				System.out.printf("%f\n",response.getProb());
				System.out.printf("%d\n",response.getTs(0));
				System.out.printf("%d\n",response.getTs(1));
				System.out.printf("%d\n",response.getTs(2));
				responseObserver.onNext(response);
				responseObserver.onCompleted();
			}
		} catch (IllegalStateException ise) {
			System.out.println("Exception caught!");
		}
	    
	}

	@Override
	public void aggregateInfectionProbability(AggregateProbRequest request, StreamObserver<AggregateProbResponse> responseObserver) {

		try {
			int replicaId = request.getReplicaId();

			String command = request.getCommand();
			if (command == null || command.isBlank()) {
				responseObserver.onError(INVALID_ARGUMENT.withDescription("Command: input cannot be empty!").asRuntimeException());
			}
			
			else if (command.equals("mean_dev")){
				AggregateProbResponse response = AggregateProbResponse.newBuilder().addStat(dService.aggregate_infection_probability(0,command)).addStat(dService.aggregate_infection_probability(1,command)).addStat(dService.aggregate_infection_probability(2,command)).addTs(_valueTs[0]).addTs(_valueTs[1]).addTs(_valueTs[2]).build();
				responseObserver.onNext(response);
				responseObserver.onCompleted();
			}

			else {
				AggregateProbResponse response = AggregateProbResponse.newBuilder().addStat(dService.aggregate_infection_probability(0,command)).addStat(dService.aggregate_infection_probability(1,command)).addStat(dService.aggregate_infection_probability(2,command)).addStat(dService.aggregate_infection_probability(3,command)).addTs(_valueTs[0]).addTs(_valueTs[1]).addTs(_valueTs[2]).build();
				responseObserver.onNext(response);
				responseObserver.onCompleted();
			}
		} catch (IllegalStateException ise) {
			System.out.println("Exception caught!");
		}

	}
	
	@Override
	public void ctrlPing(PingRequest request, StreamObserver<PingResponse> responseObserver) {
		try {
			int replicaId = request.getReplicaId();
			String input = request.getText();
			if (input == null || input.isBlank()) {
				responseObserver.onError(INVALID_ARGUMENT.withDescription("Input cannot be empty!").asRuntimeException());
			}
			
			else {
				PingResponse response = PingResponse.newBuilder().setText(dService.ctrl_ping(input)).addTs(_valueTs[0]).addTs(_valueTs[1]).addTs(_valueTs[2]).build();
				responseObserver.onNext(response);
				responseObserver.onCompleted();
			}
		} catch (IllegalStateException ise) {
			System.out.println("Exception caught!");
		}   
	}

	@Override
	public void ctrlClear(ClearRequest request, StreamObserver<ClearResponse> responseObserver) {
		int replicaId = request.getReplicaId();

		String success = dService.ctrl_clear();
		if (success.equals("All observations removed successfully.")) {
			_identifier = _identifier + (long) 1;
			_valueTs[replicaId - 1]++;
			_newOperation = new Operation(_identifier,"clear",null,null);
			_executedList.add(_newOperation);
		}

		ClearResponse response = ClearResponse.newBuilder().setSuccess(success).addTs(_valueTs[0]).addTs(_valueTs[1]).addTs(_valueTs[2]).build();
	    responseObserver.onNext(response);
	    responseObserver.onCompleted();
	}

	

	@Override
	public void update(UpdateRequest request, StreamObserver<UpdateResponse> responseObserver) {
		UpdateResponse response = UpdateResponse.newBuilder().addTs(_valueTs[0]).addTs(_valueTs[1]).addTs(_valueTs[2]).build();
		responseObserver.onNext(response);
	    responseObserver.onCompleted();
	}

	public long[] getValueTs(){
        return _valueTs;
	}
	
	public CopyOnWriteArrayList<Operation> getExecutedList() {
		return _executedList;
	}
}