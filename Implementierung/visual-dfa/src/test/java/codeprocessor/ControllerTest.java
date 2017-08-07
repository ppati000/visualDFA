package codeprocessor;

import javax.swing.JFrame;

import org.junit.Test;

import dfa.analyses.DummyElement;
import dfa.analyses.DummyFactory;
import dfa.framework.DFAExecution;
import dfa.framework.DFAPrecalcController;
import dfa.framework.NaiveWorklist;
import dfa.framework.SimpleBlockGraph;
import gui.visualgraph.GraphUIController;
import gui.visualgraph.VisualGraphPanel;

public class ControllerTest {

    @Test
    public void Test() {
        String code = "public void helloWorld(int a) {" +
                "          if (a > 4) {" +
                "              a = 10;" +
                "          } else {" +
                "             a = 0;" +
                "          }" +
                "     }";

       CodeProcessor codeProcessor = new CodeProcessor(code);

       System.out.println(codeProcessor.getClassName());
        System.out.println(codeProcessor.getPathName());

       GraphBuilder builder = new GraphBuilder(codeProcessor.getPathName(), codeProcessor.getClassName());
        SimpleBlockGraph blockGraph = builder.buildGraph("void helloWorld(int)");

       DFAExecution<DummyElement> dfa = new DFAExecution<DummyElement>(new DummyFactory(), new NaiveWorklist(), blockGraph, new DFAPrecalcController());
        dfa.setCurrentBlockStep(0);

       VisualGraphPanel panel = new VisualGraphPanel();
        panel.setVisible(true);
        JFrame frame = new JFrame();
        frame.add(panel);
        frame.setSize(600, 400);
        frame.setVisible(true);
        
        GraphUIController controller = new GraphUIController(panel);

       controller.start(dfa);
       while (true);
    }
    
    
}
