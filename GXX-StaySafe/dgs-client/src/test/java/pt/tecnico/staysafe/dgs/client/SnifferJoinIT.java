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
    private static final String NAME_NULL = "";
	private static final String NAME_ALPHA = "sniffer";
	private static final String NAME_SIZE = "sni";

	private static final String ADDRESS_OK = "Rua Alves Redol, 9, Piso 0, 1000-029 Lisboa";
	private static final String ADDRESS_NULL = "";
    private static final String ADDRESS_ERROR = "Rua Alves Redol, 9, Piso 0, 1000-029 Porto";
	
	private static final String SUCCESS = "Success to join sniffer.";

	private static final String ADDRESS_NOTCORRESPONDING = "Failed to join sniffer: invalid address for that name.";
	
	
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
    public void snifferJoinOKTest() {
        assertEquals(SUCCESS, frontend.sniffer_join(buildSnifferJoinRequest(NAME_OK,ADDRESS_OK)).getSuccess());
    }

    @Test
    public void nameBlankTest() {
		assertEquals(INVALID_ARGUMENT.getCode(),
            assertThrows(StatusRuntimeException.class, () -> frontend.sniffer_join(buildSnifferJoinRequest(NAME_NULL,ADDRESS_OK))).getStatus().getCode());
	}
	
	@Test
    public void nameTooSmallTest() {
		assertEquals(INVALID_ARGUMENT.getCode(),
            assertThrows(StatusRuntimeException.class, () -> frontend.sniffer_join(buildSnifferJoinRequest(NAME_SIZE,ADDRESS_OK))).getStatus().getCode());
	}
	
	@Test
    public void nameNotAlphaTest() {
		assertEquals(INVALID_ARGUMENT.getCode(),
            assertThrows(StatusRuntimeException.class, () -> frontend.sniffer_join(buildSnifferJoinRequest(NAME_ALPHA,ADDRESS_OK))).getStatus().getCode());
	}
	
	
	@Test
    public void addressNullTest() {
		assertEquals(INVALID_ARGUMENT.getCode(),
            assertThrows(StatusRuntimeException.class, () -> frontend.sniffer_join(buildSnifferJoinRequest(NAME_OK,ADDRESS_NULL))).getStatus().getCode());
	}
	
	@Test
    public void wrongAdressTest() {
		assertEquals(ADDRESS_NOTCORRESPONDING, frontend.sniffer_join(buildSnifferJoinRequest(NAME_OK,ADDRESS_ERROR)).getSuccess());
	}
	
}