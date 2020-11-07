package pt.tecnico.staysafe.sniffer;

import pt.tecnico.staysafe.dgs.client.*;
import pt.tecnico.staysafe.dgs.grpc.*;
import com.google.protobuf.Timestamp;
import com.google.protobuf.util.Timestamps;
import pt.tecnico.staysafe.sniffer.AddObs;

import java.util.*;
import io.grpc.StatusRuntimeException;
import java.text.ParseException;

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
			address += " ";
		}
		//tirar print
		System.out.printf("Address: %s%n", address);

		execSniffer(host, port, name, address);
	}

	private static void execSniffer(String host, int port, String snifferName, String address) {
		ArrayList<AddObs> addObs= new ArrayList<AddObs>();
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

				String[] goSplited = go.split(",", 4);

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

				
				if (goSplited.length == 4) {
					String infection = goSplited[0];
					if (!(infection.equals("infetado")) && !(infection.equals("nao-infetado"))) {
						continue;
					}
					long id = Long.parseLong(goSplited[1]);

					String[] auxIn = goSplited[2].split(" ",2);
					String timestampIn = auxIn[0] + "T" + auxIn[1] + "Z";

					String[] auxOut = goSplited[3].split(" ",2);
					String timestampOut = auxOut[0] + "T" + auxOut[1] + "Z";

					com.google.protobuf.Timestamp timeIn = null;
					com.google.protobuf.Timestamp timeOut = null;
					try {
						timeIn = Timestamps.parse(timestampIn);
						timeOut = Timestamps.parse(timestampOut);
					} catch (ParseException e) {
		                e.printStackTrace();
		            }
					
					int flag = 0;

					AddObs data = new AddObs(infection,id,timeIn,timeOut);
				    addObs.add(data);

				    String obsGo;
					do {
						System.out.printf("---Do not have more observations to report? Press ENTER.%n");
						obsGo = scanner.nextLine();
						if (obsGo.equals("") || obsGo.equals("exitSniffer")) {
							flag = 1;
							continue;
						}

						String[] obsSplited = obsGo.split(",", 4);

						String infectionObs = obsSplited[0];
						if (!(infectionObs.equals("infetado")) && !(infectionObs.equals("nao-infetado"))) {
							continue;
						}

						long idObs = Long.parseLong(obsSplited[1]);
			
						String[] auxInObs = obsSplited[2].split(" ",2);
						String timestampInObs = auxInObs[0] + "T" + auxInObs[1] + "Z";

						String[] auxOutObs = goSplited[3].split(" ",2);
						String timestampOutObs = auxOutObs[0] + "T" + auxOutObs[1] + "Z";

						com.google.protobuf.Timestamp timeInObs = null;
						com.google.protobuf.Timestamp timeOutObs = null;
						try {
							timeInObs = Timestamps.parse(timestampInObs);
							timeOutObs = Timestamps.parse(timestampOutObs);
						} catch (ParseException e) {
			                e.printStackTrace();
			            }

						if ((obsSplited.length == 4) && (infectionObs.equals("infetado") || infectionObs.equals("nao-infetado"))) {
							AddObs dataObs = new AddObs(infectionObs,idObs,timeInObs,timeOutObs);
				    		addObs.add(dataObs);
						}
					} while (flag != 1);

					Iterator<AddObs> iter = addObs.iterator();

					while (iter.hasNext()) {
						AddObs element = iter.next();
						try {
							ReportResponse responseReport;
							responseReport = client.sniffer_report(frontend, snifferName, element.getInfection(), element.getId(), element.getTimeIn(), element.getTimeOut());
							System.out.printf("%s%n", responseReport);
						} catch (StatusRuntimeException e) {
							System.out.println("Caught exception with description: " + e.getStatus().getDescription());
						}	
					}

					addObs.removeAll(addObs); 

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
