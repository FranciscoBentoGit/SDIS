package pt.tecnico.staysafe.journalist;


import pt.tecnico.staysafe.dgs.client.*;
import pt.tecnico.staysafe.dgs.grpc.*;

import java.util.*;
import io.grpc.StatusRuntimeException;

public class JournalistApp {
	private static long[] _prevTs = {0,0};
	
	public static void main(String[] args) {
		System.out.println(JournalistApp.class.getSimpleName());
		
		// receive and print arguments
		System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}
		if (args.length < 2) {
			System.out.println("Argument(s) missing!");
			System.out.printf("Usage: java %s host port%n", JournalistApp.class.getName());
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

		int replicaId = Integer.parseInt(instance);
		
		execJournalist(host, port, path, replicaId);
	}
	private static void execJournalist(String host, String port, String path, int replicaId){
		DgsClientApp client = new DgsClientApp();
		DgsFrontend frontend = new DgsFrontend(host, port, path);
		String go;

		try (Scanner scanner = new Scanner(System.in)) {
			int exit = 0;
			do {
				go = scanner.nextLine();

				String[] goSplited = go.split(" ", 2);

				if (go.equals("exitJournalist")) {
					exit = 1;
				}

				else if (go.equals("")){
					continue;
				}

				else if ((goSplited.length == 1) && (goSplited[0].equals("help"))) {
					String message = client.helpJournalist();
					System.out.printf("%s%n", message);
				}
				
				else if ((goSplited.length == 2) &&( goSplited[0].equals("single_prob"))) {
					System.out.println("Invalid command: do not have permission to execute that command.");
				}

				else if ((goSplited.length == 1) && (goSplited[0].equals("ping"))) {
					try {
						PingResponse response;
						response = client.ctrl_ping(frontend, replicaId, _prevTs[0], _prevTs[1]);
						String[] splitPing= response.toString().split(" - ", 4);
						_prevTs[0] = Long.parseLong(splitPing[1]);
						_prevTs[1] = Long.parseLong(splitPing[2]);
						System.out.printf("%s%n", response);
					} catch (StatusRuntimeException e) {
						System.out.println("Caught exception with description: " + e.getStatus().getDescription());
					}	
				}

				else if ((goSplited.length == 2) && (goSplited[0].equals("init"))) {
					client.aux_ctrl_init(frontend,goSplited[1], replicaId, _prevTs[0], _prevTs[1]);
				}

				else if ((goSplited.length == 1) && (goSplited[0].equals("clear"))) {
					ClearResponse response;
					response = client.ctrl_clear(frontend, replicaId, _prevTs[0], _prevTs[1]);
					System.out.printf("%s%n", response);
				}

				else if ((goSplited.length == 1) && (goSplited[0].equals("mean_dev"))) {
					try {
						AggregateProbResponse response;
						String command = goSplited[0];
						response = client.aggregate_infection_probability(frontend,command,replicaId, _prevTs[0], _prevTs[1]);

						////This convergence is needed as response will return stat : value 1
						//												              value 2
						//We must filter in order to get only the respective values
						String convResponse = response.toString();
						String[] splited1 = convResponse.split(" ", 2);
						String[] splited2 = splited1[1].split("\n", 2);
						String prob1 = splited2[0].toString();

						String[] splited4 =	splited2[1].split(" ", 2);	
						String[] splited5 =	splited4[1].split("\n", 2);	
						String prob2 = splited5[0].toString();

						String[] splited7 =	splited5[1].split(" ", 2);
						String[] splited8 =	splited7[1].split("\n", 2);
						String ts1 = splited8[0].toString();

						String[] splited9 =	splited8[1].split(" ", 2);
						String ts2 = splited9[1].toString();
						
						float f1 = Float.parseFloat(prob1);
						float f2 = Float.parseFloat(prob2);

						System.out.printf("%s%n%s%n",ts1,ts2);

						_prevTs[0] = (long) Float.parseFloat(ts1);
						_prevTs[1] = (long) Float.parseFloat(ts2);
						System.out.printf("1 - %d%n2 - %d%n",_prevTs[0],_prevTs[1]);

						System.out.printf("%.3f%n%.3f%n",f1,f2);
					} catch (StatusRuntimeException e) {
						System.out.println("Caught exception with description: " + e.getStatus().getDescription());
					}	
				}

				else if ((goSplited.length == 1) && (goSplited[0].equals("percentiles"))) {
					try {
						AggregateProbResponse response;
						String command = goSplited[0];
						response = client.aggregate_infection_probability(frontend,command,replicaId,_prevTs[0], _prevTs[1]);

						//Same logic as line 90, mean_dev return stat : value 1
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
						String[] splited8 =	splited7[1].split("\n", 2);
						String prob3 =	splited8[0].toString();

						String[] splited9 =	splited8[1].split(" ", 2);
						String[] splited10 = splited9[1].split("\n", 2);
						String ts1 = splited10[0].toString();

						String[] splited11 = splited10[1].split(" ", 2);
						String ts2 = splited11[1].toString();
						
						float f1 = Float.parseFloat(prob1);
						float f2 = Float.parseFloat(prob2);
						float f3 = Float.parseFloat(prob3);

						System.out.printf("%s%n%s%n",ts1,ts2);

						_prevTs[0] = (long) Float.parseFloat(ts1);
						_prevTs[1] = (long) Float.parseFloat(ts2);
						System.out.printf("1 - %d%n2 - %d%n",_prevTs[0],_prevTs[1]);
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
