package pt.tecnico.staysafe.dgs;

import java.util.*;
import pt.tecnico.staysafe.dgs.grpc.*;

public class Log {
    private String _type;
    private long[] _ts = {0,0,0};
    private SnifferJoinRequest _join;
    private ReportRequest _report;


    Log(String type, long ts1, long ts2, long ts3, SnifferJoinRequest join, ReportRequest report) {
        _type = type;
        _ts[0] = ts1;
        _ts[1] = ts2;
        _ts[2] = ts3;
        _join = join;
        _report = report;
    }

    public String getType() {
        return _type;
    }

    public long[] getTs() {
        return _ts;
    }

    public SnifferJoinRequest getJoin() {
        return _join;
    }

    public ReportRequest getReport() {
        return _report;
    }
}