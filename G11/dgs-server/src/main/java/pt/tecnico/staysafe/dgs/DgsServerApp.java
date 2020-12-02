package pt.tecnico.staysafe.dgs;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import pt.ulisboa.tecnico.sdis.zk.*;

import java.io.IOException;
import io.grpc.StatusRuntimeException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;

public class DgsServerApp {
	
	public static void main(String[] args) throws IOException, InterruptedException {
		System.out.println("StaySafe dgs server");

		final String zooHost = args[0];
		final String zooPort = args[1];
		final String parentNode = "/grpc/staysafe/dgs/";
		final String host = "localhost";
		String port = null;
		String path = null;
		int id = 0;
		
		final int initialDelay = 5;
		final int period = 30;

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
					path = parentNode + go;
					exit = 1;
				}

				else if (go.equals("2")) {
					id = Integer.parseInt(go);
					port = "808" + go;
					path = parentNode + go;
					exit = 1;
				}

				else if (go.equals("3")) {
					id = Integer.parseInt(go);
					port = "808" + go;
					path = parentNode + go;
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

		final DgsServiceImpl impl = new DgsServiceImpl();

		ZKNaming zkNaming = null;
		
		try {
			zkNaming = new ZKNaming(zooHost, zooPort);
			
			final ZKNaming zkNamingAux = new ZKNaming(zooHost, zooPort);
			// publish
			zkNaming.rebind(path, host, port);

			int portStr = Integer.parseInt(port);

			// Create a new server to listen on port
			Server server = ServerBuilder.forPort(portStr).addService(impl).build();

			// Start the replica 1
			server.start();

			// Server threads are running in the background.
			System.out.printf("Replica %d starting...%n", id);

			ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

            Runnable propagation = new Runnable() {
                @Override
                public void run(){
					final String parent = "/grpc/staysafe/dgs";
                    try {
						Collection<ZKRecord> replicaCollection = zkNamingAux.listRecords(parent);

						for (ZKRecord record : replicaCollection){
							if (record.getPath() != path) {
								String[] split = record.getPath().split("/", 5);
								int instance = Integer.parseInt(split[split.length - 1]);

								System.out.printf("Replica %d initiating update exchange...%n", instance);

								ServersFrontend frontend = new ServersFrontend(zkNaming,record.getURI());
								
								CopyOnWriteArrayList<Log> list = impl.getList();
								Iterator<Log> it = list.iterator();
								while (it.hasNext()) {
									Log i = it.next();
									if (i.getType().equals("clear")) {
										frontend.ctrl_clear(instance);
									}
									if (i.getType().equals("report")) {
										frontend.report(i.getReport(),instance);
									}
								}
							}
						}
					} catch (ZKNamingException e) {
						System.out.println("Caught exception with description: " + e.getMessage());
					}
                }
            };

            scheduler.scheduleWithFixedDelay(propagation,initialDelay,period,TimeUnit.SECONDS);


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
