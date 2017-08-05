package gui.visualgraph;

import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.view.mxGraph;
import org.imgscalr.Scalr;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Responsible for exporting the graph to {@code BufferedImage}.
 *
 * @author Patrick Petrovic
 */
public class GraphExporter {
    private static final int PADDING = 20;

    /**
     * Exports the given graph to {@code BufferedImage}.
     *
     * @param graph
     *         the {@code mxGraph} to export
     * @param scale
     *         the scale of the image (usually 1.0, 2.0 or 3.0)
     * @param statePanel
     *         the {@code JPanel} which will be rendered next to the graph
     *
     * @return a {@code BufferedImage} containing the graph and the panel
     */
    public static BufferedImage exportCurrentGraph(mxGraph graph, double scale, JPanel statePanel) {
        BufferedImage graphImage = mxCellRenderer.createBufferedImage(graph, null, scale, null, true, null);

        BufferedImage statePanelImage = new BufferedImage(statePanel.getWidth(), statePanel.getHeight(), BufferedImage.TYPE_INT_ARGB);
        statePanel.paint(statePanelImage.getGraphics());
        statePanelImage = Scalr.resize(statePanelImage, graphImage.getHeight());

        BufferedImage result = new BufferedImage(graphImage.getWidth() + statePanelImage.getWidth() + 2 * PADDING, graphImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics resultGraphics = result.getGraphics();
        resultGraphics.drawImage(graphImage, PADDING, 0, null);
        resultGraphics.drawImage(statePanelImage, graphImage.getWidth() + 2 * PADDING, 0, null);
        resultGraphics.dispose();

        return result;
    }
}
