package dfa.framework;

import soot.Unit;

/**
 * @author Sebastian Rauch 
 *
 *         An {@code ElementaryBlock} represents one {@code Unit} ('line') inside a {@code BasicBlock}. An
 *         {@code ElementaryBlock} can also be marked with a breakpoint.
 */
public class ElementaryBlock extends AbstractBlock {

    private Unit unit;

    private boolean breakpoint = false;

    /**
     * Creates an {@code ElementaryBlock} with the given {@code Unit}.
     * 
     * @param unit
     *        the {@code Unit} this {@code ElementaryBlock} represents
     */
    public ElementaryBlock(Unit unit) {
        setUnit(unit);
    }

    /**
     * Sets or deletes a breakpoint at this {@code ElementaryBlock}.
     * 
     * @param breakpoint
     *        whether there should be a breakpoint at this {@code ElementaryBlock}
     */
    public void setBreakpoint(boolean breakpoint) {
        this.breakpoint = breakpoint;
    }

    /**
     * Returns whether there is a breakpoint at this {@code ElementaryBlock}.
     * 
     * @return {@code true} iff there is a breakpoint at this {@code ElementaryBlock}
     */
    public boolean hasBreakpoint() {
        return breakpoint;
    }

    /**
     * Toggles the breakpoint at this {@code ElementaryBlock}.
     */
    public void toggleBreakpoint() {
        setBreakpoint(!hasBreakpoint());
    }

    /**
     * Returns the {@code Unit} represented by this {@code ElementaryBlock}.
     * 
     * @return the {@code Unit} represented by this {@code ElementaryBlock}
     */
    public Unit getUnit() {
        return unit;
    }

    /**
     * Sets the {@code Unit} represented by this {@code ElementaryBlock}.
     * 
     * @param unit
     *        the {@code Unit} represented by this {@code ElementaryBlock}
     */
    protected void setUnit(Unit unit) {
        this.unit = unit;
    }

}
