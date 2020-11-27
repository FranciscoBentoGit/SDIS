package pt.tecnico.staysafe.dgs;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import java.lang.Math; 



public class VectorialTimestamp {
    //replicaTimestamps(replicaId,timestamp)
    private final Map<Long,Long> replicaTimestamps = new ConcurrentHashMap<>(); 

    public VectorialTimestamp(){

    }
    //Copies all of the mappings from the specified map to this map
    public VectorialTimestamp(VectorialTimestamp from){
        replicaTimestamps.putAll(from.replicaTimestamps);
    }

    public Long getTimestampSpecificReplica(long replicaId){
        return replicaTimestamps.get(replicaId);

    }

    public void setTimestampSpecificReplica(long replicaId,long timestamp){
        replicaTimestamps.put(replicaId,timestamp);
    }

    public void merge(VectorialTimestamp otherVectorialTimestamp){
        for(Entry<long,long> entry : otherVectorialTimestamp.replicaTimestamps.entrySet()){
            long replicaId = entry.getKey();
            long mergingTimestamp = entry.getValue();
            long localTimestamp = replicaTimestamps.containsKey(replicaId) ? replicaTimestamps.get(replicaId) : Long.MIN_VALUE;
            replicaTimestamps.put(replicaId,Math.max(localTimestamp,mergingTimestamp));
        }
    }

    public boolean isAfter(VectorialTimestamp otherVectorialTimestamp){
        boolean greaterThan = false;
        for(Entry<long,long> otherEntry : otherVectorialTimestamp.replicaTimestamps.entrySet()){
            long replicaId = otherEntry.getKey();
            long otherReplicaTimestamp = otherEntry.getValue();
            long localReplicaTimestamp = this.getTimestampSpecificReplica(replicaId);
            
            if (localReplicaTimestamp == null || localReplicaTimestamp < otherReplicaTimestamp){
                return false;
            }else{
                greaterThan = true;
            }
        }

        //there is atleast one local timestamp greater or local has additional timestamps comparing the other
        return greaterThan || otherVectorialTimestamp.replicaTimestamps.size() < replicaTimestamps.size();

    }

    public boolean isEmpty(){
        return this.replicaTimestamps.isEmpty();
    }

    public Set<Entry<long,long>> entrySet(){
        return replicaTimestamps.entrySet();
    }

    //fica por fazer a readData e writeData

    @Override
    public boolean equals(Object o){
        if (this == 0){
            return true;
        }
        VectorialTimestamp other = (VectorialTimestamp) o;

        return replicaTimestamps.equals(other.replicaTimestamps);
    }

    @Override
    public String toString(){
        return replicaTimestamps.toString();

    }
}