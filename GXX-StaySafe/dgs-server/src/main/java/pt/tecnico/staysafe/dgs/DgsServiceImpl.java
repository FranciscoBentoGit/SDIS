package pt.tecnico.staysafe.dgs;

import io.grpc.stub.StreamObserver;
import pt.tecnico.staysafe.dgs.grpc.DgsGrpc;
import pt.tecnico.staysafe.dgs.grpc.*;
import com.google.protobuf.Timestamp;
import java.util.regex.*;

import static io.grpc.Status.INVALID_ARGUMENT;


public class DgsServiceImpl extends DgsGrpc.DgsImplBase {

	private DgsServices dService = new DgsServices();

	@Override
	public void snifferJoin(SnifferJoinRequest request, StreamObserver<SnifferJoinResponse> responseObserver) {

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
	    	SnifferJoinResponse response = SnifferJoinResponse.newBuilder().setSuccess(dService.sniffer_join(name, address)).build();
		    responseObserver.onNext(response);
		    responseObserver.onCompleted();
	    }
	}

	@Override
	public void snifferInfo(SnifferInfoRequest request, StreamObserver<SnifferInfoResponse> responseObserver) {

		String name = request.getName();
		if (name == null || name.isBlank()) {
	        responseObserver.onError(INVALID_ARGUMENT.withDescription("Name: input cannot be empty!").asRuntimeException());
	    } 

	    else {
	    	SnifferInfoResponse response = SnifferInfoResponse.newBuilder().setNameAddress(dService.sniffer_info(name)).build();
		    responseObserver.onNext(response);
		    responseObserver.onCompleted();
	    }  
	}

	@Override
	public void report(ReportRequest request, StreamObserver<ReportResponse> responseObserver) {

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
	    	ReportResponse response = ReportResponse.newBuilder().setSuccess(dService.report(name,infection,id,timeIn,timeOut)).build();
		    responseObserver.onNext(response);
		    responseObserver.onCompleted();
	    }   
	}

	@Override
	public void individualInfectionProbability(IndividualProbRequest request, StreamObserver<IndividualProbResponse> responseObserver) {

	    long id = request.getId();
	    if (String.valueOf(id).length() != 9) {
	    	 responseObserver.onError(INVALID_ARGUMENT.withDescription("Id: invalid input id - must have 9 digits!").asRuntimeException());
	    }
	    
	    else {
		    IndividualProbResponse response = IndividualProbResponse.newBuilder().setProb(dService.individual_infection_probability(id)).build();
		    responseObserver.onNext(response);
		    responseObserver.onCompleted();
	    }
	}

	@Override
	public void aggregateInfectionProbability(AggregateProbRequest request, StreamObserver<AggregateProbResponse> responseObserver) {

	    String command = request.getCommand();
		if (command == null || command.isBlank()) {
	        responseObserver.onError(INVALID_ARGUMENT.withDescription("Command: input cannot be empty!").asRuntimeException());
		}
	    
	    else if (command.equals("mean_dev")){
		    AggregateProbResponse response = AggregateProbResponse.newBuilder().addStat(dService.aggregate_infection_probability(0,command)).addStat(dService.aggregate_infection_probability(1,command)).build();
		    responseObserver.onNext(response);
		    responseObserver.onCompleted();
		}

		else {
			AggregateProbResponse response = AggregateProbResponse.newBuilder().addStat(dService.aggregate_infection_probability(0,command)).addStat(dService.aggregate_infection_probability(1,command)).addStat(dService.aggregate_infection_probability(2,command)).build();
		    responseObserver.onNext(response);
		    responseObserver.onCompleted();
		}
	}
	
	@Override
	public void ctrlPing(PingRequest request, StreamObserver<PingResponse> responseObserver) {

	    String input = request.getText();
	    if (input == null || input.isBlank()) {
	        responseObserver.onError(INVALID_ARGUMENT.withDescription("Input cannot be empty!").asRuntimeException());
	    }
	    
	    else {
	    	String output = "Hello " + input + "!";
		    PingResponse response = PingResponse.newBuilder().setText(output).build();
		    responseObserver.onNext(response);
		    responseObserver.onCompleted();
	    }
	}

	

	@Override
	public void ctrlInit(InitRequest request, StreamObserver<InitResponse> responseObserver) {
		String snifferName = request.getSnifferName();
		if (snifferName == null || snifferName.isBlank()) {
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

	    else{
	    	InitResponse response = InitResponse.newBuilder().setSuccess(dService.ctrl_init(snifferName,infection,id,timeIn,timeOut)).build();
		    responseObserver.onNext(response);
		    responseObserver.onCompleted();
	    }
		
	}

	@Override
	public void ctrlClear(ClearRequest request, StreamObserver<ClearResponse> responseObserver) {
		ClearResponse response = ClearResponse.newBuilder().setSuccess(dService.ctrl_clear()).build();
	    responseObserver.onNext(response);
	    responseObserver.onCompleted();
	}
}