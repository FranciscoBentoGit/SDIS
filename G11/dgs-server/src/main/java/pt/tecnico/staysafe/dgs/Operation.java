package pt.tecnico.staysafe.dgs;

import java.util.*;
import pt.tecnico.staysafe.dgs.grpc.*;

public class Operation {
    private String _type;
    private SnifferJoinRequest _join;
    private ReportRequest _report;
    private Long _identifier;


    Operation(Long identifier,String type, SnifferJoinRequest join, ReportRequest report) {
        _identifier = identifier;
        _type = type;
        _join = join;
        _report = report;
    }

    public Long getIdentifier() {
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
}