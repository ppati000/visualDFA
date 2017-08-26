package dfa.framework;

import soot.Unit;

/**
 * An {@code UnsupportedStatementException} is used to indicate that during the precalculation of a dataflow-analysis a
 * statement was encountered, that is not supported by this particular analysis.
 * 
 * @author Sebastian Rauch
 *
 */
public class UnsupportedStatementException extends DFAException {

    private static final long serialVersionUID = 1L;

    private final String stmtName;
    private final Unit unit;

    /**
     * Creates a {@code UnsupportedStatementException} with the given statement description and the {@code Unit} that
     * caused this exception.
     * 
     * @param stmtDsc
     *        the description of the unsupported statement
     * @param unit
     *        the unit that caused this exception
     */
    public UnsupportedStatementException(String stmtDsc, Unit unit) {
        super("unsupported statement: " + stmtDsc);
        this.stmtName = stmtDsc;
        this.unit = unit;
    }

    /**
     * Returns a description of the statement that caused this {@code UnsupportedStatementException}.
     * 
     * @return a description of the statement that caused this {@code UnsupportedStatementException}
     */
    public String getStatementDescription() {
        return stmtName;
    }

    /**
     * Returns the {@code Unit} that caused this {@code UnsupportedStatementException}.
     * 
     * @return the {@code Unit} that caused this {@code UnsupportedStatementException}
     */
    public Unit getUnit() {
        return unit;
    }

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder("Unsupported statement: ");
        sb.append(getStatementDescription()).append(" [").append(unit).append("]");
        return sb.toString();
    }

}