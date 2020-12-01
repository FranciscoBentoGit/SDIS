package pt.tecnico.staysafe.dgs.client;

import pt.tecnico.staysafe.dgs.grpc.*;

public class LastView {
    private long[] _vector = {0,0};
    private IndividualProbResponse _singleProb;
    private AggregateProbResponse _meanDev;
    private AggregateProbResponse _percentiles;

    public long[] getVector() {
        return _vector; 
    }

    public void setSingleProb(long[] v, IndividualProbResponse r) {
        _vector = v;
        _singleProb = r;
    }

    public IndividualProbResponse getSingleProb() {
        return _singleProb; 
    }

    public void setMeanDev(long[] v, AggregateProbResponse r) {
        _vector = v;
        _meanDev = r;
    }

    public AggregateProbResponse getMeanDev() {
        return _meanDev; 
    }

    public void setPercentiles(long[] v, AggregateProbResponse r) {
        _vector = v;
        _percentiles = r;
    }

    public AggregateProbResponse getPercentiles() {
        return _percentiles; 
    }

}