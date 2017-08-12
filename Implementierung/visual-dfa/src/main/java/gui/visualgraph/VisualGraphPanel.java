package gui.visualgraph;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import dfa.framework.AbstractBlock;
import dfa.framework.BlockState;
import dfa.framework.DFAExecution;
import gui.*;
import dfa.framework.AnalysisState;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
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
    private JToggleButton jumpToAction;
    private JButton graphExport;
    private mxGraph graph;
    private Frame parentFrame = null;
    private Map<AbstractBlock, UIAbstractBlock> blockMap;
    private UIAbstractBlock selectedBlock;

    /**
     * Creates a new {@code VisualGraphPanel}.
     */
    public VisualGraphPanel() {
        this.basicBlocks = new ArrayList<>();
        this.edges = new ArrayList<>();
        this.graph = new RestrictedMxGraph();
        this.blockMap = new HashMap<>();
        setLayout(new BorderLayout());

        jumpToAction = new JToggleButton("Jump to Action");
        graphExport = new JButton("Export Graph");
        decorateGraphButton(jumpToAction, false);
        decorateGraphButton(graphExport, true);

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
        buttonGroup.setBackground(Colors.WHITE_BACKGROUND.getColor());
        add(buttonGroup, BorderLayout.NORTH);

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

    protected void setBlockMap(Map<AbstractBlock, UIAbstractBlock> map) {
        this.blockMap = map;
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
     * @param dfa
     *         the {@code DFAExecution} that should be used to render this graph
     * @param isFirstRender
     *         If {@code true}, the auto-layouter is called after inserting all parent blocks. Used on first render.
     */
    public void renderGraph(final DFAExecution dfa, boolean isFirstRender) {
        AnalysisState analysisState = dfa.getCurrentAnalysisState();

        graph.getModel().beginUpdate();

        for (UIBasicBlock block : basicBlocks) {
            block.render(analysisState);
        }

        for (UIEdge edge : edges) {
            edge.render(analysisState);
        }

        // Apply layout before rendering child blocks, so that the layouter doesn't mess with them.
        if (isFirstRender) {
            autoLayoutAndShowGraph();

            graphExport.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    GraphExportBox exportBox = new GraphExportBox(parentFrame);

                    float scale = exportBox.getQuality().ordinal() + 1;
                    String outputPath = System.getProperty("user.home") + File.separator + "visualDFA";

                    try {
                        List<BufferedImage> images;

                        if (exportBox.isBatchExport()) {
                            images = GraphExporter.batchExport(dfa, scale, exportBox.includeLineStates());
                        } else {
                            images = new ArrayList<>();
                            BlockState state = selectedBlock == null ? null : dfa.getCurrentAnalysisState().getBlockState(selectedBlock.getDFABlock());

                            images.add(GraphExporter.exportCurrentGraph(graph, scale, selectedBlock, state));
                        }

                        File outputDir = new File(outputPath);
                        long timestamp = new Date().getTime();

                        if (!new File(outputPath).exists()) {
                            boolean result = outputDir.mkdir();
                            if (!result) {
                                throw new IOException();
                            }
                        }

                        for (int i = 0; i < images.size(); i++) {
                            File outputFile = new File(outputPath + File.separator + "export_" + timestamp + "_" + i + ".png");
                            ImageIO.write(images.get(i), "PNG", outputFile);
                        }
                    } catch (IOException ex) {
                        new MessageBox(parentFrame, "Graph Export Failed", "An error occured while saving your image(s). \n" +
                                "Please ensure " + outputPath + " is a writable directory.").setVisible(true);
                    }
                }
            });
        }

        for (UIBasicBlock block : basicBlocks) {
            block.renderChildren(analysisState);
        }

        graph.getModel().endUpdate();

        if (jumpToAction.isSelected() && blockMap != null) {
            graphComponent.getGraph().clearSelection();
            AnalysisState currentState = dfa.getCurrentAnalysisState();
            AbstractBlock currentBlock;

            if (currentState.getCurrentElementaryBlockIndex() == -1) {
                currentBlock = currentState.getCurrentBasicBlock();
            } else {
                currentBlock = currentState.getCurrentElementaryBlock();
            }

            if (currentBlock != null) {
                mxCell currentMxCell = blockMap.get(currentBlock).getMxCell();
                graph.getSelectionModel().addCell(currentMxCell);
                graphComponent.scrollCellToVisible(currentMxCell);
            } else {
                graph.getSelectionModel().clear();
            }
        }
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
        graphComponent.zoomOut();
    }

    /**
     * Sets the {@code VisualGraphPanel}'s activation state
     *
     * @param activated
     *         Iff {@code true}, user interaction is allowed.
     */
    public void setActivated(boolean activated) {
        jumpToAction.setEnabled(activated);
        graphExport.setEnabled(activated);
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

    /**
     * Sets the parent frame (used for modals)
     *
     * @param frame
     *         the parent frame
     */
    public void setParentFrame(Frame frame) {
        this.parentFrame = frame;
    }

    /**
     * Enables or disables Jump to Action
     *
     * @param enabled
     *         if it should be enabled or disabled
     */
    public void setJumpToAction(boolean enabled) {
        jumpToAction.setSelected(enabled);
    }

    /**
     * Used by {@code GraphUIController} to set the panel's selected {@code UIAbstractBlock} for later use.
     *
     * @param block
     *         the block that was determined as the current one by the {@code GraphUIController}.
     */
    public void setSelectedBlock(UIAbstractBlock block) {
        this.selectedBlock = block;
    }

    /**
     * Used by {@code GraphUIController} to get the selected block when updating the {@code StatePanelOpen}.
     *
     * @return the currently selected block
     */
    public UIAbstractBlock getSelectedBlock() {
        return this.selectedBlock;
    }

    private void autoLayoutAndShowGraph() {
        new mxHierarchicalLayout(graph).execute(graph.getDefaultParent());
        graphComponent.setVisible(true);
        graphComponent.doLayout();

        add(graphComponent, BorderLayout.CENTER);
        revalidate();
    }

    private void initialGraphState() {
        basicBlocks = new ArrayList<>();
        edges = new ArrayList<>();
        graph = new RestrictedMxGraph();

        if (graphComponent != null) {
            remove(graphComponent);
            revalidate();
        }

        graphComponent = new mxGraphComponent(graph);
        graphComponent.setBorder(new LineBorder(new Color(188, 230, 254)));
        graphComponent.getViewport().setBackground(new Color(251, 253, 255));

        graphComponent.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.getWheelRotation() > 0) {
                    zoomIn();
                } else if (e.getWheelRotation() < 0) {
                    zoomOut();
                }
            }
        });
    }

    private void decorateGraphButton(final AbstractButton button, final boolean highlightClickedOnly) {
        button.setOpaque(true);
        button.setBackground(Colors.LIGHT_TEXT.getColor());
        button.setForeground(Colors.DARK_TEXT.getColor());
        button.setBorder(new LineBorder(Colors.LIGHT_BACKGROUND.getColor(), 2, true));

        final ButtonModel startModel = button.getModel();
        startModel.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                if (highlightClickedOnly && button.getModel().isPressed() || button.getModel().isSelected()) {
                    button.setBackground(Colors.LIGHT_BACKGROUND.getColor());
                } else {
                    button.setBackground(Colors.WHITE_BACKGROUND.getColor());
                }
            }

        });
    }
}
