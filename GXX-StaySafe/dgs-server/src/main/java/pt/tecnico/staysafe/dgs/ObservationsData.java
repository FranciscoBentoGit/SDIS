package pt.tecnico.staysafe.dgs;

import java.util.*;
import com.google.protobuf.Timestamp;

public class ObservationsData{
    private String _snifferName;
    private String _infection;
    private long _id;
    private com.google.protobuf.Timestamp _timeIn;
    private com.google.protobuf.Timestamp _timeOut;
    private com.google.protobuf.Timestamp _timestamp;

    ObservationsData(String sniffer_name, String infection, long id, com.google.protobuf.Timestamp timeIn, com.google.protobuf.Timestamp timeOut, com.google.protobuf.Timestamp timestamp){
        _snifferName = sniffer_name;
        _infection = infection;
        _id = id;
        _timeIn = timeIn;
        _timeOut = timeOut;
        _timestamp = timestamp;
    } 
}