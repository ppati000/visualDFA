package dfa.framework;

/**
 * @author Sebastian Rauch 
 * 
 *         A {@code LogicalColor} describes the relation of a {@code BasicBlock} to a {@code Worklist}.
 */
public enum LogicalColor {

    /**
     * for the currently processed {@code BasicBlock}
     */
    CURRENT("current"),

    /**
     * for {@code BasicBlock}s that have not yet been visited
     */
    NOT_VISITED("not yet visited"),

    /**
     * for {@code BasicBlock}s currently on the {@code Worklist}
     */
    ON_WORKLIST("on worklist"),

    /**
     * for {@code BasicBlock}s that have been visited but currently are not on the {@code Worklist}
     */
    VISITED_NOT_ON_WORKLIST("visited but not on worklist");

    private final String description;

    private LogicalColor(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
