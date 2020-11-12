package pt.tecnico.staysafe.dgs.client;

import org.junit.jupiter.api.*;

import io.grpc.StatusRuntimeException;
import pt.tecnico.staysafe.dgs.grpc.*;

import static io.grpc.Status.INVALID_ARGUMENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SnifferJoinIT extends BaseIT {
	
	// static members
    private static final String NAME_OK = "sniffer1";
    private static final String ADDRESS_OK = "Rua Alves Redol, 9, Piso 0, 1000-029 Lisboa";
    private static final String ADDRESS_ERROR = "Rua Alves Redol, 9, Piso 0, 1000-029 Porto";
	private static final String SUCCESS = "Success to join sniffer.";
    private static final String ERROR = "Failed to join sniffer: invalid address for that name.";		
	
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
		frontend.sniffer_join(buildSnifferJoinRequest(NAME_OK,ADDRESS_OK));
	}
	
	@AfterEach
	public void tearDown() {
		
	}

	// tests     
   	@Test
    public void snifferJoinOKTest() {
        assertEquals(SUCCESS, frontend.sniffer_join(buildSnifferJoinRequest(NAME_OK,ADDRESS_OK)).getSuccess());
    }

    @Test
    public void snifferJoinERRORTest() {
        assertEquals(ERROR, frontend.sniffer_join(buildSnifferJoinRequest(NAME_OK,ADDRESS_ERROR)).getSuccess());
    }

}