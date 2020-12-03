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
	private CopyOnWriteArrayList<Log> _list;
	private DgsServiceImpl _impl;
	private long[] _valueTs;
	
	public Foo(ZKNaming zkNaming, DgsServiceImpl impl, String path){
		_zkNaming = zkNaming;
		_impl = impl;
		_path = path;
	}

	public void tick(){
		long[] ts = {0,0,0};
		try {
			_replicaCollection = _zkNaming.listRecords(parent);
			for (ZKRecord record : _replicaCollection){
				if (!record.getPath().equals(_path)) {
					String[] split = record.getPath().split("/", 5);
					int instance = Integer.parseInt(split[split.length - 1]);

					System.out.printf("Replica %d initiating update exchange...%n", instance);

					ServersFrontend frontend = new ServersFrontend(_zkNaming,record.getURI());
					
					//this replica timestamp
					_valueTs = _impl.getValueTs();
					
					//replica to comunicate timestamp
					UpdateResponse response = frontend.update();
					ts[0] = response.getTs(0);
					ts[1] = response.getTs(1);
					ts[2] = response.getTs(2);

					_list = _impl.getList();
					Iterator<Log> it = _list.iterator();
					while (it.hasNext()) {
						Log i = it.next();

						if (i.getType().equals("clear")) {
							/*if (instance == 1) {
								while (_valueTs[1] != ts[1] && _valueTs[2] != ts[2]) {
									//wait till it gets the same
								}
								frontend.ctrl_clear(instance);		
							}
							if (instance == 2) {
								while (_valueTs[0] != ts[0] && _valueTs[2] != ts[2]) {
									//wait till it gets the same
								}
								frontend.ctrl_clear(instance);		
							}
							if (instance == 3) {
								while (_valueTs[0] != ts[0] && _valueTs[1] != ts[1]) {
									//wait till it gets the same
								}
								frontend.ctrl_clear(instance);		
							}*/
						}
						if (i.getType().equals("join")) {
							frontend.sniffer_join(i.getJoin());
						}
						if (i.getType().equals("report")) {
							frontend.report(i.getReport());
						}
					}
				}
			}
		} catch (ZKNamingException e) {
			System.out.println("Caught exception with description: " + e.getMessage());
		}

	}

}