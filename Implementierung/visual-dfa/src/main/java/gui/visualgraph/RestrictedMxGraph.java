package gui.visualgraph;

import com.mxgraph.model.mxCell;
import com.mxgraph.swing.util.mxSwingConstants;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;

import java.awt.*;

/**
 * @author Patrick Petrovic
 *
 *         A subclass of {@code mxGraph} with specific attributes set.
 */
class RestrictedMxGraph extends mxGraph {
    public RestrictedMxGraph() {
        super();

        mxSwingConstants.VERTEX_SELECTION_COLOR = Styles.SELECTION_STROKE_COLOR;
        mxSwingConstants.VERTEX_SELECTION_STROKE = new BasicStroke(Styles.SELECTION_STROKE_WIDTH, 0, 0, 10.0F, null, 0);
        mxConstants.DEFAULT_FONTSIZE = Styles.TEXT_SIZE;
        mxConstants.LABEL_INSET = Styles.TEXT_PADDING_LEFT;
        mxConstants.LINE_HEIGHT = Styles.LINE_HEIGHT;
    }

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

    public boolean isCellMovable(Object cell) {
        return ((mxCell) cell).isVertex() && !getModel().isVertex(getModel().getParent(cell));
    }
}
