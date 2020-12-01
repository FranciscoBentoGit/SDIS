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

public class ReportIT extends BaseIT {
	
    // static members
    
    private static final String NAME_OK = "sniffer1";
    private static final String NAME_NULL = "";
    private static final String WRONG_NAME = "sniffer2";

    private static final String ADDRESS_OK = "Rua Alves Redol, 9, Piso 0, 1000-029 Lisboa";
    
    private static final String INFECTION_OK = "infetado";
    private static final String INFECTION_NULL = "";

    private static com.google.protobuf.Timestamp TIMEIN;
    private static com.google.protobuf.Timestamp TIMEOUT;
    
    private static final long SIZE_ID = 123456789;
    private static final long WRONG_SIZE_ID = 1234567;

    private static final String INVALID_NAME = "Failed to report: invalid name.";
    private static final String SUCCESS = "Success to report.";
    		
	
	// one-time initialization and clean-up
	@BeforeAll
	public static void oneTimeSetUp(){
        String timestampIn = "12-01-2001T19:00:00Z";
        String timestampOut = "12-01-2001T20:00:00Z";

		try {
			TIMEIN = Timestamps.parse(timestampIn);
            TIMEOUT = Timestamps.parse(timestampOut);
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
	}
	
	@AfterEach
	public void tearDown() {
		frontend.ctrl_clear(buildClearRequest()).getSuccess();
	}

	// tests     
   	@Test
    public void reportOKTest() {
        
        assertEquals(SUCCESS, frontend.report(buildReportRequest(NAME_OK,INFECTION_OK,SIZE_ID,TIMEIN,TIMEOUT)).getSuccess());
    }

    @Test
    public void nameNullTest() {
        assertEquals(INVALID_ARGUMENT.getCode(),
            assertThrows(StatusRuntimeException.class, () -> frontend.report(buildReportRequest(NAME_NULL,INFECTION_OK,SIZE_ID,TIMEIN,TIMEOUT))).getStatus().getCode());
    }
    
    @Test
    public void infectionNullTest() {
        assertEquals(INVALID_ARGUMENT.getCode(),
            assertThrows(StatusRuntimeException.class, () -> frontend.report(buildReportRequest(NAME_OK,INFECTION_NULL,SIZE_ID,TIMEIN,TIMEOUT))).getStatus().getCode());
    }

    
    @Test
    public void diffNameTest() {
        assertEquals(INVALID_NAME, frontend.report(buildReportRequest(WRONG_NAME,INFECTION_OK,SIZE_ID,TIMEIN,TIMEOUT)).getSuccess());
    }



}