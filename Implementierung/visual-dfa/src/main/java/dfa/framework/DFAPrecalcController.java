package dfa.framework;

public class DFAPrecalcController {

    private PrecalcState precalcState = PrecalcState.CALCULATING;

    private ResultState resultState = ResultState.NO_RESULT;

    private int waitTime = 0;

    private DFAExecution<? extends LatticeElement> result = null;

    public synchronized void stopPrecalc() {
        if (getPrecalcState() == PrecalcState.COMPLETED) {
            throw new IllegalStateException("a completed calculation cannot be stopped");
        }

        precalcState = PrecalcState.STOPPED;
    }

    public synchronized void pausePrecalc(int waitTime) {
        if (getPrecalcState() == PrecalcState.STOPPED) {
            throw new IllegalStateException("a stopped calculation cannot be continued");
        }

        if (getPrecalcState() == PrecalcState.COMPLETED) {
            return;
        }

        if (waitTime < 0) {
            waitTime = 0;
        }

        this.precalcState = PrecalcState.PAUSED;
        this.waitTime = waitTime;
    }

    public synchronized void continuePrecalc() {
        if (getPrecalcState() == PrecalcState.STOPPED) {
            throw new IllegalStateException("a stopped calculation cannot be continued");
        }

        if (getPrecalcState() == PrecalcState.COMPLETED) {
            return;
        }

        this.precalcState = PrecalcState.CALCULATING;
    }

    public int getWaitTime() {
        return waitTime;
    }

    public synchronized PrecalcState getPrecalcState() {
        return precalcState;
    }

    public synchronized ResultState getResultState() {
        return resultState;
    }

    public synchronized void setResult(DFAExecution<? extends LatticeElement> result, boolean completed) {
        if (this.result != null) {
            throw new IllegalStateException("a result has already been set");
        }

        if (result == null) {
            throw new IllegalArgumentException("result must not be null");
        }

        if (completed) {
            precalcState = PrecalcState.COMPLETED;
            resultState = ResultState.COMPLETE_RESULT;
        } else {
            precalcState = PrecalcState.STOPPED;
            resultState = ResultState.INTERMEDIATE_RESULT;
        }

        this.result = result;
    }

    public synchronized DFAExecution<? extends LatticeElement> getResult() {
        if (result == null) {
            throw new IllegalStateException("there is no result yet");
        }

        return result;
    }

    public enum PrecalcState {
        CALCULATING, COMPLETED, PAUSED, STOPPED
    }

    public enum ResultState {
        NO_RESULT, INTERMEDIATE_RESULT, COMPLETE_RESULT
    }

}
