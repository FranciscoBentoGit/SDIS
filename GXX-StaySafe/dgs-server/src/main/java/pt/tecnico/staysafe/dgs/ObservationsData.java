package pt.tecnico.staysafe.dgs;

import java.util.*;
import com.google.protobuf.Timestamp;
import com.google.protobuf.util.Timestamps;

public class ObservationsData{
    private String _snifferName;
    private String _state;
    private int _id;
    private String _dateIn;
    private String _dateOut;
    private com.google.protobuf.Timestamp _timestamp;

    ObservationsData(String sniffer_name, String state, int id, String dateIn, String dateOut, com.google.protobuf.Timestamp timestamp){
        _snifferName = sniffer_name;
        _state = state;
        _id = id;
        _dateIn = dateIn;
        _dateOut = dateOut;
        _timestamp = timestamp;
    } 
}