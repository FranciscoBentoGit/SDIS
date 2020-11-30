package pt.tecnico.staysafe.dgs;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import pt.ulisboa.tecnico.sdis.zk.*;

import java.io.IOException;

import java.util.concurrent.*;


public class DgsServerApp {
	
	public static void main(String[] args) throws IOException, InterruptedException {
		System.out.println("StaySafe dgs server");
		int period1 = 5;
		int period2 = 10;
		int initialDelay = 60;
		
		
		// receive and print arguments
		System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}

		final String zooHost = args[0];
		final String zooPort = args[1];
		final String host = args[2];
		final String port = args[3];
		final String port2 = args[4];
		final String path = args[5];
		final String path2 = args[6];
		final BindableService impl = new DgsServiceImpl();
		ZKNaming zkNaming = null;
		
		try {
			zkNaming = new ZKNaming(zooHost, zooPort);
			
			// publish
			zkNaming.rebind(path, host, port);
			zkNaming.rebind(path2, host, port2);

			int portStr = Integer.parseInt(port);
			int portStr2 = Integer.parseInt(port2);

			// Create a new server to listen on port
			Server server = ServerBuilder.forPort(portStr).addService(impl).build();
			Server server2 = ServerBuilder.forPort(portStr2).addService(impl).build();

			// Start the replica 1
			server.start();

			// Server threads are running in the background.
			System.out.println("Replica 1 starting...");

			// Start the replica 2
			server2.start();

			// Server threads are running in the background.
			System.out.println("Replica 2 starting...");

			// Creates a frontend for each replica
			ServersFrontend frontend1 = new ServersFrontend(zkNaming,path);
			ServersFrontend frontend2 = new ServersFrontend(zkNaming,path2);

			ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

			Runnable prop1 = new Runnable() {
				@Override
				public void run(){
					System.out.println("Replica 1 initiating update exchange...");
					frontend1.update(frontend2.sendUpdate());
				}
			};
			Runnable prop2 = new Runnable() {
				@Override
				public void run(){
					System.out.println("Replica 2 initiating update exchange...");
					frontend2.update(frontend1.sendUpdate());

				}
			};

			
			scheduler.scheduleWithFixedDelay(prop1,initialDelay,period1,TimeUnit.SECONDS);
			scheduler.scheduleWithFixedDelay(prop2,initialDelay,period2,TimeUnit.SECONDS);
			/*
				fazendo list do zkNaming
					identificando o fronted a partir do port da list
						chamar em funçao do frontend[i].update que exporta as versoes mais recentes
			*/

			// Do not exit the main thread. Wait until server is terminated.
			server.awaitTermination();
			server2.awaitTermination();
		} catch (ZKNamingException e) {
			System.out.println("Caught exception with description: " + e.getMessage());
		} finally {
			try {
				if (zkNaming != null) {
				// remove
				zkNaming.unbind(path,host,port);
				zkNaming.unbind(path2,host,port2);
				}
			} catch (ZKNamingException e) {
				System.out.println("Caught exception with description: " + e.getMessage());
			}
		}
	}
	
	
}
