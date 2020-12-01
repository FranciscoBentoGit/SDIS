package pt.tecnico.staysafe.dgs;

import java.util.*; 
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.lang.Math; 

import com.google.protobuf.Timestamp;
import com.google.protobuf.util.Timestamps;
import com.google.protobuf.Duration;

import pt.tecnico.staysafe.dgs.ObservationsData;

public class DgsServices {
	private ConcurrentHashMap<String, String> snifferHash = new ConcurrentHashMap<String, String>();
	private CopyOnWriteArrayList<ObservationsData> obsList= new CopyOnWriteArrayList<ObservationsData>();
    private long[] _valueTs = {0,0};

	public synchronized String sniffer_join(String name, String address, int replicaId) {
		if (snifferHash.containsKey(name)) {
			if (!(snifferHash.get(name).equals(address))) {
				return "Failed to join sniffer: invalid address for that name." + " - " + String.valueOf(_valueTs[0]) + " - " + String.valueOf(_valueTs[0]) + " - ";
			} else {
				snifferHash.put(name, address);
                _valueTs[replicaId - 1]++;
				return "Success to join sniffer." + " - " + String.valueOf(_valueTs[0]) + " - " + String.valueOf(_valueTs[1]) + " - ";
			}
		}
		
		snifferHash.put(name, address);
        _valueTs[replicaId - 1]++;
		return "Success to join sniffer." + " - " + String.valueOf(_valueTs[0]) + " - " + String.valueOf(_valueTs[1]) + " - ";
	}

	public synchronized String sniffer_info(String name, int replicaId) {
		if (!(snifferHash.containsKey(name))) { 
			return "Failed to find the address: name does not exist." + " - " + String.valueOf(_valueTs[0]) + " - " + String.valueOf(_valueTs[1]) + " - ";
		}

		String address = snifferHash.get(name);
		return address + " - " + String.valueOf(_valueTs[0]) + " - " + String.valueOf(_valueTs[1]) + " - ";
	}

    public synchronized String report(String name, String infection, long id, com.google.protobuf.Timestamp timeIn, com.google.protobuf.Timestamp timeOut, int replicaId) {
        if (!(snifferHash.containsKey(name))) {
            return "Failed to report: invalid name." + " - " + String.valueOf(_valueTs[0]) + " - " + String.valueOf(_valueTs[1]) + " - ";
        }

        long millis = System.currentTimeMillis();
		Timestamp timestamp = Timestamp.newBuilder().setSeconds(millis/1000).build();
        
        ObservationsData data = new ObservationsData(name,infection,id,timeIn,timeOut,timestamp);

        obsList.add(data);
        _valueTs[replicaId - 1]++;
        return "Success to report." + " - " + String.valueOf(_valueTs[0]) + " - " + String.valueOf(_valueTs[1]) + " - ";
    }

    public synchronized float individual_infection_probability(int index, long id, int replicaId){
        Iterator iter = obsList.iterator();
        int foundID = 0;
        ArrayList<ObservationsData> matchedSniffers = new ArrayList<ObservationsData>();
        float xValue = 0, diff = 0;
        float[] probability = new float[3];

        if (index == 1) {
            probability[1] = _valueTs[0];
            return probability[1];
        }

        if (index == 2) {
            probability[2] = _valueTs[1];
            return probability[2];
        }

        //Verify if is within the observations list
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
        //If it doesn't find the id, it returns 2 so we can catch the error later
        if(foundID == 0){
            return 2;
        }

        Iterator iter2 = obsList.iterator();
        while (iter2.hasNext()){
            //For every observation inside obsList(total list)
            ObservationsData obs = (ObservationsData)iter2.next();
            Iterator iter3 = matchedSniffers.iterator();

            while (iter3.hasNext()){
                //For every observation inside matchedSniffer(observations that contain the id given by argument)
                ObservationsData toCompare = (ObservationsData)iter3.next();

                if ((toCompare.getSnifferName().equals(obs.getSnifferName())) && (toCompare.getId() != obs.getId())  && obs.getInfection().equals("infetado")){
                    
                    //Case 1, no intersection
                    if ((Timestamps.compare(toCompare.getTimeIn(),obs.getTimeIn()) <= 0 ) && (Timestamps.compare(toCompare.getTimeOut(),obs.getTimeIn()) <= 0)){
                        continue;
                    }
                    
                    //Case 6, no intersection
                    else if ( (Timestamps.compare(obs.getTimeOut(),toCompare.getTimeIn()) <= 0 ) && (Timestamps.compare(obs.getTimeOut(),obs.getTimeOut()) <= 0) ){
                        continue;
                    }
                    
                    //Case 4, internal intersection
                    else if ( (Timestamps.compare(obs.getTimeIn(),toCompare.getTimeIn()) <= 0) && (Timestamps.compare(toCompare.getTimeOut(),obs.getTimeOut()) <= 0) ){
                        diff = calculateTime(toCompare.getTimeIn(),toCompare.getTimeOut()); 
                        xValue = swapValue(xValue,diff);
                        continue;
                    }
                    
                    //Case 2, left intersection
                    else if ( (Timestamps.compare(toCompare.getTimeIn(),obs.getTimeIn()) <= 0) && (Timestamps.compare(toCompare.getTimeOut(),obs.getTimeOut()) <= 0) ){
                        diff = calculateTime(obs.getTimeIn(),toCompare.getTimeOut());
                        xValue = swapValue(xValue,diff);
                        continue;
                    }

                    //Case 3, external intersection
                    else if ( (Timestamps.compare(toCompare.getTimeIn(),obs.getTimeIn()) <= 0) && (Timestamps.compare(obs.getTimeOut(),toCompare.getTimeOut()) <= 0) ){
                        diff = calculateTime(obs.getTimeIn(),obs.getTimeOut());
                        xValue = swapValue(xValue,diff);
                        continue;
                    }
                    
                    //Case 5, right intersection
                    else {
                        diff = calculateTime(toCompare.getTimeIn(),obs.getTimeOut());
                        xValue = swapValue(xValue,diff);
                        continue;
                    }

                }
            }

        }
        probability[0] = calculateProbability(xValue);

        return probability[0];

    }
    
    //Function that returns the time of intersection in minutes
    public static float calculateTime(com.google.protobuf.Timestamp from,com.google.protobuf.Timestamp to){
        Duration timeBetween = Timestamps.between(from,to);
        long seconds = timeBetween.getSeconds();
        float minutes = seconds / (float)60;
        return minutes;
    }
    
    //Function that keeps on updating the biggest xValue for every non-infected citizen
    public static float swapValue(float xValue,float diff){
        if(diff > xValue){
            return diff;
        }
        return xValue;
    }

    public static float calculateProbability(float xValue){
        double exponencial = Math.exp(15-xValue);
        float floatExponencial = (float) exponencial;
        float probability = (1 / (1 + floatExponencial));
        return probability;
    }

    public synchronized float aggregate_infection_probability(int index, String command, int replicaId){
        ArrayList<Long> nonInfectedId = new ArrayList<Long>();
        float[] responseMean = new float[4];
        float[] responsePercentile = new float[5];
        Iterator iter = obsList.iterator();
        
        //Lists the non-infected citizen id's
        while (iter.hasNext()){
            ObservationsData obs = (ObservationsData)iter.next();
            if (obs.getInfection().equals("nao-infetado")){
                if(!(nonInfectedId.contains(obs.getId()))){
                    nonInfectedId.add(obs.getId());
                }
            }
        }
        ArrayList<Float> nonInfectedProbabilities = new ArrayList<Float>();
        Iterator iter2 = nonInfectedId.iterator();
        
        //For every id, it calculates the probability and lists that probability into nonInfectedProbabilities
        while (iter2.hasNext()){
            long id = (long)iter2.next();
            nonInfectedProbabilities.add(individual_infection_probability(0,id,replicaId));
        }
        nonInfectedProbabilities.sort(Comparator.naturalOrder());
        if (command.equals("mean_dev")) {
            float media = 0 , desvio_padrao;
            for (int i=0; i < nonInfectedProbabilities.size();i++){
                media += nonInfectedProbabilities.get(i);
            }
            media = media / nonInfectedProbabilities.size();
            desvio_padrao = calculateSD(nonInfectedProbabilities,media);
            
            responseMean[0] = media;
            responseMean[1] = desvio_padrao;
            responseMean[2] = _valueTs[0];
            responseMean[3] = _valueTs[1];
            return responseMean[index];

        } else {
            float mediana,q1,q3;
            
            if((nonInfectedProbabilities.size()%2)==0){
                //Lista com tamanho par
                mediana = nonInfectedProbabilities.get(nonInfectedProbabilities.size()/2) + nonInfectedProbabilities.get((nonInfectedProbabilities.size()/2) -1);
                mediana = mediana / 2;
            }else{
                //Lista com tamanho impar
                int aux = (int) ((nonInfectedProbabilities.size()/2)-0.5);
                mediana=nonInfectedProbabilities.get(aux);
            }
            if((nonInfectedProbabilities.size()%4) == 0){
                q1 = (nonInfectedProbabilities.get((int)(nonInfectedProbabilities.size()*0.25) -1) + nonInfectedProbabilities.get((int) (nonInfectedProbabilities.size()*0.25)))/2;
                q3 = (nonInfectedProbabilities.get((int)(nonInfectedProbabilities.size()*0.75) - 1) + nonInfectedProbabilities.get((int) (nonInfectedProbabilities.size() *0.75 )) ) /2;
            }else{
                q1 = nonInfectedProbabilities.get( (int)Math.floor(nonInfectedProbabilities.size()*0.25));
                q3 = nonInfectedProbabilities.get( (int)(Math.floor(nonInfectedProbabilities.size()*0.75)));

            }

            responsePercentile[0] = mediana;
            responsePercentile[1] = q1;
            responsePercentile[2] = q3;
            responsePercentile[3] = _valueTs[0];
            responsePercentile[4] = _valueTs[1];
            return responsePercentile[index];
        }
    }

    //It calcultes the standart deviation for a certain list of probabilites and their median
    public synchronized float calculateSD(ArrayList<Float> nonInfectedProbabilities,float media){
        float standardDeviation=0;
        int size = nonInfectedProbabilities.size();
        for (float prob : nonInfectedProbabilities){
            standardDeviation +=  Math.pow(prob - media,2);
        }
        float aux = (float) standardDeviation;
        return (float) Math.sqrt(aux/size);
    }

    public synchronized String ctrl_init(String snifferName, String infection, long id, com.google.protobuf.Timestamp timeIn, com.google.protobuf.Timestamp timeOut, int replicaId) {
    	String message = report(snifferName,infection,id,timeIn,timeOut, replicaId);
    	if (message.equals("Failed to report: invalid name.")) {
    		return message;
    	}
    	return "Observation: reported successfully.";
    }

    public synchronized String ctrl_clear(int replicaId) {
    	snifferHash.clear();
    	obsList.removeAll(obsList);
        _valueTs[replicaId - 1]++;
    	return "All observations removed successfully." + " - " + String.valueOf(_valueTs[0]) + " - " + String.valueOf(_valueTs[1]) + " - ";
    }

    public synchronized String ctrl_ping(String input, int replicaId) {
    	String output = "Hello " + input + "!";
    	return output + " - " + String.valueOf(_valueTs[0]) + " - " + String.valueOf(_valueTs[1]) + " - ";
    }

}
