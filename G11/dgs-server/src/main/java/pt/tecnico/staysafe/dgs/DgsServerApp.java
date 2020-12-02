package pt.tecnico.staysafe.dgs;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import pt.ulisboa.tecnico.sdis.zk.*;

import java.io.IOException;
import io.grpc.StatusRuntimeException;
import java.util.*;


public class DgsServerApp {
	
	public static void main(String[] args) throws IOException, InterruptedException {
		System.out.println("StaySafe dgs server");

		final String zooHost = args[0];
		final String zooPort = args[1];
		String host = "localhost";
		String port = null;
		String path = null;
		int id = 0;

		System.out.println("Insert the replica number that you want to initialize!");

		try (Scanner scanner = new Scanner(System.in)) {
			String go;
			int exit = 0;
			do {
				if (!scanner.hasNextLine()) {
					exit = 1;
					continue;
				}
				
				go = scanner.nextLine();

				if (go.equals("1")) {
					id = Integer.parseInt(go);
					port = "808" + go;
					path = "/grpc/staysafe/dgs/" + go;
					exit = 1;
				}

				else if (go.equals("2")) {
					id = Integer.parseInt(go);
					port = "808" + go;
					path = "/grpc/staysafe/dgs/" + go;
					exit = 1;
				}

				else if (go.equals("3")) {
					id = Integer.parseInt(go);
					port = "808" + go;
					path = "/grpc/staysafe/dgs/" + go;
					exit = 1;
				}
				
				else {
					System.out.println("Replica number must be 1, 2 or 3!");
				}

			} while (exit != 1);
		} catch (StatusRuntimeException e) {
			System.out.println("Caught exception with description: " + e.getStatus().getDescription());
			return;
		}
		
		// receive and print arguments
		System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}

		System.out.printf("host = %s%n", host);
		System.out.printf("port = %s%n", port);
		System.out.printf("path = %s%n", path);

		final BindableService impl = new DgsServiceImpl();

		ZKNaming zkNaming = null;
		
		try {
			zkNaming = new ZKNaming(zooHost, zooPort);
			// publish
			zkNaming.rebind(path, host, port);

			int portStr = Integer.parseInt(port);

			// Create a new server to listen on port
			Server server = ServerBuilder.forPort(portStr).addService(impl).build();

			// Start the replica 1
			server.start();

			// Server threads are running in the background.
			System.out.printf("Replica %d starting...%n", id);

			// Creates a frontend for each replica
			//ServersFrontend frontend = new ServersFrontend(zkNaming,id);

			// Do not exit the main thread. Wait until server is terminated.
			server.awaitTermination();

		} catch (ZKNamingException e) {
			System.out.println("Caught exception with description: " + e.getMessage());
		} finally {
			try {
				if (zkNaming != null) {
				// remove
				zkNaming.unbind(path,host,port);
				//zkNaming.unbind(path2,host,port2);
				}
			} catch (ZKNamingException e) {
				System.out.println("Caught exception with description: " + e.getMessage());
			}
		}
	}
	
}
