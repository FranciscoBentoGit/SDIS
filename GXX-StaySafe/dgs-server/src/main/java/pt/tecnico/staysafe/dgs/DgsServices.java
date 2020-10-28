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
		if (snifferHash.contains(name)) {
			if (snifferHash.get(name) != address) {
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
		if (!(snifferHash.contains(name)) || (name.equals(""))) { 
			return "Failed to find the address: name does not exist.";
		}

		String address = snifferHash.get(name);
		return address;
	}

    public synchronized String report(String name, String observations, com.google.protobuf.Timestamp time) {
        if (!(snifferHash.contains(name))) {
            return "Failed to report: invalid name.";
        }

        String[] observationsSplited = observations.split(",",4);

        if (observationsSplited.length != 4) {
            return "Failed to report: invalid observation input.";
        }

        String state = observationsSplited[0];
        int id = Integer.parseInt(observationsSplited[1]);

        String tIn = observationsSplited[2]; 
        String tOut = observationsSplited[3];
        
        ObservationsData data = new ObservationsData(name,state,id,tIn,tOut,time);

        obsList.add(data);

        return "Success to report.";
    }

}
