package gui.visualgraph;

import com.mxgraph.view.mxGraph;
import dfa.framework.BasicBlock;
import dfa.framework.ControlFlowGraph;
import dfa.framework.DFAExecution;
import dfa.framework.ElementaryBlock;

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

    public GraphUIController(VisualGraphPanel panel) {
        this.panel = panel;
        this.graph = panel.getMxGraph();
    }

    public void start(DFAExecution dfa) {
        this.dfa = dfa;

        ControlFlowGraph dfaGraph = dfa.getCFG();
        List<BasicBlock> dfaBasicBlocks = dfaGraph.getBasicBlocks();
        Map<BasicBlock, UIBasicBlock> mappedBasicBlocks = new HashMap<>();

        // First step: Build all visual blocks from DFAFramework data.
        for (BasicBlock dfaBasicBlock : dfaBasicBlocks) {
            UIBasicBlock basicBlock = new UIBasicBlock(graph);
            List<ElementaryBlock> elementaryBlocks = dfaBasicBlock.getElementaryBlocks();

            if (elementaryBlocks.size() != 0) {
                List<UILineBlock> lineBlocks = new ArrayList<>();

                // The first lineBlock is a special case: it has no predecessor.
                lineBlocks.add(new UILineBlock(graph, basicBlock, null));
                basicBlock.insertLineBlock(lineBlocks.get(0));

                for (int i = 1; i < elementaryBlocks.size(); i++) {
                    lineBlocks.add(new UILineBlock(graph, basicBlock, lineBlocks.get(i - 1)));
                    basicBlock.insertLineBlock(lineBlocks.get(i));
                }
            }

            mappedBasicBlocks.put(dfaBasicBlock, basicBlock);
            panel.insertBasicBlock(basicBlock);
        }

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

        panel.renderGraph();
        panel.autoLayout();
    }

    public void refresh() {
        if (dfa == null) {
            throw new IllegalStateException("Graph has not been built using start() yet.");
        }

        panel.renderGraph();
    }

    public void stop() {
        dfa = null;
        panel.deleteGraph();
        graph = panel.getMxGraph();
    }

    public void setSelectedBlock(UIAbstractBlock block) {

    }

    public void toggleBreakpoint(UILineBlock block) {

    }

    public void toggleJumpToAction() {

    }

    public JPanel getVisualGraphPanel() {
        return panel;
    }

    public void exportGraph(boolean batch) {

    }
}
