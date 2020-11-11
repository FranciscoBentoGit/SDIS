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
        float xValue = 0, diff = 0;
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
                        diff = calculateTime(obs.getTimeIn(),toCompare.getTimeOut());
                        xValue = swapValue(xValue,diff);
                        continue;
                    }

                    //Caso 3, interseçao externa
                    else if ( (Timestamps.compare(toCompare.getTimeIn(),obs.getTimeIn()) <= 0) && (Timestamps.compare(obs.getTimeOut(),toCompare.getTimeOut()) <= 0) ){
                        diff = calculateTime(obs.getTimeIn(),obs.getTimeOut());
                        xValue = swapValue(xValue,diff);
                        continue;
                    }

                    else {
                        diff = calculateTime(toCompare.getTimeIn(),obs.getTimeOut());
                        xValue = swapValue(xValue,diff);
                        continue;
                    }

                }
            }

        }
        System.out.printf("%f\n",xValue);
        probability = calculateProbability(xValue);

        return probability;

    }
    public static float calculateTime(com.google.protobuf.Timestamp from,com.google.protobuf.Timestamp to){
        Duration timeBetween = Timestamps.between(from,to);
        long seconds = timeBetween.getSeconds();
        float minutes = seconds / (float)60;
        return minutes;
    }

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

    public synchronized float aggregate_infection_probability(int index, String command){
        ArrayList<Long> nonInfectedId = new ArrayList<Long>();
        float[] responseMean = new float[2];
        float[] responsePercentile = new float[3];
        Iterator iter = obsList.iterator();
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
        while (iter2.hasNext()){
            long id = (long)iter2.next();
            nonInfectedProbabilities.add(individual_infection_probability(id));
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
            return responsePercentile[index];
        }
    }

    public synchronized float calculateSD(ArrayList<Float> nonInfectedProbabilities,float media){
        float standardDeviation=0;
        int size = nonInfectedProbabilities.size();
        for (float prob : nonInfectedProbabilities){
            standardDeviation +=  Math.pow(prob - media,2);
        }
        float aux = (float) standardDeviation;
        return (float) Math.sqrt(aux/size);
    }

    public synchronized String ctrl_init(String snifferName, String infection, long id, com.google.protobuf.Timestamp timeIn, com.google.protobuf.Timestamp timeOut) {
    	String message = report(snifferName,infection,id,timeIn,timeOut);
    	if (message.equals("Failed to report: invalid name.")) {
    		return message;
    	}
    	return "Observation: reported successfully.";
    }

    public synchronized String ctrl_clear() {
    	snifferHash.clear();
    	obsList.removeAll(obsList);
    	return "All observations removed successfully.";
    }

}
