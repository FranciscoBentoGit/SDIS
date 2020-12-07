package pt.tecnico.staysafe.dgs.client;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.Iterator;
import com.google.protobuf.Timestamp;
import com.google.protobuf.util.Timestamps;

import io.grpc.StatusRuntimeException;
import java.text.ParseException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import pt.tecnico.staysafe.dgs.grpc.DgsGrpc;
import pt.tecnico.staysafe.dgs.grpc.*;


public class DgsClientApp {
	
	
	public static void main(String[] args) {
		System.out.println(DgsClientApp.class.getSimpleName());
		
		// receive and print arguments
		System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}

		// check arguments
		if (args.length < 3) {
			System.out.println("Argument(s) missing!");
			System.out.printf("Usage: java %s host port%n", DgsClientApp.class.getName());
			return;
		}

		final String host = args[0];
		final String port = args[1];
		final String path = args[2];

		String[] split = path.split("/", 5);
		String instance = split[split.length - 1];
		int replicaId = Integer.parseInt(instance);

		execClient(host, port, path, replicaId, instance);
	}

	private static void execClient(String host, String port, String path, int replicaId, String instance) {
		DgsFrontend frontend = new DgsFrontend(host, port, path);
		String go;
		int flag = 0;
		try (Scanner scanner = new Scanner(System.in)) {
			do {
				go = scanner.nextLine();
				String[] goSplited = go.split(" ", 2);  //init file snifferName address -- separa init do restante
				if (go.equals("exitClient")) {
					flag = 1;
				}
				
				else if (go.equals("")){
					continue;
				}

				else if ((goSplited.length == 1) && (goSplited[0].equals("ping"))) {
					try {
						PingResponse response;
						response = ctrl_ping(frontend, replicaId);
						String newResponsePing = response.getText();
						System.out.printf("%s%n%n", newResponsePing);
					} catch (StatusRuntimeException e) {
						System.out.printf("Caught exception with description: " + e.getStatus().getDescription());
						System.out.printf(" when trying to contact replica %d at localhost:808%s%n", replicaId, instance);
						if (e.getStatus().getDescription().equals("io exception")) {
							
							long[] oldTs = frontend.getOldTs();
							ConcurrentHashMap<Long,IndividualProbResponse> oldSingleProb = frontend.getSingleProb();
							AggregateProbResponse oldMeanDev = frontend.getMeanDev();
							AggregateProbResponse oldPercentiles = frontend.getPercentiles();
							
							frontend = changePing(host, port);

							frontend.setTs(oldTs);
							frontend.setSingleProb(oldSingleProb);
							frontend.setMeanDev(oldMeanDev);
							frontend.setPercentiles(oldPercentiles);

							//Send information through new channel, so the dead replica gets unbided
							server_unbind(frontend, host, port, path);
							
							//changes replicaId to the new one
							replicaId = frontend.getReplicaId();
						}
					}
				}

				else if ((goSplited.length == 1) && (goSplited[0].equals("clear"))) {
					try {
					ClearResponse response;
					response = ctrl_clear(frontend, replicaId);
					String newResponseClear = response.getSuccess();
					System.out.printf("%s%n%n", newResponseClear);
					} catch (StatusRuntimeException e) {
						System.out.printf("Caught exception with description: " + e.getStatus().getDescription());
						System.out.printf(" when trying to contact replica %d at localhost:808%s%n", replicaId, instance);
						if (e.getStatus().getDescription().equals("io exception")) {	
							
							long[] oldTs = frontend.getOldTs();
							ConcurrentHashMap<Long,IndividualProbResponse> oldSingleProb = frontend.getSingleProb();
							AggregateProbResponse oldMeanDev = frontend.getMeanDev();
							AggregateProbResponse oldPercentiles = frontend.getPercentiles();
							
							frontend = changeClear(host, port);

							frontend.setTs(oldTs);
							frontend.setSingleProb(oldSingleProb);
							frontend.setMeanDev(oldMeanDev);
							frontend.setPercentiles(oldPercentiles);

							//Send information through new channel, so the dead replica gets unbided
							server_unbind(frontend, host, port, path);
							
							//changes replicaId to the new one
							replicaId = frontend.getReplicaId();
						}
					}
				}

				else if ((goSplited.length == 1) && (goSplited[0].equals("help"))) {
					String message = helpCtrl();
					System.out.printf("%s%n", message);
				}

				else if ((goSplited.length == 2) && (goSplited[0].equals("init"))) {
					aux_ctrl_init(frontend,goSplited[1], replicaId);
				}
				
				else{
					System.out.println("Invalid input.");
				}

			} while (flag != 1);
		}
	}

	//Function that helps all clients doing ctrl_init
	public static void aux_ctrl_init(DgsFrontend frontend, String preInfo, int replicaId) {
		ArrayList<String> obs = new ArrayList<String>();
		ArrayList<ObservationsInit> obsInit = new ArrayList<ObservationsInit>();
		//It splits fileName from snifferName and Address
		String[] info = preInfo.split(" ",3);
		
		if(info.length != 3){
			System.out.println("Invalid input!");
			return;
		}

		String file = info[0];
		String snifferName = info[1];
		String address = info[2];
		int flag = 0;
		try {
			SnifferJoinResponse message;
			message = sniffer_join(frontend,snifferName,address, replicaId);
			System.out.println(message.getSuccess());
			
			if (!(message.getSuccess().equals("Success to join sniffer."))) {
				flag = 1;
			} 
			
		} catch (StatusRuntimeException e) {
			flag = 1;
			System.out.println("Caught exception with description: " + e.getStatus().getDescription());
		}

		if (flag == 1){
			return;
		}

		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = reader.readLine();

			while (line != null) {
				obs.add(line);
				//read next line
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Iterator<String> iter = obs.iterator();
		while (iter.hasNext()) {
			String toReport = iter.next();
			String[] splitLine = toReport.split(",",4);
			
			String infection = splitLine[0];
			long id = Long.parseLong(splitLine[1]);

			//To able to insert timeIn and timeOut as Timestamp variables
			String[] auxIn = splitLine[2].split(" ",2);
			String timestampIn = auxIn[0] + "T" + auxIn[1] + "Z";
			String[] auxOut = splitLine[3].split(" ",2);
			String timestampOut = auxOut[0] + "T" + auxOut[1] + "Z";

			com.google.protobuf.Timestamp timeIn = null;
			com.google.protobuf.Timestamp timeOut = null;
			try {
				timeIn = Timestamps.parse(timestampIn);
				timeOut = Timestamps.parse(timestampOut);
			} catch (ParseException e) {
				e.printStackTrace();
			}

			ObservationsInit data = new ObservationsInit(snifferName,infection,id,timeIn,timeOut);
			obsInit.add(data);
		}	

		Iterator<ObservationsInit> iter1 = obsInit.iterator();
		while (iter1.hasNext()) {
			ObservationsInit element = iter1.next();
			try {
				ReportResponse response;
				response = sniffer_report(frontend, element.getSnifferName(), element.getInfection(), element.getId(), element.getTimeIn(), element.getTimeOut(), replicaId);
				System.out.printf("%s%n", response.getSuccess());
			} catch (StatusRuntimeException e) {
				System.out.println("Caught exception with description: " + e.getStatus().getDescription());
			}
		}
	}

	public static PingResponse ctrl_ping(DgsFrontend frontend, int replicaId) {
		PingResponse response;
		PingRequest request = PingRequest.newBuilder().setText("friend").setReplicaId(replicaId).build();
		response = frontend.ctrl_ping(request);
		return response;
	}

	public static ClearResponse ctrl_clear(DgsFrontend frontend, int replicaId) {
		ClearResponse response;
		ClearRequest request = ClearRequest.newBuilder().setReplicaId(replicaId).build();
		response = frontend.ctrl_clear(request);
		return response;
	}
	
	public static SnifferJoinResponse sniffer_join(DgsFrontend frontend, String snifferName, String address, int replicaId) {
		SnifferJoinResponse response;
		SnifferJoinRequest request = SnifferJoinRequest.newBuilder().setName(snifferName).setAddress(address).setReplicaId(replicaId).build();
		response = frontend.sniffer_join(request);
		return response;
	}

	public static SnifferInfoResponse sniffer_info(DgsFrontend frontend, String snifferName, int replicaId) {
		SnifferInfoResponse response;
		SnifferInfoRequest request = SnifferInfoRequest.newBuilder().setName(snifferName).setReplicaId(replicaId).build();
		response = frontend.sniffer_info(request);
		return response;
	}

	public static void sleep_request(int sleepTime) {
		try {
		    Thread.sleep(sleepTime / 1000);
		} catch (InterruptedException ie) {
		    Thread.currentThread().interrupt();
		}
	}

	public static ReportResponse sniffer_report(DgsFrontend frontend, String snifferName, String infection, long id, com.google.protobuf.Timestamp timeIn, com.google.protobuf.Timestamp timeOut, int replicaId) {
		ReportResponse response;
		ReportRequest request = ReportRequest.newBuilder().setName(snifferName).setInfection(infection).setId(id).setTimeIn(timeIn).setTimeOut(timeOut).setReplicaId(replicaId).build();
		response = frontend.report(request);
		return response;
	}

	public static IndividualProbResponse individual_infection_probability(DgsFrontend frontend, long id, int replicaId) {
		IndividualProbResponse response;
		IndividualProbRequest request = IndividualProbRequest.newBuilder().setId(id).setReplicaId(replicaId).build();
		response = frontend.individual_infection_probability(request);
		return response;
	}

	public static AggregateProbResponse aggregate_infection_probability(DgsFrontend frontend, String command, int replicaId) {
		AggregateProbResponse response;
		AggregateProbRequest request = AggregateProbRequest.newBuilder().setCommand(command).setReplicaId(replicaId).build();
		response = frontend.aggregate_infection_probability(request);
		return response;
	}

	public static UnbindResponse server_unbind(DgsFrontend frontend, String host, String port, String path) {
		UnbindResponse response;
		UnbindRequest request = UnbindRequest.newBuilder().setHost(host).setPort(port).setPath(path).build();
		response = frontend.unbind(request);
		return response;
	}

	public static String helpCtrl() {
		String ctrlInit = "** init <filePath> <snifferName> <address> -- insert all observations inside the file , associating them with the given sniffer and address.\n";
		String ctrlClear = "** clear -- remove all observations and sniffers.\n";
		String ctrlPing = "** ping -- returns Hello only if the server is alive.\n";

		String message = ctrlInit + "\n" + ctrlClear + "\n" + ctrlPing;

		return message;
	}

	public static String helpSniffer() {
		String sniffer = "You can do the following commands:\n";
		String snifferInfo = "** getInfo -- returns the sniffer's address\n";
		String snifferSleep = "** sleep,<time> --  interrupts the execution for a certain <time>.\n";
		String snifferReport = "** <infetado/nao-infetado>,<id>,<timeIn>,<timeOut> -- submits a report with an observation with the parameters <infection_state>,<id>,<timeIn>,<timeOut>.\n";
		String snifferExit = "** exitSniffer -- closes the sniffer.\n";
		String ctrl = helpCtrl();

		String message = sniffer + "\n" + snifferInfo + "\n" + snifferSleep + "\n" + snifferReport + "\n" + snifferExit + "\n"+ ctrl;

		return message;
	}

	public static String helpResearcher() {
		String researcher = "You can do the following commands:\n";
		String researcherProb = "** single_prob <id>,... -- returns the probability of the person related to the id being infected, at most 3 id's at the same time.\n";
		String researcherMean = "** mean_dev -- returns the average and standard deviation of the non-infected persons.\n";
		String researcherPercentiles = "** percentiles -- returns median, Q1(25% percentile) and Q3(75% percentile) of the non-infected persons.\n";
		String researcherExit = "** exitResearcher -- closes the researcher.\n";
		String ctrl = helpCtrl();

		String message = researcher + "\n" + researcherProb + "\n" + researcherMean + "\n" + researcherPercentiles  + "\n" + researcherExit + "\n" +  ctrl;

		return message;
	}
	public static String helpJournalist() {
		String journalist = "You can do the following commands:\n";
		String journalistMean = "** mean_dev -- returns the average and standard deviation of the non-infected persons.\n";
		String journalistPercentiles = "** percentiles -- returns median, Q1(25% percentile) and Q3(75% percentile) of the non-infected persons.\n";
		String journalistExit = "** exitJournalist -- closes the journalist.\n";
		String ctrl = helpCtrl();

		String message = journalist + "\n" + journalistMean + "\n" + journalistPercentiles + "\n" + journalistExit + "\n" + ctrl;


		return message;
	}

	public static DgsFrontend changeJoin(String host, String port, String snifferName, String address) {
		int catched = 1;
		DgsFrontend frontend = null;
		int replicaId = 0;
		String instance = null;
		while (catched == 1) {
			//Generates a random replica comunication channel, to tolerate the fault
			Random rand = new Random();
			replicaId = rand.nextInt(3) + 1;
			instance = String.valueOf(replicaId);
			String path = "/grpc/staysafe/dgs/" + instance;
			frontend = new DgsFrontend(host, port, path);
			try {
				System.out.printf("Trying to contact replica %d at localhost:808%s...%n", replicaId, instance);
				SnifferJoinResponse responseJoin;
				responseJoin = sniffer_join(frontend, snifferName, address, replicaId);
				String newResponseJoin = responseJoin.getSuccess();
				System.out.printf("%s%n%n", newResponseJoin);
				catched = 0;
			} catch (StatusRuntimeException e2) {
				//do nothing
			}
		}

		//Found an alive replica and announce the new comunication channel
		System.out.printf("Contacting now with replica %d at localhost:808%s...%n%n", replicaId, instance);
		return frontend;
	}

	public static DgsFrontend changeInfo(String host, String port, String snifferName, String address) {
		int catched = 1;
		DgsFrontend frontend = null;
		int replicaId = 0;
		String instance = null;
		while (catched == 1) {
			//Generates a random replica comunication channel, to tolerate the fault
			Random rand = new Random();
			replicaId = rand.nextInt(3) + 1;
			instance = String.valueOf(replicaId);
			String path = "/grpc/staysafe/dgs/" + instance;
			frontend = new DgsFrontend(host, port, path);
			try {
				System.out.printf("Trying to contact replica %d at localhost:808%s...%n", replicaId, instance);
				SnifferInfoResponse responseInfo;
				responseInfo = sniffer_info(frontend, snifferName, replicaId);
				String newResponseInfo = responseInfo.getNameAddress();
				System.out.printf("%s%n%n", newResponseInfo);
				catched = 0;
			} catch (StatusRuntimeException e2) {
				//do nothing
			}
		}

		//Found an alive replica and announce the new comunication channel
		System.out.printf("Contacting now with replica %d at localhost:808%s...%n%n", replicaId, instance);
		return frontend;
	}

	public static DgsFrontend changeReport(String host, String port, String snifferName, String address, String infection, long id, com.google.protobuf.Timestamp timeIn,  com.google.protobuf.Timestamp timeOut) {
		int catched = 1;
		DgsFrontend frontend = null;
		int replicaId = 0;
		String instance = null;
		while (catched == 1) {
			//Generates a random replica comunication channel, to tolerate the fault
			Random rand = new Random();
			replicaId = rand.nextInt(3) + 1;
			instance = String.valueOf(replicaId);
			String path = "/grpc/staysafe/dgs/" + instance;
			frontend = new DgsFrontend(host, port, path);
			try {
				System.out.printf("Trying to contact replica %d at localhost:808%s...%n", replicaId, instance);
				ReportResponse responseReport;
				responseReport = sniffer_report(frontend, snifferName, infection, id, timeIn, timeOut, replicaId);
				String newResponseReport = responseReport.getSuccess();
				System.out.printf("%s%n%n", newResponseReport);
				catched = 0;
			} catch (StatusRuntimeException e2) {
				//do nothing
			}
		}

		//Found an alive replica and announce the new comunication channel
		System.out.printf("Contacting now with replica %d at localhost:808%s...%n%n", replicaId, instance);
		return frontend;
	}

	public static DgsFrontend changePing(String host, String port) {
		int catched = 1;
		DgsFrontend frontend = null;
		int replicaId = 0;
		String instance = null;
		while (catched == 1) {
			//Generates a random replica comunication channel, to tolerate the fault
			Random rand = new Random();
			replicaId = rand.nextInt(3) + 1;
			instance = String.valueOf(replicaId);
			String path = "/grpc/staysafe/dgs/" + instance;
			frontend = new DgsFrontend(host, port, path);
			try {
				System.out.printf("Trying to contact replica %d at localhost:808%s...%n", replicaId, instance);
				PingResponse response;
				response = ctrl_ping(frontend, replicaId);
				String newResponsePing = response.getText();
				System.out.printf("%s%n%n", newResponsePing);
				catched = 0;
			} catch (StatusRuntimeException e2) {
				//do nothing
			}
		}

		//Found an alive replica and announce the new comunication channel
		System.out.printf("Contacting now with replica %d at localhost:808%s...%n%n", replicaId, instance);
		return frontend;
	}

	public static DgsFrontend changeClear(String host, String port) {
		int catched = 1;
		DgsFrontend frontend = null;
		int replicaId = 0;
		String instance = null;
		while (catched == 1) {
			//Generates a random replica comunication channel, to tolerate the fault
			Random rand = new Random();
			replicaId = rand.nextInt(3) + 1;
			instance = String.valueOf(replicaId);
			String path = "/grpc/staysafe/dgs/" + instance;
			frontend = new DgsFrontend(host, port, path);
			try {
				System.out.printf("Trying to contact replica %d at localhost:808%s...%n", replicaId, instance);
				ClearResponse response;
				response = ctrl_clear(frontend, replicaId);
				String newResponseClear = response.getSuccess();
				System.out.printf("%s%n%n", newResponseClear);
				catched = 0;
			} catch (StatusRuntimeException e2) {
				//do nothing
			}
		}

		//Found an alive replica and announce the new comunication channel
		System.out.printf("Contacting now with replica %d at localhost:808%s...%n%n", replicaId, instance);
		return frontend;
	}

	public static DgsFrontend changeSingle(String host, String port, String[] ids) {
		int catched = 1;
		DgsFrontend frontend = null;
		int replicaId = 0;
		String instance = null;
		while (catched == 1) {
			//Generates a random replica comunication channel, to tolerate the fault
			Random rand = new Random();
			replicaId = rand.nextInt(3) + 1;
			instance = String.valueOf(replicaId);
			String path = "/grpc/staysafe/dgs/" + instance;
			frontend = new DgsFrontend(host, port, path);
			for (int i = 0; i < ids.length; i++) {
				try {
					IndividualProbResponse response;
					response = individual_infection_probability(frontend, Long.parseLong(ids[i]),replicaId);

					float prob = response.getProb();

					//If the function individual_infection_probability returns 2.0, because it must return a float,it detects it's irrealistic and means id not found
					if (Float.compare(prob,(float)2.0) == 0) {
						String error = "Id: not found!"; 
						System.out.printf("%s%n%n",error);
					} else {
						System.out.printf("%.3f%n%n",prob);
					}
					catched = 0;
				} catch (StatusRuntimeException e2) {
					//do nothing
				}
			}
		}

		//Found an alive replica and announce the new comunication channel
		System.out.printf("Contacting now with replica %d at localhost:808%s...%n%n", replicaId, instance);
		return frontend;
	}

	public static DgsFrontend changeMean(String host, String port, String command) {
		int catched = 1;
		DgsFrontend frontend = null;
		int replicaId = 0;
		String instance = null;
		while (catched == 1) {
			//Generates a random replica comunication channel, to tolerate the fault
			Random rand = new Random();
			replicaId = rand.nextInt(3) + 1;
			instance = String.valueOf(replicaId);
			String path = "/grpc/staysafe/dgs/" + instance;
			frontend = new DgsFrontend(host, port, path);
			try {
				AggregateProbResponse response;
				response = aggregate_infection_probability(frontend,command,replicaId);
				
				float f1 = response.getStat(0);
				float f2 = response.getStat(1);

				if ((Float.compare(f1,(float)3.0) == 0) && (Float.compare(f2,(float)3.0) == 0)) {
					String error = "Empty: no observations found"; 
					System.out.printf("%s%n%n",error);
				} else {
					System.out.printf("%.3f%n%.3f%n%.3f%n%n",f1,f2);
				}
				catched = 0;
			} catch (StatusRuntimeException e2) {
				//do nothing
			}
		}

		//Found an alive replica and announce the new comunication channel
		System.out.printf("Contacting now with replica %d at localhost:808%s...%n%n", replicaId, instance);
		return frontend;
	}

	public static DgsFrontend changePercentiles(String host, String port, String command) {
		int catched = 1;
		DgsFrontend frontend = null;
		int replicaId = 0;
		String instance = null;
		while (catched == 1) {
			//Generates a random replica comunication channel, to tolerate the fault
			Random rand = new Random();
			replicaId = rand.nextInt(3) + 1;
			instance = String.valueOf(replicaId);
			String path = "/grpc/staysafe/dgs/" + instance;
			frontend = new DgsFrontend(host, port, path);
			try {
				AggregateProbResponse response;
				response = aggregate_infection_probability(frontend,command,replicaId);
				
				float f1 = response.getStat(0);
				float f2 = response.getStat(1);
				float f3 = response.getStat(2);

				if ((Float.compare(f1,(float)3.0) == 0) && (Float.compare(f2,(float)3.0) == 0) && (Float.compare(f3,(float)3.0) == 0)) {
					String error = "Empty: no observations found"; 
					System.out.printf("%s%n%n",error);
				} else {
					System.out.printf("%.3f%n%.3f%n%.3f%n%n",f1,f2,f3);
				}
				catched = 0;
			} catch (StatusRuntimeException e2) {
				//do nothing
			}
		}

		//Found an alive replica and announce the new comunication channel
		System.out.printf("Contacting now with replica %d at localhost:808%s...%n%n", replicaId, instance);
		return frontend;
	}
}
