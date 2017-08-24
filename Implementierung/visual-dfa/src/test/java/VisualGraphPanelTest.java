import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;
import dfa.framework.AnalysisState;
import dfa.framework.BasicBlock;
import dfa.framework.DFAExecution;
import dfa.framework.ElementaryBlock;
import gui.visualgraph.*;

import static org.junit.Assert.*;
import org.junit.*;
import soot.Unit;

import static org.mockito.Mockito.mock;

import java.util.Arrays;

public class VisualGraphPanelTest {
    private final double DELTA = 0.001;

    private VisualGraphPanel panel;
    private mxGraph graph;

    private ElementaryBlock mockBlock = new ElementaryBlock(mock(Unit.class));
    private AnalysisState mockState = mock(AnalysisState.class);
    private BasicBlock mockBasicBlock = mock(BasicBlock.class);
    private DFAExecution dfa = mock(DFAExecution.class);

    @Before
    public void createPanel() {
        panel = new VisualGraphPanel();
        graph = panel.getMxGraph();
    }

    @Test
    public void graphShouldContainBasicBlock() {
        panel.insertBasicBlock(new UIBasicBlock(graph, mockBasicBlock, dfa));

        panel.renderGraph(dfa);
        Object[] cells = graph.getChildVertices(graph.getDefaultParent());

        assertEquals(1, cells.length);
        assertEquals(Styles.LINE_HEIGHT, ((mxCell) cells[0]).getGeometry().getHeight(), DELTA);

        assertEquals(0, graph.getChildVertices(cells[0]).length);
    }

    @Test
    public void graphShouldContainChildBlocks() {
        UIBasicBlock basicBlock = new UIBasicBlock(graph, mockBasicBlock, dfa);
        UILineBlock firstLineBlock = new UILineBlock(mockBlock, panel.getGraphComponent(), graph, basicBlock, null);
        UILineBlock secondLineBlock = new UILineBlock(mockBlock, panel.getGraphComponent(), graph, basicBlock, firstLineBlock);
        basicBlock.insertLineBlock(firstLineBlock);
        basicBlock.insertLineBlock(secondLineBlock);

        panel.insertBasicBlock(basicBlock);
        panel.renderGraph(dfa);

        Object[] cells = graph.getChildVertices(graph.getDefaultParent());
        mxCell parentCell = (mxCell) cells[0];

        assertEquals(1, panel.getBasicBlocks().size());
        assertEquals(1, cells.length);
        assertEquals(3 * Styles.LINE_HEIGHT, parentCell.getGeometry().getHeight(), DELTA);

        Object[] childCellObjects = graph.getChildVertices(parentCell);
        mxCell[] childCells = Arrays.copyOf(childCellObjects, childCellObjects.length, mxCell[].class);

        assertEquals(5, childCells.length); // 1 separator + 2 cells + 2 breakpoint cells.
        assertEquals(0, childCells[0].getGeometry().getHeight(), DELTA);
        assertEquals(Styles.LINE_HEIGHT, childCells[0].getGeometry().getY(), DELTA);
        assertEquals(Styles.LINE_HEIGHT, childCells[1].getGeometry().getHeight(), DELTA);
        assertEquals(Styles.LINE_HEIGHT, childCells[1].getGeometry().getY(), DELTA);
        assertEquals(Styles.LINE_HEIGHT / 2, childCells[2].getGeometry().getHeight(), DELTA); // breakpoint cell
        assertEquals(Styles.LINE_HEIGHT, childCells[3].getGeometry().getHeight(), DELTA);
        assertEquals(2 * Styles.LINE_HEIGHT, childCells[3].getGeometry().getY(), DELTA);
    }

    @Test
    public void graphShouldContainEdges() {
        UIBasicBlock basicBlock1 = new UIBasicBlock(graph, mockBasicBlock, dfa);
        UIBasicBlock basicBlock2 = new UIBasicBlock(graph, mockBasicBlock, dfa);
        UIEdge edge = new UIEdge(graph, basicBlock1, basicBlock2);

        panel.insertBasicBlock(basicBlock1);
        panel.insertBasicBlock(basicBlock2);
        panel.insertEdge(edge);
        panel.renderGraph(dfa);

        Object[] cells = graph.getChildVertices(graph.getDefaultParent());
        mxCell firstCell = (mxCell) cells[0];
        mxCell secondCell = (mxCell) cells[1];


        assertEquals(2, panel.getBasicBlocks().size());
        assertEquals(2, cells.length);

        assertEquals(1, graph.getEdges(firstCell).length);
        assertEquals(1, graph.getEdges(secondCell).length);
        assertEquals(1, graph.getEdgesBetween(firstCell, secondCell).length);
    }
}
