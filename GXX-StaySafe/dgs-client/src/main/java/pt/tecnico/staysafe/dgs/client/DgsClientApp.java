package pt.tecnico.staysafe.dgs.client;

import java.util.Scanner;
import com.google.protobuf.Timestamp;

import pt.tecnico.staysafe.dgs.grpc.DgsGrpc;
import pt.tecnico.staysafe.dgs.grpc.*;


public class DgsClientApp {
	
	public static void main(String[] args) {
		System.out.println(DgsClientApp.class.getSimpleName());
		
		// receive and print arguments
		System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}

		// check arguments
		if (args.length < 2) {
			System.out.println("Argument(s) missing!");
			System.out.printf("Usage: java %s host port%n", DgsClientApp.class.getName());
			return;
		}

		final String host = args[0];
		final int port = Integer.parseInt(args[1]);
	}
	
	public static SnifferJoinResponse sniffer_join(DgsFrontend frontend, String snifferName, String address) {
		SnifferJoinResponse response;
		SnifferJoinRequest request = SnifferJoinRequest.newBuilder().setName(snifferName).setAddress(address).build();
		response = frontend.sniffer_join(request);
		return response;
	}

	public static SnifferInfoResponse sniffer_info(DgsFrontend frontend, String snifferName) {
		SnifferInfoResponse response;
		SnifferInfoRequest request = SnifferInfoRequest.newBuilder().setName(snifferName).build();
		response = frontend.sniffer_info(request);
		return response;
	}

	public static void sleep_request(int sleepTime) {
		try {
		    Thread.sleep(sleepTime * 1000);
		} catch (InterruptedException ie) {
		    Thread.currentThread().interrupt();
		}
	}

	public static ReportResponse sniffer_report(DgsFrontend frontend, String snifferName, String observation, Timestamp timestamp) {
		ReportResponse response;
		ReportRequest request = ReportRequest.newBuilder().setName(snifferName).setObservations(observation).setTime(timestamp).build();
		response = frontend.report(request);
		return response;
	}

}
