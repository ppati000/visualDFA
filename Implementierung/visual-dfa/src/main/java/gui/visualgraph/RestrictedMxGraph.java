package gui.visualgraph;

import com.mxgraph.model.mxCell;
import com.mxgraph.swing.util.mxSwingConstants;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import gui.Colors;

import java.awt.*;

/**
 * @author Patrick Petrovic
 *
 *         A subclass of {@code mxGraph} with specific attributes set.
 */
class RestrictedMxGraph extends mxGraph {
    public RestrictedMxGraph() {
        super();

        // NOTE: Do NOT attempt to change mxConstants.LABEL_INSET. This will break cell selection!
        mxSwingConstants.VERTEX_SELECTION_COLOR = Colors.BACKGROUND.getColor().brighter();
        mxSwingConstants.VERTEX_SELECTION_STROKE = new BasicStroke(Styles.SELECTION_STROKE_WIDTH, 0, 0, 10.0F, null, 0);
        mxConstants.DEFAULT_FONTSIZE = Styles.TEXT_SIZE;
        mxConstants.LINE_HEIGHT = Styles.LINE_HEIGHT;

        setHtmlLabels(true); // With htmlLabels == false, text is not centered properly.
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
