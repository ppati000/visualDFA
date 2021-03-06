package dfa.analyses.testanalyses;

import dfa.framework.CompositeDataFlowAnalysis;
import dfa.framework.SimpleBlockGraph;

/**
 * A dummy analysis used for testing.
 * 
 * @author Patrick Petrovic
 */
public class DummyAnalysis extends CompositeDataFlowAnalysis<DummyElement> {

    /**
     * Creates a {@code DummyAnalysis} for the given {@code SimpleBlockGraph}.
     *
     * @param blockGraph
     *        the {@code SimpleBlockGraph} the {@code DummyAnalysis} is based on
     */
    public DummyAnalysis(SimpleBlockGraph blockGraph) {
        super(new DummyJoin(), new DummyTransition(), new DummyInitializer(blockGraph));
    }

}
