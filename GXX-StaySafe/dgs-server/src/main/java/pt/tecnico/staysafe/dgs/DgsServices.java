package pt.tecnico.staysafe.dgs;


import java.util.*; 
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.google.protobuf.Timestamp;

import pt.tecnico.staysafe.dgs.ObservationsData;

public class DgsServices {
	private ConcurrentHashMap<String, String> snifferHash = new ConcurrentHashMap<String, String>();
	private CopyOnWriteArrayList<ObservationsData> obsList= new CopyOnWriteArrayList<ObservationsData>();

	public synchronized String sniffer_join(String name, String address) {
		if (snifferHash.containsKey(name)) {
			if (!(snifferHash.get(name).equals(address))) {
				return "Failed to join sniffer: invalid address for that name.";
			} else {
				snifferHash.put(name, address);
				return "Success to join sniffer.";
			}
		}
		
		snifferHash.put(name, address);
		return "Success to join sniffer.";
	}

	public synchronized String sniffer_info(String name) {
		if (!(snifferHash.containsKey(name))) { 
			return "Failed to find the address: name does not exist.";
		}

		String address = snifferHash.get(name);
		return address;
	}

    public synchronized String report(String name, String infection, long id, com.google.protobuf.Timestamp timeIn, com.google.protobuf.Timestamp timeOut) {
        if (!(snifferHash.containsKey(name))) {
            return "Failed to report: invalid name.";
        }

        long millis = System.currentTimeMillis();
		Timestamp timestamp = Timestamp.newBuilder().setSeconds(millis/1000).build();
        
        ObservationsData data = new ObservationsData(name,infection,id,timeIn,timeOut,timestamp);

        obsList.add(data);

        return "Success to report.";
    }

}
