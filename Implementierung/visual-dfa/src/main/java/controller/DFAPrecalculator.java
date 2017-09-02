package controller;

import dfa.framework.DFAException;
import dfa.framework.DFAExecution;
import dfa.framework.DFAFactory;
import dfa.framework.DFAPrecalcController;
import dfa.framework.LatticeElement;
import dfa.framework.SimpleBlockGraph;
import dfa.framework.Worklist;

/**
 * Unit, used by the {@code DFAExecution} to precalculate all analysis steps in
 * a different thread.
 * 
 * @author Anika Nietzer
 */
public class DFAPrecalculator implements Runnable {

    private DFAFactory<? extends LatticeElement> factory;
    private Worklist worklist;
    private SimpleBlockGraph simpleBlockGraph;
    private DFAPrecalcController precalcController;
    private Controller controller;

    /**
     * Creates a new {@code DFAPrecalculator} to calculate all steps at the
     * beginning of the analysis.
     * 
     * @param factory
     *            {@code DFAFactory} that decides which analysis will be
     *            executed
     * @param worklist
     *            {@code Worklist} that will be used for the analysis
     * @param simpleBlockGraph
     *            {@code SimpleBlockGraph} on that the analysis will be
     *            performed
     * @param precalcController
     *            {@code DFAPrecalcController} that is responsible for this
     *            calculation
     * @param controller
     *            {@code Controller} to inform the user about an exception
     * 
     */
    public DFAPrecalculator(DFAFactory<? extends LatticeElement> factory, Worklist worklist,
            SimpleBlockGraph simpleBlockGraph, DFAPrecalcController precalcController, Controller controller) {
        if (factory == null) {
            throw new IllegalArgumentException("factory must not be null");
        }
        if (worklist == null) {
            throw new IllegalArgumentException("worklist must not be null");
        }
        if (simpleBlockGraph == null) {
            throw new IllegalArgumentException("simpleBlockGraph must not be null");
        }
        if (precalcController == null) {
            throw new IllegalArgumentException("precalcController must not be null");
        }
        if (controller == null) {
            throw new IllegalArgumentException("controller must not be null");
        }
        this.factory = factory;
        this.worklist = worklist;
        this.simpleBlockGraph = simpleBlockGraph;
        this.precalcController = precalcController;
        this.controller = controller;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    /**
     * Used during creation of a new thread, that performs the steps of the
     * analysis. Creates a new {@code MessageBox} to inform the user in case of
     * a failure.
     */
    public void run() {
        try {
            new DFAExecution(this.factory, this.worklist, this.simpleBlockGraph, this.precalcController);
            this.controller.completedAnalysis();
        } catch (DFAException e) {
            this.controller.createExceptionBox(e.getMessage());
            this.controller.visibilityInput();
        } catch (RuntimeException e) {
            System.out.println("An unexpected error occured during the precalculation of the analysis: ");
            e.printStackTrace();
        }
    }
}
