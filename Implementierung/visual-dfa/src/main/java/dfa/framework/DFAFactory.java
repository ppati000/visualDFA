package dfa.framework;

/**
 * @author Sebastian Rauch
 *
 *         A {@code DFAFactory} provides the name and direction of a dataflow analysis. It creates a
 *         {@code DataFlowAnalysis} from a given {@code SingleTailedBlockGraph}.
 */
public abstract class DFAFactory {

    /**
     * Returns the name of the dataflow analysis represented by this {@code DFAFactory}.
     * 
     * @return the name of the dataflow analysis represented by this {@code DFAFactory}
     */
    public abstract String getName();

    /**
     * Returns the direction of the dataflow analysis represented by this {@code DFAFactory}
     * 
     * @return the direction of the dataflow analysis represented by this {@code DFAFactory}.
     */
    public abstract DFADirection getDirection();

    /**
     * Provides initial states for all {@code Block}s.
     * 
     * @param blockGraph
     *        the {@code SimpleBlockGraph} the {@code DataFlowAnalysis} should be executed on.
     * 
     * @return a {@code DataFlowAnalysis} for the given {@code SimpleBlockGraph}
     */
    public abstract DataFlowAnalysis<? extends LatticeElement> getAnalysis(SimpleBlockGraph blockGraph);
}
