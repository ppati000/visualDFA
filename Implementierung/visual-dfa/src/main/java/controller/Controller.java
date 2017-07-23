package controller;

import java.util.List;
import java.util.logging.Logger;

import javax.swing.JPanel;

import codeprocessor.*;
import dfa.framework.DFAExecution;
import dfa.framework.DFAFactory;
import dfa.framework.SimpleBlockGraph;
import dfa.framework.Worklist;
import gui.*;
import gui.visualgraph.VisualGraphPanel;
import gui.visualgraph.GraphUIController;

/**
 * 
 * @author Anika Nietzer Central unit, that is responsible for the communication
 *         between the GUI and the remaining packages of the program.
 *
 */
public class Controller {

	private static final String analysisPackageName = "toDefine"; // TODO define
																	// the
																	// packageName
	private ProgramFrame programFrame;
	private DFAExecution dfaExecution;
	private GraphUIController graphUIController;
	private VisualGraphPanel visualGraphPanel;
	private AnalysisLoader analysisLoader;
	private Thread autoplay;

	/**
	 * Creates a new {@code Controller} and loads the available analyses with an
	 * instance of {@code AnalysisLoader}. Creates a {@code VisualGraphPanel}
	 * and a {@code GraphUIController}.
	 */
	public Controller() {
		this.analysisLoader = new AnalysisLoader(analysisPackageName);
		Logger logger;
		this.analysisLoader.loadAnalyses(logger);
		this.visualGraphPanel = new VisualGraphPanel();
		this.graphUIController = new GraphUIController(visualGraphPanel);
	}

	/**
	 * Leads to the calculation of the next block by the DFAFramework and
	 * ensures that the {@code GraphUIController} actualizes the
	 * {@code VisualGraphPanel}.
	 * 
	 * @return weather there was a next block to show or not
	 */
	public boolean nextBlock() {
		boolean hasNextBlock = this.dfaExecution.nextBlockStep();
		if (hasNextBlock) {
			this.graphUIController.refresh();
		}
		return hasNextBlock;
	}

	/**
	 * Leads to the calculation of the next line by the DFAFramework and ensures
	 * that the {@code GraphUIController} updates the {@code VisualGraphPanel}.
	 * 
	 * @return weather there was a next line to show or not
	 */
	public boolean nextLine() {
		boolean hasNextLine = this.dfaExecution.nextElementaryStep();
		if (hasNextLine) {
			this.graphUIController.refresh();
		}
		return hasNextLine;
	}

	/**
	 * Leads to the calculation of the previous line by the DFAFramework and
	 * ensures that the {@code GraphUIController} updates the
	 * {@code VisualGraphPanel}.
	 * 
	 * @return weather there was a previous line to show or not
	 */
	public boolean previousLine() {
		boolean hasPreviousLine = this.dfaExecution.previousElementaryStep();
		if (hasPreviousLine) {
			this.graphUIController.refresh();
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
			this.graphUIController.refresh();
		}
		return hasPreviousBlock;
	}

	/**
	 * Leads to the calculation of a given step in the analysis from the
	 * DFAFramework and ensures that the {@code GraphUIController} updates the
	 * {@code VisualGraphPanel}.
	 * 
	 * @param step
	 *            step to show in the animation
	 * @return weather the step can be shown or not
	 */
	public boolean jumpToStep(int step) {
		this.dfaExecution.setCurentElementaryStep(step);
		// TODO Elementary or Block Step?
		return false;
	}

	/**
	 * Creates a new {@code AutoplayDriver} to replay the different steps of the
	 * analysis if a delay bigger than zero is selected or jumps to the last
	 * step of the analysis if the chosen delay is zero.
	 */
	public void play() {
		if (getDelay() == 0) {
			this.dfaExecution.setCurentElementaryStep(this.dfaExecution.getTotalElementarySteps());
			return;
		}
		AutoplayDriver autoplayDriver = new AutoplayDriver(this);
		// Start in new Thread
		// TODO was passiert mit den ganzen sachen, die man in der gui anstellen
		// kann
		this.programFrame.getControlPanel().setActivated(ControlPanelState.PLAYING);
		this.getVisualGraphPanel().setActivated(false);
		this.autoplay = new Thread(autoplayDriver);
		this.autoplay.start();

	}

	/**
	 * Returns the chosen delay
	 * 
	 * @return delay, the user has set
	 */
	public int getDelay() {
		return this.programFrame.getControlPanel().getDelaySliderPosition();
	}

	/**
	 * Stops the {@code AutoplayDriver}.
	 */
	@SuppressWarnings("deprecation")
	public void pause() {
		this.autoplay.stop();
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
	public void startAnalysis() {
		// Collect information
		programFrame.getInputPanel().setActivated(false);
		String analysisName = programFrame.getInputPanel().getAnalysis();
		String worklistName = programFrame.getInputPanel().getWorklist();
		String code = programFrame.getInputPanel().getCode();
		boolean hasFilter = programFrame.getInputPanel().isFilterSelected();

		// Process code with instance of CodeProcessor
		CodeProcessor processor = new CodeProcessor(code);
		if (processor.wasSuccessful() == false) {
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
		// TODO create MethodSelectionBox
		String methodSignature = "test";
		SimpleBlockGraph blockGraph = graphBuilder.buildGraph(methodSignature);
		WorklistManager manager = new WorklistManager();
		Worklist worklist = manager.getWorklist(worklistName);

		// TODO get Analysis from analyses package

		DFAFactory dfaFactory;
		DFAPrecalculator precalculator = new DFAPrecalculator(dfaFactory, worklist, blockGraph);
		Thread precalc = new Thread(precalculator);
		// TODO test duration 
		this.dfaExecution = precalculator.getDFAExecution();
		this.graphUIController.start(dfaExecution);
		this.visualGraphPanel.setActivated(true);
		this.programFrame.getControlPanel().setActivated(ControlPanelState.ACTIVATED);
		this.programFrame.getStatePanelOpen().setActivated(true);
	}

	/**
	 * Deletes the actual {@code DFAExecution} and the content of the
	 * {@code VisualGraphPanel} through the {@code GraphUIController}. The
	 * {@code ControlPanel}, the {@code StatePanel} and the
	 * {@code VisualGraphPanel} are deactivated and the {@code InputPanel} is
	 * activated.
	 */
	public void stopAnalysis() {
		// TODO open information box
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
		this.programFrame = programFrame;
	}

	/**
	 * Returns a list of the names of the analyses that were found during
	 * program start by the {@code AnalysisLoader}.
	 * 
	 * @return list of names of the found analyses
	 */
	public List<String> getAnalysis() {
		return analysisLoader.getAnalysesNames();
	}

	/**
	 * Returns the {@code VisualGraphPanel} created by the
	 * {@code GraphUIController).
	 * 
	 * @return the instance of {@code VisualGraphPanel}
	 */
	public VisualGraphPanel getVisualGraphPanel() {
		return this.visualGraphPanel;
	}

}
