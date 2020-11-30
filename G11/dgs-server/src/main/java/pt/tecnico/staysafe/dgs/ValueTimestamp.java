package pt.tecnico.staysafe.dgs;

import java.util.*;

import java.util.concurrent.CopyOnWriteArrayList;


public class ValueTimestamp {
    private CopyOnWriteArrayList<Long> _vecList = new CopyOnWriteArrayList<Long> ();

    public ValueTimestamp(){
        _vecList = new CopyOnWriteArrayList<Long>();
        _vecList.add(0,(long) 1);
        _vecList.add(1,(long) 0);
    }

    public CopyOnWriteArrayList<Long> getVecList(){
        return _vecList;
    }

    public void updateSequenceNumber(int replicaId, long SN){
        _vecList.set(replicaId-1,SN);
    }

    public long getReplicaSN(int replicaId){
        return _vecList.get(replicaId-1);
    }
}