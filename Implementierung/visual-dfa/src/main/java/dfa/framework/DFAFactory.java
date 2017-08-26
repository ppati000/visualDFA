package dfa.framework;

/**
 * A {@code DFAFactory} provides the name and direction of a dataflow analysis. It creates a {@code DataFlowAnalysis}
 * from a given {@code SingleTailedBlockGraph}.
 * 
 * @author Sebastian Rauch
 *
 * @param <E>
 *        the type of {@code LatticeElement} used by this {@code DFAFactory}
 */
public abstract class DFAFactory<E extends LatticeElement> {

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
    public abstract DataFlowAnalysis<E> getAnalysis(SimpleBlockGraph blockGraph);
}
