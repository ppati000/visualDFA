package gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * The {@code StatePanelClosed} class can be switched with the
 * {@code StatePanelOpen} class, depending on whether the user wants to see the
 * state of the data flow analysis.
 * 
 * @author Michael
 *
 */

public class StatePanelClosed extends JPanel {

    private static final long serialVersionUID = 1L;
    
    private ProgramFrame frame;
    private JButton openButton;

    /**
     * Create the panel. Set the {@code PragramFrame}, so the StatePanel can be
     * switched.
     * 
     * @param frame
     *            the {@code ProgramFrame} which contains the StatePanel
     * @see ProgramFrame
     */
    public StatePanelClosed(ProgramFrame frame) {
        this.frame = frame;

        setBackground(Colors.BACKGROUND.getColor());
        setLayout(new BorderLayout());

        openButton = new JButton();
        new JButtonDecorator(new JComponentDecorator()).decorateIconButton(openButton, "icons/open-button.png", 0.1,
                new OpenListener(), null);
        add(openButton, BorderLayout.CENTER);

    }

    /**
     * Activate or deactivate the Panel and its content.
     * 
     * @param b
     *            activate {@code true} or deactivate {@code false} the panel
     */
    public void setActivated(boolean b) {

    }

    /**
     * Listener for the Button which opens the StatePanel.
     * 
     * @author Michael
     *
     */
    private class OpenListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            frame.switchStatePanel();

        }

    }
}
