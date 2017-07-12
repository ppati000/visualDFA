package dfa.framework;

import java.util.Map;
import java.util.Set;

import soot.Unit;
import soot.toolkits.graph.Block; 

/**
 * @author Sebastian Rauch
 * 
 *         A {@code CompositeDataFlowAnalysis} combines a given {@code Join} and a given {@code Transition} into a
 *         {@code DataFlowAnalysis}
 *
 * @param <E>
 *        the type of {@code LatticeElement} used in this {@code CompositeDataFlowAnalysis}
 */
public abstract class CompositeDataFlowAnalysis<E extends LatticeElement> implements DataFlowAnalysis<E> {

    private Join<E> join;
    private Transition<E> transition;

    /**
     * Creates a {@code CompositeDataFlowAnalysis} from a {@code Join} and a {@code Transition}.
     * 
     * @param join
     *        the {@code Join} to use to perform {@code transition}
     * @param transition
     *        the {@code Transition} to use perform {@code join}
     */
    public CompositeDataFlowAnalysis(Join<E> join, Transition<E> transition) {
        this.join = join;
        this.transition = transition;
    }

    @Override
    public E transition(E element, Unit unit) {
        return transition.execute(element, unit);
    }

    @Override
    public E join(Set<E> elements) {
        return join.join(elements);
    }

    @Override
    public abstract Map<Block, BlockState<E>> getInitialStates();

}
