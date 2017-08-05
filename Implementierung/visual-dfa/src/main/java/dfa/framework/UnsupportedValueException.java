package dfa.framework;

import soot.Value;

/**
 * @author Sebastian Rauch
 * 
 *         An {@code Exception} thrown to indicate that an unsupported {@code soot.Value} was encountered.
 */
public class UnsupportedValueException extends DFAException {

    private static final long serialVersionUID = 1L;

    private final String valueDsc;
    private final Value value;

    /**
     * Creates a {@code UnsupportedValueException} with the given value description and {@code soot.Value}.
     * 
     * @param valueDsc
     *        a description of the encountered {@code soot.Value}
     * @param value
     *        the encountered {@code soot.Value}
     */
    public UnsupportedValueException(String valueDsc, Value value) {
        super("unsupported value: " + valueDsc);
        this.valueDsc = valueDsc;
        this.value = value;
    }

    /**
     * Returns a description of the {@code soot.Value}.
     * 
     * @return a description of the {@code soot.Value}
     */
    public String getValueDescription() {
        return valueDsc;
    }

    /**
     * Returns the {@code soot.Value}.
     * 
     * @return the {@code soot.Value}
     */
    public Value getValue() {
        return value;
    }

    @Override
    public String getMessage() {
        return "unsupported Value encountered: " + valueDsc;
    }

}
