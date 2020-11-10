package pt.tecnico.staysafe.dgs.client;

import java.util.Scanner;
import com.google.protobuf.Timestamp;
import io.grpc.StatusRuntimeException;

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

				String[] goSplited = go.split(" ", 2);  //init snifferName,address,... -- possivel init

				if ((goSplited.length == 1) && (goSplited[0].equals("ping"))) {
					try {
						PingResponse response;
						response = ctrl_ping(frontend);
						System.out.printf("%s%n", response);
					} catch (StatusRuntimeException e) {
						System.out.println("Caught exception with description: " + e.getStatus().getDescription());
					}	
				}

				if ((goSplited.length == 1) && (goSplited[0].equals("clear"))) {
						ClearResponse response;
						response = ctrl_clear(frontend);
						System.out.printf("%s%n", response);
				}

			} while (flag != 1);
		}
	}

	public static PingResponse ctrl_ping(DgsFrontend frontend) {
		PingResponse response;
		PingRequest request = PingRequest.newBuilder().setText("friend").build();
		response = frontend.ctrl_ping(request);
		return response;
	}

	public static ClearResponse ctrl_clear(DgsFrontend frontend) {
		ClearResponse response;
		ClearRequest request = ClearRequest.getDefaultInstance();
		response = frontend.ctrl_clear(request);
		return response;
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
		    Thread.sleep(sleepTime / 1000);
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

	public static IndividualProbResponse individual_infection_probability(DgsFrontend frontend, long id) {
		IndividualProbResponse response;
		IndividualProbRequest request = IndividualProbRequest.newBuilder().setId(id).build();
		response = frontend.individual_infection_probability(request);
		return response;
	}

	public static AggregateProbResponse aggregate_infection_probability(DgsFrontend frontend, String command) {
		AggregateProbResponse response;
		AggregateProbRequest request = AggregateProbRequest.newBuilder().setCommand(command).build();
		response = frontend.aggregate_infection_probability(request);
		return response;
	}

	public static String helpCtrl() {
		String ctrlInit = "** init -- nao sei o q faz ainda\n";
		String ctrlClear = "** clear -- remove all observations and sniffers\n";
		String ctrlPing = "** ping -- returns Hello only if the server is alive\n";

		String message = ctrlInit + "\n" + ctrlClear + "\n" + ctrlPing;

		return message;
	}

	public static String helpSniffer() {
		String sniffer = "You can do the following commands:\n";
		String snifferInfo = "** getInfo -- returns the sniffer's address\n";
		String snifferSleep = "** sleep,[time] --  interrupts the execution for a certain [time]\n";
		String snifferReport = "** [infetado/nao-infetado],[id],[timeIn],[timeOut] -- submits a report with an observation with the parameters [infection_state],[id],[timeIn],[timeOut]\n";

		String ctrl = helpCtrl();

		String message = sniffer + "\n" + snifferInfo + "\n" + snifferSleep + "\n" + snifferReport + "\n" + ctrl;

		return message;
	}

	public static String helpResearcher() {
		String researcher = "You can do the following commands:\n";
		String researcherProb = "** single_prob [id],... -- returns the probability of the person related to the id being infected, at most 3 id's at the same time\n";

		String ctrl = helpCtrl();

		String message = researcher + "\n" + researcherProb + "\n" + ctrl;

		return message;
	}
}
