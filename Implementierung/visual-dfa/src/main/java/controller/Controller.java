package controller;

import java.util.List;
import java.util.logging.Logger;

import codeprocessor.*;
import dfa.framework.AnalysisLoader;
import dfa.framework.DFAExecution;
import dfa.framework.DFAFactory;
import dfa.framework.DFAPrecalcController;
import dfa.framework.LatticeElement;
import dfa.framework.SimpleBlockGraph;
import dfa.framework.StaticAnalysisLoader;
import dfa.framework.Worklist;
import dfa.framework.WorklistManager;
import gui.visualgraph.VisualGraphPanel;
import gui.ControlPanelState;
import gui.GenericBox;
import gui.MessageBox;
import gui.MethodSelectionBox;
import gui.Option;
import gui.ProgramFrame;
import gui.visualgraph.GraphUIController;

/**
 * 
 * @author Anika Nietzer Central unit, that is responsible for the communication
 *         between the GUI and the remaining packages of the program.
 *
 */
public class Controller {

    // private static final String PACKAGE_NAME = "dfa.analyses"; // no longer
    // needed
    private static final String CLASS_PATH = System.getProperty("user.dir");
    private static final String ABORT_PRECALC_MESSAGE = "Do you want to stop the precalculation? You can also show intermediate results if the analysis state allows this.";
    private static final String ABORT_MESSAGE = "This leads to a complete deletion of the graph and the calculation. Would you like to continue?";
    private static final String EXCEPTION_TITLE = "Exception caused by analysis calculation";

    private static final int WAIT_FOR_STOP = 500;

    private static final String PROGRAM_OUTPUT_PATH = System.getProperty("user.home")
            + System.getProperty("file.separator") + "visualDfa";

    private OptionFileParser fileParser;
    private ProgramFrame programFrame;
    private DFAExecution<? extends LatticeElement> dfaExecution;
    private GraphUIController graphUIController;
    private VisualGraphPanel visualGraphPanel;
    private AnalysisLoader analysisLoader;
    private WorklistManager worklistManager;
    private Thread precalcThread;
    private DFAPrecalcController precalcController;
    private boolean shouldContinue = false;

    public DFAExecution getDFAExecution() {
        return this.dfaExecution;
    }

    /**
     * Creates a new {@code Controller} and loads the available analyses with an
     * instance of {@code AnalysisLoader}. Creates a {@code VisualGraphPanel}
     * and a {@code GraphUIController}.
     */
    public Controller() {

        // TODO @Anika the dirPrefix is only a temporary fix
        // the analyses-classes can be put in .../anyDirectory/dfa/analyses and
        // the argument to the
        // AnalysisLoader-constructor must then be '.../anyDirectory' (the
        // immediate parent of /dfa), otherwise the
        // package structure does not match the folder structure

        try {
            // TODO
            String dirPrefix = System.getProperty("file.separator") + "src" + System.getProperty("file.separator")
                    + "test" + System.getProperty("file.separator") + "resources";
            this.analysisLoader = new StaticAnalysisLoader(CLASS_PATH + dirPrefix);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        Logger logger = Logger.getAnonymousLogger();
        try {
            this.analysisLoader.loadAnalyses(logger);
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
        }
        this.worklistManager = WorklistManager.getInstance();
        this.visualGraphPanel = new VisualGraphPanel();
        this.graphUIController = new GraphUIController(visualGraphPanel);
    }

    /**
     * Defines the code that is written in the editor of the input panel at
     * program start.
     */
    public void setDefaultCode() {
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
        this.programFrame.getInputPanel().setCode(codeExample);

    }

    /**
     * Leads to the calculation of the next block by the DFAFramework and
     * ensures that the {@code GraphUIController} updates the
     * {@code VisualGraphPanel}.
     * 
     * @return whether there was a next block to show or not
     */
    public boolean nextBlock() {
        boolean hasNextBlock = this.dfaExecution.nextBlockStep();
        if (hasNextBlock) {
            this.refreshProgramFrame();
        } else {
            this.dfaExecution.setCurrentElementaryStep(this.dfaExecution.getTotalElementarySteps() - 1);
            this.refreshProgramFrame();
        }
        return hasNextBlock;
    }

    /**
     * Leads to the calculation of the next line by the DFAFramework and ensures
     * that the {@code GraphUIController} updates the {@code VisualGraphPanel}.
     * 
     * @return whether there was a next line to show or not
     */
    public boolean nextLine() {
        boolean hasNextLine = this.dfaExecution.nextElementaryStep();
        if (hasNextLine) {
            this.refreshProgramFrame();
        }
        return hasNextLine;
    }

    /**
     * Leads to the calculation of the previous line by the DFAFramework and
     * ensures that the {@code GraphUIController} updates the
     * {@code VisualGraphPanel}.
     * 
     * @return whether there was a previous line to show or not
     */
    public boolean previousLine() {
        boolean hasPreviousLine = this.dfaExecution.previousElementaryStep();
        if (hasPreviousLine) {
            this.refreshProgramFrame();
        }
        return hasPreviousLine;
    }

    /**
     * Leads to the calculation of the previous block by the DFAFramework and
     * ensures that the {@code GraphUIController} updates the
     * {@code VisualGraphPanel}.
     * 
     * @return whether there was a previous block to show or not
     */
    public boolean previousBlock() {
        int currentStep = this.dfaExecution.getCurrentElementaryStep();
        this.dfaExecution.setCurrentBlockStep(this.dfaExecution.getCurrentBlockStep());
        if (currentStep == this.dfaExecution.getCurrentElementaryStep()) {
            this.dfaExecution.previousBlockStep();
        }
        this.refreshProgramFrame();
        return true;
    }

    private void refreshProgramFrame() {
        try {
            this.graphUIController.refresh();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        this.programFrame.getControlPanel().setSliderStep(this.dfaExecution.getCurrentElementaryStep());
    }

    /**
     * Leads to the calculation of a given step in the analysis from the
     * DFAFramework and ensures that the {@code GraphUIController} updates the
     * {@code VisualGraphPanel}.
     * 
     * @param step
     *            step to show in the animation
     */
    public void jumpToStep(int step) {
        try {
            this.dfaExecution.setCurrentElementaryStep(step);
            this.graphUIController.refresh();
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a new {@code AutoplayDriver} to replay the different steps of the
     * analysis if a delay bigger than zero is selected or jumps to the last
     * step of the analysis if the chosen delay is zero.
     */
    public void play() {
        if (getDelay() == 0
                || this.dfaExecution.getTotalElementarySteps() - 1 == this.dfaExecution.getCurrentElementaryStep()) {
            this.dfaExecution.setCurrentElementaryStep(this.dfaExecution.getTotalElementarySteps() - 1);
            this.programFrame.getControlPanel().setSliderStep(this.dfaExecution.getTotalElementarySteps() - 1);
            this.graphUIController.refresh();
            return;
        }
        AutoplayDriver autoplayDriver = null;
        try {
            autoplayDriver = new AutoplayDriver(this);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        visibilityPlaying();
        this.shouldContinue = true;
        Thread autoplayThread = new Thread(autoplayDriver);
        try {
            autoplayThread.start();
        } catch (IllegalThreadStateException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that return the chosen delay selected in the delaySlider of the
     * {@code ControlPanel}.
     * 
     * @return delay, the user has set
     */
    public int getDelay() {
        return this.programFrame.getControlPanel().getDelaySliderPosition();
    }

    /**
     * Method that checks if the Thread of the {@code AutoplayDriver} should
     * continue or not. The {@code AutoplayDriver} should stop if the animation
     * has reached a breakpoint or if the user paused it.
     * 
     * @return {@code true} if the autoplay should continue, {@code false}
     *         otherwise
     */
    public boolean shouldContinue() {
        if (this.dfaExecution.isAtBreakpoint()) {
            return false;
        } else {
            return this.shouldContinue;
        }
    }

    /**
     * Stops the {@code AutoplayDriver}.
     */
    public void pause() {
        this.shouldContinue = false;
        visibilityWorking();
    }

    /**
     * Creates a new {@code CodeProcessor} to process the input of the user and
     * creates a {@code SimpleBlockGraph} of the chosen method. Precalculates
     * the steps of the analysis with the {@code DFAPrecalculator} and an
     * instance of {@code DFAPrecalcController}. The {@code GraphUIController}
     * is invoked to display the CFG. The {@code ControlPanel}, the
     * {@code StatePanel} and the {@code VisualGraphPanel} are activated and the
     * {@code InputPanel} is deactivated.
     */
    public DFAPrecalcController startAnalysis(String methodSignature) {
        // Collect information
        visibilityPrecalculating();
        String analysisName = programFrame.getInputPanel().getAnalysis();
        String worklistName = programFrame.getInputPanel().getWorklist();
        String code = programFrame.getInputPanel().getCode();
        boolean hasFilter = programFrame.getInputPanel().isFilterSelected();

        // Process code with instance of {@code CodeProcessor}
        CodeProcessor processor = new CodeProcessor(code);
        if (!processor.wasSuccessful()) {
            new MessageBox(programFrame, "Compilation Error", processor.getErrorMessage());
            visibilityInput();
            return null;
        }
        String packageName = processor.getPath();
        String className = processor.getClassName();

        // build Graph with {@code GraphBuilder}
        GraphBuilder graphBuilder = new GraphBuilder(packageName, className);
        Filter filter;
        if (hasFilter) {
            filter = new StandardFilter();
        } else {
            filter = new NoFilter();
        }

        List<String> methodList = graphBuilder.getMethods(filter);
        if (methodSignature == null) {
            MethodSelectionBox selectionBox = new MethodSelectionBox(programFrame, methodList);
            if (selectionBox.getOption() == Option.CANCEL_OPTION) {
                visibilityInput();
                return null;
            }
            methodSignature = selectionBox.getSelectedMethod();
        }

        SimpleBlockGraph blockGraph = graphBuilder.buildGraph(methodSignature);
        this.precalcController = new DFAPrecalcController();
        DFAPrecalculator precalculator = null;
        try {
            Worklist worklist = this.worklistManager.getWorklist(worklistName, blockGraph);
            // @SuppressWarnings("unchecked")
            DFAFactory<? extends LatticeElement> dfaFactory = analysisLoader.getDFAFactory(analysisName);
            precalculator = new DFAPrecalculator(dfaFactory, worklist, blockGraph, this.precalcController, this);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        this.precalcThread = new Thread(precalculator);
        try {
            this.precalcThread.start();
        } catch (IllegalThreadStateException e) {
            e.printStackTrace();
        }
        visibilityPrecalculating();
        return this.precalcController;
    }

    /**
     * Method that is invoked when the precalcution is terminated. It sets the
     * {@code DFAExecution} and controls the setting of the {@code ControlPanel}
     * . The {@code GraphUIController} is started.
     */
    public void completedAnalysis() {
        this.dfaExecution = this.precalcController.getResult();
        this.dfaExecution.setCurrentElementaryStep(0);
        this.programFrame.getControlPanel().setTotalSteps(this.dfaExecution.getTotalElementarySteps());
        this.programFrame.getControlPanel().setSliderStep(0);
        this.graphUIController.start(this.dfaExecution);
        this.visualGraphPanel.setJumpToAction(true);
        this.graphUIController.refresh();
        visibilityWorking();
    }

    /**
     * Deletes the current {@code DFAExecution} and the content of the
     * {@code VisualGraphPanel} through the {@code GraphUIController}. The
     * {@code ControlPanel}, the {@code StatePanel} and the
     * {@code VisualGraphPanel} are deactivated and the {@code InputPanel} is
     * activated. First the Thread in that the precalculation is running is
     * stopped with the {@code DFAPrecalcController}. If that does not work the
     * deprecated method {@code Thread.stop} is needed to deal with infinite
     * loops in the precalculation. The used Dataflow Analysis can be
     * implemented by the user and the correctness of theses analyses can not be
     * assumed.
     */
    @SuppressWarnings("deprecation")
    public void stopAnalysis() {
        if (precalcController.getPrecalcState() == DFAPrecalcController.PrecalcState.CALCULATING
                || precalcController.getPrecalcState() == DFAPrecalcController.PrecalcState.PAUSED) {
            GenericBox closeBox = new GenericBox(this.programFrame, "Stop Calculation", ABORT_PRECALC_MESSAGE, "Yes",
                    "No", "Intermediate Results", false, Option.NO_OPTION);
            if (closeBox.getOption() == Option.NO_OPTION) {
                if (!(precalcController.getPrecalcState() == DFAPrecalcController.PrecalcState.CALCULATING)) {
                    visibilityInput();
                    return;
                }
                this.precalcController.stopPrecalc();
                synchronized (this) {
                    try {
                        wait(WAIT_FOR_STOP);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
                if (this.precalcThread.isAlive()) {
                    this.precalcThread.stop();
                }
                visibilityInput();
            } else if ((closeBox.getOption() == Option.YES_OPTION)) {
                // shows an intermediate result if possible
                if (!(precalcController.getPrecalcState() == DFAPrecalcController.PrecalcState.CALCULATING)) {
                    return;
                }
                this.precalcController.stopPrecalc();
                synchronized (this) {
                    try {
                        wait(WAIT_FOR_STOP);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
                if (this.precalcThread.isAlive()) {
                    this.precalcThread.stop();
                    visibilityInput();
                }
            }
        } else if (this.fileParser.shouldShowBox()) {
            GenericBox closeBox = new GenericBox(this.programFrame, "Stop", ABORT_MESSAGE, "Yes", "No", null, true,
                    Option.NO_OPTION);
            if (closeBox.getOption() == Option.NO_OPTION) {
                return;
            }
            if (!closeBox.showAgain()) {
                this.fileParser.setShowBox(false);
            }
        }
        visibilityInput();
        this.graphUIController.stop();
        this.programFrame.getStatePanelOpen().reset();
        this.dfaExecution = null;
    }

    protected void visibilityPrecalculating() {
        this.visualGraphPanel.setActivated(false);
        this.programFrame.getInputPanel().setActivated(false);
        this.programFrame.getControlPanel().setActivated(ControlPanelState.PRECALCULATING);
        this.programFrame.getStatePanelOpen().setActivated(false);
    }

    protected void visibilityPlaying() {
        this.visualGraphPanel.setActivated(false);
        this.programFrame.getInputPanel().setActivated(false);
        this.programFrame.getControlPanel().setActivated(ControlPanelState.PLAYING);
        this.programFrame.getStatePanelOpen().setActivated(true);
    }

    protected void visibilityInput() {
        this.visualGraphPanel.setActivated(false);
        this.programFrame.getInputPanel().setActivated(true);
        this.programFrame.getControlPanel().setActivated(ControlPanelState.DEACTIVATED);
        this.programFrame.getStatePanelOpen().setActivated(false);
    }

    protected void visibilityWorking() {
        this.visualGraphPanel.setActivated(true);
        this.programFrame.getInputPanel().setActivated(false);
        this.programFrame.getControlPanel().setActivated(ControlPanelState.ACTIVATED);
        this.programFrame.getStatePanelOpen().setActivated(true);
    }

    /**
     * Method that shows the message of the exception occured during a DFA
     * precalculation in a message box.
     * 
     * @param message
     *            message of the exception
     */
    public void createExceptionBox(String message) {
        new MessageBox(this.programFrame, EXCEPTION_TITLE, message);
    }

    /**
     * Sets the {@code ProgramFrame} to programFrame.
     * 
     * @param programFrame
     *            programFrame that should be set
     */
    public void setProgramFrame(ProgramFrame programFrame) {
        if (programFrame == null) {
            throw new IllegalStateException("programFrame must not be null");
        }
        this.programFrame = programFrame;
        this.visualGraphPanel.setParentFrame(this.programFrame);
        this.graphUIController.setStatePanel(this.programFrame.getStatePanelOpen());
        visibilityInput();
    }

    /**
     * Returns a list of the names of the analyses that were found during
     * program start by the {@code AnalysisLoader}.
     * 
     * @return list of names of the found analyses
     */
    public List<String> getAnalyses() {
        List<String> analyses = null;
        try {
            analyses = this.analysisLoader.getAnalysesNames();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        return analyses;
    }

    /**
     * Returns a list of names of the {@code Worklist} currently available in
     * the {@code WorklistManager}.
     * 
     * @return a list of names of the available {@code Worklist}s
     */
    public List<String> getWorklists() {
        return this.worklistManager.getWorklistNames();
    }

    /**
     * Returns the {@code VisualGraphPanel}.
     * 
     * @return the instance of {@code VisualGraphPanel}
     */
    public VisualGraphPanel getVisualGraphPanel() {
        if (this.visualGraphPanel == null) {
            throw new IllegalStateException("visualGraphPanel must not be null");
        }
        return this.visualGraphPanel;
    }

    /**
     * Returns the program output path.
     * 
     * @return the path were output files of the programs are stored
     */
    public String getProgramOutputPath() {
        return Controller.PROGRAM_OUTPUT_PATH;
    }

    /**
     * 
     */
    public void parseOptionFile() {
        this.fileParser = new OptionFileParser(Controller.PROGRAM_OUTPUT_PATH, this.programFrame);
    }

}
