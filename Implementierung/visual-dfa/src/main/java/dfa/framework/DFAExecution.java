package dfa.framework;

public class DFAExecution {

    public DFAExecution(DFAFactory dfaFactory, Worklist initialWorklist, SimpleBlockGraph blockGraph) {
        // TODO implement
    }

    public ControlFlowGraph getCFG() {
        // TODO implement
        return null;
    }

    public DFADirection getDirection() {
        // TODO implement
        return null;
    }

    public int getTotalElementarySteps() {
        // TODO implement
        return 0;
    }

    public int getTotalBlockSteps() {
        // TODO implement
        return 0;
    }

    public int getCurrentElementaryStep() {
        // TODO
        return 0;
    }
    
    public void setCurrentElementaryStep(int eStep) {
        // TODO implement
    }

    public int getCurrentBlockStep() {
        // TODO implement
        return 0;
    }

    public void setCurrentBlockStep(int bStep) {
        // TODO implement
    }

    public boolean nextElementaryStep() {
        // TODO implement
        return false;
    }

    public boolean previousElementaryStep() {
        // TODO implement
        return false;
    }

    public boolean nextBlockStep() {
        // TODO implement
        return false;
    }

    public boolean previousBlockStep() {
        // TODO implement
        return false;
    }

    public boolean isAtBreakpoint() {
        // TODO implement
        return false;
    }

    public AnalysisState<LatticeElement> getCurrentAnalysisState() {
        // TODO implement
        return null;
    }

    public DFAExecution clone() {
        // TODO implement
        return null;
    }

}
