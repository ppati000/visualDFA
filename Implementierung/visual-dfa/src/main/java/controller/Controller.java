package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import codeprocessor.*;
import dfa.framework.AnalysisLoader;
import dfa.framework.DFAExecution;
import dfa.framework.DFAFactory;
import dfa.framework.DFAPrecalcController;
import dfa.framework.LatticeElement;
import dfa.framework.SimpleBlockGraph;
import dfa.framework.Worklist;
import dfa.framework.WorklistManager;
import gui.visualgraph.VisualGraphPanel;
import gui.ControlPanelState;
import gui.MessageBox;
import gui.MethodSelectionBox;
import gui.Option;
import gui.OptionBox;
import gui.ProgramFrame;
import gui.visualgraph.GraphUIController;

/**
 * 
 * @author Anika Nietzer Central unit, that is responsible for the communication
 *         between the GUI and the remaining packages of the program.
 *
 */
public class Controller {

    private static final String PACKAGE_NAME = "dfa.analyses";
    private static final String CLASS_PATH = System.getProperty("user.dir");
    private static final String CALC_MESSAGE = "This calculation is taking longer than expected. Do you want to continue and show intermediate results?";
    private static final String ABORT_MESSAGE = "This process leads to a complete deletion of the graph and the calculation. Do you want to continue?";
    private static final String EXCEPTION_TITLE = "Exception caused by analysis calculation";
    private static final String PATH_SELECTION = "Select the path to your \"jre\" folder that is located in the JDK(!) folder. If you set the path wrong, the program will not work."
            + "Example: C:\\Programme\\Java\\jdk1.7.0_76\\jre";
    private static final int TIME_TO_WAIT = 150;
    private static final String PATH_SEPARATOR = System.getProperty("os.name").contains("win") ? "\\" : "/";
    private static final String NO_COMPILER_FOUND = "Please check the file YourHomeDirectory\\visualDfa\\jdkPath.txt and change it to the path of "
            + "your jre folder in your JDK(!)1.7 folder. " + "For Example C:\\Program Files\\Java\\jdk1.7.0_76\\jre."
            + "Afterwards restart the program.";
    private ProgramFrame programFrame;
    private DFAExecution<? extends LatticeElement> dfaExecution;
    private GraphUIController graphUIController;
    private VisualGraphPanel visualGraphPanel;
    private AnalysisLoader analysisLoader;
    private WorklistManager worklistManager;
    private Thread autoplay;
    private Thread precalc;
    private DFAPrecalcController precalcController;
    private DFAPrecalculator precalculator;
    private boolean shouldContinue = false;

    /**
     * Creates a new {@code Controller} and loads the available analyses with an
     * instance of {@code AnalysisLoader}. Creates a {@code VisualGraphPanel}
     * and a {@code GraphUIController}.
     */
    public Controller() {
        try {
            this.analysisLoader = new AnalysisLoader(PACKAGE_NAME, CLASS_PATH);
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
     * 
     */
    public void pathSelection() {
        String pathName = System.getProperty("user.home") + PATH_SEPARATOR + "visualDfa";
        File dir = new File(pathName.trim());
        if (!dir.exists()) {
            dir.mkdir();
        }
        String fileName = "jdkPath.txt";
        String completePath = pathName + PATH_SEPARATOR + fileName;
        File jdkPath = new File(completePath.toString());
        if (jdkPath.exists()) {
            File sourceFile = new File(pathName + PATH_SEPARATOR + fileName);
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(sourceFile));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            String input;
            String text = new String();
            try {
                while ((input = reader.readLine()) != null) {
                    text += input;
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            File dirJDK = new File(text.trim());
            if (dirJDK.exists()) {
                System.setProperty("java.home", text.trim());
                return;
            }
        }

        new MessageBox(this.programFrame, "JDK Path", PATH_SELECTION);
        jdkPath = this.programFrame.getCompilerPath();
        if (!jdkPath.exists()) {
            new MessageBox(this.programFrame, "No Compiler found", NO_COMPILER_FOUND);
            return;
        }
        System.setProperty("java.home", jdkPath.getAbsolutePath());
        File iniFile = new File(dir, fileName);
        FileWriter writer;
        try {
            writer = new FileWriter(iniFile);
            writer.write(jdkPath.getAbsolutePath());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
        this.autoplay = new Thread(autoplayDriver);
        try {
            this.autoplay.start();
        } catch (IllegalThreadStateException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the chosen delay.
     * 
     * @return delay, the user has set
     */
    public int getDelay() {
        return this.programFrame.getControlPanel().getDelaySliderPosition();
    }

    /**
     * @return if autoplay should continue and tests if a breakpoint was reached
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
     * the steps of the analysis with the {@code DFAPrecalculator}. The
     * {@code GraphUIController} is invoked to display the CFG. The
     * {@code ControlPanel}, the {@code StatePanel} and the
     * {@code VisualGraphPanel} are activated and the {@code InputPanel} is
     * deactivated.
     */
    public void startAnalysis() {
        // Collect information
        visibilityPrecalculating();
        String analysisName = programFrame.getInputPanel().getAnalysis();
        String worklistName = programFrame.getInputPanel().getWorklist();
        String code = programFrame.getInputPanel().getCode();
        boolean hasFilter = programFrame.getInputPanel().isFilterSelected();

        // Process code with instance of {@code CodeProcessor}
        CodeProcessor processor = null;
        try {
            processor = new CodeProcessor(code);
        } catch (NullPointerException e) {
            new MessageBox(this.programFrame, "No Compiler Found", NO_COMPILER_FOUND);
        }
        if (!processor.wasSuccessful()) {
            new MessageBox(programFrame, "Compilation Error", processor.getErrorMessage());
            visibilityInput();
            return;
        }
        String packageName = processor.getPathName();
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
        MethodSelectionBox selectionBox = new MethodSelectionBox(programFrame, methodList);
        if (selectionBox.getOption() == Option.CANCEL_OPTION) {
            visibilityInput();
            return;
        }
        String methodSignature = selectionBox.getSelectedMethod();
        SimpleBlockGraph blockGraph = graphBuilder.buildGraph(methodSignature);
        this.precalcController = new DFAPrecalcController();

        try {
            Worklist worklist = this.worklistManager.getWorklist(worklistName, blockGraph);
            DFAFactory<LatticeElement> dfaFactory = analysisLoader.getDFAFactory(analysisName);
            this.precalculator = new DFAPrecalculator(dfaFactory, worklist, blockGraph, this.precalcController, this);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        this.precalc = new Thread(precalculator);
        try {
            this.precalc.start();
        } catch (IllegalThreadStateException e) {
            e.printStackTrace();
        }
        visibilityPrecalculating();
        int i = 0;
        synchronized (this) {
            while (i < TIME_TO_WAIT
                    && !(precalcController.getPrecalcState() == DFAPrecalcController.PrecalcState.COMPLETED)) {
                // TODO
                try {
                    wait(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                i++;
            }
        }
        if (i >= TIME_TO_WAIT) {
            stopAnalysis();
        }

        while (!(precalcController.getPrecalcState() == DFAPrecalcController.PrecalcState.COMPLETED)
                && precalc.isAlive())
            ;
        // TODO Problem?
        this.dfaExecution = precalculator.getDFAExecution();
        this.dfaExecution.setCurrentElementaryStep(0);
        this.programFrame.getControlPanel().setTotalSteps(this.dfaExecution.getTotalElementarySteps());
        this.programFrame.getControlPanel().setSliderStep(0);
        this.graphUIController.start(this.dfaExecution);
        this.graphUIController.refresh();
        visibilityWorking();
    }

    /**
     * Deletes the current {@code DFAExecution} and the content of the
     * {@code VisualGraphPanel} through the {@code GraphUIController}. The
     * {@code ControlPanel}, the {@code StatePanel} and the
     * {@code VisualGraphPanel} are deactivated and the {@code InputPanel} is
     * activated. Deprecated method thread.stop is needed to deal with infinite
     * loops in the precalculation. The used Dataflow Analysis can be
     * implemented by the user and we cannot assume the correctness of theses
     * analyses.
     */
    @SuppressWarnings("deprecation")
    public void stopAnalysis() {
        if (precalcController.getPrecalcState() == DFAPrecalcController.PrecalcState.CALCULATING
                || precalcController.getPrecalcState() == DFAPrecalcController.PrecalcState.PAUSED) {

            OptionBox optionBox = new OptionBox(this.programFrame, CALC_MESSAGE);
            if (optionBox.getOption() == Option.NO_OPTION) {
                // deletes the complete calculation
                this.precalcController.stopPrecalc();
                synchronized (this) {
                    try {
                        wait(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (this.precalc.isAlive()) {
                    this.precalc.stop();
                }

                visibilityInput();
            } else if ((optionBox.getOption() == Option.YES_OPTION)) {
                // shows an intermediate result if possible
                this.precalcController.stopPrecalc();
                try {
                    wait(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (this.precalc.isAlive()) {
                    this.precalc.stop();
                    visibilityInput();
                } else {
                    this.dfaExecution = this.precalculator.getDFAExecution();
                    this.dfaExecution.setCurrentElementaryStep(0);
                    this.graphUIController.start(this.dfaExecution);
                    this.programFrame.getControlPanel().setTotalSteps(this.dfaExecution.getTotalElementarySteps() - 1);
                    visibilityWorking();
                }
            }
        } else {
            OptionBox optionBox = new OptionBox(this.programFrame, "Stop", ABORT_MESSAGE);
            if (optionBox.getOption() == Option.YES_OPTION) {
                visibilityInput();
                this.graphUIController.stop();
                this.programFrame.getStatePanelOpen().reset();
                this.dfaExecution = null;
            }
        }

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

}
