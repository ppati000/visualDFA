package controller;

import org.junit.Test;
import gui.ProgramFrame;
import static org.junit.Assert.*;

@SuppressWarnings("javadoc")
public class ProgramStartTest {

    Controller controller;
    ProgramFrame programFrame;

    @Test
    public void test01() {
        this.controller = new Controller();
        this.programFrame = new ProgramFrame(this.controller);
        this.controller.setProgramFrame(this.programFrame);
        assertNotEquals(null, this.controller.getAnalyses());
        assertNotEquals(null, this.controller.getWorklists());
        assertNotEquals(null, this.controller.getVisualGraphPanel());
        assertEquals(2, this.controller.getWorklists().size());
        assertEquals(true, this.controller.getAnalyses().contains("Constant-Folding"));
        assertEquals(true, this.controller.getAnalyses().contains("Constant-Bits"));
        assertEquals(true, this.controller.getAnalyses().contains("Reaching-Definitions"));
        assertEquals(true, this.controller.getAnalyses().contains("Taint-Analysis"));
    }

}
