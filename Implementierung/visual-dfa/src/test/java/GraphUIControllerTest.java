import codeprocessor.CodeProcessor;
import codeprocessor.GraphBuilder;
import com.mxgraph.swing.mxGraphComponent;
import dfa.analyses.testanalyses.DummyElement;
import dfa.analyses.testanalyses.DummyFactory;
import dfa.framework.*;
import gui.visualgraph.*;

import static org.junit.Assert.*;

import org.junit.*;

import java.util.List;

public class GraphUIControllerTest {
    private VisualGraphPanel panel;
    private GraphUIController controller;

    @Before
    public void createPanel() {
        panel = new VisualGraphPanel();
        controller = new GraphUIController(panel);
    }

    @Test(expected = IllegalStateException.class)
    public void refreshShouldNotBePossibleBeforeStart() {
        controller.refresh();
    }

    @Test(expected = IllegalStateException.class)
    public void startShouldNotBePossibleTwice() {
        CodeProcessor codeProcessor = new CodeProcessor("void emptyInside() {}");
        GraphBuilder builder = new GraphBuilder(codeProcessor.getPath(), codeProcessor.getClassName());
        SimpleBlockGraph blockGraph = builder.buildGraph("void emptyInside()");
        DFAExecution<DummyElement> dfa = new DFAExecution<>(new DummyFactory(), new NaiveWorklist(), blockGraph, new DFAPrecalcController());

        controller.start(dfa);
        controller.start(dfa);
    }

    @Test
    public void shouldBuildGraphOnStartAndUpdateOnRefresh() {
        String code = "public class shouldCreateGraphOnStartClass { public void helloWorld(boolean print) {" +
                "          if (print) {" +
                "              System.out.println(\"Hello World!\");" +
                "          } else {" +
                "              System.out.println(\"Not Hello World!\");" +
                "          }" +
                "      } }";

        // Verifying the text of the UIBasicBlocks gives us confidence that the UILineBlocks were inserted correctly.
        // Whether or not they were also *rendered* correctly is another story, tested in VisualGraphPanelTest.
        String startBlockOutput = "this := @this: shouldCreateGraphOnStartClass\n" +
                "print := @parameter0: boolean\n" +
                "if print == 0 goto $r0 = <java.lang.System: java.io.PrintStream out>";

        String secondBlockOutput = "$r1 = <java.lang.System: java.io.PrintStream out>\n" +
                "virtualinvoke $r1.<java.io.PrintStream: void println()>()\n" +
                "goto [?= return]";

        String thirdBlockOutput = "$r0 = <java.lang.System: java.io.PrintStream out>\n" +
                "virtualinvoke $r0.<java.io.PrintStream: void println()>()";

        CodeProcessor codeProcessor = new CodeProcessor(code);
        assertEquals("", codeProcessor.getErrorMessage());
        GraphBuilder builder = new GraphBuilder(codeProcessor.getPath(), codeProcessor.getClassName());
        SimpleBlockGraph blockGraph = builder.buildGraph("void helloWorld(boolean)");
        DFAExecution<DummyElement> dfa = new DFAExecution<>(new DummyFactory(), new NaiveWorklist(), blockGraph, new DFAPrecalcController());
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
}
