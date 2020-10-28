package pt.tecnico.staysafe.dgs;


import java.util.*; 
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.google.protobuf.Timestamp;
import com.google.protobuf.util.Timestamps;
import java.text.ParseException;

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

    public synchronized String report(String name, String observations, com.google.protobuf.Timestamp time) throws ParseException {
        if (!(snifferHash.contains(name))) {
            return "Failed to report: invalid name.";
        }

        String[] observationsSplitted = observations.split(",",4);

        if (observationsSplitted.length != 4) {
            return "Failed to report: invalid observation input.";
        }

        String state = observationsSplitted[0];
        int id = Integer.parseInt(observationsSplitted[1]);

        String[] date1Parse = observationsSplitted[2].split(" ",2);
        String[] date2Parse = observationsSplitted[3].split(" ",2);

        String date1InParse = date1Parse[0];
        String time1InParse = date1Parse[1];

        String date2OutParse = date2Parse[0];
        String time2OutParse = date2Parse[1];

        String timestampInTz = date1InParse + "T" + time1InParse + "Z";
        String timestampOutTz = date2OutParse + "T" + time2OutParse + "Z";

        Timestamp tIn, tOut;

        try {
        	tIn = com.google.protobuf.util.Timestamps.parse(timestampInTz);
        	tOut = com.google.protobuf.util.Timestamps.parse(timestampOutTz);
        } catch (ParseException e) {
        	e.printStackTrace();
        }
        

        ObservationsData data = new ObservationsData(name,state,id,tIn,tOut,time);

        obsList.add(data);

        return "Success to report.";
    }

    /*public synchronized float individual_infection_probability(int id){
        Iterator iter = obsList.iterator();
        int foundID = 0;
        ArrayList<ObservationsData> matchedSniffers = new ArrayList<ObservationsData>();
        int xValue ;
        float probability;

        //Verifica se o id se encontra na lista de observaçoes
        while (iter.hasNext()){
            ObservationsData obs = (ObservationsData)iter.next();
            if ((obs.id== id)){
                foundID = 1;
            }
        }
        //Se não encontra o ID, retornamos 2.00 para que posteriormente possamos verificar o valor e lançar o erro de ID nao encontrado
        if(foundID == 0){
            return 2.00;
        }
        Iterator iter = obsList.iterator();
        while (iter.hasNext()){
            ObservationsData obs = (ObservationsData)iter.next();
            if ((obs.id == id) && (obs.state.equals("nao-infetado")) ){
                matchedSniffers.add(obs);
            }
        }
        //A unica razão pela qual ele encontra sniffers mas não adiciona à lista matchedSniffers é o facto de ele estar já infetado
        if(matchedSniffers.length() == 0){
            return 1.00;
        }
        Iterator iter2 = obsList.iterator();
        while (iter2.hasNext()){
            ObservationsData obs = (ObservationsData)iter2.next();
            Iterator iter3 = matchedSniffers.iterator();
            while (iter3.hasNext()){
                ObservationsData toCompare = (ObservationsData)iter3.next();
                if ((toCompare.snifferName.equals(obs.snifferName)) && (toCompare.id != obs.id)  && obs.state.equals("infetado")){*/
}
