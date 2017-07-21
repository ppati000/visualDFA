package gui.visualgraph;

import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;

/**
 * @author Patrick Petrovic
 *
 *         A subclass of {@code mxGraph} with specific attributes set.
 */
class RestrictedMxGraph extends mxGraph {
    public boolean isCellFoldable(Object cell, boolean collapse) {
        return false;
    }

    public boolean isCellSelectable(Object cell) {
        return !model.isEdge(cell);
    }

    public boolean isCellEditable(Object cell) {
        return false;
    }

    public boolean isCellConnectable(Object cell) {
        return false;
    }

    public boolean isCellResizable(Object cell) {
        return false;
    }

    // TODO: Make only parent cells movable
    public boolean isCellMovable(Object cell) {
        return ((mxCell) cell).isVertex();
    }
}
