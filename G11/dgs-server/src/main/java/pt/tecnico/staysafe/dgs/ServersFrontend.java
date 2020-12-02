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

			//Collection<ZKRecord> collection = listRecords​("grpc/staysafe/dgs");
			//System.out.printf();
			
			// lookup
			ZKRecord record = zkNaming.lookup(path);
			String target = record.getURI();
			
			final ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();		
			_stub = DgsGrpc.newBlockingStub(channel);

		} catch (ZKNamingException e) {
			System.out.println("Caught exception with description: " + e.getMessage());
		}
	}

}