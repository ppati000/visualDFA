package dfa.framework;

/**
 * A {@code DFAPrecalcController} is used to control the precalculation of a {@code DFAExecution}. When precalculating
 * an analysis, a {@code DFAExecution} should regularly check it's given {@code DFAPrecalcController} to see, if it
 * should stop the calculation (e. g. because it already took quite some time).
 * 
 * @author Sebastian Rauch
 *
 */
public class DFAPrecalcController {

    private PrecalcState precalcState = PrecalcState.CALCULATING;

    private ResultState resultState = ResultState.NO_RESULT;

    private int waitTime = 0;

    private DFAExecution<? extends LatticeElement> result = null;

    /**
     * Tells the {@code DFAPrecalcController} to stop the precalculation.
     */
    public synchronized void stopPrecalc() {
        if (getPrecalcState() == PrecalcState.COMPLETED) {
            throw new IllegalStateException("a completed calculation cannot be stopped");
        }

        precalcState = PrecalcState.STOPPED;
    }

    /**
     * Tells the {@code DFAPrecalcController} to pause the precalculation. The precalculation will be repeatedly paused
     * for {@code wiatTime}.
     * 
     * @param waitTime
     *        the time to repeatedly wait (in ms)
     */
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

    /**
     * Tells the {@code DFAPrecalcController} to continue the precalculation (when paused).
     */
    public synchronized void continuePrecalc() {
        if (getPrecalcState() == PrecalcState.STOPPED) {
            throw new IllegalStateException("a stopped calculation cannot be continued");
        }

        if (getPrecalcState() == PrecalcState.COMPLETED) {
            return;
        }

        this.precalcState = PrecalcState.CALCULATING;
    }

    /**
     * Returns the set wait time (in ms).
     * 
     * @return the set wait time (in ms)
     */
    public int getWaitTime() {
        return waitTime;
    }

    /**
     * Returns the current {@code PrecalcState}.
     * 
     * @return the current {@code PrecalcState}
     */
    public synchronized PrecalcState getPrecalcState() {
        return precalcState;
    }

    /**
     * Returns the current {@code ResultState}.
     * 
     * @return the current {@code ResultState}
     */
    public synchronized ResultState getResultState() {
        return resultState;
    }

    /**
     * Sets the result and the {@code ResultState} according to the {@code completed} parameter.
     * 
     * @param result
     *        the result of the precalculation
     * @param completed
     *        whether the result is a completely precalculated {@code DFAExecution}
     */
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

    /**
     * Returns the precalculated {@code DFAExecution}.
     * 
     * @return the precalculated {@code DFAExecution}
     */
    public synchronized DFAExecution<? extends LatticeElement> getResult() {
        if (result == null) {
            throw new IllegalStateException("there is no result yet");
        }

        return result;
    }

    /**
     * The state of the precalculation.
     * 
     * @author Sebastian Rauch
     *
     */
    public enum PrecalcState {
        /**
         * precalculation is continuing
         */
        CALCULATING,

        /**
         * precalculation was completed
         */
        COMPLETED,

        /**
         * precalculation is paused
         */
        PAUSED,

        /**
         * precalculation was stopped (cannot be resumed)
         */
        STOPPED
    }

    public enum ResultState {
        /**
         * there is no result yet
         */
        NO_RESULT,

        /**
         * there is an intermediate result (i. e. the precalculation was not completed)
         */
        INTERMEDIATE_RESULT,

        /**
         * there is a completed result
         */
        COMPLETE_RESULT
    }

}
