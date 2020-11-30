package pt.tecnico.staysafe.dgs;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import com.google.protobuf.Timestamp;

import pt.tecnico.staysafe.dgs.grpc.DgsGrpc;
import pt.tecnico.staysafe.dgs.grpc.*;
import pt.ulisboa.tecnico.sdis.zk.*;

public class ServersFrontend {
	private DgsGrpc.DgsBlockingStub _stub;
	private ValueTimestamp _valueTimestamp;
	private int _replicaId;
	private String[] _auxSplit;

	public ServersFrontend(ZKNaming zkNaming, String path) {
		try {
			// lookup
			ZKRecord record = zkNaming.lookup(path);
			String target = record.getURI();
			_auxSplit = path.split("/",5);
			_replicaId = Integer.parseInt(_auxSplit[_auxSplit.length - 1]);
			_valueTimestamp = new ValueTimestamp();
			
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
	public void update(long otherSN){
		long thisSN = _valueTimestamp.getReplicaSN(_replicaId);
		if (otherSN > thisSN){
			_valueTimestamp.updateSequenceNumber(_replicaId,otherSN);
			//executa a historia dos logs
			return;
		}else{
			return;
		}
	}

	public long sendUpdate(){
		return _valueTimestamp.getReplicaSN(_replicaId);
	}

	public long  incrementSN(){
		long aux = _valueTimestamp.getReplicaSN(_replicaId)+ ((long) 1);
		_valueTimestamp.updateSequenceNumber(_replicaId, aux);
		return _valueTimestamp.getReplicaSN(_replicaId);
	}
}