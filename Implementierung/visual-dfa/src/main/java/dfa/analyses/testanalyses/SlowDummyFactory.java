package dfa.analyses.testanalyses;

import dfa.framework.DFADirection;
import dfa.framework.DFAFactory;
import dfa.framework.DataFlowAnalysis;
import dfa.framework.SimpleBlockGraph;

public class SlowDummyFactory extends DFAFactory<DummyElement>{
    
    private int waitTime = 500;

    @Override
    public String getName() {
        return "Slow Dummy Analysis";
    }

    @Override
    public DFADirection getDirection() {
        return DFADirection.FORWARD;
    }

    @Override
    public DataFlowAnalysis<DummyElement> getAnalysis(SimpleBlockGraph blockGraph) {
        return new SlowDummyAnalysis(blockGraph, waitTime);
    }
    
}
