import codeprocessor.CodeProcessor;
import codeprocessor.GraphBuilder;
import controller.Controller;

import com.mxgraph.swing.mxGraphComponent;
import dfa.analyses.testanalyses.DummyElement;
import dfa.analyses.testanalyses.DummyFactory;
import dfa.framework.*;
import gui.StatePanelOpen;
import gui.visualgraph.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.*;
import org.mockito.InOrder;

import java.util.List;

@Ignore
public class GraphUIControllerTest {
    private VisualGraphPanel panel;
    private GraphUIController controller;
    private String programOutputPath;

    private final String exampleCode = "public class shouldCreateGraphOnStartClass { public void helloWorld(boolean print) {" +
            "          if (print) {" +
            "              System.out.println(\"Hello World!\");" +
            "          } else {" +
            "              System.out.println(\"Not Hello World!\");" +
            "          }" +
            "      } }";

    // Verifying the text of the UIBasicBlocks gives us confidence that the UILineBlocks were inserted correctly.
    // Whether or not they were also *rendered* correctly is another story, tested in VisualGraphPanelTest.
    private final String startBlockOutput = "this := @this: shouldCreateGraphOnStartClass\n" +
            "print := @parameter0: boolean\n" +
            "if print == 0 goto $r0 = <java.lang.System: java.io.PrintStream out>";

    private final String secondBlockOutput = "$r1 = <java.lang.System: java.io.PrintStream out>\n" +
            "virtualinvoke $r1.<java.io.PrintStream: void println()>()\n" +
            "goto [?= return]";

    private final String thirdBlockOutput = "$r0 = <java.lang.System: java.io.PrintStream out>\n" +
            "virtualinvoke $r0.<java.io.PrintStream: void println()>()";


    @Before
    public void createPanel() {
        Controller ctrl = new Controller();
        this.programOutputPath = ctrl.getProgramOutputPath();
        panel = new VisualGraphPanel();
        controller = new GraphUIController(panel);
    }

    @Test(expected = IllegalStateException.class)
    public void refreshShouldNotBePossibleBeforeStart() {
        controller.refresh();
    }

    @Test(expected = IllegalStateException.class)
    public void startShouldNotBePossibleTwice() {
        CodeProcessor codeProcessor = new CodeProcessor("void emptyInside() {}", this.programOutputPath);
        GraphBuilder builder = new GraphBuilder(codeProcessor.getPath(), codeProcessor.getClassName());
        SimpleBlockGraph blockGraph = builder.buildGraph("void emptyInside()");
        DFAExecution<DummyElement> dfa = new DFAExecution<>(new DummyFactory(), new NaiveWorklist(), blockGraph, new DFAPrecalcController());

        controller.start(dfa);
        controller.start(dfa);
    }

    @Test
    public void shouldBuildGraphOnStartAndUpdateOnRefresh() {
        DFAExecution dfa = buildDFA(exampleCode);
        List<BasicBlock> dfaBasicBlocks = dfa.getCFG().getBasicBlocks();

        controller.start(dfa);
        assertNotEquals(null, panel.getMxGraph());
        assertNotEquals(null, panel.getGraphComponent());

        List<UIBasicBlock> uiBasicBlocks = panel.getBasicBlocks();
        assertEquals(4, uiBasicBlocks.size());

        UIBasicBlock startBlock = uiBasicBlocks.get(0);
        assertEquals(startBlockOutput, startBlock.getText());
        assertEquals(0, startBlock.getBlockNumber());
        assertEquals(-1, startBlock.getLineNumber());
        assertEquals(startBlock.getDFABlock(), dfaBasicBlocks.get(0));

        UIBasicBlock secondBlock = uiBasicBlocks.get(1);
        assertEquals(secondBlockOutput, secondBlock.getText());
        assertEquals(1, secondBlock.getBlockNumber());
        assertEquals(-1, secondBlock.getLineNumber());
        assertEquals(secondBlock.getDFABlock(), dfaBasicBlocks.get(1));

        UIBasicBlock thirdBlock = uiBasicBlocks.get(2);
        assertEquals(thirdBlockOutput, thirdBlock.getText());
        assertEquals(2, thirdBlock.getBlockNumber());
        assertEquals(-1, thirdBlock.getLineNumber());
        assertEquals(thirdBlock.getDFABlock(), dfaBasicBlocks.get(2));

        UIBasicBlock endBlock = uiBasicBlocks.get(3);
        assertEquals("return", endBlock.getText());
        assertEquals(3, endBlock.getBlockNumber());
        assertEquals(-1, endBlock.getLineNumber());
        assertEquals(endBlock.getDFABlock(), dfaBasicBlocks.get(3));

        List<UIEdge> edges = panel.getEdges();
        assertEquals(4, edges.size());

        UIEdge firstEdge = edges.get(0);
        assertEquals(startBlock, firstEdge.getFrom());
        assertEquals(secondBlock, firstEdge.getTo());

        UIEdge secondEdge = edges.get(1);
        assertEquals(startBlock, secondEdge.getFrom());
        assertEquals(thirdBlock, secondEdge.getTo());

        UIEdge thirdEdge = edges.get(2);
        assertEquals(secondBlock, thirdEdge.getFrom());
        assertEquals(endBlock, thirdEdge.getTo());

        UIEdge fourthEdge = edges.get(3);
        assertEquals(thirdBlock, fourthEdge.getFrom());
        assertEquals(endBlock, fourthEdge.getTo());


        panel.setJumpToAction(true);
        assertEquals(null, panel.getSelectedBlock());
        dfa.nextBlockStep();

        controller.refresh();
        assertEquals(panel.getSelectedBlock(), secondBlock);

        mxGraphComponent oldComponent = panel.getGraphComponent();
        controller.stop();
        mxGraphComponent newComponent = panel.getGraphComponent();
        assertNotEquals(oldComponent, newComponent);

        // Should be able to start a second time.
        controller.start(dfa);
        assertEquals(newComponent, panel.getGraphComponent());

        List<UIBasicBlock> newUIBasicBlocks = panel.getBasicBlocks();
        assertEquals(4, newUIBasicBlocks.size());
    }

    @Test
    public void shouldUpdateStatePanel() {
        StatePanelOpen mockPanel = mock(StatePanelOpen.class);
        DFAExecution dfa = buildDFA(exampleCode);

        controller.setStatePanel(mockPanel);
        panel.setJumpToAction(true);
        controller.start(dfa);
        controller.refresh();

        InOrder inOrder = inOrder(mockPanel);
        inOrder.verify(mockPanel, times(1)).reset(); // Verify reset() was not called after the other methods.
        inOrder.verify(mockPanel, times(1)).setIn("⊤");
        inOrder.verify(mockPanel, times(1)).setOut("⊥");
        inOrder.verify(mockPanel, times(1)).setSelectedLine(startBlockOutput, 0, 0);
        verifyNoMoreInteractions(mockPanel);
        reset(mockPanel);

        panel.getMxGraph().getSelectionModel().clear();

        verify(mockPanel, times(1)).reset();
        verifyNoMoreInteractions(mockPanel);
        reset(mockPanel);

        dfa.setCurrentElementaryStep(2);
        controller.refresh();

        inOrder.verify(mockPanel, times(1)).setIn("something");
        inOrder.verify(mockPanel, times(1)).setOut("something");
        inOrder.verify(mockPanel, times(1)).setSelectedLine("print := @parameter0: boolean", 0, 1);
        verifyNoMoreInteractions(mockPanel);
        reset(mockPanel);
    }

    private DFAExecution buildDFA(String code) {
        CodeProcessor codeProcessor = new CodeProcessor(code, this.programOutputPath);
        assertEquals("", codeProcessor.getErrorMessage());
        GraphBuilder builder = new GraphBuilder(codeProcessor.getPath(), codeProcessor.getClassName());
        SimpleBlockGraph blockGraph = builder.buildGraph("void helloWorld(boolean)");
        return new DFAExecution<>(new DummyFactory(), new NaiveWorklist(), blockGraph, new DFAPrecalcController());
    }
}
