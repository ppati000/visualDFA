package controllerTests;

import org.junit.BeforeClass;
import org.junit.Test;

import controller.Controller;

import static org.junit.Assert.*;

import dfa.framework.DFAExecution;
import dfa.framework.LatticeElement;
import gui.ProgramFrame;

@SuppressWarnings("javadoc")
public class ControllerNavigationTest {

    static Controller controller;
    static ProgramFrame programFrame;
    static private DFAExecution<? extends LatticeElement> dfaExecution;

    @BeforeClass
    public static void setup() {
        controller = new Controller();
        programFrame = new ProgramFrame(controller);
        controller.setProgramFrame(programFrame);
        controller.setDefaultCode();
        controller.startAnalysis("void helloWorld(boolean,int)");
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        dfaExecution = controller.getDFAExecution();
    }

    @Test
    public void nextBlock() {
        ControllerNavigationTest.dfaExecution
                .setCurrentBlockStep((int) ControllerNavigationTest.dfaExecution.getTotalBlockSteps() / 2);
        int currentBlock = ControllerNavigationTest.dfaExecution.getCurrentBlockStep();
        ControllerNavigationTest.controller.nextBlock();
        if (currentBlock == ControllerNavigationTest.dfaExecution.getTotalBlockSteps() - 1) {
            assertEquals(0, ControllerNavigationTest.dfaExecution.getCurrentBlockStep() - currentBlock);
        } else {
            assertEquals(1, ControllerNavigationTest.dfaExecution.getCurrentBlockStep() - currentBlock);
        }
    }

    @Test
    public void nextBlockEndOfProgram() {
        ControllerNavigationTest.dfaExecution
                .setCurrentBlockStep(ControllerNavigationTest.dfaExecution.getTotalBlockSteps() - 1);
        int currentBlock = ControllerNavigationTest.dfaExecution.getCurrentBlockStep();
        ControllerNavigationTest.controller.nextBlock();
        if (currentBlock == ControllerNavigationTest.dfaExecution.getTotalBlockSteps() - 1) {
            assertEquals(0, ControllerNavigationTest.dfaExecution.getCurrentBlockStep() - currentBlock);
        } else {
            assertEquals(1, ControllerNavigationTest.dfaExecution.getCurrentBlockStep() - currentBlock);
        }
    }

    @Test
    public void nextLine() {
        ControllerNavigationTest.dfaExecution
                .setCurrentElementaryStep((int) ControllerNavigationTest.dfaExecution.getTotalElementarySteps() / 2);
        int currentLine = ControllerNavigationTest.dfaExecution.getCurrentElementaryStep();
        ControllerNavigationTest.controller.nextLine();
        if (currentLine == ControllerNavigationTest.dfaExecution.getTotalElementarySteps() - 1) {
            assertEquals(0, ControllerNavigationTest.dfaExecution.getCurrentElementaryStep() - currentLine);
        } else {
            assertEquals(1, ControllerNavigationTest.dfaExecution.getCurrentElementaryStep() - currentLine);
        }
    }

    @Test
    public void nextLineEndOfProgram() {
        ControllerNavigationTest.dfaExecution
                .setCurrentElementaryStep(ControllerNavigationTest.dfaExecution.getTotalElementarySteps() - 1);
        int currentLine = ControllerNavigationTest.dfaExecution.getCurrentElementaryStep();
        ControllerNavigationTest.controller.nextLine();
        if (currentLine == ControllerNavigationTest.dfaExecution.getTotalElementarySteps() - 1) {
            assertEquals(0, ControllerNavigationTest.dfaExecution.getCurrentElementaryStep() - currentLine);
        } else {
            assertEquals(1, ControllerNavigationTest.dfaExecution.getCurrentElementaryStep() - currentLine);
        }
    }

    @Test
    public void previousBlock() {
        ControllerNavigationTest.dfaExecution
                .setCurrentBlockStep((int) ControllerNavigationTest.dfaExecution.getTotalBlockSteps() / 2);
        int blockBefore = ControllerNavigationTest.dfaExecution.getCurrentBlockStep();
        int elementaryStepBefore = ControllerNavigationTest.dfaExecution.getCurrentElementaryStep();
        ControllerNavigationTest.controller.previousBlock();
        int blockAfterwards = ControllerNavigationTest.dfaExecution.getCurrentBlockStep();
        ControllerNavigationTest.dfaExecution.setCurrentBlockStep(blockBefore);
        int elementaryBaseStep = ControllerNavigationTest.dfaExecution.getCurrentElementaryStep();

        if (blockBefore == 0) {
            assertEquals(0, ControllerNavigationTest.dfaExecution.getCurrentBlockStep());
            assertEquals(0, ControllerNavigationTest.dfaExecution.getCurrentElementaryStep());
        } else if ((elementaryStepBefore - elementaryBaseStep) == 0) {
            assertEquals(1, blockBefore - blockAfterwards);
        } else {
            assertEquals(1, blockBefore - blockAfterwards);
        }
    }

    @Test
    public void previousBlockBeginProgram() {
        ControllerNavigationTest.dfaExecution.setCurrentElementaryStep(0);
        int blockBefore = ControllerNavigationTest.dfaExecution.getCurrentBlockStep();
        int elementaryStepBefore = ControllerNavigationTest.dfaExecution.getCurrentElementaryStep();
        ControllerNavigationTest.controller.previousBlock();
        int blockAfterwards = ControllerNavigationTest.dfaExecution.getCurrentBlockStep();
        ControllerNavigationTest.dfaExecution.setCurrentBlockStep(blockBefore);
        int elementaryBaseStep = ControllerNavigationTest.dfaExecution.getCurrentElementaryStep();

        if (blockBefore == 0) {
            assertEquals(0, ControllerNavigationTest.dfaExecution.getCurrentBlockStep());
            assertEquals(0, ControllerNavigationTest.dfaExecution.getCurrentElementaryStep());
        } else if ((elementaryStepBefore - elementaryBaseStep) == 0) {
            assertEquals(1, blockBefore - blockAfterwards);
        } else {
            assertEquals(1, blockBefore - blockAfterwards);
        }
    }

    @Test
    public void previousBlockBeginBlock() {
        ControllerNavigationTest.dfaExecution
                .setCurrentBlockStep((int) ControllerNavigationTest.dfaExecution.getTotalBlockSteps() / 2);
        ControllerNavigationTest.dfaExecution.nextElementaryStep();
        int blockBefore = ControllerNavigationTest.dfaExecution.getCurrentBlockStep();
        int elementaryStepBefore = ControllerNavigationTest.dfaExecution.getCurrentElementaryStep();
        ControllerNavigationTest.controller.previousBlock();
        int blockAfterwards = ControllerNavigationTest.dfaExecution.getCurrentBlockStep();
        ControllerNavigationTest.dfaExecution.setCurrentBlockStep(blockBefore);
        int elementaryBaseStep = ControllerNavigationTest.dfaExecution.getCurrentElementaryStep();
        if (blockBefore == 0) {
            assertEquals(0, ControllerNavigationTest.dfaExecution.getCurrentBlockStep());
            assertEquals(0, ControllerNavigationTest.dfaExecution.getCurrentElementaryStep());
        } else if ((elementaryStepBefore - elementaryBaseStep) == 0) {
            assertEquals(1, blockBefore - blockAfterwards);
        } else {
            assertEquals(0, blockBefore - blockAfterwards);
        }
    }

    @Test
    public void previousLine() {
        ControllerNavigationTest.dfaExecution
                .setCurrentElementaryStep((int) ControllerNavigationTest.dfaExecution.getTotalBlockSteps() / 2);
        int currentLine = ControllerNavigationTest.dfaExecution.getCurrentElementaryStep();
        ControllerNavigationTest.controller.previousLine();
        if (currentLine == 0) {
            assertEquals(0, ControllerNavigationTest.dfaExecution.getCurrentElementaryStep() - currentLine);
        } else {
            assertEquals(-1, ControllerNavigationTest.dfaExecution.getCurrentElementaryStep() - currentLine);
        }
    }

    @Test
    public void previousLineBeginProgram() {
        ControllerNavigationTest.dfaExecution.setCurrentElementaryStep(0);
        int currentLine = ControllerNavigationTest.dfaExecution.getCurrentElementaryStep();
        ControllerNavigationTest.controller.previousLine();
        System.out.println(dfaExecution.getCurrentElementaryStep());
        if (currentLine == 0) {
            assertEquals(0, ControllerNavigationTest.dfaExecution.getCurrentElementaryStep() - currentLine);
        } else {
            assertEquals(1, ControllerNavigationTest.dfaExecution.getCurrentElementaryStep() - currentLine);
        }
    }

    @Test
    public void jumpToStep() {
        controller.jumpToStep(dfaExecution.getTotalElementarySteps() - 1);
        assertEquals(dfaExecution.getTotalElementarySteps() - 1, dfaExecution.getCurrentElementaryStep());
    }

    @Test
    public void play() {
        int elementaryBefore = dfaExecution.getCurrentElementaryStep();
        controller.play();
        int wait = controller.getDelay() * 2;
        try {
            Thread.sleep(wait);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        controller.pause();
        int elementaryAfterwards = dfaExecution.getCurrentElementaryStep();
        System.out.println(elementaryAfterwards - elementaryBefore);
        assertEquals(2, elementaryAfterwards - elementaryBefore);
    }
}
