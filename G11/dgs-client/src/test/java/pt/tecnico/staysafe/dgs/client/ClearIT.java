package pt.tecnico.staysafe.dgs.client;

import org.junit.jupiter.api.*;

import io.grpc.StatusRuntimeException;
import pt.tecnico.staysafe.dgs.grpc.*;

import static io.grpc.Status.INVALID_ARGUMENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ClearIT extends BaseIT {
	
	// static members
	private static final String SUCCESS = "All observations removed successfully.";
	
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
    public void clearOKTest() {
        assertEquals(SUCCESS, frontend.ctrl_clear(buildClearRequest()).getSuccess());
    }

}