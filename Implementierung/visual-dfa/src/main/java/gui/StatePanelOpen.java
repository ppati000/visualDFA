package gui;

import javax.swing.JPanel;

/**
 * The StatePanelOpen Class contains UI-elements to let the user see the state
 * of the data flow analysis.
 * 
 * @author Michael
 *
 */
public class StatePanelOpen extends JPanel {

    private ProgramFrame frame;

    /**
     * Create the panel. Set the PragramFrame, so the StatePanel can be
     * switched.
     * 
     * @param The
     *            ProgramFrame which contains the StatePanel.
     * @see ProgramFrame
     */
    public StatePanelOpen(ProgramFrame frame) {
        this.frame = frame;
        // TODO
    }

    /**
     * Activate or deactivate the Panel and its contents.
     * 
     * @param b
     *            Activate [true] or deactivate [false] the panel.
     */
    public void setActivated(boolean b) {
        // TODO
    }

    public void setIn(String latticeElement) {
        System.out.println(latticeElement); // TODO
    }

    public void setOut(String latticeElement) {
        System.out.println(latticeElement); // TODO
    }

    public void setSelectedLine(String s, int blockNumber, int lineNumber) {
        // TODO Hey Michi. Denk dran: lineNumber kann auch -1 sein! In diesem Fall die lineNumber bitte nicht anzeigen :)
        System.out.println(s + "\n" + blockNumber + "\n" + lineNumber);
    }

}
