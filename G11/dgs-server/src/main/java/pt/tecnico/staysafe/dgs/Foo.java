package pt.tecnico.staysafe.dgs;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import pt.ulisboa.tecnico.sdis.zk.*;

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
	
	public Foo(ZKNaming zkNaming,DgsServiceImpl impl,String path){
		_zkNaming = zkNaming;
		_impl = impl;
		_path = path;
	
	}

	public void tick(){
		try {
			_replicaCollection = _zkNaming.listRecords(parent);
			for (ZKRecord record : _replicaCollection){
				if (!record.getPath().equals(_path)) {
					String[] split = record.getPath().split("/", 5);
					int instance = Integer.parseInt(split[split.length - 1]);

					System.out.printf("Replica %d initiating update exchange...%n", instance);

					ServersFrontend frontend = new ServersFrontend(_zkNaming,record.getURI());
					
					_list = _impl.getList();
					Iterator<Log> it = _list.iterator();
					while (it.hasNext()) {
						Log i = it.next();
						if (i.getType().equals("clear")) {
							frontend.ctrl_clear(instance);
						}
						if (i.getType().equals("report")) {
							frontend.report(i.getReport(),instance);
						}
					}
				}
			}
		} catch (ZKNamingException e) {
			System.out.println("Caught exception with description: " + e.getMessage());
		}

	}

}