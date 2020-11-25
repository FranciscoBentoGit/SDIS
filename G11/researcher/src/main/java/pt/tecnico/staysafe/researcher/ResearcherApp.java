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

		if (args.length == 3) {
			int pathInt = Integer.parseInt(args[2]);
			if (pathInt < 0 || pathInt > 2) {
				System.out.println("Invalid argument: invalid replica number.");
				return;
			}
			instance = args[2];
		}

		if (args.length == 2) {
			Random rand = new Random();
			int pathInt = rand.nextInt(2) + 1;
			instance = String.valueOf(pathInt);
		}

		path = "/grpc/staysafe/dgs/" + instance;
		System.out.printf("%s%n", path);

		execResearcher(host, port, path);
	}

	private static void execResearcher(String host, String port, String path) {
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
						System.out.printf("single_prob: too much arguments, at most 3 id's!");
						exit = 1;
						continue;
					} 

					else {
						for(int i = 0; i < ids.length; i++) {
							try {
								IndividualProbResponse response;
								response = client.individual_infection_probability(frontend, Long.parseLong(ids[i]));
								
								//This convergence is needed as response will return prob : value
								String convResponse = response.toString();
								String[] splited = convResponse.split(" ", 2);
								
								String prob = splited[1].toString();
								
								//If the function individual_infection_probability returns 2.0, because it must return a float,it detects it's irrealistic and means id not found
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

				else if ((goSplited.length == 2) && (goSplited[0].equals("init"))) {
					client.aux_ctrl_init(frontend,goSplited[1]);
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
						response = client.aggregate_infection_probability(frontend,command);

						//Same logic as line 77, mean_dev return stat : value 1
						//												value 2
						//We must filter in order to get only the respective values
						String convResponse = response.toString();
						String[] splited = convResponse.split(" ", 2);
						String[] splited2 = splited[1].split("\n", 2);
						String[] splited3 = splited2[1].split(" ", 2);
						
						String prob1 = splited2[0].toString();
						String prob2 = splited3[1].toString();
						
						float f1 = Float.parseFloat(prob1);
						float f2 = Float.parseFloat(prob2);
						System.out.printf("%.3f%n%.3f%n",f1,f2);
					} catch (StatusRuntimeException e) {
						System.out.println("Caught exception with description: " + e.getStatus().getDescription());
					}	
				}

				else if ((goSplited.length == 1) && (goSplited[0].equals("percentiles"))) {
					try {
						AggregateProbResponse response;
						String command = goSplited[0];
						response = client.aggregate_infection_probability(frontend,command);

						
						//Same logic as line 124, mean_dev return stat : value 1
						//												 value 2
						//                                               value 3
						//We must filter in order to get only the respective values
						String convResponse = response.toString();
						String[] splited1 = convResponse.split(" ", 2);
						String[] splited2 = splited1[1].split("\n", 2);
						String prob1 = splited2[0].toString();

						String[] splited4 =	splited2[1].split(" ", 2);	
						String[] splited5 =	splited4[1].split("\n", 2);	
						String prob2 = splited5[0].toString();

						String[] splited7 =	splited5[1].split(" ", 2);
						String prob3 =	splited7[1].toString();
						
						float f1 = Float.parseFloat(prob1);
						float f2 = Float.parseFloat(prob2);
						float f3 = Float.parseFloat(prob3);

						System.out.printf("%.3f%n%.3f%n%.3f%n",f1,f2,f3);
					} catch (StatusRuntimeException e) {
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
