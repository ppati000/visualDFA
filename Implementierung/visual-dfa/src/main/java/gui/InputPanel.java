package gui;

import java.awt.event.ActionListener;

import javax.swing.JPanel;

import controller.Controller;

/**
 * The InputPanel Class contains UI-elements to let the user start a new data
 * flow analysis.
 * 
 * @author Michael
 *
 */
public class InputPanel extends JPanel {

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
    public InputPanel(Controller ctrl) {
        this.ctrl = ctrl;
        // TODO UI-Elements
    }

    /**
     * Activate or deactivate the InputPanel. If deactivated, all JComponents
     * which are children of this Panel are deactivated.
     * 
     * @param b
     *            Whether the panel should be activated [true] or deactivated
     *            [false].
     */
    public void setActivated(boolean b) {
        // TODO
    }

    /**
     * Look up the currently selected data flow analysis and return its name.
     * 
     * @return The name of the data flow analysis.
     */
    public String getAnalysis() {
        // TODO
        return null;
    }

    /**
     * Get the text from the CodeField and return it to the caller.
     * 
     * @return the text from the CodeField.
     *
     */
    public String getCode() {
        // TODO
        return null;
    }

    /**
     * Look up the currently selected worklist-algorithm and return its name.
     * 
     * @return The name of the worklist-algorithm.
     */
    public String getWorklist() {
        // TODO
        return null;
    }

    /**
     * Look up if a filter is selected and return this value.
     * 
     * @return [true] if a filter is selected, [false] if not.
     */
    public boolean isFilterSelected() {
        // TODO
        return false;
    }

}
