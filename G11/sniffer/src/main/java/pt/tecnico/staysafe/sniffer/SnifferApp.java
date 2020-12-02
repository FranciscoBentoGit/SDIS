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
		
		int i, j, count = 0;

		// receive and print arguments
		System.out.printf("Received %d arguments%n", args.length);
		for (i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}

		// check arguments - 8 e numero minimo supondo q rua tem no min 1 arg e andar tambem
		if (args.length < 8) {
			System.out.println("Argument(s) missing!");
			System.out.printf("Usage: java %s host port%n", SnifferApp.class.getName());
			return;
		}

		if (!(args[0].equals("localhost")) || !(args[1].equals("2181"))) {
			System.out.println("Invalid argument(s)!");
			return;
		}

		String host = args[0];
		String port = args[1];
		String name = args[2], address = "", path = "", instance = "";

		for (j = 3; j < i; j++) {
			count++;
			if ((i-j) == 1) {
				address += args[j];
			} else {
				address += args[j];
				address += " ";
			}
		}
		
		String[] aux = address.split(" ", count);
		
		try {
			int pos = count-1;
			int pathInt = Integer.parseInt(aux[pos]);
			if (pathInt < 0 || pathInt > 2) {
				System.out.println("Invalid argument: invalid replica number.");
				return;
			}
			instance = aux[pos];
			address = "";
			for (int e = 0; e < pos; e++) {
				if ((pos-e) == 1) {
				address += aux[e];
				} else {
					address += aux[e];
					address += " ";
				}
			}
		} catch (NumberFormatException e) {
			System.out.println("Number of replica to contact not given, one will be generated.");
			Random rand = new Random();
			int pathInt = rand.nextInt(2) + 1;
			System.out.printf("%d%n", pathInt);
			instance = String.valueOf(pathInt);
		}

		path = "/grpc/staysafe/dgs/" + instance;
		System.out.printf("%s%n", path);
		System.out.printf("%s%n", address);

		int replicaId = Integer.parseInt(instance);

		execSniffer(host, port, path, name, address, replicaId);
	}

	private static void execSniffer(String host, String port, String path, String snifferName, String address, int replicaId) {
		
		//This addObs list will contain all observations that an user will try to report in one cycle
		ArrayList<AddObs> addObs= new ArrayList<AddObs>();
		
		DgsClientApp client = new DgsClientApp();
		DgsFrontend frontend = new DgsFrontend(host, port, path);
		String go = "";
		
		try {
			SnifferJoinResponse responseJoin;
			responseJoin = client.sniffer_join(frontend, snifferName, address, replicaId);
			String newResponseJoin = responseJoin.getSuccess();
			System.out.printf("%s%n", newResponseJoin);
		} catch (StatusRuntimeException e) {
			System.out.println("Caught exception with description: " + e.getStatus().getDescription());
			return;
		}

		try (Scanner scanner = new Scanner(System.in)) {
			int exit = 0;
			do {
				if (!scanner.hasNextLine()) {
					System.out.println("Done: file already read!");
					return;
				}

				go = scanner.nextLine();
				
				String[] check = go.split(" ", 2);
				String[] goSplited = go.split(",", 4);

				if (go.equals("exitSniffer")) {
					exit = 1;
					continue;
				}

				if (go.equals("")) {
					continue;
				}

				else if (check[0].equals("#")) {
					continue;
				}

				else if ((check.length == 2) && (check[0].equals("init"))) {
					String[] info = check[1].split(" ",3);
					
					//If a sniffer tries to init and the given snifferName isnt the same as his, it should fail
					if (!(info[1].equals(snifferName))) {
						System.out.printf("Invalid input: cannot init another sniffer, only this one - %s.%n", snifferName);
					} else{
						client.aux_ctrl_init(frontend,check[1], replicaId);
					}
				}

				else if ((goSplited.length == 1) && (goSplited[0].equals("getInfo"))) {

					try {
						SnifferInfoResponse responseInfo;
						responseInfo = client.sniffer_info(frontend, snifferName, replicaId);
						String newResponseInfo = responseInfo.getNameAddress();
						System.out.printf("%s%n", newResponseInfo);
					} catch (StatusRuntimeException e) {
						System.out.println("Caught exception with description: " + e.getStatus().getDescription());
					}
				}

				else if ((goSplited.length == 1) && (goSplited[0].equals("help"))) {
					String message = client.helpSniffer();
					System.out.printf("%s%n", message);
				}

				else if ((goSplited.length == 2) && (goSplited[0].equals("sleep"))) {
					int sleepTime = Integer.parseInt(goSplited[1]);
					client.sleep_request(sleepTime);
				}

				//This is called the observation mode, it will keep the code in this loop until the user either presses ENTER or types exitSniffer
				else if (goSplited.length == 4) {
					
					String infection = goSplited[0];
					if (!(infection.equals("infetado")) && !(infection.equals("nao-infetado"))) {
						continue;
					}
					long id = Long.parseLong(goSplited[1]);

					//To able to insert timeIn and timeOut as Timestamp variables
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

					String obsGo = "";
					int aux = 0;
					do {
						System.out.printf("---Do not have more observations to report? Press ENTER.%n");
						if (!scanner.hasNextLine()) {
							obsGo = "exitSniffer";
							flag = 1;
							aux = 1;
							continue;
						}

						if (!obsGo.equals("exitSniffer")) {
							obsGo = scanner.nextLine();
						}
						
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

						String[] auxOutObs = obsSplited[3].split(" ",2);
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

					//When detects flag == 1, the code knows its time to report every single observation within addObs, one by one
					while (iter.hasNext()) {
						AddObs element = iter.next();
						try {
							ReportResponse responseReport;
							responseReport = client.sniffer_report(frontend, snifferName, element.getInfection(), element.getId(), element.getTimeIn(), element.getTimeOut(), replicaId);
							String newResponseReport = responseReport.getSuccess();
							System.out.printf("%s%n", newResponseReport);
						} catch (StatusRuntimeException e) {
							System.out.println("Caught exception with description: " + e.getStatus().getDescription());
						}	
					}

					//You must clear addObs so you can repeat another cicle of observation mode if you wish
					addObs.removeAll(addObs); 

					if (aux == 1) {
						System.out.println("Done: file already read!");
					}

					if (obsGo.equals("exitSniffer")) {
						exit = 1;
					}

				}

				else if ((goSplited.length == 1) && (goSplited[0].equals("ping"))) {
					try {
						PingResponse response;
						response = client.ctrl_ping(frontend, replicaId);
						String newResponsePing = response.getText();
						System.out.printf("%s%n", newResponsePing);
					} catch (StatusRuntimeException e) {
						System.out.println("Caught exception with description: " + e.getStatus().getDescription());
					}	
				}

				else if ((goSplited.length == 1) && (goSplited[0].equals("clear"))) {
					ClearResponse response;
					response = client.ctrl_clear(frontend, replicaId);
					String newResponseClear = response.getSuccess();
					System.out.printf("%s%n", newResponseClear);
				}

				else if ((goSplited.length == 1) && (goSplited[0].equals("mean_dev"))) {
					System.out.printf("Invalid command: do not have permission to execute that command.");
				}

				else if ((goSplited.length == 1) && (goSplited[0].equals("percentiles"))) {
					System.out.printf("Invalid command: do not have permission to execute that command.");
				}

				else if ((goSplited.length > 1) && (goSplited[0].equals("single_prob"))) {
					System.out.printf("Invalid command: do not have permission to execute that command.");
				}

				else {
					System.out.printf("Invalid input!%n");
				}

			} while (exit != 1);
		}
	}

}
