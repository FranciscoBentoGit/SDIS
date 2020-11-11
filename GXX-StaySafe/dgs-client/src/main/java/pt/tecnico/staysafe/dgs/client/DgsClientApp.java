package pt.tecnico.staysafe.dgs.client;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.Iterator;
import com.google.protobuf.Timestamp;
import com.google.protobuf.util.Timestamps;

import io.grpc.StatusRuntimeException;
import java.text.ParseException;

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
		if (args.length < 2) {
			System.out.println("Argument(s) missing!");
			System.out.printf("Usage: java %s host port%n", DgsClientApp.class.getName());
			return;
		}

		final String host = args[0];
		final int port = Integer.parseInt(args[1]);

		execClient(host, port);
	}

	private static void execClient(String host, int port) {
		DgsFrontend frontend = new DgsFrontend(host, port);
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
						response = ctrl_ping(frontend);
						System.out.printf("%s%n", response);
					} catch (StatusRuntimeException e) {
						System.out.println("Caught exception with description: " + e.getStatus().getDescription());
					}	
				}

				else if ((goSplited.length == 1) && (goSplited[0].equals("clear"))) {
						ClearResponse response;
						response = ctrl_clear(frontend);
						System.out.printf("%s%n", response);
				}

				else if ((goSplited.length == 2) && (goSplited[0].equals("init"))) {
					aux_ctrl_init(frontend,goSplited[1]);
				}
				
				else{
					System.out.println("Invalid input.");
				}

			} while (flag != 1);
		}
	}

	public static void aux_ctrl_init(DgsFrontend frontend, String preInfo) {
		ArrayList<String> obs = new ArrayList<String>();
		ArrayList<ObservationsInit> obsInit = new ArrayList<ObservationsInit>();
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
			message = sniffer_join(frontend,snifferName,address);
			System.out.println(message);
			String compare = message.toString();
			String[] splited = compare.split(" ",2);
			String[] splitMarks = splited[1].split("\"",3);
			if (!(splitMarks[1].equals("Success to join sniffer."))){
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
				System.out.println(line); //tirar
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
				InitResponse response;
				response = ctrl_init(frontend, element.getSnifferName(), element.getInfection(), element.getId(), element.getTimeIn(), element.getTimeOut());
				System.out.printf("%s%n", response);
			} catch (StatusRuntimeException e) {
				System.out.println("Caught exception with description: " + e.getStatus().getDescription());
			}
		}
	}

	public static PingResponse ctrl_ping(DgsFrontend frontend) {
		PingResponse response;
		PingRequest request = PingRequest.newBuilder().setText("friend").build();
		response = frontend.ctrl_ping(request);
		return response;
	}

	public static InitResponse ctrl_init(DgsFrontend frontend, String snifferName, String infection, long id, com.google.protobuf.Timestamp timeIn, com.google.protobuf.Timestamp timeOut) {
		InitResponse response;
		InitRequest request = InitRequest.newBuilder().setSnifferName(snifferName).setInfection(infection).setId(id).setTimeIn(timeIn).setTimeOut(timeOut).build();
		response = frontend.ctrl_init(request);
		return response;
	}

	public static ClearResponse ctrl_clear(DgsFrontend frontend) {
		ClearResponse response;
		ClearRequest request = ClearRequest.getDefaultInstance();
		response = frontend.ctrl_clear(request);
		return response;
	}
	
	public static SnifferJoinResponse sniffer_join(DgsFrontend frontend, String snifferName, String address) {
		SnifferJoinResponse response;
		SnifferJoinRequest request = SnifferJoinRequest.newBuilder().setName(snifferName).setAddress(address).build();
		response = frontend.sniffer_join(request);
		return response;
	}

	public static SnifferInfoResponse sniffer_info(DgsFrontend frontend, String snifferName) {
		SnifferInfoResponse response;
		SnifferInfoRequest request = SnifferInfoRequest.newBuilder().setName(snifferName).build();
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

	public static ReportResponse sniffer_report(DgsFrontend frontend, String snifferName, String infection, long id, com.google.protobuf.Timestamp timeIn, com.google.protobuf.Timestamp timeOut) {
		ReportResponse response;
		ReportRequest request = ReportRequest.newBuilder().setName(snifferName).setInfection(infection).setId(id).setTimeIn(timeIn).setTimeOut(timeOut).build();
		response = frontend.report(request);
		return response;
	}

	public static IndividualProbResponse individual_infection_probability(DgsFrontend frontend, long id) {
		IndividualProbResponse response;
		IndividualProbRequest request = IndividualProbRequest.newBuilder().setId(id).build();
		response = frontend.individual_infection_probability(request);
		return response;
	}

	public static AggregateProbResponse aggregate_infection_probability(DgsFrontend frontend, String command) {
		AggregateProbResponse response;
		AggregateProbRequest request = AggregateProbRequest.newBuilder().setCommand(command).build();
		response = frontend.aggregate_infection_probability(request);
		return response;
	}

	public static String helpCtrl() {
		String ctrlInit = "** init <filePath> <snifferName> <address> -- insert all observations inside the file , associating them with the given sniffer and address\n";
		String ctrlClear = "** clear -- remove all observations and sniffers\n";
		String ctrlPing = "** ping -- returns Hello only if the server is alive\n";

		String message = ctrlInit + "\n" + ctrlClear + "\n" + ctrlPing;

		return message;
	}

	public static String helpSniffer() {
		String sniffer = "You can do the following commands:\n";
		String snifferInfo = "** getInfo -- returns the sniffer's address\n";
		String snifferSleep = "** sleep,<time> --  interrupts the execution for a certain <time>\n";
		String snifferReport = "** <infetado/nao-infetado>,<id>,<timeIn>,<timeOut> -- submits a report with an observation with the parameters <infection_state>,<id>,<timeIn>,<timeOut>\n";

		String ctrl = helpCtrl();

		String message = sniffer + "\n" + snifferInfo + "\n" + snifferSleep + "\n" + snifferReport + "\n" + ctrl;

		return message;
	}

	public static String helpResearcher() {
		String researcher = "You can do the following commands:\n";
		String researcherProb = "** single_prob <id>,... -- returns the probability of the person related to the id being infected, at most 3 id's at the same time\n";
		String researcherMean = "** mean_dev -- returns the average and standard deviation of the non-infected persons.";
		String researcherPercentiles = "** percentiles -- returns median, Q1(25% percentile) and Q3(75% percentile) of the non-infected persons.";

		String ctrl = helpCtrl();

		String message = researcher + "\n" + researcherProb + "\n" + researcherMean + "\n" + researcherPercentiles + "\n" + ctrl;

		return message;
	}
	public static String helpJournalist() {
		String journalist = "You can do the following commands:\n";
		String journalistMean = "** mean_dev -- returns the average and standard deviation of the non-infected persons.";
		String journalistPercentiles = "** percentiles -- returns median, Q1(25% percentile) and Q3(75% percentile) of the non-infected persons.";

		String ctrl = helpCtrl();

		String message = journalist + "\n" + journalistMean + "\n" + journalistPercentiles + "\n" + ctrl;


		return message;
	}
}
