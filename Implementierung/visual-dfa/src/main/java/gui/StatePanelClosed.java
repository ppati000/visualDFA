package gui;

import javax.swing.JPanel;

/**
 * The StatePanelClosed class can be switched with the StatePanelOpen class,
 * depending on whether the user wants to see the state of the data flow
 * analysis.
 * 
 * @author Michael
 *
 */

public class StatePanelClosed extends JPanel {

    private ProgramFrame frame;

    /**
     * Create the panel. Set the PragramFrame, so the StatePanel can be
     * switched.
     * 
     * @param frame
     *            The ProgramFrame which contains the StatePanel.
     * @see ProgramFrame
     */
    public StatePanelClosed(ProgramFrame frame) {
        this.frame = frame;
    }

    /**
     * Activate or deactivate the Panel and its content.
     * 
     * @param b
     *            Activate [true] or deactivate [false] the panel.
     */
    public void setActivated(boolean b) {

    }
}
