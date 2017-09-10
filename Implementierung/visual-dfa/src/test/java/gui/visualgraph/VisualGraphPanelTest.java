package gui.visualgraph;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;

import dfa.framework.BasicBlock;
import dfa.framework.DFAExecution;
import dfa.framework.ElementaryBlock;
import dfa.framework.LatticeElement;
import soot.Unit;

public class VisualGraphPanelTest {
    private final double DELTA = 0.001;

    private VisualGraphPanel panel;
    private mxGraph graph;

    private ElementaryBlock mockBlock = new ElementaryBlock(mock(Unit.class));
    private BasicBlock mockBasicBlock = mock(BasicBlock.class);
    @SuppressWarnings("unchecked")
    private DFAExecution<? extends LatticeElement> dfa = mock(DFAExecution.class);

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
        mxCell basicBlockCell = (mxCell) cells[0];
        assertEquals(Styles.LINE_HEIGHT, basicBlockCell.getGeometry().getHeight(), DELTA);
        assertEquals(0, graph.getChildVertices(basicBlockCell).length);

        assertEquals(true, graph.isCellMovable(basicBlockCell));
        assertEquals(true, graph.isCellSelectable(basicBlockCell));
        assertEquals(false, graph.isCellFoldable(basicBlockCell, false));
        assertEquals(false, graph.isCellFoldable(basicBlockCell, true));
        assertEquals(false, graph.isCellEditable(basicBlockCell));
        assertEquals(false, graph.isCellConnectable(basicBlockCell));
        assertEquals(false, graph.isCellResizable(basicBlockCell));

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

        assertEquals(false, graph.isCellMovable(childCells[0]));
        assertEquals(true, graph.isCellSelectable(childCells[0]));
        assertEquals(false, graph.isCellFoldable(childCells[0], false));
        assertEquals(false, graph.isCellFoldable(childCells[0], true));
        assertEquals(false, graph.isCellEditable(childCells[0]));
        assertEquals(false, graph.isCellConnectable(childCells[0]));
        assertEquals(false, graph.isCellResizable(childCells[0]));
    }

    @Test
    public void graphShouldContainEdges() {
        UIBasicBlock basicBlock1 = new UIBasicBlock(graph, mockBasicBlock, dfa);
        UIBasicBlock basicBlock2 = new UIBasicBlock(graph, mockBasicBlock, dfa);
        UIEdge uiEdge = new UIEdge(graph, basicBlock1, basicBlock2);

        panel.insertBasicBlock(basicBlock1);
        panel.insertBasicBlock(basicBlock2);
        panel.insertEdge(uiEdge);
        panel.renderGraph(dfa);

        Object[] cells = graph.getChildVertices(graph.getDefaultParent());
        mxCell firstCell = (mxCell) cells[0];
        mxCell secondCell = (mxCell) cells[1];


        assertEquals(2, panel.getBasicBlocks().size());
        assertEquals(2, cells.length);

        assertEquals(1, graph.getEdges(firstCell).length);
        assertEquals(1, graph.getEdges(secondCell).length);

        Object[] edges = graph.getEdgesBetween(firstCell, secondCell);
        assertEquals(1, edges.length);

        mxCell edge = (mxCell) edges[0];

        assertEquals(false, graph.isCellMovable(edge));
        assertEquals(false, graph.isCellSelectable(edge));
        assertEquals(false, graph.isCellFoldable(edge, false));
        assertEquals(false, graph.isCellFoldable(edge, true));
        assertEquals(false, graph.isCellEditable(edge));
        assertEquals(false, graph.isCellConnectable(edge));
        assertEquals(false, graph.isCellResizable(edge));
    }

    @Test
    public void shouldSetBreakpoint() {
        UIBasicBlock basicBlock = new UIBasicBlock(graph, mockBasicBlock, dfa);
        UILineBlock lineBlock = new UILineBlock(mockBlock, panel.getGraphComponent(), graph, basicBlock);

        panel.insertBasicBlock(basicBlock);
        basicBlock.insertLineBlock(lineBlock);
        panel.renderGraph(dfa);

        mxCell breakpointCell = lineBlock.getBreakpointCell();
        assertEquals("strokeColor=none;fillColor=rgba(255, 255, 255, 0)", breakpointCell.getStyle());
        assertEquals(false, lineBlock.getDFABlock().hasBreakpoint());
        assertEquals(false, lineBlock.hasBreakpoint());

        lineBlock.toggleBreakpoint();
        assertEquals("strokeColor=none;fillColor=#dd7063", breakpointCell.getStyle());
        assertEquals(true, lineBlock.getDFABlock().hasBreakpoint());
        assertEquals(true, lineBlock.hasBreakpoint());

        lineBlock.toggleBreakpoint();
        assertEquals("strokeColor=none;fillColor=rgba(255, 255, 255, 0)", breakpointCell.getStyle());
        assertEquals(false, lineBlock.getDFABlock().hasBreakpoint());
        assertEquals(false, lineBlock.hasBreakpoint());
    }
}
