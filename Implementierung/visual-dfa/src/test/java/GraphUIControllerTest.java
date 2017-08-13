import codeprocessor.CodeProcessor;
import codeprocessor.GraphBuilder;
import dfa.analyses.testanalyses.DummyElement;
import dfa.analyses.testanalyses.DummyFactory;
import dfa.framework.DFAExecution;
import dfa.framework.DFAPrecalcController;
import dfa.framework.NaiveWorklist;
import dfa.framework.SimpleBlockGraph;
import gui.visualgraph.*;

import static org.junit.Assert.*;

import org.junit.*;

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

    @Test
    public void shouldCreateGraphOnStart() {
        // TODO: Use example code to start a concrete DFAFramework analysis.
        // Stuff to be tested: right blocks, properly connected edges, right text, right association to DFA block.

        String code = "public class shouldCreateGraphOnStartClass { public void helloWorld(boolean print) {" +
                "          if (print) {" +
                "              System.out.println(\"Hello World!\");" +
                "          } else {" +
                "              System.out.println(\"Not Hello World!\");" +
                "          }" +
                "      } }";

        CodeProcessor codeProcessor = new CodeProcessor(code);

        assertEquals("", codeProcessor.getErrorMessage());

        System.out.println(codeProcessor.getClassName());
        System.out.println(codeProcessor.getPathName());

        GraphBuilder builder = new GraphBuilder(codeProcessor.getPathName(), codeProcessor.getClassName());
        SimpleBlockGraph blockGraph = builder.buildGraph("void helloWorld(boolean)");

        DFAExecution<DummyElement> dfa = new DFAExecution<DummyElement>(new DummyFactory(), new NaiveWorklist(), blockGraph, new DFAPrecalcController());
    }
}
