package pt.tecnico.staysafe.researcher;

import pt.tecnico.staysafe.dgs.client.*;
import pt.tecnico.staysafe.dgs.grpc.*;

import java.util.*;
import io.grpc.StatusRuntimeException;

public class ResearcherApp {
	
	public static void main(String[] args) {
		System.out.println(ResearcherApp.class.getSimpleName());
		
		// receive and print arguments
		System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}
		if (args.length < 2) {
			System.out.println("Argument(s) missing!");
			System.out.printf("Usage: java %s host port%n", ResearcherApp.class.getName());
			return;
		}

		if (!(args[0].equals("localhost")) || !(args[1].equals("2181"))) {
			System.out.println("Invalid argument(s)!");
			return;
		}

		String host = args[0];
		String port = args[1];
		String path = "", instance = "";

		//If receives a third argument, it connects to the specific replica manager
		if (args.length == 3) {
			int pathInt = Integer.parseInt(args[2]);
			if (pathInt < 0 || pathInt > 3) {
				System.out.println("Invalid argument: invalid replica number.");
				return;
			}
			instance = args[2];
		}

		//If no third argument is given, it connects to a random replica manager
		if (args.length == 2) {
			Random rand = new Random();
			int pathInt = rand.nextInt(3) + 1;
			instance = String.valueOf(pathInt);
		}

		path = "/grpc/staysafe/dgs/" + instance;
		
		int replicaId = Integer.parseInt(instance);
		System.out.printf("Contacting replica %d at localhost:808%s...%n",replicaId,instance);

		execResearcher(host, port, path, replicaId, instance);
	}

	private static void execResearcher(String host, String port, String path, int replicaId, String instance) {
		DgsClientApp client = new DgsClientApp();
		DgsFrontend frontend = new DgsFrontend(host, port, path);
		String go;

		try (Scanner scanner = new Scanner(System.in)) {
			int exit = 0;
			do {
				go = scanner.nextLine();

				String[] goSplited = go.split(" ", 2);

				if (go.equals("exitResearcher")) {
					exit = 1;
				}

				else if (go.equals("")){
					continue;
				}

				else if ((goSplited.length == 1) && (goSplited[0].equals("help"))) {
					String message = client.helpResearcher();
					System.out.printf("%s%n", message);
				}
				
				//ids will try to split by ",", if it reaches length 4 it means you wrote more ids that you should have
				else if ((goSplited.length == 2) &&( goSplited[0].equals("single_prob"))) {
					String[] ids = goSplited[1].split(",",4);

					if (ids.length == 4){
						System.out.printf("single_prob: too much arguments, at most 3 id's!%n");
						exit = 1;
						continue;
					} 

					else {
						for(int i = 0; i < ids.length; i++) {
							try {
								IndividualProbResponse response;
								response = client.individual_infection_probability(frontend, Long.parseLong(ids[i]),replicaId);

								float prob = response.getProb();

								//If the function individual_infection_probability returns 2.0, because it must return a float,it detects it's irrealistic and means id not found
								if (Float.compare(prob,(float)2.0) == 0) {
									String error = "Id: not found!"; 
									System.out.printf("%s%n%n",error);
								} else {
									System.out.printf("%.3f%n%n",prob);
								}
							} catch (StatusRuntimeException e) {
								System.out.printf("Caught exception with description: " + e.getStatus().getDescription());
								System.out.printf(" when trying to contact replica %d at localhost:808%s%n",replicaId,instance);
								if (e.getStatus().getDescription().equals("io exception")) {
									frontend = client.changeSingle(host, port, ids);
									//Send information through new channel, so the dead replica gets unbided
									client.server_unbind(frontend, host, port, path);
									
									//changes replicaId to the new one
									replicaId = frontend.getReplicaId();
								}
							}
						}
					}
				}

				else if ((goSplited.length == 2) && (goSplited[0].equals("init"))) {
					client.aux_ctrl_init(frontend,goSplited[1],replicaId);
				}

				else if ((goSplited.length == 1) && (goSplited[0].equals("ping"))) {
					try {
						PingResponse response;
						response = client.ctrl_ping(frontend,replicaId);
						String newResponsePing = response.getText();
						System.out.printf("%s%n%n", newResponsePing);
					} catch (StatusRuntimeException e) {
						System.out.printf("Caught exception with description: " + e.getStatus().getDescription());
						System.out.printf(" when trying to contact replica %d at localhost:808%s%n",replicaId,instance);
						if (e.getStatus().getDescription().equals("io exception")) {
							frontend = client.changePing(host, port);
							//Send information through new channel, so the dead replica gets unbided
							client.server_unbind(frontend, host, port, path);
							
							//changes replicaId to the new one
							replicaId = frontend.getReplicaId();
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
							frontend = client.changeClear(host, port);
							//Send information through new channel, so the dead replica gets unbided
							client.server_unbind(frontend, host, port, path);
							
							//changes replicaId to the new one
							replicaId = frontend.getReplicaId();
						}
					}
				}

				else if ((goSplited.length == 1) && (goSplited[0].equals("mean_dev"))) {
					String command = null;
					try {
						AggregateProbResponse response;
						command = goSplited[0];
						response = client.aggregate_infection_probability(frontend,command,replicaId);
						
						float f1 = response.getStat(0);
						float f2 = response.getStat(1);

						if ((Float.compare(f1,(float)3.0) == 0) && (Float.compare(f2,(float)3.0) == 0)) {
							String error = "Empty: no observations found"; 
							System.out.printf("%s%n%n",error);
						} else {
							System.out.printf("%.3f%n%.3f%n%.3f%n%n",f1,f2);
						}
					} catch (StatusRuntimeException e) {
						System.out.printf("Caught exception with description: " + e.getStatus().getDescription());
						System.out.printf(" when trying to contact replica %d at localhost:808%s%n", replicaId, instance);
						if (e.getStatus().getDescription().equals("io exception")) {
							frontend = client.changeMean(host, port, command);
							//Send information through new channel, so the dead replica gets unbided
							client.server_unbind(frontend, host, port, path);
							
							//changes replicaId to the new one
							replicaId = frontend.getReplicaId();
						}
					}	
				}

				else if ((goSplited.length == 1) && (goSplited[0].equals("percentiles"))) {
					String command = null;
					try {
						AggregateProbResponse response;
						command = goSplited[0];
						response = client.aggregate_infection_probability(frontend,command,replicaId);
						
						float f1 = response.getStat(0);
						float f2 = response.getStat(1);
						float f3 = response.getStat(2);

						if ((Float.compare(f1,(float)3.0) == 0) && (Float.compare(f2,(float)3.0) == 0) && (Float.compare(f3,(float)3.0) == 0)) {
							String error = "Empty: no observations found"; 
							System.out.printf("%s%n%n",error);
						} else {
							System.out.printf("%.3f%n%.3f%n%.3f%n%n",f1,f2,f3);
						}
					} catch (StatusRuntimeException e) {
						System.out.printf("Caught exception with description: " + e.getStatus().getDescription());
						System.out.printf(" when trying to contact replica %d at localhost:808%s%n", replicaId, instance);
						if (e.getStatus().getDescription().equals("io exception")) {
							frontend = client.changePercentiles(host, port, command);
							//Send information through new channel, so the dead replica gets unbided
							client.server_unbind(frontend, host, port, path);
							
							//changes replicaId to the new one
							replicaId = frontend.getReplicaId();
						}
					}
				}

				else {
					System.out.printf("Invalid input!%n%n");
				}

			} while (exit != 1);
		}

		
	}
}
