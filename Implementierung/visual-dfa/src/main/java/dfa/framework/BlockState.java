package dfa.framework;

/**
 * @author Sebastian Rauch
 * 
 *         A {@code BlockState} represents an in-state an an out-state.
 *
 * @param <E>
 *        the type of {@code LatticeElement} used in this {@code BlockState}
 */
public class BlockState<E extends LatticeElement> {

    private E inState;
    private E outState;

    /**
     * Creates a {@code BlockState} with the given in- and -out-state.
     * 
     * @param inState
     *        the in-state
     * @param outState
     *        the out-state
     */
    public BlockState(E inState, E outState) {
        setInState(inState);
        setOutState(outState);
    }

    /**
     * Returns the in-state.
     * 
     * @return the in-state
     */
    public E getInState() {
        return inState;
    }

    /**
     * Sets the in-state.
     * 
     * @param inState
     *        the in-state
     */
    protected void setInState(E inState) {
        this.inState = inState;
    }

    /**
     * Returns the out-state.
     * 
     * @return the out-state.
     */
    public E getOutState() {
        return outState;
    }

    /**
     * Sets the out-state.
     * 
     * @param outState
     *        the out-state
     */
    protected void setOutState(E outState) {
        this.outState = outState;
    }

}
