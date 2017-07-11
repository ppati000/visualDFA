package controller;

import dfa.framework.DFAExecution;
import dfa.framework.DataFlowAnalysis;
import dfa.framework.SimpleBlockGraph;
import dfa.framework.Worklist;
import soot.toolkits.graph.BlockGraph;

/**
 * 
 * @author Anika Nietzer 
 * 			Unit, used by the {@code DFAExecution} to precalculate
 *         	all analysis steps in a different thread.
 *
 */
public class DFAPrecalculator implements Runnable {
	
	private DFAExecution dfaExecution;

	/**
	 * Creates a new {@code DFAPrecalculator} to calculate all steps at the
	 * beginning of the analysis.
	 * 
	 * @param dfa
	 *            {@code DataFlowAnalysis} that will be performed
	 * @param worklist
	 *            {@code Worklist} that will be used for the analysis
	 * @param simpleBlockGraph
	 *            {@code SimpleBlockGraph} on that the analysis will be
	 *            performed
	 */
	public DFAPrecalculator(DataFlowAnalysis dfa, Worklist worklist, SimpleBlockGraph simpleBlockGraph) {

	}

	/**
	 * Used during creation of a new thread, that performs the steps of the
	 * analysis.
	 */
	public void run() {

	}

	/**
	 * Returns the dfaExecution.
	 * @return created instance of {@code DFAExecution}
	 */
	public DFAExecution getDFAExecution() {
		return dfaExecution;
	}

}
