package dfa.analyses;

import dfa.framework.CompositeDataFlowAnalysis;
import dfa.framework.SimpleBlockGraph;

/**
 * @author Nils Jessen
 * @author Sebastian Rauch
 * 
 *         A {@code ConstantFoldingAnalysis} is a {@code DataFlowAnalysis} that performs constant-folding.
 */
public class ConstantFoldingAnalysis extends CompositeDataFlowAnalysis<ConstantFoldingElement> {

    /**
     * Creates a {@code ConstantFoldingAnalysis} for the given {@code SimpleBlockGraph}.
     * 
     * @param blockGraph
     *        the {@code SimpleBlockGraph} the {@code ConstantFoldingAnalysis} is based on
     */
    public ConstantFoldingAnalysis(SimpleBlockGraph blockGraph) {
        super(new ConstantFoldingJoin(), new ConstantFoldingTransition(), new ConstantFoldingInitializer(blockGraph));
    }

}
