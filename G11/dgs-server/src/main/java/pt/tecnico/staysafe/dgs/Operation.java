package pt.tecnico.staysafe.dgs;

import java.util.*;
import pt.tecnico.staysafe.dgs.grpc.*;

public class Operation {

    private long _identifier;
    private String _type;
    private SnifferJoinRequest _join;
    private ReportRequest _report;
    private ClearRequest _clear;

    Operation(long identifier, String type, SnifferJoinRequest join, ReportRequest report, ClearRequest clear) {
        _identifier = identifier;
        _type = type;
        _join = join;
        _report = report;
        _clear = clear;
    }

    public long getIdentifier() {
        return _identifier;
    }

    public String getType() {
        return _type;
    }

    public SnifferJoinRequest getJoin() {
        return _join;
    }

    public ReportRequest getReport() {
        return _report;
    }

    public ClearRequest getClear() {
        return _clear;
    }
}