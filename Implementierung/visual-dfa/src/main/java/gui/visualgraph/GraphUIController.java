package gui.visualgraph;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;
import com.mxgraph.view.mxGraph;
import dfa.framework.*;
import gui.StatePanelOpen;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Patrick Petrovic
 *
 *         Main interface for interaction. Provides important methods to get data from DFAFramework and to build the
 *         visual graph. Additionally, it handles creation of VisualGraphPanel and graph export.
 */
public class GraphUIController {
    private VisualGraphPanel panel;
    private mxGraph graph;
    private DFAExecution dfa;
    private StatePanelOpen statePanel = null;

    /**
     * Creates a new {@code GraphUIController}.
     *
     * @param panel
     *         the {@code VisualGraphPanel} this controller should operate on
     */
    public GraphUIController(VisualGraphPanel panel) {
        this.panel = panel;
        this.graph = panel.getMxGraph();
    }

    /**
     * Gets all relevant data from DFAFramework and orders {@code VisualGraphPanel} to create and render the
     * corresponding visual graph.
     *
     * @param dfa
     *         the {@code DFAExecution} of the current data-flow analysis
     */
    public void start(final DFAExecution dfa) {
        if (this.dfa != null) {
            throw new IllegalStateException("Visual graph was already built.");
        }

        this.dfa = dfa;

        Map<AbstractBlock, UIAbstractBlock> mappedAbstractBlocks = new HashMap<>();
        ControlFlowGraph dfaGraph = dfa.getCFG();
        List<BasicBlock> dfaBasicBlocks = dfaGraph.getBasicBlocks();
        Map<BasicBlock, UIBasicBlock> mappedBasicBlocks = new HashMap<>();
        List<UIAbstractBlock> uiBlocks = new ArrayList<>();
        final Map<mxCell, UIAbstractBlock> mxCellMap = new HashMap<>();

        // First step: Build all visual blocks from DFAFramework data.
        for (BasicBlock dfaBasicBlock : dfaBasicBlocks) {
            UIBasicBlock basicBlock = new UIBasicBlock(graph, dfaBasicBlock, dfa);
            uiBlocks.add(basicBlock);
            List<ElementaryBlock> elementaryBlocks = dfaBasicBlock.getElementaryBlocks();

            if (elementaryBlocks.size() != 0) {
                List<UILineBlock> lineBlocks = new ArrayList<>();

                // The first UILineBlock is a special case: it has no predecessor.
                ElementaryBlock firstElementaryBlock = elementaryBlocks.get(0);
                UILineBlock firstLineBlock = new UILineBlock(firstElementaryBlock, panel.getGraphComponent(), graph, basicBlock);
                lineBlocks.add(firstLineBlock);
                uiBlocks.add(firstLineBlock);
                basicBlock.insertLineBlock(lineBlocks.get(0));
                mappedAbstractBlocks.put(firstElementaryBlock, firstLineBlock);

                for (int i = 1; i < elementaryBlocks.size(); i++) {
                    ElementaryBlock currentElementaryBlock = elementaryBlocks.get(i);
                    UILineBlock newLineBlock = new UILineBlock(currentElementaryBlock, panel.getGraphComponent(), graph, basicBlock, lineBlocks.get(i - 1));
                    lineBlocks.add(newLineBlock);
                    uiBlocks.add(newLineBlock);
                    basicBlock.insertLineBlock(lineBlocks.get(i));
                    mappedAbstractBlocks.put(currentElementaryBlock, newLineBlock);
                }
            }

            mappedAbstractBlocks.put(dfaBasicBlock, basicBlock);
            mappedBasicBlocks.put(dfaBasicBlock, basicBlock);
            panel.insertBasicBlock(basicBlock);
        }

        panel.setBlockMap(mappedAbstractBlocks);

        // Now all visual blocks have been built, so we can create edges.
        for (BasicBlock dfaBasicBlock : dfaBasicBlocks) {
            UIBasicBlock currentBlock = mappedBasicBlocks.get(dfaBasicBlock);
            List<UIBasicBlock> successors = new ArrayList<>();

            for (BasicBlock dfaSuccessor : dfaGraph.getSuccessors(dfaBasicBlock)) {
                successors.add(mappedBasicBlocks.get(dfaSuccessor));
            }

            for (UIBasicBlock successor : successors) {
                panel.insertEdge(new UIEdge(graph, currentBlock, successor));
            }
        }

        panel.renderGraph(dfa, true);

        for (UIAbstractBlock block : uiBlocks) {
            mxCellMap.put(block.getMxCell(), block);
        }

        graph.getSelectionModel().addListener(mxEvent.CHANGE, new mxEventSource.mxIEventListener() {
            @Override
            public void invoke(Object o, mxEventObject mxEventObject) {
                if (statePanel != null) {
                    // Weird API: "removed" cells are actually the newly selected cells.
                    ArrayList<mxCell> selectedCells = (ArrayList<mxCell>) mxEventObject.getProperty("removed");
                    if (selectedCells != null && selectedCells.size() > 0) {
                        mxCell selectedCell = selectedCells.get(0);
                        AbstractBlock selectedBlock = mxCellMap.get(selectedCell).getDFABlock();

                        BlockState currentState = dfa.getCurrentAnalysisState().getBlockState(selectedBlock);
                        String inState = currentState.getInState().getStringRepresentation();
                        String outState = currentState.getOutState().getStringRepresentation();

                        statePanel.setIn(inState);
                        statePanel.setOut(outState);
                    }
                }
            }
        });
    }

    /**
     * Sets the {@code StatePanelOpen} to show the results in. If {@code null}, no results will be shown.
     *
     * @param statePanel
     *         the state panel
     */
    public void setStatePanel(StatePanelOpen statePanel) {
        this.statePanel = statePanel;
    }

    /**
     * Refreshes the (already rendered) visual graph using the {@code DFAExecution} instance previously given to {@code
     * start()}.
     */
    public void refresh() {
        if (dfa == null) {
            throw new IllegalStateException("Graph has not been built using start() yet.");
        }

        panel.renderGraph(dfa, false);
    }

    /**
     * Deletes the current visual graph and clears the {@code DFAExecution} previously given to {@code start()}
     */
    public void stop() {
        dfa = null;
        panel.deleteGraph();
        graph = panel.getMxGraph();
    }

    public JPanel getVisualGraphPanel() {
        return panel;
    }

    public void exportGraph(boolean batch) {

    }
}
