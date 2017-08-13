package dfa.analyses.testanalyses;

import dfa.framework.CompositeDataFlowAnalysis;
import dfa.framework.SimpleBlockGraph;

/**
 * @author Sebastian Rauch
 *
 *         A dummy analysis that takes a long time for transitions.
 */
public class SlowDummyAnalysis extends CompositeDataFlowAnalysis<DummyElement> {

    /**
     * Creates a {@code DummyAnalysisSlow} for the given {@code SimpleBlockGraph}.
     *
     * @param blockGraph
     *        the {@code SimpleBlockGraph} the {@code DummyAnalysis} is based on
     * @param waitTime
     *        the time (in ms) to wait before each transition
     */
    public SlowDummyAnalysis(SimpleBlockGraph blockGraph, int waitTime) {
        super(new DummyJoin(), new SlowDummyTransition(waitTime), new DummyInitializer(blockGraph));
    }

}
