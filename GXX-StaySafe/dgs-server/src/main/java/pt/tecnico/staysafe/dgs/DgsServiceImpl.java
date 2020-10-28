package pt.tecnico.staysafe.dgs;

import io.grpc.stub.StreamObserver;
import pt.tecnico.staysafe.dgs.grpc.DgsGrpc;
import pt.tecnico.staysafe.dgs.grpc.*;
import com.google.protobuf.Timestamp;
import java.text.ParseException;

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
	    	responseObserver.onError(INVALID_ARGUMENT.withDescription("Name: invalid name!").asRuntimeException());
		}

	    String address = request.getAddress();
	    if (address == null || address.isBlank()) {
	        responseObserver.onError(INVALID_ARGUMENT.withDescription("Address: input cannot be empty!").asRuntimeException());
	    }

	    SnifferJoinResponse response = SnifferJoinResponse.newBuilder().setSuccess(dService.sniffer_join(name, address)).build();

	    responseObserver.onNext(response);
	    responseObserver.onCompleted();
	}

	@Override
	public void snifferInfo(SnifferInfoRequest request, StreamObserver<SnifferInfoResponse> responseObserver) {

		String name = request.getName();
		if (name == null || name.isBlank()) {
	        responseObserver.onError(INVALID_ARGUMENT.withDescription("Name: input cannot be empty!").asRuntimeException());
	    }

	    SnifferInfoResponse response = SnifferInfoResponse.newBuilder().setNameAddress(dService.sniffer_info(name)).build();
	    
	    responseObserver.onNext(response);
	    responseObserver.onCompleted();
	}

	@Override
	public void report(ReportRequest request, StreamObserver<ReportResponse> responseObserver) {

		String name = request.getName();
		if (name == null || name.isBlank()) {
	        responseObserver.onError(INVALID_ARGUMENT.withDescription("Name: input cannot be empty!").asRuntimeException());
	    }

	    String observations = request.getObservations();
		if (observations == null || observations.isBlank()) {
	        responseObserver.onError(INVALID_ARGUMENT.withDescription("Observations: input cannot be empty!").asRuntimeException());
	    }

	    com.google.protobuf.Timestamp time = request.getTime();
	    if (!(time.isInitialized())) {
	    	responseObserver.onError(INVALID_ARGUMENT.withDescription("Time: invalid input time!").asRuntimeException());
	    }

	    ReportResponse response = ReportResponse.newBuilder().setSuccess(dService.report(name,observations,time)).build();
	    
	    responseObserver.onNext(response);
	    responseObserver.onCompleted();
	}

}