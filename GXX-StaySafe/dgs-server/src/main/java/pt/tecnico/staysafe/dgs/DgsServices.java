package pt.tecnico.staysafe.dgs;

import java.util.*; 
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.google.protobuf.Timestamp;
import com.google.protobuf.util.Timestamps;
import com.google.protobuf.Duration;

import java.lang.Math; 

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

    public synchronized float individual_infection_probability(long id){
        Iterator iter = obsList.iterator();
        int foundID = 0;
        ArrayList<ObservationsData> matchedSniffers = new ArrayList<ObservationsData>();
        int xValue = 0, diff = 0;
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
            return 2;
        }

        Iterator iter2 = obsList.iterator();
        while (iter2.hasNext()){
            ObservationsData obs = (ObservationsData)iter2.next();
            Iterator iter3 = matchedSniffers.iterator();

            while (iter3.hasNext()){
                ObservationsData toCompare = (ObservationsData)iter3.next();

                if ((toCompare.getSnifferName().equals(obs.getSnifferName())) && (toCompare.getId() != obs.getId())  && obs.getInfection().equals("infetado")){
                    //Caso 1, não tem interseção
                    if ((Timestamps.compare(toCompare.getTimeIn(),obs.getTimeIn()) <= 0 ) && (Timestamps.compare(toCompare.getTimeOut(),obs.getTimeIn()) <= 0)){
                        continue;
                    }
                    //Caso 6, não tem interseção
                    else if ( (Timestamps.compare(obs.getTimeOut(),toCompare.getTimeIn()) <= 0 ) && (Timestamps.compare(obs.getTimeOut(),obs.getTimeOut()) <= 0) ){
                        continue;
                    }
                    //Caso 4, interseção interna
                    else if ( (Timestamps.compare(obs.getTimeIn(),toCompare.getTimeIn()) <= 0) && (Timestamps.compare(toCompare.getTimeOut(),obs.getTimeOut()) <= 0) ){
                        diff = calculateTime(toCompare.getTimeIn(),toCompare.getTimeOut()); //passa o tempo do between(duration) para uma int minutos
                        xValue = swapValue(xValue,diff);//funçao que recebe diff e ve se o valor da diff é maior do que o valor que esta na variavel xValue, se sim da update
                        continue;
                    }
                    //Caso 2, interseçao esqueda
                    else if ( (Timestamps.compare(toCompare.getTimeIn(),obs.getTimeIn()) <= 0) && (Timestamps.compare(toCompare.getTimeOut(),obs.getTimeOut()) <= 0) ){
                        System.out.printf("entrei2%n");
                        diff = calculateTime(obs.getTimeIn(),toCompare.getTimeOut());
                        xValue = swapValue(xValue,diff);
                        continue;
                    }

                    //Caso 3, interseçao externa
                    else if ( (Timestamps.compare(toCompare.getTimeIn(),obs.getTimeIn()) <= 0) && (Timestamps.compare(obs.getTimeOut(),toCompare.getTimeOut()) <= 0) ){
                        System.out.printf("entrei3%n");
                        diff = calculateTime(obs.getTimeIn(),obs.getTimeOut());
                        xValue = swapValue(xValue,diff);
                        continue;
                    }

                    else {
                        System.out.printf("entrei4%n");
                        diff = calculateTime(toCompare.getTimeIn(),obs.getTimeOut());
                        xValue = swapValue(xValue,diff);
                        continue;
                    }

                }
            }

        }
        System.out.printf("diff: %d%n", diff);
        System.out.printf("minutos: %d%n", xValue);
        probability = calculateProbability(xValue);

        return probability;

    }
    public static int calculateTime(com.google.protobuf.Timestamp from,com.google.protobuf.Timestamp to){
        Duration timeBetween = Timestamps.between(from,to);
        long seconds = timeBetween.getSeconds();
        int intSeconds = (int) seconds;
        int minutes = intSeconds / 60;
        return minutes;
    }

    public static int swapValue(int xValue,int diff){
        if(diff > xValue){
            return diff;
        }
        return xValue;
    }

    public static float calculateProbability(int xValue){
        double exponencial = Math.exp(15-xValue);
        float floatExponencial = (float) exponencial;
        float probability = (1 / (1 + floatExponencial));
        return probability;
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
