package gui.visualgraph;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import dfa.framework.AnalysisState;
import dfa.framework.BasicBlock;
import dfa.framework.DFAExecution;
import dfa.framework.LogicalColor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Patrick Petrovic
 *
 *         This class represents a basic block in the visual graph.
 */
public class UIBasicBlock extends UIAbstractBlock {

    private List<UILineBlock> lineBlocks;
    private BasicBlock dfaBasicBlock;
    private DFAExecution dfa;
    private LogicalColor currentColor = null;

    /**
     * Creates and inserts a new {@code mxCell} into the {@code mxGraph}, or updates the {@code mxCell} if it already
     * exists. Additionally, calls {@code render()} on every {@code LineBlock} this {@code BasicBlock} consists of.
     *
     * @param analysisState
     *         the current {@code AnalysisState} that should be used to render this block
     */
    @Override
    public void render(AnalysisState analysisState) {
        if (cell == null) {
            cell = (mxCell) graph.insertVertex(graph.getDefaultParent(), null, "", 10, 10, Styles.BLOCK_WIDTH,
                    (lineBlocks.size() + 1) * Styles.LINE_HEIGHT, "");

            graph.setCellStyles(mxConstants.STYLE_FILLCOLOR, Styles.INITIAL_COLOR, new Object[]{cell});
        } else {
            LogicalColor newColor = dfa.getCurrentAnalysisState().getColor(dfaBasicBlock);

            if (!newColor.equals(currentColor)) {
                String colorStyle;

                switch (dfa.getCurrentAnalysisState().getColor(dfaBasicBlock)) {
                    case CURRENT:
                        colorStyle = Styles.CURRENT_COLOR;
                        break;
                    case NOT_VISITED:
                        colorStyle = Styles.INITIAL_COLOR;
                        break;
                    case ON_WORKLIST:
                        colorStyle = Styles.ON_WORKLIST_COLOR;
                        break;
                    case VISITED_NOT_ON_WORKLIST:
                        colorStyle = Styles.VISITED_COLOR;
                        break;
                    default:
                        throw new IllegalStateException("Error: Unknown LogicalColor.");
                }

                graph.setCellStyles(mxConstants.STYLE_FILLCOLOR, colorStyle, new Object[]{cell});
                currentColor = newColor;
            }
        }
    }

    /**
     * Renders all {@code UILineBlock}s that have previously been added. Additionally, it inserts a top bar above the
     * first {@code UILineBlock}. This isn't done in {@code render()} so the auto-layouter can be run before child cells
     * are rendered (otherwise the layouter would change children which is not wanted)
     *
     * @param analysisState
     *         the state that should be used to render the children
     */
    public void renderChildren(AnalysisState analysisState) {
        if (lineBlocks.size() > 0) {
            mxGeometry topBarGeometry = new mxGeometry(0, Styles.LINE_HEIGHT, Styles.BLOCK_WIDTH, 0);
            topBarGeometry.setRelative(false);
            mxCell topBar = new mxCell("", topBarGeometry, Styles.TRANSPARENT_COLOR);
            topBar.setVertex(true);
            graph.addCell(topBar, cell);

            for (UILineBlock lineBlock : lineBlocks) {
                lineBlock.render(analysisState);
            }
        }
    }

    /**
     * Inserts a given line block into this basic block, below existing {@code LineBlock} if applicable.
     *
     * @param block
     *         the {@code LineBlock} to insert
     */
    public void insertLineBlock(UILineBlock block) {
        this.lineBlocks.add(block);
    }

    /**
     * Constructs a new basic block which will operate on the given {@code mxGraph}.
     *
     * @param graph
     *         the current {@code mxGraph}
     * @param dfaBasicBlock
     *         the corresponding {@code dfa.framework.BasicBlock}
     * @param dfa
     *         the current {@code DFAExecution}
     */
    public UIBasicBlock(mxGraph graph, BasicBlock dfaBasicBlock, DFAExecution dfa) {
        this.graph = graph;
        this.dfa = dfa;
        this.dfaBasicBlock = dfaBasicBlock;
        lineBlocks = new ArrayList<>();
    }

    /**
     * Returns the corresponding DFAFramework block.
     *
     * @return the corresponding DFAFramework block
     */
    @Override
    public BasicBlock getDFABlock() {
        return dfaBasicBlock;
    }

    /**
     * Returns the text content of all lines in this block. (multi-line {@code String}).
     *
     * @return text content
     */
    @Override
    public String getText() {
        String text = "";

        for (UILineBlock child : lineBlocks) {
            text += (child.getText() + "\n");
        }

        return text.trim();
    }

    /**
     * Returns this block's number.
     *
     * @return block number
     */
    @Override
    public int getBlockNumber() {
        return blockNumber;
    }

    /**
     * Returns -1 as this is a parent block (method is used as a sentinel for graph export and {@code StatePanelOpen}).
     *
     * @return -1
     */
    @Override
    public int getLineNumber() {
        return -1;
    }
}
