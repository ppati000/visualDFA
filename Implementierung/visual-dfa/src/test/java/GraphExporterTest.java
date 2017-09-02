import codeprocessor.CodeProcessor;
import codeprocessor.GraphBuilder;
import com.mxgraph.view.mxGraph;

import dfa.analyses.testanalyses.DummyElement;
import dfa.analyses.testanalyses.DummyFactory;
import dfa.framework.*;
import gui.visualgraph.*;

import static org.junit.Assert.*;

import org.junit.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

@Ignore // TODO QA: These tests are not passing on Windows because of minor rendering differences.
public class GraphExporterTest {
    private VisualGraphPanel panel;
    private JPanel fakeStatePanel;
    private AnalysisState analysisStateMock = mock(AnalysisState.class);
    private DFAExecution dfa = mock(DFAExecution.class);
    private BasicBlock basicBlock = mock(BasicBlock.class);
    private mxGraph graph;
    private final Object lock = new Object();
    private final GraphExporter graphExporter = new GraphExporter();
    private final GraphExporter mockExporter = mock(GraphExporter.class);

    @Before
    public void createPanelAndGraph() {
        panel = new VisualGraphPanel();
        graph = panel.getMxGraph();

        UIBasicBlock basicBlock1 = new UIBasicBlock(graph, basicBlock, dfa);
        UIBasicBlock basicBlock2 = new UIBasicBlock(graph, basicBlock, dfa);
        UIBasicBlock basicBlock3 = new UIBasicBlock(graph, basicBlock, dfa);
        UIBasicBlock basicBlock4 = new UIBasicBlock(graph, basicBlock, dfa);
        UIBasicBlock basicBlock5 = new UIBasicBlock(graph, basicBlock, dfa);
        UIBasicBlock basicBlock6 = new UIBasicBlock(graph, basicBlock, dfa);

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

        panel.renderGraph(dfa);

        fakeStatePanel = new JPanel();
        fakeStatePanel.setLayout(new BorderLayout());
        fakeStatePanel.setSize(new Dimension(300, 1080));
        fakeStatePanel.setBackground(Color.MAGENTA);
        fakeStatePanel.setVisible(true);
        fakeStatePanel.repaint();
    }

    @Test
    public void shouldCreateSmallExportImage() throws IOException {
        BufferedImage exportedImage = graphExporter.exportCurrentGraph(graph, 1.0, null, null);
        BufferedImage referenceImage = ImageIO.read(getClass().getResourceAsStream("/export-small.png"));

        assertEquals("", TestUtils.bufferedImagesEqual(exportedImage, referenceImage, 10, 100, 10));
    }

    @Test
    public void shouldCreateMediumExportImage() throws IOException {
        BufferedImage exportedImage = graphExporter.exportCurrentGraph(graph, 2.0, null, null);
        BufferedImage referenceImage = ImageIO.read(getClass().getResourceAsStream("/export-medium.png"));

        assertEquals("", TestUtils.bufferedImagesEqual(exportedImage, referenceImage, 10, 300, 10));
    }

    @Test
    public void shouldCreateLargeExportImage() throws IOException {
        BufferedImage exportedImage = graphExporter.exportCurrentGraph(graph, 3.0, null, null);
        BufferedImage referenceImage = ImageIO.read(getClass().getResourceAsStream("/export-large.png"));

        assertEquals("", TestUtils.bufferedImagesEqual(exportedImage, referenceImage, 10, 800, 10));
    }

    @Test
    public void shouldPerformBatchExport() throws InterruptedException {
        String code = "public class shouldPerformBatchExportClass { int test(int a) { " +
                "        if (a < 10) { " +
                "          a = 20; " +
                "        } " +
                "        return a*2; " +
                "      } }";


        CodeProcessor codeProcessor = new CodeProcessor(code);
        GraphBuilder builder = new GraphBuilder(codeProcessor.getPath(), codeProcessor.getClassName());
        SimpleBlockGraph blockGraph = builder.buildGraph("int test(int)");

        WorklistManager manager = WorklistManager.getInstance();
        final DFAExecution<DummyElement> dfa = new DFAExecution<DummyElement>(new DummyFactory(), manager.getWorklist(manager.getWorklistNames().get(0), blockGraph), blockGraph, new DFAPrecalcController());
        dfa.setCurrentBlockStep(0);

        final TestGraphExportCallback testCallback = new TestGraphExportCallback();
        new Thread(new Runnable() {
            @Override
            public void run() {
                graphExporter.batchExportAsync(dfa, 1.0, true, testCallback);
            }
        }).start();

        synchronized (lock) {
            lock.wait();
        }

        ArrayList<BufferedImage> imageList = testCallback.images;

        assertEquals(dfa.getTotalElementarySteps() + 1, testCallback.maxStep);
        assertEquals(dfa.getTotalElementarySteps(), imageList.size());
        assertTrue(TestUtils.deltaEqual(603, imageList.get(0).getWidth(), 60));
        assertTrue(TestUtils.deltaEqual(299, imageList.get(0).getHeight(), 60));

        for (int i = 0; i < dfa.getTotalElementarySteps(); i++) {
            assertEquals(i, (int) testCallback.steps.get(i));
        }

        final TestGraphExportCallback blockTestCallback = new TestGraphExportCallback();

        new Thread(new Runnable() {
            @Override
            public void run() {
                graphExporter.batchExportAsync(dfa, 3.0, false, blockTestCallback);
            }
        }).start();

        synchronized (lock) {
            lock.wait();
        }

        ArrayList<BufferedImage> blockImageList = blockTestCallback.images;

        assertEquals(dfa.getTotalBlockSteps() + 1, blockTestCallback.maxStep);
        assertEquals(dfa.getTotalBlockSteps(), blockImageList.size());
        assertTrue(TestUtils.deltaEqual(1729, blockImageList.get(0).getWidth(), 180));
        assertTrue(TestUtils.deltaEqual(895, blockImageList.get(0).getHeight(), 180));

        for (int i = 0; i < dfa.getTotalBlockSteps(); i++) {
            assertEquals(i, (int) blockTestCallback.steps.get(i));
        }
    }

    @Test
    public void graphBatchExportThreadShouldWork() throws InterruptedException {
        new GraphBatchExportThread(mockExporter, dfa, 3.0, false, null).run(); // Runs in current thread.
        verify(mockExporter, times(1)).batchExportAsync(dfa, 3.0, false, null);
    }

    private class TestGraphExportCallback implements GraphExportCallback {
        private ArrayList<BufferedImage> images = new ArrayList<>();
        private ArrayList<Integer> steps = new ArrayList<>();
        private int maxStep;

        @Override
        public void setMaxStep(int step) {
            maxStep = step;
        }

        @Override
        public void setExportStep(int step) {
            steps.add(step);
        }

        @Override
        public void onImageExported(BufferedImage image) {
            images.add(image);
        }

        @Override
        public void done() {
            synchronized (lock) {
                lock.notifyAll();
            }
        }
    }
}
