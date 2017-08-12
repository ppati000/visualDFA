package gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
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
    private JButton openButton;

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
        
        setBackground(Colors.BACKGROUND.getColor());
        setLayout(new BorderLayout());
        
        openButton = new JButton();
        new JButtonDecorator(new JComponentDecorator()).decorateIconButton(openButton, "icons/open-button.png", 0.1, new OpenListener(), null);
        add(openButton, BorderLayout.NORTH);
        
    }

    /**
     * Activate or deactivate the Panel and its content.
     * 
     * @param b
     *            Activate [true] or deactivate [false] the panel.
     */
    public void setActivated(boolean b) {

    }
    
    private class OpenListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            frame.switchStatePanel();
            
        }
        
    }
}
