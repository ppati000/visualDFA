package gui;

import java.awt.event.ActionListener;

import javax.swing.JPanel;

import controller.Controller;

/**
 * The ControlPanel Class contains UI-elements to let the user control the
 * control flow graph.
 * 
 * @author Michael
 *
 */
public class ControlPanel extends JPanel {

    private Controller ctrl;

    /**
     * Create the panel. Set the controller, so the ActionListeners can access
     * it.
     * 
     * @param ctrl
     *            The Controller to be accessed in case of events.
     * 
     * @see controller.Controller
     * @see ActionListener
     */
    public ControlPanel(Controller ctrl) {
        this.ctrl = ctrl;
        // TODO UI Elements
    }

    /**
     * Set the step on the step-slider, which should be coherent with the state
     * of the control flow graph.
     * 
     * @param step
     *            The step to be set.
     */
    public void setSliderStep(int step) {
        // TODO
    }

    /**
     * Set the total number of steps on the step-slider. Should be coherent to
     * the total number of steps in the control flow graph.
     * 
     * @param steps
     *            The total number of steps.
     */
    public void setTotalSteps(int steps) {
        // TODO
    }

    /**
     * Set the activity state of the ControlPanel. ACTIVATED: All UI-elements
     * are activated. PLAYING: Only the DelaySlider and the PauseButton are
     * activated. DEACTIVATED: All UI-elements are deactivated.
     * 
     * @param cps
     *            An ENUM which defines the activity state of the ControlPanel.
     * 
     * @see ControlPanelState
     */
    public void setActivated(ControlPanelState cps) {
        // TODO
    }

    /**
     * Look up the current position of the DelaySlider and return it.
     * 
     * @return The position of the DelaySlider.
     */
    public int getDelaySliderPosition() {
        // TODO
        return 0;
    }

    /**
     * Look up the current position of the StepSlider and return it.
     * 
     * @return The position of the StepSlider.
     */
    public int getSliderStep() {
        // TODO
        return 0;
    }

}
