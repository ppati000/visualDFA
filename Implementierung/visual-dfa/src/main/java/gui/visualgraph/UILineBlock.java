package gui.visualgraph;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraph;
import dfa.framework.AnalysisState;
import dfa.framework.ElementaryBlock;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author Patrick Petrovic
 *
 *         Represents a child block (i.e. a clickable line of code) of a basic block in the visual graph.
 */
public class UILineBlock extends UIAbstractBlock {
    private final UIBasicBlock parent;
    private final UILineBlock previous;
    private mxCell breakpointCell;
    private boolean hasBreakpoint = false;
    private mxGraphComponent graphComponent;
    private ElementaryBlock elementaryBlock;

    /**
     * Creates a new {@code LineBlock}.
     *
     * @param elementaryBlock
     *         the corresponding {@code dfa.framework.ElementaryBlock} (used to set color and text)
     * @param graphComponent
     *         the {@code mxGraphComponent} of the current graph
     * @param graph
     *         the {@code mxGraph} this block will be inserted into
     * @param parent
     *         the {@code UIBasicBlock} this {@code LineBlock} will be inserted into
     * @param previous
     *         the {@code UILineBlock} which will be rendered directly above this one
     */
    public UILineBlock(ElementaryBlock elementaryBlock, mxGraphComponent graphComponent, mxGraph graph, UIBasicBlock parent, UILineBlock previous) {
        this.previous = previous;
        this.parent = parent;
        this.elementaryBlock = elementaryBlock;
        this.graph = graph;
        this.graphComponent = graphComponent;
    }

    /**
     * Creates a new {@code LineBlock} with no predecessor. Yields same result as the other constructor with its last
     * argument set to {@code null}.
     *
     * @param elementaryBlock
     *         the corresponding {@code dfa.framework.ElementaryBlock} (used to set color and text)
     * @param graphComponent
     *         the {@code mxGraphComponent} of the current graph
     * @param graph
     *         the {@code mxGraph} this block will be inserted into
     * @param parent
     *         the {@code UIBasicBlock} this {@code LineBlock} will be inserted into
     */
    public UILineBlock(ElementaryBlock elementaryBlock, mxGraphComponent graphComponent, mxGraph graph, UIBasicBlock parent) {
        this(elementaryBlock, graphComponent, graph, parent, null);
    }


    /**
     * Inserts this {@code LineBlock} into its parent in {@code mxGraph} or updates it if already inserted.
     */
    @Override
    public void render(final AnalysisState analysisState) { // TODO: Add AnalysisState parameter.
        if (cell == null) {
            // Place the LineBlock below the previous one, if applicable.
            double yValue = previous == null ? Styles.LINE_HEIGHT : previous.getMxCell().getGeometry().getY() + Styles.LINE_HEIGHT;
            mxGeometry geo = new mxGeometry(0, yValue, Styles.BLOCK_WIDTH, Styles.LINE_HEIGHT);
            geo.setRelative(false);

            FontMetrics metrics = graphComponent.getFontMetrics(new Font(Font.MONOSPACED, Font.PLAIN, Styles.TEXT_SIZE));

            // Handle long labels: Use BLOCK_WIDTH - 20 for word wrap to leave some space for appending "…".
            String[] splitLabel = mxUtils.wordWrap(elementaryBlock.getUnit().toString(), metrics, Styles.BLOCK_WIDTH - 20);
            String label = splitLabel.length > 1 ? splitLabel[0] + "…" : splitLabel[0];

            // < and & characters have to be escaped (they are reserved HTML symbols).
            String escapedHtmlLabel = label.replace("&", "&amp;").replace("<", "&lt;");
            String formattedHtmlLabel = "<span style=\"font-family:monospace;\">" + escapedHtmlLabel + "</span>";

            cell = new mxCell(formattedHtmlLabel, geo, Styles.NO_BORDER + Styles.TEXT_ALIGN_LEFT + Styles.TEXT_COLOR);
            graph.setCellStyles(mxConstants.STYLE_FILLCOLOR, Styles.TRANSPARENT_COLOR, new Object[]{cell});
            graph.setCellStyles(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_MIDDLE, new Object[]{cell});

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
                        hasBreakpoint = !hasBreakpoint;
                        elementaryBlock.setBreakpoint(hasBreakpoint);
                        render(null); // No analysisState needed; we only want to update the breakpoint cell.
                    }
                }
            });
        } else if (analysisState != null && elementaryBlock.equals(analysisState.getCurrentElementaryBlock())) {
            graph.setCellStyles(mxConstants.STYLE_FILLCOLOR, Styles.HIGHLIGHT_COLOR, new Object[]{cell});
        } else {
            graph.setCellStyles(mxConstants.STYLE_FILLCOLOR, Styles.TRANSPARENT_COLOR, new Object[]{cell});
        }

        if (hasBreakpoint) {
            graph.setCellStyles(mxConstants.STYLE_FILLCOLOR, Styles.BREAKPOINT_COLOR, new Object[]{breakpointCell});
        } else {
            graph.setCellStyles(mxConstants.STYLE_FILLCOLOR, Styles.TRANSPARENT_COLOR, new Object[]{breakpointCell});
        }
    }

    @Override
    public String getText() {
        return elementaryBlock.getUnit().toString();
    }

    @Override
    public int[] getBlockAndLineNumbers() {
        return new int[] {parent.blockNumber, blockNumber};
    }

    /**
     * Returns the corresponding DFAFramework block.
     *
     * @return the corresponding DFAFramework block
     */
    @Override
    public ElementaryBlock getDFABlock() {
        return elementaryBlock;
    }
}
