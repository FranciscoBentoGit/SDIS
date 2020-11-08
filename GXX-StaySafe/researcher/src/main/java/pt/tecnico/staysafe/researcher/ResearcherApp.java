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

	private static void execResearcher(String host, int port){
		DgsClientApp client = new DgsClientApp();
		DgsFrontend frontend = new DgsFrontend(host,port);
		String go;

		try (Scanner scanner = new Scanner(System.in)){
			int exit = 0;
			do {
				go = scanner.nextLine();

				if (go.equals("") || go.equals("exitResearcher")){
					exit = 1;
				}
				String[] goSplited = go.split(" ", 2);
				if ((goSplited.length == 2) &&( goSplited[0].equals("single_prob"))){
					String[] ids = goSplited[1].split(",",4);
					if ( ids.length == 4){
						continue;
					} 
				}
			}
		}

		
	}
}
