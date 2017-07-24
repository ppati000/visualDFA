package dfa.framework;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Sebastian Rauch
 * 
 *         A {@code DFAExecution} precalculates a {@code DataFlowAnalysis} and provides the intermediate analysis-steps.
 *
 * @param <E>
 *        the type of {@code LatticeElement} used in this {@code DFAExecution}
 */
public class DFAExecution<E extends LatticeElement> {

    private final Worklist initialWorklist;

    private final DFADirection direction;

    private final DataFlowAnalysis<E> dfa;

    private final ControlFlowGraph cfg;

    private List<AnalysisState<E>> analysisStates = new ArrayList<>();
    private List<Integer> blockSteps = new ArrayList<>();

    private int currentElementaryStep = -1;
    private int currentBlockStep = -1;

    /**
     * Creates a {@code DFAExecution} from a given {@code DFAFactory}, an initial {@code Worklist} and a
     * {@code SimpleBlockGraph}.
     * 
     * @param dfaFactory
     *        a {@code DFAFactory} from which a {@code DataFlowAnalysis} will be generated
     * @param initialWorklist
     *        an (empty) worklist to use in this {@code DFAExecution}
     * @param blockGraph
     *        the {@code SimpleBlockGraph} the analysis is based on
     * 
     * @throws IllegalArgumentException
     *         if any of {@code initialWorklist} or {@code blockGraph} is {@code null}
     * 
     * @throws NullPointerException
     *         if {@code dfaFactory} is {@code null}
     */
    public DFAExecution(DFAFactory<E> dfaFactory, Worklist initialWorklist, SimpleBlockGraph blockGraph) {
        if (initialWorklist == null) {
            throw new IllegalArgumentException("initialWorklist must not be null");
        }

        if (blockGraph == null) {
            throw new IllegalArgumentException("blockGraph must not be null");
        }

        this.initialWorklist = initialWorklist;
        this.direction = dfaFactory.getDirection();

        dfa = dfaFactory.getAnalysis(blockGraph);

        this.cfg = new ControlFlowGraph(blockGraph);

        precalc();
    }

    private DFAExecution(DFAExecution<E> copyFrom) {
        this.initialWorklist = copyFrom.initialWorklist;
        this.direction = copyFrom.getDirection();
        this.dfa = copyFrom.dfa;
        this.cfg = copyFrom.getCFG();

        this.analysisStates = copyFrom.analysisStates;
        this.blockSteps = copyFrom.blockSteps;

        setCurrentElementaryStep(copyFrom.getCurrentElementaryStep());
        setCurrentBlockStep(copyFrom.getCurrentBlockStep());
    }

    /**
     * Returns the {@code ControlFlowGraph} that is used to execute the dataflow-analysis.
     * 
     * @return the {@code ControlFlowGraph} used to execute the dataflow-analysis
     */
    public ControlFlowGraph getCFG() {
        return cfg;
    }

    /**
     * Returns the direction of the {@code DataFlowAnalysis} that is executed.
     * 
     * @return the direction of the {@code DataFlowAnalysis} that is executed
     */
    public DFADirection getDirection() {
        return direction;
    }

    /**
     * Returns the total number of elementary-steps.
     * 
     * @return the total number of elementary-steps
     */
    public int getTotalElementarySteps() {
        return analysisStates.size();
    }

    /**
     * Returns the total number of block-steps.
     * 
     * @return the total number of block-steps
     */
    public int getTotalBlockSteps() {
        return blockSteps.size();
    }

    /**
     * Returns the current elementary-step.
     * 
     * @return the current elementary-step, always in range {@code [0, ..., getTotalElementarySteps() - 1]}
     */
    public int getCurrentElementaryStep() {
        return currentElementaryStep;
    }

    /**
     * Sets the current elementary-step. The current block-step is updated to be consistent with the current
     * elementary-step.
     * 
     * @param elementaryStep
     *        the elementary-step to set as current elementary-step
     * 
     * @throws IllegalArgumentException
     *         if {@code elementaryStep} is not in range {@code [0, ..., getTotalElementarySteps() - 1]}
     */
    public void setCurrentElementaryStep(int elementaryStep) {
        if (elementaryStep < 0 || elementaryStep >= getTotalElementarySteps()) {
            throw new IndexOutOfBoundsException("invalid elementaryStep: " + elementaryStep);
        }

        currentElementaryStep = elementaryStep;
        int idx = Collections.binarySearch(blockSteps, currentElementaryStep);

        if (idx < 0) { // currentElementaryStep has not been found
            idx = -(idx + 1); // this gives the correct index of the corresponding block-step
        }
        currentBlockStep = idx;
    }

    /**
     * Returns the current block-step.
     * 
     * @return the current block-step, always in range {@code [0, ..., getTotalBlockSteps() - 1]}
     */
    public int getCurrentBlockStep() {
        return currentBlockStep;
    }

    /**
     * Sets the current block-step. The current elementary-step is updated to be consistent with the current block-step.
     * 
     * @param blockStep
     *        the block-step to set as current block-step
     * 
     * @throws IllegalArgumentException
     *         if {@code blockStep} is not in range {@code [0, ..., getTotalBlockSteps() - 1]}
     */
    public void setCurrentBlockStep(int blockStep) {
        if (blockStep < 0 || blockStep >= getTotalBlockSteps()) {
            throw new IndexOutOfBoundsException("invalid blockStep: " + blockStep);
        }

        currentBlockStep = blockStep;
        currentElementaryStep = blockSteps.get(blockStep);
    }

    /**
     * Increases the current elementary-step (by 1) if possible.
     * 
     * @return {@code true} if there was a next elementary-step, {@code false} otherwise
     */
    public boolean nextElementaryStep() {
        int nextStep = getCurrentElementaryStep() + 1;
        if (nextStep < getTotalElementarySteps()) {
            setCurrentElementaryStep(nextStep);
            return true;
        }

        return false;
    }

    /**
     * Decreases the current elementary-step by 1 if possible.
     * 
     * @return {@code true} if there was a next elementary-step, {@code false} otherwise
     */
    public boolean previousElementaryStep() {
        int prevStep = getCurrentElementaryStep() - 1;
        if (prevStep >= 0) {
            setCurrentElementaryStep(prevStep);
            return true;
        }

        return false;
    }

    /**
     * Advances the current block-step (by 1) if possible.
     * 
     * @return {@code true} if there was a next block-step, {@code false} otherwise
     */
    public boolean nextBlockStep() {
        int nextBlockStep = getCurrentBlockStep() + 1;
        if (nextBlockStep < getTotalBlockSteps()) {
            setCurrentBlockStep(nextBlockStep);
            return true;
        }

        return false;
    }

    /**
     * Decreases the current block-step by 1 if possible.
     * 
     * @return {@code true} if there was a next block-step, {@code false} otherwise
     */
    public boolean previousBlockStep() {
        int currentBlockStep = getCurrentBlockStep() - 1;
        if (currentBlockStep >= 0) {
            setCurrentBlockStep(currentBlockStep);
            return true;
        }

        return false;
    }

    /**
     * Returns whether the current elementary-step is at {@code ElementaryBlock} that has a breakpoint.
     * 
     * @return {@code true} if the {@code ElementaryBlock} of the current elementary-step has a breakpoint,
     *         {@code false} otherwise
     */
    public boolean isAtBreakpoint() {
        AnalysisState<? extends LatticeElement> currentState = getCurrentAnalysisState();
        BasicBlock bBlock = currentState.getCurrentBasicBlock();

        if (bBlock == null) {
            return false;
        }

        int eBlockIdx = currentState.getCurrentElementaryBlockIndex();
        return bBlock.getElementaryBlock(eBlockIdx).hasBreakpoint();
    }

    /**
     * Returns the current {@code AnalysisState}, i. e. the state of the analysis at the current elementary-step.
     * 
     * @return the current {@code AnalysisState}
     */
    public AnalysisState<? extends LatticeElement> getCurrentAnalysisState() {
        return analysisStates.get(currentElementaryStep);
    }

    /**
     * Creates a <em>shallow copy</em> of this {@code DFAExecution}. The returned copy shares the {@code AnalysisState}s
     * with the original but can have different current block- or elementary-steps.
     */
    @Override
    public DFAExecution<E> clone() {
        return new DFAExecution<E>(this);
    }

    private void precalc() {
        // TODO implement
        // this is where the magic happens
    }

}
