package pt.tecnico.staysafe.dgs.client;

import java.util.*;

import java.util.concurrent.CopyOnWriteArrayList;


public class PreviousTimestamp {
    //In case of being an update , it should add the update as(replica,sequence number)
    private CopyOnWriteArrayList<Long> _vecList;

    public PreviousTimestamp(){
        _vecList = new CopyOnWriteArrayList<Long>();
    }

    public CopyOnWriteArrayList<Long> getVecList(){
        return _vecList;
    }

    public void addToVecList(int replicaId, long SN){
        _vecList.add(replicaId,SN);
    }

    public long getReplicaSN(int replicaId){
        return _vecList.get(replicaId);
    }
}