package gui.visualgraph;

import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;

import dfa.framework.AnalysisState;

/**
 * Represents an edge in the visual graph.
 * 
 * @author Patrick Petrovic
 */
public class UIEdge extends VisualGraphElement {

    private UIBasicBlock from, to;

    /**
     * Creates a new edge between two given {@code BasicBlock}s.
     *
     * @param graph
     *        the graph this edge will operate on
     * @param from
     *        the BasicBlock this edge will originate from
     * @param to
     *        the BasicBlock to which this edge will lead
     */
    public UIEdge(mxGraph graph, UIBasicBlock from, UIBasicBlock to) {
        this.graph = graph;
        this.from = from;
        this.to = to;
    }

    /**
     * Inserts edge into {@code mxGraph}, if it wasn't already inserted.
     *
     * @param analysisState
     *        the analysisState that should be used to render this edge (reserved for future uses)
     */
    @Override
    public void render(@SuppressWarnings("rawtypes") AnalysisState analysisState) {
        if (cell == null) {
            cell = (mxCell) graph.insertEdge(graph.getDefaultParent(), null, null, from.getMxCell(), to.getMxCell());
        }
    }

    /**
     * Returns the block this edge originates from.
     *
     * @return origin block
     */
    public UIBasicBlock getFrom() {
        return from;
    }

    /**
     * Returns the block this edge points to.
     *
     * @return block pointed to
     */
    public UIBasicBlock getTo() {
        return to;
    }
}
