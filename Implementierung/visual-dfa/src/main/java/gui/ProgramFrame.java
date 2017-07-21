package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import controller.Controller;

/**
 * The frame which contains all panels.
 * 
 * @author Michael
 *
 */
public class ProgramFrame extends JFrame {

    private static final long serialVersionUID = 5047834266753718967L;
    // TODO use complete names
    private JPanel contentPane;
    private JPanel inPan;
    private JPanel ctrlPan;
    private JPanel statPanOpen;
    private JPanel statPanClosed;
    private JPanel vgPan;

    private Controller ctrl;

    /**
     * Creates a JFrame and its content. Sets the minimum Size to 1920x1080
     * Pixels and the Layout of the ContentPane.
     * 
     * @param ctrl
     *            The controller to which the actionListeners will correspond.
     * 
     * @see Controller
     * @see java.awt.event.ActionListener
     */

    public ProgramFrame(Controller ctrl) {

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // TODO constants for resolution
        setBounds(0, 0, 1920, 1080);
        // setMinimumSize(new Dimension(1920, 1080));
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        inPan = new InputPanel(ctrl);
        contentPane.add(inPan, BorderLayout.WEST);

        JPanel centerPan = new JPanel();
        contentPane.add(centerPan, BorderLayout.CENTER);
        centerPan.setLayout(new BorderLayout(0, 0));

        // TODO
        /*
         * JPanel vgPan = ctrl.getVisualGraphPanel(); panel.add(panel_2,
         * BorderLayout.CENTER);
         */

        ctrlPan = new ControlPanel(ctrl);
        centerPan.add(ctrlPan, BorderLayout.SOUTH);

        statPanOpen = new StatePanelOpen(this);
        contentPane.add(statPanOpen, BorderLayout.EAST);

        statPanClosed = new StatePanelClosed(this);
    }

    /**
     * This method will switch between the StatePanelOpen and the
     * StatePanelClosed. Only one of them will be shown at a time.
     * 
     * @see StatePanelOpen
     * @see StatePanelClosed
     */
    public void switchStatePanel() {
        // TODO
    }

    /**
     * This method will return the InputPanel.
     * 
     * @return The InputPanel
     * @see InputPanel
     */
    public JPanel getInputPanel() {
        return inPan;
    }

    /**
     * This method will return the ControlPanel.
     * 
     * @return The ControlPanel
     * @see ControlPanel
     */
    public JPanel getControlPanel() {
        return ctrlPan;
    }

    /**
     * This method will return the StatePanelOpen.
     * 
     * @return The StatePanelOpen
     * @see StatePanelOpen
     */
    public JPanel getStatePanelOpen() {
        return statPanOpen;
    }
}
