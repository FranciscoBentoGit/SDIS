package pt.tecnico.staysafe.dgs.client;

import org.junit.jupiter.api.*;

import io.grpc.StatusRuntimeException;
import pt.tecnico.staysafe.dgs.grpc.*;

import static io.grpc.Status.INVALID_ARGUMENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SnifferInfoIT extends BaseIT {
	
	// static members
    private static final String NAME_OK = "sniffer1";
    private static final String ADDRESS_OK = "Rua Alves Redol, 9, Piso 0, 1000-029 Lisboa";	
	
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
    public void snifferInfoOKTest() {
        assertEquals(ADDRESS_OK, frontend.sniffer_info(buildSnifferInfoRequest(NAME_OK)).getNameAddress());
    }

}
