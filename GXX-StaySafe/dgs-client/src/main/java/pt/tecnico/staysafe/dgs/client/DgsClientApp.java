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

		execClient(host, port);
	}

	private static void execClient(String host, int port) {
		DgsFrontend frontend = new DgsFrontend(host, port);
		String go;
		int flag = 0;
		try (Scanner scanner = new Scanner(System.in)) {
			do {
				go = scanner.nextLine();
				if (go.equals("") || go.equals("exitClient")) {
					flag = 1;
				}

				if (go.equals("ping")) {
					PingResponse response;
					PingRequest request = PingRequest.newBuilder().setText("friend").build();
					response = frontend.ctrl_ping(request);
					System.out.printf("%s%n", response);
				}

				if (go.equals("clear")) {
					ClearResponse response;
					ClearRequest request = ClearRequest.getDefaultInstance();
					response = frontend.ctrl_clear(request);
					System.out.printf("%s%n", response);
				}

			} while (flag != 1);
		}
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

	public static ReportResponse sniffer_report(DgsFrontend frontend, String snifferName, String infection, long id, com.google.protobuf.Timestamp timeIn, com.google.protobuf.Timestamp timeOut) {
		ReportResponse response;
		ReportRequest request = ReportRequest.newBuilder().setName(snifferName).setInfection(infection).setId(id).setTimeIn(timeIn).setTimeOut(timeOut).build();
		response = frontend.report(request);
		return response;
	}

}
