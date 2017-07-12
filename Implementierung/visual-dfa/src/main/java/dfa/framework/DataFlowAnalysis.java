package dfa.framework;

import java.util.Map;
import java.util.Set;

import soot.Unit;
import soot.toolkits.graph.Block;

/**
 * @author Sebastian Rauch
 * 
 *         A {@code DataFlowAnalysis} provides operations needed for dataflow-analysis. Specifically it provides methods
 *         to perform joins and transitions as well as initialize the states of all basic-blocks.
 *
 * @param <E>
 *        the type of {@code LatticeElement} used in this {@code DataFlowAnalysis}
 */
public interface DataFlowAnalysis<E extends LatticeElement> extends Transition<E>, Join<E> {

    /**
     * {@inheritDoc}
     */
    E transition(E element, Unit unit);

    /**
     * {@inheritDoc}
     */
    E join(Set<E> elements);

    /**
     * Determines the initial states for the basic blocks.
     * 
     * @return the initial states for the basic blocks
     */
    Map<Block, BlockState<E>> getInitialStates();

}
