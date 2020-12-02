package pt.tecnico.staysafe.dgs;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import com.google.protobuf.Timestamp;

import pt.tecnico.staysafe.dgs.grpc.DgsGrpc;
import pt.tecnico.staysafe.dgs.grpc.*;
import pt.ulisboa.tecnico.sdis.zk.*;

public class ServersFrontend {
	private DgsGrpc.DgsBlockingStub _stub;
	private int _replicaNumber;

	public ServersFrontend(ZKNaming zkNaming, String path, int id) {
		try {
			_replicaNumber = id;
			
			// lookup
			ZKRecord record = zkNaming.lookup(path);
			String target = record.getURI();
			
			final ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();		
			_stub = DgsGrpc.newBlockingStub(channel);

		} catch (ZKNamingException e) {
			System.out.println("Caught exception with description: " + e.getMessage());
		}
	}

    /*public PingResponse ctrl_ping(int replicaId) {
		PingResponse response;
		PingRequest request = PingRequest.newBuilder().setText("friend").setReplicaId(replicaId).build();
		response = _stub.ctrlPing(request);
		return response;
	}

    public ClearResponse ctrl_clear(int replicaId) {
		ClearResponse response;
		ClearRequest request = ClearRequest.newBuilder().setReplicaId(replicaId).build();
		response = _stub.ctrlClear(request);
		return response;
	}*/
}