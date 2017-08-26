package dfa.framework;

import java.util.Set;

/**
 * A {@code Join} executes a join-operation on a {@code Set} of {@code LatticeElement}s.
 *
 * @param <E>
 *        the type of {@code LatticeElement} to perform joins on
 * 
 * @author Sebastian Rauch
 */
public interface Join<E extends LatticeElement> {

    /**
     * Calculates the join of the given {@code elements}.
     * 
     * @param elements
     *        the {@code LatticeElement}s to join
     * @return the join of the given {@code LatticeElement}s
     */
    E join(Set<E> elements);

}
