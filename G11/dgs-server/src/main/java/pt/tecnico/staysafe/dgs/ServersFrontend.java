package pt.tecnico.staysafe.dgs;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import com.google.protobuf.Timestamp;

import pt.tecnico.staysafe.dgs.grpc.DgsGrpc;
import pt.tecnico.staysafe.dgs.grpc.*;
import pt.ulisboa.tecnico.sdis.zk.*;

public class ServersFrontend {
	private DgsGrpc.DgsBlockingStub _stub;

	public ServersFrontend(ZKNaming zkNaming, String path) {
		try {
			// lookup
			ZKRecord record = zkNaming.lookup(path);
			String target = record.getURI();
			
			final ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
			
			DgsGrpc.DgsBlockingStub stub = DgsGrpc.newBlockingStub(channel);
			_stub = stub;
		} catch (ZKNamingException e) {
			System.out.println("Caught exception with description: " + e.getMessage());
		}
	}

    public PingResponse ctrl_ping(PingRequest request) {
		PingResponse response;
		response = _stub.ctrlPing(request);
		return response;
	}

    public ClearResponse ctrl_clear(ClearRequest request) {
		ClearResponse response;
		response = _stub.ctrlClear(request);
		return response;
	}
}