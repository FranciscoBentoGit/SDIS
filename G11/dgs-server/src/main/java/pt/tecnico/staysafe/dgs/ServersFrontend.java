package pt.tecnico.staysafe.dgs;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import com.google.protobuf.Timestamp;

import pt.tecnico.staysafe.dgs.grpc.DgsGrpc;
import pt.tecnico.staysafe.dgs.grpc.*;
import pt.ulisboa.tecnico.sdis.zk.*;

import java.util.concurrent.CopyOnWriteArrayList;

public class ServersFrontend {
    private DgsGrpc.DgsBlockingStub _stub;
    private int _replicaNumber;

    public ServersFrontend(ZKNaming zkNaming, String URI) {
        String target = URI;

        final ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
        _stub = DgsGrpc.newBlockingStub(channel);

    }

    public UpdateResponse update() {
		UpdateResponse response;
        UpdateRequest request = UpdateRequest.getDefaultInstance();;
		response = _stub.update(request);
		return response;
	}

    public SnifferJoinResponse sniffer_join(SnifferJoinRequest request) {
		SnifferJoinResponse response;
		response = _stub.snifferJoin(request);
		return response;
	}

    public ReportResponse report(ReportRequest request) {
        ReportResponse response;
        response = _stub.report(request);
        return response;
    }

    public ClearResponse ctrl_clear(int replicaId) {
        ClearResponse response;
        ClearRequest request = ClearRequest.newBuilder().setReplicaId(replicaId).build();
        response = _stub.ctrlClear(request);
        return response;
    }
}