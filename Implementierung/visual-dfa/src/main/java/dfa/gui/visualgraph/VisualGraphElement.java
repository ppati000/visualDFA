package dfa.gui.visualgraph;

import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;

/**
 * @author Patrick Petrovic
 *
 *         Represents a block or an edge in {@code mxGraph}.
 */
abstract class VisualGraphElement {
    protected mxCell cell;
    protected mxGraph graph;

    /**
     * Inserts the element into {@code mxGraph} or updates it if it was already inserted.
     */
    public abstract void render(); // TODO: Add AnalysisState parameter.

    /**
     * Returns this element's {@code mxCell}
     *
     * @return this element's {@code mxCell} ({@code null} if not rendered at least once)
     */
    public mxCell getMxCell() {
        return cell;
    }
}
