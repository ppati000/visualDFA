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

import static gui.visualgraph.Styles.BREAKPOINT_PADDING;
import static gui.visualgraph.Styles.BREAKPOINT_SIZE;

/**
 * @author Patrick Petrovic
 *
 * Represents a child block (i.e. a clickable line of code) of a basic block in the visual graph.
 */
public class UILineBlock extends UIAbstractBlock {
    private final UIBasicBlock parent;
    private final UILineBlock previous;
    private mxCell breakpointCell;
    private boolean hasBreakpoint = false;
    private mxGraphComponent graphComponent;
    private ElementaryBlock elementaryBlock;
    private boolean isCurrentBlock = false;

    private final int BREAKPOINT_SIZE_WITH_PADDINGS = 2 * BREAKPOINT_PADDING + BREAKPOINT_SIZE;

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
            mxGeometry geo = new mxGeometry(BREAKPOINT_SIZE_WITH_PADDINGS, yValue, Styles.BLOCK_WIDTH - BREAKPOINT_SIZE_WITH_PADDINGS, Styles.LINE_HEIGHT);
            geo.setRelative(false);

            FontMetrics metrics = graphComponent.getFontMetrics(new Font(Font.MONOSPACED, Font.PLAIN, Styles.TEXT_SIZE));
            String baseText = elementaryBlock.getUnit().toString()
                    .replaceAll("^(static|virtual|special|interface|dynamic)invoke\\s", "invoke ")
                    .replaceAll("\\s(static|virtual|special|interface|dynamic)invoke\\s", " invoke ")
                    .replaceAll("\\sgoto\\s.*$", " goto") // Remove everything after "goto" to avoid confusion from text like 'goto (branch)'.
                    .replaceAll("^goto\\s.*$", "goto");

            // Handle long labels: Use BLOCK_WIDTH - 20 for word wrap to leave some space for appending "...".
            String[] splitLabel = mxUtils.wordWrap(baseText, metrics, Styles.BLOCK_WIDTH - 20);
            String label = splitLabel.length > 1 ? splitLabel[0] + "\u2026" : splitLabel[0];

            // < and & characters have to be escaped (they are reserved HTML symbols).
            String escapedHtmlLabel = label.replace("&", "&amp;").replace("<", "&lt;");
            String formattedHtmlLabel = "<span style=\"font-family:monospace;\">" + escapedHtmlLabel + "</span>";

            cell = new mxCell(formattedHtmlLabel, geo, Styles.NO_BORDER + Styles.TEXT_ALIGN_LEFT + Styles.TEXT_COLOR);
            graph.setCellStyles(mxConstants.STYLE_FILLCOLOR, Styles.TRANSPARENT_COLOR, new Object[]{cell});
            graph.setCellStyles(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_MIDDLE, new Object[]{cell});

            cell.setVertex(true);
            graph.addCell(cell, parent.getMxCell());

            mxGeometry breakpointGeo = new mxGeometry(BREAKPOINT_PADDING, yValue + BREAKPOINT_PADDING, Styles.BREAKPOINT_SIZE, Styles.BREAKPOINT_SIZE);
            breakpointCell = new mxCell("", breakpointGeo, Styles.NO_BORDER);
            breakpointCell.setVertex(true);
            graph.setCellStyles(mxConstants.STYLE_FILLCOLOR, Styles.TRANSPARENT_COLOR, new Object[]{breakpointCell});
            graph.addCell(breakpointCell, parent.getMxCell());

            // Add a listener for activation and deactivation of breakpoint cell.
            graphComponent.getGraphControl().addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    mxCell cell = (mxCell) graphComponent.getCellAt(e.getX(), e.getY());
                    if (cell != null && cell.equals(breakpointCell)) {
                        toggleBreakpoint();
                    }
                }
            });
        } else {
            boolean isNowCurrentBlock = analysisState != null && elementaryBlock.equals(analysisState.getCurrentElementaryBlock());
            String newStyle = isNowCurrentBlock ? Styles.HIGHLIGHT_COLOR : Styles.TRANSPARENT_COLOR;

            if (isCurrentBlock != isNowCurrentBlock) {
                graph.setCellStyles(mxConstants.STYLE_FILLCOLOR, newStyle, new Object[]{cell});
                isCurrentBlock = isNowCurrentBlock;
            }
        }
    }

    /**
     * Toggles this line's breakpoint on or off.
     */
    public void toggleBreakpoint() {
        hasBreakpoint = !hasBreakpoint;
        elementaryBlock.setBreakpoint(hasBreakpoint);

        String newStyle = hasBreakpoint ? Styles.BREAKPOINT_COLOR : Styles.TRANSPARENT_COLOR;
        graph.setCellStyles(mxConstants.STYLE_FILLCOLOR, newStyle, new Object[]{breakpointCell});
    }

    /**
     * Returns true if this line has an active breakpoint, false if not.
     *
     * @return true iff lin has active breakpoint
     */
    public boolean hasBreakpoint() {
        return hasBreakpoint;
    }

    /**
     * Returns this line's breakpoint cell.
     *
     * @return breakpoint cell.
     */
    public mxCell getBreakpointCell() {
        return breakpointCell;
    }

    /**
     * Returns this line's text content (a single line {@code String}).
     *
     * @return text content
     */
    @Override
    public String getText() {
        return elementaryBlock.getUnit().toString();
    }

    /**
     * Returns the parent block number.
     *
     * @return parent block number
     */
    @Override
    public int getBlockNumber() {
        return parent.blockNumber;
    }

    /**
     * Returns this line's number.
     *
     * @return line number
     */
    @Override
    public int getLineNumber() {
        return blockNumber;
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
