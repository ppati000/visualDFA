package gui.visualgraph;

import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.view.mxGraph;
import dfa.framework.DFAExecution;
import gui.ProgramFrame;
import gui.StatePanelOpen;
import org.imgscalr.Scalr;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

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

    public static ArrayList<BufferedImage> batchExport(DFAExecution dfa, double scale, boolean includeLineSteps) {
        dfa = dfa.clone();

        StatePanelOpen statePanel = new StatePanelOpen(null);
        statePanel.setSize(300, 1080);
        VisualGraphPanel panel = new VisualGraphPanel();
        panel.setJumpToAction(true);

        GraphUIController controller = new GraphUIController(panel);
        controller.setStatePanel(statePanel);
        controller.start(dfa);

        if (includeLineSteps) {
            return performLineBatchExport(dfa, controller, panel, statePanel, scale);
        }

        return performBlockBatchExport(dfa, controller, panel, statePanel, scale);
    }

    private static ArrayList<BufferedImage> performBlockBatchExport(DFAExecution dfa, GraphUIController controller,
                                                           VisualGraphPanel panel, StatePanelOpen statePanel, double scale) {
        ArrayList<BufferedImage> result = new ArrayList<>();

        for (int blockStep = 0; blockStep < dfa.getTotalBlockSteps(); blockStep++) {
            dfa.setCurrentBlockStep(blockStep);
            controller.refresh();

            result.add(exportCurrentGraph(panel.getMxGraph(), scale, statePanel));
        }

        return result;
    }

    private static ArrayList<BufferedImage> performLineBatchExport(DFAExecution dfa, GraphUIController controller,
                                                           VisualGraphPanel panel, StatePanelOpen statePanel, double scale) {
        ArrayList<BufferedImage> result = new ArrayList<>();

        for (int lineStep = 0; lineStep < dfa.getTotalElementarySteps(); lineStep++) {
            dfa.setCurrentElementaryStep(lineStep);
            controller.refresh();

            result.add(exportCurrentGraph(panel.getMxGraph(), scale, statePanel));
        }

        return result;
    }
}
