package gui.visualgraph;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;
import com.mxgraph.view.mxGraph;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author Patrick Petrovic
 *
 *         Represents a child block (i.e. a clickable line of code) of a basic block in the visual graph.
 */
public class UILineBlock extends UIAbstractBlock {
    private final String text;
    private final UIBasicBlock parent;
    private UILineBlock previous;
    private mxCell breakpointCell;
    private boolean hasBreakpoint = false;
    private mxGraphComponent graphComponent;

    /**
     * Creates a new {@code LineBlock}.
     *
     * @param graphComponent
     *         the {@code mxGraphComponent} of the current graph
     * @param graph
     *         the {@code mxGraph} this block will be inserted into
     * @param parent
     *         the {@code BasicBlock} this {@code LineBlock} will be inserted into
     * @param previous
     *         the {@code LineBlock} which will be rendered directly above this one
     */
    public UILineBlock(mxGraphComponent graphComponent, mxGraph graph, UIBasicBlock parent, UILineBlock previous) { // TODO: Add DFAFramework.ElementaryBlock parameter.
        this.previous = previous;
        this.parent = parent;
        this.text = "Test Text"; // TODO: Get text from corresponding DFAFramework block.
        this.graph = graph;
        this.graphComponent = graphComponent;
    }

    /**
     * Inserts this {@code LineBlock} into its parent in {@code mxGraph} or updates it if already inserted.
     */
    @Override
    public void render() { // TODO: Add AnalysisState parameter.
        if (cell == null) {
            // Place the LineBlock below the previous one, if applicable.
            double yValue = previous == null ? Styles.LINE_HEIGHT : previous.getMxCell().getGeometry().getY() + Styles.LINE_HEIGHT;
            mxGeometry geo = new mxGeometry(0, yValue, Styles.BLOCK_WIDTH, Styles.LINE_HEIGHT);
            geo.setRelative(false);

            cell = new mxCell(text, geo, Styles.NO_BORDER + Styles.TEXT_ALIGN_LEFT
                    + Styles.TEXT_ALIGN_VERTICAL_CENTER + Styles.TEXT_COLOR);
            graph.setCellStyles(mxConstants.STYLE_FILLCOLOR, Styles.TRANSPARENT_COLOR, new Object[]{cell});
            cell.setVertex(true);
            graph.addCell(cell, parent.getMxCell());

            mxGeometry breakpointGeo = new mxGeometry(Styles.BREAKPOINT_PADDING, Styles.BREAKPOINT_PADDING, Styles.BREAKPOINT_SIZE, Styles.BREAKPOINT_SIZE);
            breakpointCell = new mxCell("", breakpointGeo, Styles.NO_BORDER);
            breakpointCell.setVertex(true);
            graph.addCell(breakpointCell, cell);

            // Add a listener for activation and deactivation of breakpoint cell.
            graphComponent.getGraphControl().addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    mxCell cell = (mxCell) graphComponent.getCellAt(e.getX(), e.getY());
                    if (cell != null && cell.equals(breakpointCell)) {
                        hasBreakpoint = !hasBreakpoint; // TODO: set breakpoint in DFAFramework
                        render();
                    }
                }
            });
        } else {
            // TODO: Set highlight color
        }

        if (hasBreakpoint) {
            graph.setCellStyles(mxConstants.STYLE_FILLCOLOR, Styles.BREAKPOINT_COLOR, new Object[]{breakpointCell});
        } else {
            graph.setCellStyles(mxConstants.STYLE_FILLCOLOR, Styles.TRANSPARENT_COLOR, new Object[]{breakpointCell});
        }
    }
}
