package gui.visualgraph;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Patrick Petrovic
 *
 *         Panel used to display the visual graph.
 */
public class VisualGraphPanel extends JPanel {
    private List<UIBasicBlock> basicBlocks;
    private List<UIEdge> edges;
    private mxGraphComponent graphComponent;
    private JButton jumpToAction;
    private JButton graphExport;
    private mxGraph graph;
    private JLayeredPane buttonPane;

    /**
     * Creates a new {@code VisualGraphPanel}.
     */
    public VisualGraphPanel() {
        this.basicBlocks = new ArrayList<>();
        this.edges = new ArrayList<>();
        this.graph = new RestrictedMxGraph();

        // TODO: Make these buttons look nice
        // TODO: Add listeners to buttons
        // TODO: Find a replacement for GlassPane to make buttons visible
        jumpToAction = new JButton("Jump to Action");
        graphExport = new JButton("Export Graph");
    }

    /**
     * Inserts a given {@code UIBasicBlock} which will be rendered when {@code renderGraph()} is called.
     *
     * @param block
     *         the block to be added
     */
    public void insertBasicBlock(UIBasicBlock block) {
        basicBlocks.add(block);
    }

    /**
     * Inserts a given {@code UIEdge} which will be rendered when {@code renderGraph()} is called.
     *
     * @param edge
     *         the edge to be added
     */
    public void insertEdge(UIEdge edge) {
        edges.add(edge);
    }

    /**
     * Renders all previously inserted blocks and edges.
     *
     * @param applyLayout
     *         If {@code true}, the auto-layouter is called after inserting all parent blocks. Used on first render.
     */
    public void renderGraph(boolean applyLayout) {
        graph.getModel().beginUpdate();

        for (UIBasicBlock block : basicBlocks) {
            block.render();
        }

        for (UIEdge edge : edges) {
            edge.render();
        }

        // Apply layout before rendering child blocks, so that the layouter doesn't mess with them.
        if (applyLayout) {
            autoLayout();
        }

        for (UIBasicBlock block : basicBlocks) {
            block.renderChildren();
        }

        graph.getModel().endUpdate();
    }

    /**
     * Makes the currently shown graph invisible and deletes it.
     */
    public void deleteGraph() {
        initialGraphState();
    }

    /**
     * Increases the graph's zoom level.
     */
    public void zoomIn() {
        graphComponent.zoomIn();
    }

    /**
     * Decreases the graph's zoom level.
     */
    public void zoomOut() {
        graphComponent.zoomIn();
    }

    /**
     * Sets the {@code VisualGraphPanel}'s activation state
     *
     * @param activated
     *         Iff {@code true}, user interaction is allowed.
     */
    public void setActivated(boolean activated) {
        // TODO implement
    }

    /**
     * Returns a list of {@code BasicBlock}s in the graph.
     *
     * @return list of {@code BasicBlock}s
     */
    public List<UIBasicBlock> getBasicBlocks() {
        return basicBlocks;
    }

    /**
     * Returns this panel's {@code mxGraph}.
     *
     * @return {@code mxGraph}
     */
    public mxGraph getMxGraph() {
        return graph;
    }

    private void autoLayout() {
        new mxHierarchicalLayout(graph).execute(graph.getDefaultParent());
        graphComponent = new mxGraphComponent(graph);
        graphComponent.setVisible(true);
        graphComponent.doLayout();
        add(graphComponent);
    }

    private void initialGraphState() {
        basicBlocks = new ArrayList<>();
        edges = new ArrayList<>();
        graph = new RestrictedMxGraph();
        remove(graphComponent);
        graphComponent = null;
    }
}
