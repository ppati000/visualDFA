package dfa.gui.visualgraph;

import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Patrick Petrovic
 *
 *         This class represents a basic block in the visual graph.
 */
class UIBasicBlock extends UIAbstractBlock {

    private List<UILineBlock> lineBlocks;

    /**
     * Creates and inserts a new {@code mxCell} into the {@code mxGraph}, or updates the {@code mxCell} if it already
     * exists. Additionally, calls {@code render()} on every {@code LineBlock} this {@code BasicBlock} consists of.
     */
    @Override
    public void render() { // TODO: Add AnalysisState parameter.
        if (cell == null) {
            cell = (mxCell) graph.insertVertex(graph.getDefaultParent(), null, "", 10, 10, Styles.BLOCK_WIDTH,
                    (lineBlocks.size() + 1) * Styles.LINE_HEIGHT, "");

            cell.setStyle(Styles.INITIAL_COLOR);
        } else {
            // TODO: Set style of mxCell based on DFAFramework's logical color.
        }

        for (UILineBlock lineBlock : lineBlocks) {
            lineBlock.render();
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
     */
    public UIBasicBlock(mxGraph graph) {
        this.graph = graph;
        lineBlocks = new ArrayList<>();
    }
}
