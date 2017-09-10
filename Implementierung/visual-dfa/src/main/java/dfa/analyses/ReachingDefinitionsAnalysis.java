package dfa.analyses;

import dfa.framework.CompositeDataFlowAnalysis;
import dfa.framework.SimpleBlockGraph;

/**
 * A {@code ReachingDefinitionsAnalysis} is a {@code DataFlowAnalysis} that performs reaching-definitions.
 * 
 * @author Nils Jessen
 */
public class ReachingDefinitionsAnalysis extends CompositeDataFlowAnalysis<ReachingDefinitionsElement> {

    /**
     * Creates a {@code ReachingDefinitionsAnalysis} for the given {@code SimpleBlockGraph}.
     * 
     * @param blockGraph
     *        the {@code SimpleBlockGraph} the {@code ReachingDefinitionsAnalysis} is based on
     */
    public ReachingDefinitionsAnalysis(SimpleBlockGraph blockGraph) {
        super(new ReachingDefinitionsJoin(), new ReachingDefinitionsTransition(),
                new ReachingDefinitionsInitializer(blockGraph));
    }
}