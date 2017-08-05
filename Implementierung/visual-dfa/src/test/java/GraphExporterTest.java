import com.mxgraph.view.mxGraph;
import gui.visualgraph.*;

import static org.junit.Assert.*;

import org.junit.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GraphExporterTest {
    private VisualGraphPanel panel;
    private JPanel fakeStatePanel;
    private mxGraph graph;

    @Before
    public void createPanelAndGraph() {
        panel = new VisualGraphPanel();
        graph = panel.getMxGraph();

        UIBasicBlock basicBlock1 = new UIBasicBlock(graph);
        UIBasicBlock basicBlock2 = new UIBasicBlock(graph);
        UIBasicBlock basicBlock3 = new UIBasicBlock(graph);
        UIBasicBlock basicBlock4 = new UIBasicBlock(graph);
        UIBasicBlock basicBlock5 = new UIBasicBlock(graph);
        UIBasicBlock basicBlock6 = new UIBasicBlock(graph);

        UIEdge edge1 = new UIEdge(graph, basicBlock1, basicBlock2);
        UIEdge edge2 = new UIEdge(graph, basicBlock2, basicBlock3);
        UIEdge edge3 = new UIEdge(graph, basicBlock2, basicBlock4);
        UIEdge edge4 = new UIEdge(graph, basicBlock3, basicBlock5);
        UIEdge edge5 = new UIEdge(graph, basicBlock4, basicBlock5);
        UIEdge edge6 = new UIEdge(graph, basicBlock5, basicBlock6);

        panel.insertBasicBlock(basicBlock1);
        panel.insertBasicBlock(basicBlock2);
        panel.insertBasicBlock(basicBlock3);
        panel.insertBasicBlock(basicBlock4);
        panel.insertBasicBlock(basicBlock5);
        panel.insertBasicBlock(basicBlock6);

        panel.insertEdge(edge1);
        panel.insertEdge(edge2);
        panel.insertEdge(edge3);
        panel.insertEdge(edge4);
        panel.insertEdge(edge5);
        panel.insertEdge(edge6);

        panel.renderGraph(true);

        fakeStatePanel = new JPanel();
        fakeStatePanel.setLayout(new BorderLayout());
        fakeStatePanel.setSize(new Dimension(300, 1080));
        fakeStatePanel.setBackground(Color.MAGENTA);
        fakeStatePanel.setVisible(true);
        fakeStatePanel.repaint();
    }

    @Test
    public void shouldCreateSmallExportImage() throws IOException {
        BufferedImage exportedImage = GraphExporter.exportCurrentGraph(graph, 1.0, fakeStatePanel);
        BufferedImage referenceImage = ImageIO.read(getClass().getResourceAsStream("/export-small.png"));

        assertEquals("", TestUtils.bufferedImagesEqual(exportedImage, referenceImage, 10, 100));
    }

    @Test
    public void shouldCreateMediumExportImage() throws IOException {
        BufferedImage exportedImage = GraphExporter.exportCurrentGraph(graph, 2.0, fakeStatePanel);
        BufferedImage referenceImage = ImageIO.read(getClass().getResourceAsStream("/export-medium.png"));

        assertEquals("", TestUtils.bufferedImagesEqual(exportedImage, referenceImage, 10, 300));
    }

    @Test
    public void shouldCreateLargeExportImage() throws IOException {
        BufferedImage exportedImage = GraphExporter.exportCurrentGraph(graph, 3.0, fakeStatePanel);
        BufferedImage referenceImage = ImageIO.read(getClass().getResourceAsStream("/export-large.png"));

        assertEquals("", TestUtils.bufferedImagesEqual(exportedImage, referenceImage, 10, 800));
    }
}
