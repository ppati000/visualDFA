package dfa.framework;

import java.util.Map;
import java.util.Set;

import soot.Unit;
import soot.toolkits.graph.Block;

/**
 * @author Sebastian Rauch
 * 
 *         A {@code CompositeDataFlowAnalysis} combines a {@code Join}, a {@code Transition} and an {@code Initializer}
 *         into a {@code DataFlowAnalysis}.
 *
 * @param <E>
 *        the type of {@code LatticeElement} used in this {@code CompositeDataFlowAnalysis}
 */
public class CompositeDataFlowAnalysis<E extends LatticeElement> implements DataFlowAnalysis<E> {

    private Join<E> join;
    private Transition<E> transition;
    private Initializer<E> initializer;

    /**
     * Creates a {@code CompositeDataFlowAnalysis} from a {@code Join} and a {@code Transition}.
     * 
     * @param join
     *        the {@code Join} to use to perform {@code transition}
     * @param transition
     *        the {@code Transition} to use perform {@code join}
     * @param initializer
     *        the {@code Initializer} used to initialize the analysis
     * 
     * @throws IllegalArgumentException
     *         if {@code join} or {@code transition} or {@code initializer} is {@code null}
     */
    public CompositeDataFlowAnalysis(Join<E> join, Transition<E> transition, Initializer<E> initializer) {
        if (join == null) {
            throw new IllegalArgumentException("join must not be null");
        }

        if (transition == null) {
            throw new IllegalArgumentException("transition must not be null");
        }

        if (initializer == null) {
            throw new IllegalArgumentException("initializer must not be null");
        }

        this.join = join;
        this.transition = transition;
        this.initializer = initializer;
    }

    @Override
    public E transition(E element, Unit unit) {
        return transition.transition(element, unit);
    }

    @Override
    public E join(Set<E> elements) {
        return join.join(elements);
    }

    @Override
    public Map<Block, BlockState<E>> getInitialStates() {
        return initializer.getInitialStates();
    }

}
