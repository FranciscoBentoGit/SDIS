package pt.tecnico.staysafe.dgs;

import java.util.*;
import pt.tecnico.staysafe.dgs.grpc.*;

public class Log {
    private String _type;
    private ReportRequest _report;


    Log(String type, ReportRequest report) {
        _type = type;
        _report = report;
    }

    public String getType() {
        return _type;
    }

    public ReportRequest getReport() {
        return _report;
    }
}