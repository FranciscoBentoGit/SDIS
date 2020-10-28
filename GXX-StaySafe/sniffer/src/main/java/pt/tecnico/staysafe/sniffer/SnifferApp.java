package pt.tecnico.staysafe.sniffer;

import pt.tecnico.staysafe.dgs.client.*;
import pt.tecnico.staysafe.dgs.grpc.*;
import com.google.protobuf.Timestamp;

import java.util.*;
import java.text.SimpleDateFormat;  
import java.lang.*;

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
		
		System.out.printf("Address: %s", address);

		execSniffer(host,port,name,address);
	}

	private static void execSniffer(String host, int port, String snifferName, String address) {
		DgsFrontend frontend = new DgsFrontend(host,port);
		String go;

		SnifferJoinRequest request = SnifferJoinRequest.newBuilder().setName(snifferName).setAddress(address).build();
		frontend.sniffer_join(request);

		try (Scanner scanner = new Scanner(System.in)) {
			do {
				go = scanner.nextLine();

				String goSplited[] = go.split(",", 4);

				if ((goSplited.length == 1) && (goSplited.equals("getInfo"))) {
					SnifferInfoRequest request = SnifferInfoRequest.newBuilder().setName(snifferName).build();
					frontend.sniffer_info(request);
				}

				if ((goSplited.length == 2) && (goSplited.equals("sleep"))) {
					int sleepTime = Integer.parseInt(goSplited[1]);
					try {
					    Thread.sleep(sleepTime * 1000);
					} catch (InterruptedException ie) {
					    Thread.currentThread().interrupt();
					}
				}

				if ((goSplited.length == 4) && (goSplited.equals("infetado") || goSplited.equals("nao-infetado"))) {
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
					Date date = new Date(System.currentTimeMillis());
					ReportRequest request = ReportRequest.newBuilder().setName(snifferName).setObservations(go).setTime(date).build();
					frontend.sniffer_info(request);
				}

			} while (!scanner.equals("exitSniffer"));
		}
	}

}
