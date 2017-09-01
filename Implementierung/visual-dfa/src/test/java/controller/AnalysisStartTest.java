package controller;

import org.junit.Before;
import org.junit.Test;

import dfa.framework.DFAExecution;
import dfa.framework.DFAPrecalcController;
import dfa.framework.DFAPrecalcController.PrecalcState;
import gui.ProgramFrame;

import static org.junit.Assert.*;

/**
 * @author Anika
 *
 */
public class AnalysisStartTest {

    Controller controller;
    ProgramFrame programFrame;

 

    /**
     * 
     */
    @Before
    public void setup() {
        this.controller = new Controller();
        this.programFrame = new ProgramFrame(this.controller);
        this.controller.setProgramFrame(this.programFrame);
    }
    
    @Test
    public void test01() {
        String code = "Hello World; System.out.println(\"hi\")";
        this.programFrame.getInputPanel().setCode(code);
        assertEquals(code, this.programFrame.getInputPanel().getCode());
    }
    
    @Test
    public void test02() {
        this.controller.setDefaultCode();
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
    public void test03() {
        this.controller.setDefaultCode();
        DFAPrecalcController precalcController = this.controller.startAnalysis("void helloWorld(boolean,int)");
        while(precalcController.getPrecalcState() != PrecalcState.COMPLETED) {
            
        }
        DFAExecution dfaExecution = precalcController.getResult();
        assertNotEquals(null, dfaExecution);
        assertEquals(0, dfaExecution.getCurrentBlockStep());
        assertEquals(0, dfaExecution.getCurrentElementaryStep());
        
    }

}
