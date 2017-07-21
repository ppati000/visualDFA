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

    public void insertBasicBlock(UIBasicBlock block) {
        basicBlocks.add(block);
    }

    public void insertEdge(UIEdge edge) {
        edges.add(edge);
    }

    public void renderGraph() {
        graph.getModel().beginUpdate();

        for (UIBasicBlock block : basicBlocks) {
            block.render();
        }

        for (UIEdge edge : edges) {
            edge.render();
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
     * Applies the auto-layouter on the graph.
     */
    public void autoLayout() {
        // TODO: Not working as expected yet
        new mxHierarchicalLayout(graph).execute(graph.getDefaultParent());
        graphComponent = new mxGraphComponent(graph);
        graphComponent.setVisible(true);
        graphComponent.doLayout();
        add(graphComponent);
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

    private void initialGraphState() {
        basicBlocks = new ArrayList<>();
        edges = new ArrayList<>();
        graph = new RestrictedMxGraph();
        remove(graphComponent);
        graphComponent = null;
    }
}
