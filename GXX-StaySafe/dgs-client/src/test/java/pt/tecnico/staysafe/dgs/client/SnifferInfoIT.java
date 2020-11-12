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
	private static final String NAME_NULL = "";
	private static final String DIFF_NAME = "sniffer2";
	private static final String ADDRESS_OK = "Rua Alves Redol, 9, Piso 0, 1000-029 Lisboa";
	private static final String NAME_DOESNT_EXIST = "Failed to find the address: name does not exist.";
	
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
		frontend.ctrl_clear(buildClearRequest()).getSuccess();
	}

	// tests     
   	@Test
    public void snifferInfoOKTest() {
        assertEquals(ADDRESS_OK, frontend.sniffer_info(buildSnifferInfoRequest(NAME_OK)).getNameAddress());
	}
	
	@Test
    public void nameBlankTest() {
		assertEquals(INVALID_ARGUMENT.getCode(),
            assertThrows(StatusRuntimeException.class, () -> frontend.sniffer_info(buildSnifferInfoRequest(NAME_NULL))).getStatus().getCode());
	}
	@Test
    public void diffNameTest() {
        assertEquals(NAME_DOESNT_EXIST, frontend.sniffer_info(buildSnifferInfoRequest(DIFF_NAME)).getNameAddress());
	}

}
