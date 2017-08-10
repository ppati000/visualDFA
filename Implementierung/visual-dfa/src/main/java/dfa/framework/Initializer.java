package dfa.framework;

import java.util.Map;

import soot.toolkits.graph.Block;

/**
 * @author Sebastian Rauch 
 * 
 *         A {@code Initializer} assigns an initial {@code BlockState} to each basic-block of a {@code BlockGraph}.
 *
 * @param <E>
 *        the type of {@code LatticeElement} to perform joins on
 */
public interface Initializer<E extends LatticeElement> {

    /**
     * Determines the initial states for the basic-blocks.
     * 
     * @return the initial states for the basic-blocks
     */
    Map<Block, BlockState<E>> getInitialStates();

}
