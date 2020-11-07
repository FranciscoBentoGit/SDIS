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

    public synchronized float individual_infection_probability(int id){
        Iterator iter = obsList.iterator();
        int foundID = 0;
        ArrayList<ObservationsData> matchedSniffers = new ArrayList<ObservationsData>();
        int xValue ;
        float probability;

        //Verifica se o id se encontra na lista de observaçoes
        while (iter.hasNext()){
            ObservationsData obs = (ObservationsData)iter.next();
            if ((obs.getId() == id) && (obs.getInfection().equals("nao-infetado"))){
                foundID = 1;
                matchedSniffers.add(obs);
            }
            if ((obs.getId() == id) && (obs.getInfection().equals("infetado"))){
                return 1;
            }
        }
        //Se não encontra o ID, retornamos 2.00 para que posteriormente possamos verificar o valor e lançar o erro de ID nao encontrado
        if(foundID == 0){
            return 0;
        }

        Iterator iter2 = obsList.iterator();
        while (iter2.hasNext()){
            ObservationsData obs = (ObservationsData)iter2.next();
            Iterator iter3 = matchedSniffers.iterator();

            while (iter3.hasNext()){
                ObservationsData toCompare = (ObservationsData)iter3.next();

                if ((toCompare.getSnifferName().equals(obs.getSnifferName())) && (toCompare.getId() != obs.getId())  && obs.getInfection().equals("infetado")){
                    continue;
                }
            }

        }

        return 0;

    }

    public synchronized String ctrl_init(String snifferName, String address, String infection, long id, com.google.protobuf.Timestamp timeIn, com.google.protobuf.Timestamp timeOut) {
    	String message;
    	message = sniffer_join(snifferName,address);
    	if (message.equals("Failed to join sniffer: invalid address for that name.")) {
    		return "Init: failed to join sniffer_info.";
    	}
    	message = report(snifferName,infection,id,timeIn,timeOut);
    	if (message.equals("Failed to report: invalid name.")) {
    		return "Init: failed to report.";
    	}
    	return "Init success.";
    }

    public synchronized String ctrl_clear() {
    	snifferHash.clear();
    	obsList.removeAll(obsList);
    	return "All observations removed successfully.";
    }

}
