package controllerTests;

import org.junit.Before;
import org.junit.Test;

import controller.Controller;
import dfa.framework.DFAExecution;
import dfa.framework.DFAPrecalcController;
import dfa.framework.DFAPrecalcController.PrecalcState;
import dfa.framework.LatticeElement;
import gui.ProgramFrame;

import static org.junit.Assert.*;

@SuppressWarnings("javadoc")
public class AnalysisStartTest {

    Controller controller;
    ProgramFrame programFrame;

    @Before
    public void setup() {
        this.controller = new Controller();
        this.programFrame = new ProgramFrame(this.controller);
        this.controller.setProgramFrame(this.programFrame);
    }

    @Test
    public void setCode() {
        String code = "Hello World; System.out.println(\"hi\")";
        this.programFrame.getInputPanel().setCode(code);
        assertEquals(code, this.programFrame.getInputPanel().getCode());
    }

    @Test
    public void setCodeToInputPanel() {
        this.controller.setDefaultCode();
        //@formatter:off
        String codeExample = 
                "public class Example {" + System.lineSeparator()
              + "  public void helloWorld(boolean print, int x) {" + System.lineSeparator() 
              + "    if (print) {" + System.lineSeparator() 
              + "      System.out.println(\"Hello World!\");" + System.lineSeparator()
              + "        while (x < 10) {" + System.lineSeparator() 
              + "          x = x + 1;" + System.lineSeparator() 
              + "          if (x == 5) {" + System.lineSeparator()
              + "            int y = 5;" + System.lineSeparator() 
              + "            x = y * 3;" + System.lineSeparator() 
              + "          }" + System.lineSeparator() 
              + "        }" + System.lineSeparator() 
              + "    } else {" + System.lineSeparator() 
              + "       x = 0;" + System.lineSeparator() 
              + "    }" + System.lineSeparator() 
              + "  }" + System.lineSeparator()
              + "}";
        //@formatter:on
        assertEquals(codeExample, this.programFrame.getInputPanel().getCode());
    }

    @Test
    public void testStartAnalysis() {
        this.controller.setDefaultCode();
        DFAPrecalcController precalcController = this.controller.startAnalysis("void helloWorld(boolean,int)");
        while (precalcController.getPrecalcState() != PrecalcState.COMPLETED) {

        }
        DFAExecution<? extends LatticeElement> dfaExecution = precalcController.getResult();
        assertNotEquals(null, dfaExecution);
        assertEquals(0, dfaExecution.getCurrentBlockStep());
        assertEquals(0, dfaExecution.getCurrentElementaryStep());

    }

}
