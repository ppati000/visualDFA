package dfa.gui.visualgraph;

import com.mxgraph.view.mxGraph;
import dfa.framework.DFAExecution;

import javax.swing.*;

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
    }

    public void refresh() {

    }

    public void stop() {

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
