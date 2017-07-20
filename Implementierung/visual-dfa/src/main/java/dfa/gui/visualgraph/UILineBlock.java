package dfa.gui.visualgraph;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.view.mxGraph;

/**
 * @author Patrick Petrovic
 *
 *         Represents a child block (i.e. a clickable line of code) of a basic block in the visual graph.
 */
class UILineBlock extends UIAbstractBlock {
    private final String text;
    private final UIBasicBlock parent;
    private UILineBlock previous;

    /**
     * Creates a new {@code LineBlock}.
     *
     * @param parent
     *         the {@code BasicBlock} this {@code LineBlock} will be inserted into
     * @param previous
     *         the {@code LineBlock} which will be rendered directly above this one
     */
    public UILineBlock(mxGraph graph, UIBasicBlock parent, UILineBlock previous) { // TODO: Add DFAFramework.ElementaryBlock parameter.
        this.previous = previous;
        this.parent = parent;
        this.text = "Test Text"; // TODO: Get text from corresponding DFAFramework block.
        this.graph = graph;
    }

    /**
     * Inserts this {@code LineBlock} into its parent in {@code mxGraph}.
     */
    @Override
    public void render() { // TODO: Add AnalysisState parameter.
        if (cell == null) {
            // Place the LineBlock below the previous one, if applicable.
            double yValue = previous == null ? Styles.LINE_HEIGHT : previous.getMxCell().getGeometry().getY() + Styles.LINE_HEIGHT;
            mxGeometry geo = new mxGeometry(0, yValue, Styles.BLOCK_WIDTH, Styles.LINE_HEIGHT);
            geo.setRelative(false);

            cell = new mxCell(text, geo, Styles.TRANSPARENT_COLOR + Styles.BORDER);
            cell.setVertex(true);
            graph.addCell(cell, parent.getMxCell());
        } else {
            // TODO: Set highlight color if this elementary block is the current analysis block (based on DFAFramework data).
        }
    }
}
