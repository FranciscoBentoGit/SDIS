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

		if (!(args[0].equals("localhost")) || !(args[1].equals("8080"))) {
			System.out.println("Invalid argument(s)!");
			return;
		}

		String host = args[0];
		int port = Integer.parseInt(args[1]);

		execResearcher(host, port);
	}

	private static void execResearcher(String host, int port) {
		DgsClientApp client = new DgsClientApp();
		DgsFrontend frontend = new DgsFrontend(host,port);
		String go;

		try (Scanner scanner = new Scanner(System.in)) {
			int exit = 0;
			do {
				go = scanner.nextLine();

				String[] goSplited = go.split(" ", 2);

				if (go.equals("") || go.equals("exitResearcher")) {
					exit = 1;
				}

				else if ((goSplited.length == 1) && (goSplited[0].equals("help"))) {
					String message = client.helpResearcher();
					System.out.printf("%s%n", message);
				}
				
				else if ((goSplited.length == 2) &&( goSplited[0].equals("single_prob"))) {
					String[] ids = goSplited[1].split(",",4);

					if (ids.length == 4){
						System.out.printf("single_prob: too much arguments, at most 3 id's!");
						exit = 1;
						continue;
					} 

					else {
						for(int i = 0; i < ids.length; i++) {
							try {
								IndividualProbResponse response;
								response = client.individual_infection_probability(frontend, Long.parseLong(ids[i]));
								String convResponse = response.toString();
								String[] splited = convResponse.split(" ", 2);
								
								String prob = splited[1].toString();
								if (prob.equals("2.0\n")) {
									prob = "Id: not found!"; 
									System.out.printf("%s%n",prob);
								} else {
									float f = Float.parseFloat(prob);
									System.out.printf("%.3f%n",f);
								}
							} catch (StatusRuntimeException e) {
								System.out.println("Caught exception with description: " + e.getStatus().getDescription());
							}
						}
					}
				}

				else if ((goSplited.length == 1) && (goSplited[0].equals("ping"))) {
					try {
						PingResponse response;
						response = client.ctrl_ping(frontend);
						System.out.printf("%s%n", response);
					} catch (StatusRuntimeException e) {
						System.out.println("Caught exception with description: " + e.getStatus().getDescription());
					}	
				}

				else if ((goSplited.length == 1) && (goSplited[0].equals("clear"))) {
					ClearResponse response;
					response = client.ctrl_clear(frontend);
					System.out.printf("%s%n", response);
				}

				else if ((goSplited.length == 1) && (goSplited[0].equals("mean_dev"))) {
					try {
						AggregateProbResponse response;
						String command = goSplited[0];
						System.out.printf("%s",command);
						response = client.aggregate_infection_probability(frontend,command);
						System.out.printf("%s%n", response);
					} catch (StatusRuntimeException e) {
						System.out.println("entrei");
						System.out.println("Caught exception with description: " + e.getStatus().getDescription());
					}	
				}

				else if ((goSplited.length == 1) && (goSplited[0].equals("percentiles"))) {
					try {
						AggregateProbResponse response;
						String command = goSplited[0];
						response = client.aggregate_infection_probability(frontend,command);
						System.out.printf("%s%n", response);
					} catch (StatusRuntimeException e) {
						System.out.println("entreiPercentiles");
						System.out.println("Caught exception with description: " + e.getStatus().getDescription());
					}
				}

				else {
					System.out.printf("Invalid input!%n");
				}

			} while (exit != 1);
		}

		
	}
}
