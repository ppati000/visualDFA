package dfa.gui.visualgraph;

import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;

/**
 * @author Patrick Petrovic
 *
 *         Represents an edge in the visual graph.
 */
class Edge extends VisualGraphElement {

    private BasicBlock from, to;

    /**
     * Creates a new edge between two given {@code BasicBlock}s.
     *
     * @param graph
     *         the graph this edge will operate on
     * @param from
     *         the BasicBlock this edge will originate from
     * @param to
     *         the BasicBlock to which this edge will lead
     */
    public Edge(mxGraph graph, BasicBlock from, BasicBlock to) {
        this.graph = graph;
        this.from = from;
        this.to = to;
    }

    /**
     * Inserts edge into {@code mxGraph}, if it wasn't already inserted.
     */
    @Override
    public void render() {
        if (cell == null) {
            cell = (mxCell) graph.insertEdge(graph.getDefaultParent(), null, null, from.getMxCell(), to.getMxCell());
        }
    }
}
