package gui.visualgraph;

import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;
import dfa.framework.AnalysisState;

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
     *
     * @param analysisState
     *         the {@code AnalysisState} that should be used to render this {@code VisualGraphElement}.
     */
    public abstract void render(AnalysisState analysisState);

    /**
     * Returns this element's {@code mxCell}
     *
     * @return this element's {@code mxCell} ({@code null} if not rendered at least once)
     */
    public mxCell getMxCell() {
        return cell;
    }
}
