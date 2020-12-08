package pt.tecnico.staysafe.dgs;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import pt.ulisboa.tecnico.sdis.zk.*;
import pt.tecnico.staysafe.dgs.grpc.*;

import java.io.IOException;
import io.grpc.StatusRuntimeException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;

public class Foo{
	private ZKNaming _zkNaming;
	private String _path;
	private String _zooHost;
	private String _zooPort;
	private String parent = "/grpc/staysafe/dgs";
	private Collection<ZKRecord> _replicaCollection;
	private CopyOnWriteArrayList<Operation> _list;
	private DgsServiceImpl _impl;
	private long[] _valueTs;
	
	public Foo(ZKNaming zkNaming, DgsServiceImpl impl, String path, String zooHost, String zooPort){
		_zkNaming = zkNaming;
		_impl = impl;
		_path = path;
		_zooHost = zooHost;
		_zooPort = zooPort;
	}

	public void tick(){
		long[] ts = {0,0,0};
		String[] myPath = _path.split("/", 5);
		int myInstance = Integer.parseInt(myPath[myPath.length - 1]);

		//This replica timestamp
		_valueTs = _impl.getValueTs();
		System.out.printf("%n-----------------------------------------------------------------------------%n");
		System.out.printf("Replica %d propagating with TS{%d,%d,%d}%n%n", myInstance, _valueTs[0], _valueTs[1], _valueTs[2]);
		
		try {
			//Gets the list of replica managers of this zookeeper
			_replicaCollection = _zkNaming.listRecords(parent);
			
			//For every record on that list
			for (ZKRecord record : _replicaCollection){
				int flag = 0;
				//If it finds a replica that is not herself
				if (!record.getPath().equals(_path)) {
					String[] split = record.getPath().split("/", 5);
					
					//Other replica timestamp
					int instance = Integer.parseInt(split[split.length - 1]);

					//Creates a communication channel between this replica and the replica selected from the for cicle
					ServersFrontend frontend = new ServersFrontend(_zkNaming,record.getURI());

					try {
						frontend.ctrl_ping(myInstance);
					} catch (StatusRuntimeException e) {
						if (e.getStatus().getDescription().equals("io exception")) {
							int catched = 1;
							while (catched == 1) {
								Random rand = new Random();
								int replicaId = rand.nextInt(3) + 1;
								String aux = String.valueOf(replicaId);
								String path = "/grpc/staysafe/dgs/" + aux;
								ZKRecord record2 = _zkNaming.lookup(path);
								String target = record2.getURI();
								ServersFrontend frontend2 = new ServersFrontend(_zkNaming, target);
								try {
									UnbindRequest request = UnbindRequest.newBuilder().setHost(_zooHost).setPort(_zooPort).setPath(record.getPath()).build();
									frontend2.unbind(request);
									catched = 0;
								} catch (StatusRuntimeException e2) {
									//do nothing
								}
							}
							continue;
						}
					}
					
					//Function to get timestamp from the other replica
					UpdateResponse response = frontend.update();
					
					ts[0] = response.getTs(0);
					ts[1] = response.getTs(1);
					ts[2] = response.getTs(2);
					System.out.printf("Replica %d TS{%d,%d,%d} before update%n", instance, ts[0], ts[1], ts[2]);

					System.out.printf("Replica %d initiating update exchange...%n%n", instance);
					
					//For each operation done in this replica
					_list = _impl.getLogList();
					Iterator<Operation> it = _list.iterator();
					while (it.hasNext()) {
						Operation i = it.next();

						if (i.getType().equals("clear")) {
							//If value of [this replica] column is bigger than the others column
							if(_valueTs[myInstance-1] > ts[myInstance-1]) {
								frontend.ctrl_clear(myInstance);
							}
							
						}

						if (i.getType().equals("join")) {
							//If value of [this replica] column is bigger than the others column
							if(_valueTs[myInstance-1] > ts[myInstance-1]) {
								frontend.sniffer_join(i.getJoin());
							}

						}

						if (i.getType().equals("report")) {
							//If value of [this replica] column is bigger than the others column
							if(_valueTs[myInstance-1] > ts[myInstance-1]) {
								frontend.report(i.getReport());
							}
						}
					}
				}
			}
			
		} catch (ZKNamingException e) {
			System.out.println("Caught exception with description: " + e.getMessage());
		}
	}

}