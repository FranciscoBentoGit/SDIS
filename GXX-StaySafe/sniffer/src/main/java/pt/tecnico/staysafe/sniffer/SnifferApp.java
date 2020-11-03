package pt.tecnico.staysafe.sniffer;

import pt.tecnico.staysafe.dgs.client.*;
import pt.tecnico.staysafe.dgs.grpc.*;
import com.google.protobuf.Timestamp;

import java.util.*;
import io.grpc.StatusRuntimeException;

public class SnifferApp {

	public static void main(String[] args) {
		System.out.println(SnifferApp.class.getSimpleName());
		
		int i, j;

		// receive and print arguments
		System.out.printf("Received %d arguments%n", args.length);
		for (i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}

		// check arguments  - 8 e numero minimo supondo q rua tem no min 1 arg e andar tambem
		if (args.length < 8) {
			System.out.println("Argument(s) missing!");
			System.out.printf("Usage: java %s host port%n", SnifferApp.class.getName());
			return;
		}

		if (!(args[0].equals("localhost")) || !(args[1].equals("8080"))) {
			System.out.println("Invalid argument(s)!");
			return;
		}

		String host = args[0];
		int port = Integer.parseInt(args[1]);
		String name = args[2], address = "";

		for (j = 3; j < i; j++) {
			address += args[j];
		}
		
		System.out.printf("Address: %s%n", address);

		execSniffer(host, port, name, address);
	}

	private static void execSniffer(String host, int port, String snifferName, String address) {
		DgsClientApp client = new DgsClientApp();
		DgsFrontend frontend = new DgsFrontend(host,port);
		String go;
		
		try {
			SnifferJoinResponse responseJoin;
			responseJoin = client.sniffer_join(frontend, snifferName, address);
			System.out.printf("%s%n", responseJoin);
		} catch (StatusRuntimeException e) {
			System.out.println("Caught exception with description: " + e.getStatus().getDescription());
		}
		

		try (Scanner scanner = new Scanner(System.in)) {
			int exit = 0;
			do {
				go = scanner.nextLine();

				if (go.equals("") || go.equals("exitSniffer")) {
					exit = 1;
				}

				String goSplited[] = go.split(",", 4);

				if ((goSplited.length == 1) && (goSplited[0].equals("getInfo"))) {
					try {
						SnifferInfoResponse responseInfo;
						responseInfo = client.sniffer_info(frontend, snifferName);
						System.out.printf("%s%n", responseInfo);
					} catch (StatusRuntimeException e) {
						System.out.println("Caught exception with description: " + e.getStatus().getDescription());
					}
				}

				if ((goSplited.length == 2) && (goSplited[0].equals("sleep"))) {
					int sleepTime = Integer.parseInt(goSplited[1]);
					client.sleep_request(sleepTime);
				}

				if ((goSplited.length == 4) && (goSplited[0].equals("infetado") || goSplited[0].equals("nao-infetado"))) {
					int flag = 0;
					String obsGo;
					ArrayList<String> addObs = new ArrayList<String>();
					addObs.add(go);
					do {
						System.out.printf("---Do not have more observations to report? Press ENTER.%n");
						obsGo = scanner.nextLine();
						String obsSplited[] = obsGo.split(",", 4);
						if ((obsSplited.length == 4) && (obsSplited[0].equals("infetado") || obsSplited[0].equals("nao-infetado"))) {
							addObs.add(obsGo);
						}
						if (obsGo.equals("") || obsGo.equals("exitSniffer")) {
							flag = 1;
						}
					} while (flag != 1);

					for (int i = 0; i < addObs.size(); i++) {
						long millis = System.currentTimeMillis();
						Timestamp timestamp = Timestamp.newBuilder().setSeconds(millis/1000).build();
						try {
							ReportResponse responseReport;
							responseReport = client.sniffer_report(frontend, snifferName, addObs.get(i), timestamp);
							System.out.printf("%s%n", responseReport);
						} catch (StatusRuntimeException e) {
							System.out.println("Caught exception with description: " + e.getStatus().getDescription());
						}						
					}//eficiencia???

					if (obsGo.equals("exitSniffer")) {
						exit = 1;
					}
				}

				if ((goSplited.length == 1) && (goSplited[0].equals("mean_dev"))) {
					System.out.printf("Invalid command: do not have permission to execute that command.");
				}

				if ((goSplited.length == 1) && (goSplited[0].equals("percentiles"))) {
					System.out.printf("Invalid command: do not have permission to execute that command.");
				}

				if ((goSplited.length > 1) && (goSplited[0].equals("single_prob"))) {
					System.out.printf("Invalid command: do not have permission to execute that command.");
				}

			} while (exit != 1);
		}
	}

}
