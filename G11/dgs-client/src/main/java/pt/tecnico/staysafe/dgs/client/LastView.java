package pt.tecnico.staysafe.dgs.client;

import pt.tecnico.staysafe.dgs.grpc.*;

public class LastView {
    private IndividualProbResponse _singleProb;
    private AggregateProbResponse _meanDev;
    private AggregateProbResponse _percentiles;


    public void setSingleProb(IndividualProbResponse r) {
        _singleProb = r;
    }

    public IndividualProbResponse getSingleProb() {
        return _singleProb; 
    }

    public void setMeanDev(AggregateProbResponse r) {
        _meanDev = r;
    }

    public AggregateProbResponse getMeanDev() {
        return _meanDev; 
    }

    public void setPercentiles (AggregateProbResponse r) {
        _percentiles = r;
    }

    public AggregateProbResponse getPercentiles() {
        return _percentiles; 
    }

}