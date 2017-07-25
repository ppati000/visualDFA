package controller;

import java.util.List;
import java.util.logging.Logger;

import codeprocessor.*;
import dfa.framework.AnalysisLoader;
import dfa.framework.DFAExecution;
import dfa.framework.DFAFactory;
import dfa.framework.SimpleBlockGraph;
import dfa.framework.Worklist;
import dfa.framework.WorklistManager;
import gui.*;
import gui.visualgraph.VisualGraphPanel;
import gui.visualgraph.GraphUIController;

// TODO Exceptions wo genau
// TODO Logger
// TODO bei Autoplay blocksteps anzeigen

/**
 * 
 * @author Anika Nietzer Central unit, that is responsible for the communication
 *         between the GUI and the remaining packages of the program.
 *
 */
public class Controller {

    private static final String analysisPackageName = "dfa.Analyses"; // TODO
                                                                      // define
                                                                      // packageName
    private static final String classPath = "dontKnow"; // TODO define
    private static final String calcMessage = "This calculation is taking longer than expected. Do you want to continue?";
    private static final String abortMessage = "This process leads to a complete deletion of the graph and the calculation. Do you want to continue?";

    private ProgramFrame programFrame;
    private DFAExecution dfaExecution;
    private GraphUIController graphUIController;
    private VisualGraphPanel visualGraphPanel;
    private AnalysisLoader analysisLoader;
    private Thread autoplay;
    private boolean continueAutoplay;

    /**
     * Creates a new {@code Controller} and loads the available analyses with an
     * instance of {@code AnalysisLoader}. Creates a {@code VisualGraphPanel}
     * and a {@code GraphUIController}.
     */
    public Controller() {
        try {
            this.analysisLoader = new AnalysisLoader(analysisPackageName, classPath);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        Logger logger = Logger.getAnonymousLogger(); // TODO
        try {
            this.analysisLoader.loadAnalyses(logger);
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
        }
        this.visualGraphPanel = new VisualGraphPanel();
        this.graphUIController = new GraphUIController(visualGraphPanel);
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
        boolean hasPreviousBlock = this.dfaExecution.previousBlockStep();
        if (hasPreviousBlock) {
            this.refreshProgramFrame();
        }
        return hasPreviousBlock;
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
     * @return whether the step can be shown or not
     */
    public void jumpToStep(int step) {
        if (0 < step || step > this.dfaExecution.getTotalElementarySteps() - 1) {
            throw new IllegalArgumentException("step is out of range");
        }
        this.dfaExecution.setCurrentElementaryStep(step);
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
            return;
        }
        AutoplayDriver autoplayDriver = null;
        try {
            autoplayDriver = new AutoplayDriver(this);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        this.programFrame.getControlPanel().setActivated(ControlPanelState.PLAYING);
        this.getVisualGraphPanel().setActivated(false);
        this.autoplay = new Thread(autoplayDriver);
        this.continueAutoplay = true;
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

    public boolean shouldAutoplayContinue() {
        if (this.dfaExecution.isAtBreakpoint()) {
            return false;
        }
        return this.continueAutoplay;
    }

    /**
     * Stops the {@code AutoplayDriver}.
     */
    public void pause() {
        this.continueAutoplay = false;
        this.getVisualGraphPanel().setActivated(true);
        this.programFrame.getControlPanel().setActivated(ControlPanelState.ACTIVATED);
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
    // TODO warum wird stop ben�tigt
    @SuppressWarnings("deprecation")
    public void startAnalysis() {
        // Collect information
        programFrame.getInputPanel().setActivated(false);
        String analysisName = programFrame.getInputPanel().getAnalysis();
        String worklistName = programFrame.getInputPanel().getWorklist();
        String code = programFrame.getInputPanel().getCode();
        boolean hasFilter = programFrame.getInputPanel().isFilterSelected();

        // Process code with instance of CodeProcessor
        CodeProcessor processor = new CodeProcessor(code);
        if (!processor.wasSuccessful()) {
            // TODO implement Error
            return;
        }
        String packageName = processor.getPackageName();
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
        String methodSignature = selectionBox.getSelectedMethod();
        SimpleBlockGraph blockGraph = graphBuilder.buildGraph(methodSignature);
        WorklistManager manager = new WorklistManager();
        Worklist worklist = null;
        try {
            worklist = manager.getWorklist(worklistName, blockGraph);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        DFAFactory dfaFactory = null;
        try {
            dfaFactory = analysisLoader.getDFAFactory(analysisName);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        DFAPrecalculator precalculator = null;
        try {
            new DFAPrecalculator(dfaFactory, worklist, blockGraph);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        Thread precalc = new Thread(precalculator);
        try {
            precalc.start();
        } catch (IllegalThreadStateException e) {
            e.printStackTrace();
        }
        this.graphUIController.start(dfaExecution);
        while (precalc.isAlive()) {
            try {
                wait(6000); // TODO which value do we want to use
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            OptionBox optionBox = new OptionBox(this.programFrame, calcMessage);
            if (optionBox.getOption() == Option.YES_OPTION) {
                precalc.stop();
                this.programFrame.getInputPanel().setActivated(true);
                return;
            }
        }

        this.dfaExecution = precalculator.getDFAExecution();
        precalculator = null;
        this.visualGraphPanel.setActivated(true);
        this.programFrame.getControlPanel().setActivated(ControlPanelState.ACTIVATED);
        this.programFrame.getStatePanelOpen().setActivated(true);
    }

    /**
     * Deletes the current {@code DFAExecution} and the content of the
     * {@code VisualGraphPanel} through the {@code GraphUIController}. The
     * {@code ControlPanel}, the {@code StatePanel} and the
     * {@code VisualGraphPanel} are deactivated and the {@code InputPanel} is
     * activated.
     */
    public void stopAnalysis() {
        OptionBox option = new OptionBox(this.programFrame, abortMessage);
        if (!(option.getOption() == Option.YES_OPTION)) {
            return;
        }
        this.dfaExecution = null;
        this.graphUIController.stop();
        this.visualGraphPanel.setActivated(false);
        this.programFrame.getInputPanel().setActivated(true);
        this.programFrame.getControlPanel().setActivated(ControlPanelState.DEACTIVATED);
        this.programFrame.getStatePanelOpen().setActivated(false);
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
    }

    /**
     * Returns a list of the names of the analyses that were found during
     * program start by the {@code AnalysisLoader}.
     * 
     * @return list of names of the found analyses
     */
    public List<String> getAnalysis() {
        return this.analysisLoader.getAnalysesNames();
    }

    /**
     * Returns the {@code VisualGraphPanel} created by the
     * {@code GraphUIController).
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
