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
	private String parent = "/grpc/staysafe/dgs";
	private Collection<ZKRecord> _replicaCollection;
	private CopyOnWriteArrayList<Operation> _list;
	private DgsServiceImpl _impl;
	private long[] _valueTs;
	
	public Foo(ZKNaming zkNaming, DgsServiceImpl impl, String path){
		_zkNaming = zkNaming;
		_impl = impl;
		_path = path;
	}

	public void tick(){
		long[] ts = {0,0,0};

		//This replica timestamp
		_valueTs = _impl.getValueTs();
		System.out.printf("nossa 0 - %d%n", _valueTs[0]);
		System.out.printf("nossa 1 - %d%n", _valueTs[1]);
		System.out.printf("nossa 2 - %d%n", _valueTs[2]);
		
		String[] myPath = _path.split("/", 5);
		int myInstance = Integer.parseInt(myPath[myPath.length - 1]);
		try {
			//Gets the list of replica managers of this zookeeper
			_replicaCollection = _zkNaming.listRecords(parent);
			
			//For every record on that list
			for (ZKRecord record : _replicaCollection){
				
				//If it finds a replica that is not herself
				if (!record.getPath().equals(_path)) {
					String[] split = record.getPath().split("/", 5);
					
					//Other replica timestamp
					int instance = Integer.parseInt(split[split.length - 1]);

					System.out.printf("Replica %d initiating update exchange...%n", instance);

					//Creates a communication channel between this replica and the replica selected from the for cicle
					ServersFrontend frontend = new ServersFrontend(_zkNaming,record.getURI());

					//Function to get timestamp from the other replica
					UpdateResponse response = frontend.update();
					
					ts[0] = response.getTs(0);
					ts[1] = response.getTs(1);
					ts[2] = response.getTs(2);
					System.out.printf("contacto 0 - %d%n", ts[0]);
					System.out.printf("contacto 1 - %d%n", ts[1]);
					System.out.printf("contacto 2 - %d%n", ts[2]);
					
					//For each operation done in this replica
					_list = _impl.getLogList();
					Iterator<Operation> it = _list.iterator();
					while (it.hasNext()) {
						Operation i = it.next();

						if (i.getType().equals("clear")) {
							//If value of [this replica] column is bigger than the others column
							if(_valueTs[myInstance-1] > ts[myInstance-1]) {
								//If the operation identifier is bigger than the latest version of the other replica
								if (i.getIdentifier() > ts[myInstance-1]) {
									frontend.ctrl_clear(myInstance);
								}
							}
							
						}

						if (i.getType().equals("join")) {
							//If value of [this replica] column is bigger than the others column
							if(_valueTs[myInstance-1] > ts[myInstance-1]) {
								//If the operation identifier is bigger than the latest version of the other replica
								if (i.getIdentifier() > ts[myInstance-1]) {
									frontend.sniffer_join(i.getJoin());
								}
							}

						}

						if (i.getType().equals("report")) {
							//If value of [this replica] column is bigger than the others column
							if(_valueTs[myInstance-1] > ts[myInstance-1]) {
								//If the operation identifier is bigger than the latest version of the other replica
								if (i.getIdentifier() > ts[myInstance-1]) {
									frontend.report(i.getReport());
								}
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