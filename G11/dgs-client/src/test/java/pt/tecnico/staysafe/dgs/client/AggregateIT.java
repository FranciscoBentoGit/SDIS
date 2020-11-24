package pt.tecnico.staysafe.dgs.client;

import org.junit.jupiter.api.*;

import java.text.ParseException;
import io.grpc.StatusRuntimeException;
import pt.tecnico.staysafe.dgs.grpc.*;

import com.google.protobuf.Timestamp;
import com.google.protobuf.util.Timestamps;

import static io.grpc.Status.INVALID_ARGUMENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AggregateIT extends BaseIT {
	
    // static members
    
    private static final String NAME_OK = "sniffer1";
    private static final String ADDRESS_OK = "Rua Alves Redol, 9, Piso 0, 1000-029 Lisboa";
    
    private static final String INFECTION= "infetado";
    private static final String NON_INFECTION= "nao-infetado";
    
    private static com.google.protobuf.Timestamp TIME1IN;
    private static com.google.protobuf.Timestamp TIME1OUT;
    private static com.google.protobuf.Timestamp TIME2IN;
    private static com.google.protobuf.Timestamp TIME2OUT;
    private static com.google.protobuf.Timestamp TIME3IN;
    private static com.google.protobuf.Timestamp TIME3OUT;
    
    private static final long ID1 = 111111111;
    private static final long ID2 = 222222222;
	private static final long ID3 = 333333333;
	
	private static final int INDEX0 = 0;
    private static final int INDEX1 = 1;
    private static final int INDEX2 = 2;
    
	private static final String COMMAND_MEAN = "mean_dev";
	private static final String COMMAND_PERCENTILE = "percentiles";
	private static final String COMMAND_NULL = "";

    private static final float MEAN = (float)0.6344705;
	private static final float DEV = (float)0.36552912;
	private static final float MEDIAN = (float)0.6344705;
	private static final float Q1 = (float)0.26894143;
	private static final float Q3 = (float)0.99999964;
    		
	
	// one-time initialization and clean-up
	@BeforeAll
	public static void oneTimeSetUp(){
        String timestamp1In = "12-01-2000T19:00:00Z";
        String timestamp1Out = "12-01-2000T19:30:00Z";
        String timestamp2In = "12-01-2000T19:31:00Z";
        String timestamp2Out = "12-01-2000T19:45:00Z";
        String timestamp3In = "12-01-2000T17:00:00Z";
        String timestamp3Out = "12-01-2000T19:45:00Z";

		try {
			TIME1IN = Timestamps.parse(timestamp1In);
            TIME1OUT = Timestamps.parse(timestamp1Out);
            TIME2IN = Timestamps.parse(timestamp2In);
            TIME2OUT = Timestamps.parse(timestamp2Out);
            TIME3IN = Timestamps.parse(timestamp3In);
            TIME3OUT = Timestamps.parse(timestamp3Out);
            return;
		} catch (ParseException e) {
			e.printStackTrace();
		}
	
    }
        

	@AfterAll
	public static void oneTimeTearDown() {
		
	}
	
	// initialization and clean-up for each test
	@BeforeEach
	public void setUp() {
        frontend.sniffer_join(buildSnifferJoinRequest(NAME_OK,ADDRESS_OK));
        frontend.report(buildReportRequest(NAME_OK,NON_INFECTION,ID1,TIME1IN,TIME1OUT));
        frontend.report(buildReportRequest(NAME_OK,NON_INFECTION,ID2,TIME2IN,TIME2OUT));
        frontend.report(buildReportRequest(NAME_OK,INFECTION,ID3,TIME3IN,TIME3OUT));
	}
	
	@AfterEach
	public void tearDown() {
		frontend.ctrl_clear(buildClearRequest()).getSuccess();
	}

	// tests     
   	@Test
    public void aggregateMeanDevOKTest() {
        
		assertEquals(MEAN, frontend.aggregate_infection_probability(buildAggregateProbRequest(COMMAND_MEAN)).getStat(INDEX0));
		assertEquals(DEV, frontend.aggregate_infection_probability(buildAggregateProbRequest(COMMAND_MEAN)).getStat(INDEX1));
	}
	
	@Test
    public void aggregatePercentilesOKTest() {
        
		assertEquals(MEDIAN, frontend.aggregate_infection_probability(buildAggregateProbRequest(COMMAND_PERCENTILE)).getStat(INDEX0));
		assertEquals(Q1, frontend.aggregate_infection_probability(buildAggregateProbRequest(COMMAND_PERCENTILE)).getStat(INDEX1));
		assertEquals(Q3, frontend.aggregate_infection_probability(buildAggregateProbRequest(COMMAND_PERCENTILE)).getStat(INDEX2));
	}
	
	@Test
    public void invalidCommandTest() {
        assertEquals(INVALID_ARGUMENT.getCode(),
            assertThrows(StatusRuntimeException.class, () -> frontend.aggregate_infection_probability(buildAggregateProbRequest(COMMAND_NULL))).getStatus().getCode());
    }


}