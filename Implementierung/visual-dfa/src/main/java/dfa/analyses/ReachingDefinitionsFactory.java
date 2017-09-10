package dfa.analyses;

import dfa.framework.DFADirection;
import dfa.framework.DFAFactory;
import dfa.framework.DataFlowAnalysis;
import dfa.framework.SimpleBlockGraph;

/**
 * A {@code ReachingDefinitionsFactory} creates {@code ReachingDefinitionsAnalysis}-Instances from a
 * {@code SimpleBlockGraph}.
 *
 * @author Nils Jessen
 */
public class ReachingDefinitionsFactory extends DFAFactory<ReachingDefinitionsElement> {

    @Override
    public String getName() {
        return "Reaching-Definitions";
    }

    @Override
    public DFADirection getDirection() {
        return DFADirection.FORWARD;
    }

    @Override
    public DataFlowAnalysis<ReachingDefinitionsElement> getAnalysis(SimpleBlockGraph blockGraph) {
        return new ReachingDefinitionsAnalysis(blockGraph);
    }

}