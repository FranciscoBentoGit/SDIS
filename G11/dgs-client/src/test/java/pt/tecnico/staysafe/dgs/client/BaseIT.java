package pt.tecnico.staysafe.dgs.client;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;
import java.util.Properties;

import com.google.protobuf.Timestamp;

import pt.tecnico.staysafe.dgs.grpc.*;


public class BaseIT {

	private static final String TEST_PROP_FILE = "/test.properties";
	protected static Properties testProps;

	static DgsFrontend frontend;
	
	@BeforeAll
	public static void oneTimeSetup () throws IOException {
		testProps = new Properties();
		
		try {
			testProps.load(BaseIT.class.getResourceAsStream(TEST_PROP_FILE));
			System.out.println("Test properties:");
			System.out.println(testProps);
		}catch (IOException e) {
			final String msg = String.format("Could not load properties file {}", TEST_PROP_FILE);
			System.out.println(msg);
			throw e;
		}

		//final String host = testProps.getProperty("server.host");
		//final int port = Integer.parseInt(testProps.getProperty("server.port"));
		//final String port = testProps.getProperty("server.port");
		//final String path = "/grpc/staysafe/dgs/1";
		frontend = new DgsFrontend("localhost", "2181", "/grpc/staysafe/dgs/1");
	}
	
	@AfterAll
	public static void cleanup() {
		
	}

	protected PingRequest buildPingRequest(String text) {
		return PingRequest.newBuilder().setText(text).setReplicaId(1).build();
	}

	protected ClearRequest buildClearRequest() {
		return ClearRequest.newBuilder().setReplicaId(1).build();
	}

	protected SnifferJoinRequest buildSnifferJoinRequest(String name, String address) {
		return SnifferJoinRequest.newBuilder().setName(name).setAddress(address).setReplicaId(1).build();
	}

	protected SnifferInfoRequest buildSnifferInfoRequest(String name) {
		return SnifferInfoRequest.newBuilder().setName(name).setReplicaId(1).build();
	}
	protected ReportRequest buildReportRequest(String snifferName,String infection,long id, Timestamp timeIn,Timestamp timeOut) {
		return ReportRequest.newBuilder().setName(snifferName).setInfection(infection).setId(id).setTimeIn(timeIn).setTimeOut(timeOut).setReplicaId(1).build();
	}

	protected IndividualProbRequest buildIndividualProbRequest(long id) {
		return IndividualProbRequest.newBuilder().setId(id).setReplicaId(1).build();
	}
	
	protected AggregateProbRequest buildAggregateProbRequest(String command) {
		return AggregateProbRequest.newBuilder().setCommand(command).setReplicaId(1).build();
	}

	/*protected InitRequest buildInitRequest(String snifferName, String infection, long id, google.protobuf.Timestamp timeIn, google.protobuf.Timestamp timeOut) {
		return InitRequest.newBuilder().setSnifferName(snifferName).setInfection(infection).setId(id).setTimeIn(timeIn).setTimeOut(timeOut).build();
	}*/

}
