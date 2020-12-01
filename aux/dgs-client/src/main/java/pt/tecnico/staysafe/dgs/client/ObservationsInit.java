package pt.tecnico.staysafe.dgs.client;

import java.util.*;
import com.google.protobuf.Timestamp;

public class ObservationsInit{
    private String _snifferName;
    private String _infection;
    private long _id;
    private com.google.protobuf.Timestamp _timeIn;
    private com.google.protobuf.Timestamp _timeOut;

    ObservationsInit(String sniffer_name, String infection, long id, com.google.protobuf.Timestamp timeIn, com.google.protobuf.Timestamp timeOut){
        _snifferName = sniffer_name;
        _infection = infection;
        _id = id;
        _timeIn = timeIn;
        _timeOut = timeOut;
    }

    public String getSnifferName() {
        return _snifferName;
    }

    public String getInfection() {
        return _infection;
    }

    public long getId() {
        return _id;
    }

    public com.google.protobuf.Timestamp getTimeIn() {
        return _timeIn;
    }

    public com.google.protobuf.Timestamp getTimeOut() {
        return _timeOut;
    }
}