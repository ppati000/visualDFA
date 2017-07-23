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
	 * {@code Controller).
	 * 
	 * @param controller
	 *            instance of {@code Controller}
	 */
	public AutoplayDriver(Controller controller) {
		this.controller = controller;
	}

	@Override
	/**
	 * Used during creation of a new thread, that performs the steps of the
	 * analysis.
	 */
	public void run() {
		boolean hasNextLine = false;
		//TODO switch order
		do {
			try {
				Thread.sleep((long) (controller.getDelay() * 1000));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			hasNextLine = controller.nextLine();
		} while (hasNextLine);
		this.controller.pause();
	}

}
