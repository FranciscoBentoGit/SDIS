package pt.tecnico.staysafe.sniffer;

import pt.tecnico.staysafe.dgs.client.*;
import pt.tecnico.staysafe.dgs.grpc.*;
import com.google.protobuf.Timestamp;
import com.google.protobuf.util.Timestamps;
import pt.tecnico.staysafe.sniffer.AddObs;

import java.util.*;
import io.grpc.StatusRuntimeException;
import java.text.ParseException;
import java.util.concurrent.ConcurrentHashMap;

public class SnifferApp {

	public static void main(String[] args) {
		System.out.println(SnifferApp.class.getSimpleName());
		
		int i, j, count = 0;

		// receive and print arguments
		System.out.printf("Received %d arguments%n", args.length);
		for (i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}

		
		// 8 arguments is the minimum taking in consideration that street and floor take atleast 1 arguments each
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
			if (pathInt < 0 || pathInt > 3) {
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
			int pathInt = rand.nextInt(3) + 1;
			instance = String.valueOf(pathInt);
		}

		path = "/grpc/staysafe/dgs/" + instance;
		
		int replicaId = Integer.parseInt(instance);

		System.out.printf("Contacting replica %d at localhost:808%s...%n", replicaId, instance);

		execSniffer(host, port, path, name, address, replicaId, instance);
	}

	private static void execSniffer(String host, String port, String path, String snifferName, String address, int replicaId, String instance) {
		
		//This addObs list will contain all observations that an user will try to report in one cycle
		ArrayList<AddObs> addObs= new ArrayList<AddObs>();
		
		DgsClientApp client = new DgsClientApp();
		DgsFrontend frontend = new DgsFrontend(host, port, path);
		String go = "";
		
		//Auxiliar function that call rpc SnifferJoin
		frontend = join(host, port, path, snifferName, address, replicaId, instance, client, frontend);
		if (frontend == null) {
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
						System.out.printf("Invalid input: cannot init another sniffer, only this one - %s.%n%n", snifferName);
					} else{
						client.aux_ctrl_init(frontend,check[1], replicaId);
					}
				}

				else if ((goSplited.length == 1) && (goSplited[0].equals("getInfo"))) {
					try {
						SnifferInfoResponse responseInfo;
						responseInfo = client.sniffer_info(frontend, snifferName, replicaId);
						String newResponseInfo = responseInfo.getNameAddress();
						System.out.printf("%s%n%n", newResponseInfo);
					} catch (StatusRuntimeException e) {
						System.out.printf("Caught exception with description: " + e.getStatus().getDescription());
						System.out.printf(" when trying to contact replica %d at localhost:808%s%n",replicaId,instance);
						if (e.getStatus().getDescription().equals("io exception")) {							
							
							//Before changing the frontend variable, make a backup to guarantee consistency
							long[] oldTs = frontend.getOldTs();
							ConcurrentHashMap<Long,IndividualProbResponse> oldSingleProb = frontend.getSingleProb();
							AggregateProbResponse oldMeanDev = frontend.getMeanDev();
							AggregateProbResponse oldPercentiles = frontend.getPercentiles();
							
							frontend = client.changeInfo(host, port, snifferName, address);	
							
							//Update the new frontend variables with the previous backup
							frontend.setTs(oldTs);
							frontend.setSingleProb(oldSingleProb);
							frontend.setMeanDev(oldMeanDev);
							frontend.setPercentiles(oldPercentiles);
							
							//Send information through new channel, so the dead replica gets unbided
							client.server_unbind(frontend, host, port, path);

							//changes replicaId to the new one
							replicaId = frontend.getReplicaId();

							//Auxiliar function that call rpc SnifferJoin
							frontend = join(host, port, path, snifferName, address, replicaId, instance, client, frontend);
							if (frontend == null) {
								return;
							}
						}
					}
				}

				else if ((goSplited.length == 1) && (goSplited[0].equals("help"))) {
					String message = client.helpSniffer();
					System.out.printf("%s%n%n", message);
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
							System.out.printf("%s%n%n", newResponseReport);
						} catch (StatusRuntimeException e) {
							System.out.printf("Caught exception with description: " + e.getStatus().getDescription());
							System.out.printf(" when trying to contact replica %d at localhost:808%s%n",replicaId,instance);
							if (e.getStatus().getDescription().equals("io exception")) {
								
								//Before changing the frontend variable, make a backup to guarantee consistency
								long[] oldTs = frontend.getOldTs();
								ConcurrentHashMap<Long,IndividualProbResponse> oldSingleProb = frontend.getSingleProb();
								AggregateProbResponse oldMeanDev = frontend.getMeanDev();
								AggregateProbResponse oldPercentiles = frontend.getPercentiles();
								
								frontend = client.changeReport(host, port, snifferName, address, element.getInfection(), element.getId(), element.getTimeIn(), element.getTimeOut());

								//Update the new frontend variables with the previous backup
								frontend.setTs(oldTs);
								frontend.setSingleProb(oldSingleProb);
								frontend.setMeanDev(oldMeanDev);
								frontend.setPercentiles(oldPercentiles);
								
								//Send information through new channel, so the dead replica gets unbided
								client.server_unbind(frontend, host, port, path);

								//changes replicaId to the new one
								replicaId = frontend.getReplicaId();

								//Auxiliar function that call rpc SnifferJoin
								frontend = join(host, port, path, snifferName, address, replicaId, instance, client, frontend);
								if (frontend == null) {
									return;
								}
							}
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
						System.out.printf("%s%n%n", newResponsePing);
					} catch (StatusRuntimeException e) {
						System.out.printf("Caught exception with description: " + e.getStatus().getDescription());
						System.out.printf(" when trying to contact replica %d at localhost:808%s%n",replicaId,instance);
						if (e.getStatus().getDescription().equals("io exception")) {
							
							//Before changing the frontend variable, make a backup to guarantee consistency
							long[] oldTs = frontend.getOldTs();
							ConcurrentHashMap<Long,IndividualProbResponse> oldSingleProb = frontend.getSingleProb();
							AggregateProbResponse oldMeanDev = frontend.getMeanDev();
							AggregateProbResponse oldPercentiles = frontend.getPercentiles();
							
							frontend = client.changePing(host, port);

							//Update the new frontend variables with the previous backup
							frontend.setTs(oldTs);
							frontend.setSingleProb(oldSingleProb);
							frontend.setMeanDev(oldMeanDev);
							frontend.setPercentiles(oldPercentiles);
							
							//Send information through new channel, so the dead replica gets unbided
							client.server_unbind(frontend, host, port, path);
							
							//changes replicaId to the new one
							replicaId = frontend.getReplicaId();

							//Auxiliar function that call rpc SnifferJoin
							frontend = join(host, port, path, snifferName, address, replicaId, instance, client, frontend);
							if (frontend == null) {
								return;
							}
						}
					}	
				}

				else if ((goSplited.length == 1) && (goSplited[0].equals("clear"))) {
					try {
						ClearResponse response;
						response = client.ctrl_clear(frontend, replicaId);
						String newResponseClear = response.getSuccess();
						System.out.printf("%s%n%n", newResponseClear);
					} catch (StatusRuntimeException e) {
						System.out.printf("Caught exception with description: " + e.getStatus().getDescription());
						System.out.printf(" when trying to contact replica %d at localhost:808%s%n",replicaId,instance);
						if (e.getStatus().getDescription().equals("io exception")) {
							
							//Before changing the frontend variable, make a backup to guarantee consistency
							long[] oldTs = frontend.getOldTs();
							ConcurrentHashMap<Long,IndividualProbResponse> oldSingleProb = frontend.getSingleProb();
							AggregateProbResponse oldMeanDev = frontend.getMeanDev();
							AggregateProbResponse oldPercentiles = frontend.getPercentiles();
							
							frontend = client.changeClear(host, port);

							//Update the new frontend variables with the previous backup
							frontend.setTs(oldTs);
							frontend.setSingleProb(oldSingleProb);
							frontend.setMeanDev(oldMeanDev);
							frontend.setPercentiles(oldPercentiles);
							
							//Send information through new channel, so the dead replica gets unbided
							client.server_unbind(frontend, host, port, path);
							
							//changes replicaId to the new one
							replicaId = frontend.getReplicaId();
							
							//Auxiliar function that call rpc SnifferJoin
							frontend = join(host, port, path, snifferName, address, replicaId, instance, client, frontend);
							if (frontend == null) {
								return;
							}
						}
					}
				}

				else if ((goSplited.length == 1) && (goSplited[0].equals("mean_dev"))) {
					System.out.printf("Invalid command: do not have permission to execute that command.%n%n");
				}

				else if ((goSplited.length == 1) && (goSplited[0].equals("percentiles"))) {
					System.out.printf("Invalid command: do not have permission to execute that command.%n%n");
				}

				else if ((goSplited.length > 1) && (goSplited[0].equals("single_prob"))) {
					System.out.printf("Invalid command: do not have permission to execute that command.%n%n");
				}

				else {
					System.out.printf("Invalid input!%n%n");
				}

			} while (exit != 1);
		}
	}

	public static DgsFrontend join(String host, String port, String path, String snifferName, String address, int replicaId, String instance, DgsClientApp client, DgsFrontend frontend) {
		try {
			SnifferJoinResponse responseJoin;
			responseJoin = client.sniffer_join(frontend, snifferName, address, replicaId);
			String newResponseJoin = responseJoin.getSuccess();
			System.out.printf("%s%n%n", newResponseJoin);
			return frontend;
		} catch (StatusRuntimeException e) {
			System.out.printf("Caught exception with description: " + e.getStatus().getDescription());
			System.out.printf(" when trying to contact replica %d at localhost:808%s%n", replicaId, instance);
			if (e.getStatus().getDescription().equals("io exception")) {
				frontend = client.changeJoin(host, port, snifferName, address);
				//Send information through new channel, so the dead replica gets unbided
				client.server_unbind(frontend, host, port, path);
            } else {
				return null;
			}
		}
		return frontend;
	}

}
