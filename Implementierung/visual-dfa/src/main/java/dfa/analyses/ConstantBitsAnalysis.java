package dfa.analyses;

import dfa.framework.CompositeDataFlowAnalysis;
import dfa.framework.SimpleBlockGraph;

/**
 * @author Nils Jessen
 * 
 *         A {@code ConstantBitsAnalysis} is a {@code DataFlowAnalysis} that performs constant-bits.
 */
public class ConstantBitsAnalysis extends CompositeDataFlowAnalysis<ConstantBitsElement> {

    /**
     * Creates a {@code ConstantBitsAnalysis} for the given {@code SimpleBlockGraph}.
     * 
     * @param blockGraph
     *        the {@code SimpleBlockGraph} the {@code ConstantBitsAnalysis} is based on
     */
    public ConstantBitsAnalysis(SimpleBlockGraph blockGraph) {
        super(new ConstantBitsJoin(), new ConstantBitsTransition(), new ConstantBitsInitializer(blockGraph));
    }

}
