package pt.tecnico.staysafe.dgs.client;

import org.junit.jupiter.api.*;

import io.grpc.StatusRuntimeException;
import pt.tecnico.staysafe.dgs.grpc.*;

import static io.grpc.Status.INVALID_ARGUMENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PingIT extends BaseIT {
	
	// static members
	private static final String EMPTY_PING = "";
	private static final String PING = "friend";
	
	
	// one-time initialization and clean-up
	@BeforeAll
	public static void oneTimeSetUp(){
		
	}

	@AfterAll
	public static void oneTimeTearDown() {
		
	}
	
	// initialization and clean-up for each test
	@BeforeEach
	public void setUp() {
		
	}
	
	@AfterEach
	public void tearDown() {
		
	}

	// tests     
   	@Test
    public void pingOKTest() {
        assertEquals("Hello friend!", frontend.ctrl_ping(buildPingRequest(PING)).getText());
    }

    @Test
    public void emptyPingTest() {
        assertEquals(INVALID_ARGUMENT.getCode(),
            assertThrows(StatusRuntimeException.class, () -> frontend.ctrl_ping(buildPingRequest(EMPTY_PING))).getStatus().getCode());
    }

}