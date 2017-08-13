package controller;

/**
 * 
 * @author Anika Nietzer An {@code AutoplayDriver} represents a unit, that is
 *         responsible for the automatic performance of the animation of the
 *         analysis-steps in a different thread.
 *
 */
public class AutoplayDriver implements Runnable {

    private Controller controller;

    /**
     * Performs the automatic replay of the analysis by using methods of the
     * {@code Controller}.
     * 
     * @param controller
     *            instance of {@code Controller}
     */
    public AutoplayDriver(Controller controller) {
        if (controller == null) {
            throw new IllegalArgumentException("controller must not be null");
        }
        this.controller = controller;
    }

    @Override
    /**
     * Used during creation of a new thread, that performs the steps of the
     * analysis.
     */
    public void run() {
        // number of times you should wait 50ms long
        int delayTimes = this.controller.getDelay() * 20;
        lineIterator: while (this.controller.nextLine()) {
            for (int i = 0; i < delayTimes; i++) {
                try {
                    Thread.sleep((long) 50);
                    delayTimes = this.controller.getDelay() * 20;
                    if (!this.controller.shouldContinue()) {
                        break lineIterator;
                    }
                } catch (InterruptedException e) {
                }
            }

        }
        this.controller.visibilityWorking();
    }

}
