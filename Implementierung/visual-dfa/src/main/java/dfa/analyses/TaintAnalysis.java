package dfa.analyses;

import dfa.framework.CompositeDataFlowAnalysis;
import dfa.framework.SimpleBlockGraph;

/**
 * @author Sebastian Rauch
 * 
 *         A {@code TaintAnalysis} is a {@code DataFlowAnalysis} that performs taint-analysis.
 */
public class TaintAnalysis extends CompositeDataFlowAnalysis<TaintElement> {

    /**
     * Creates a {@code TaintAnalysis} for the given {@code SimpleBlockGraph}.
     * 
     * @param blockGraph
     *        the {@code SimpleBlockGraph} the {@code TaintAnalysis} is based on
     */
    public TaintAnalysis(SimpleBlockGraph blockGraph) {
        // TODO 
        super(new TaintJoin(), new TaintTransition(), new TaintInitializer(blockGraph));
    }


}
