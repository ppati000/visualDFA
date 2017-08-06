package gui.visualgraph;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import gui.*;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private Frame parentFrame;

    private final Color BLUE_HIGHLIGHT_COLOR = new Color(188, 230, 254);
    private final Color ALMOST_WHITE_COLOR = new Color(251, 253, 255);
    private final Color TEXT_COLOR = new Color(17, 37, 48);

    /**
     * Creates a new {@code VisualGraphPanel}.
     */
    public VisualGraphPanel() {
        this.basicBlocks = new ArrayList<>();
        this.edges = new ArrayList<>();
        this.graph = new RestrictedMxGraph();
        setLayout(new BorderLayout());

        // TODO: Add listeners to buttons
        jumpToAction = new GraphJButton("Jump to Action");
        graphExport = new GraphJButton("Export Graph");

        jumpToAction.setIcon(IconLoader.loadIcon("icons/map-marker.png", 0.2));
        jumpToAction.setPreferredSize(new Dimension(145, 40));

        graphExport.setIcon(IconLoader.loadIcon("icons/share-symbol.png", 0.2));
        graphExport.setPreferredSize(new Dimension(130, 40));

        JPanel buttonGroup = new JPanel();
        buttonGroup.setLayout(new FlowLayout(FlowLayout.RIGHT));
        buttonGroup.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        buttonGroup.setSize(getWidth(), 60);
        buttonGroup.add(graphExport);
        buttonGroup.add(jumpToAction);
        buttonGroup.setBackground(ALMOST_WHITE_COLOR);
        add(buttonGroup, BorderLayout.NORTH);

        graphExport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: Decide which export to do and do actual export.
                new GraphExportBox(parentFrame).setVisible(true);
            }
        });

        initialGraphState();
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

    /**
     * Returns this panel's {@code mxGraphComponent}.
     *
     * @return {@code mxGraphComponent}
     */
    public mxGraphComponent getGraphComponent() {
        return graphComponent;
    }

    public void setParentFrame(Frame frame) {
        this.parentFrame = frame;
    }

    private void autoLayout() {
        new mxHierarchicalLayout(graph).execute(graph.getDefaultParent());
        graphComponent.setVisible(true);
        graphComponent.doLayout();
        add(graphComponent, BorderLayout.CENTER);
    }

    private void initialGraphState() {
        basicBlocks = new ArrayList<>();
        edges = new ArrayList<>();
        graph = new RestrictedMxGraph();
        if (graphComponent != null) {
            remove(graphComponent);
        }
        graphComponent = new mxGraphComponent(graph);
        graphComponent.setBorder(new LineBorder(new Color(188, 230, 254)));
        graphComponent.getViewport().setBackground(new Color(251, 253, 255));
    }

    private class GraphJButton extends JButton {
        GraphJButton(String text) {
            super(text);
            setOpaque(true);
            setBackground(ALMOST_WHITE_COLOR);
            setForeground(TEXT_COLOR);
            setBorder(new LineBorder(BLUE_HIGHLIGHT_COLOR, 2, true));

            final ButtonModel startModel = getModel();
            startModel.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {
                    if (getModel().isPressed()) {
                        setBackground(BLUE_HIGHLIGHT_COLOR);
                    } else {
                        setBackground(ALMOST_WHITE_COLOR);
                    }
                }

            });
        }
    }
}
