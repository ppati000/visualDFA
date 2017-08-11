package dfa.analyses;

import dfa.framework.DFADirection;
import dfa.framework.DFAFactory;
import dfa.framework.DataFlowAnalysis;
import dfa.framework.SimpleBlockGraph;

/**
 * @author Sebastian Rauch
 * 
 *         A {@code TaintFactory} creates {@code TaintAnalysis}-Instances from {@code SimpleBlockGraph}.
 *
 */
public class TaintFactory extends DFAFactory<TaintElement> {

    @Override
    public String getName() {
        return "taint";
    }

    @Override
    public DFADirection getDirection() {
        return DFADirection.FORWARD;
    }

    @Override
    public DataFlowAnalysis<TaintElement> getAnalysis(SimpleBlockGraph blockGraph) {
        return new TaintAnalysis(blockGraph);
    }

}
