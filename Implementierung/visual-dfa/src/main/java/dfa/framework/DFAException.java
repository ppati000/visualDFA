package dfa.framework;

/**
 * A {@code DFAException} is thrown to indicate that a problem occurred during a dataflow-analysis.
 * 
 * @author Sebastian Rauch
 *
 */
public class DFAException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a {@code DFAException} with the given message.
     * 
     * @param msg
     *        the message for this {@code DFAException}
     */
    public DFAException(String msg) {
        super(msg);
    }

}
