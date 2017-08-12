package gui;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

import controller.Controller;
import gui.visualgraph.VisualGraphPanel;

/**
 * The frame which contains all panels.
 * 
 * @author Michael
 *
 */
public class ProgramFrame extends JFrame {

    private static final long serialVersionUID = 5047834266753718967L;

    private JPanel contentPane;
    private InputPanel inputPanel;
    private ControlPanel controlPanel;
    private StatePanelOpen statePanelOpen;
    private StatePanelClosed statePanelClosed;
    private VisualGraphPanel visualGraphPanel;
    private boolean isStatePanelOpen;

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

        this.ctrl = ctrl;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // TODO constants for resolution
        setBounds(0, 0, 1920, 1080);
        // setMinimumSize(new Dimension(1920, 1080));
        contentPane = new JPanel();

        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        inputPanel = new InputPanel(ctrl);
        contentPane.add(inputPanel, BorderLayout.WEST);

        JPanel centerPan = new JPanel();
        contentPane.add(centerPan, BorderLayout.CENTER);
        centerPan.setLayout(new BorderLayout(0, 0));

        JPanel vgPan = ctrl.getVisualGraphPanel();
        centerPan.add(vgPan, BorderLayout.CENTER);

        controlPanel = new ControlPanel(ctrl);
        centerPan.add(controlPanel, BorderLayout.SOUTH);

        statePanelOpen = new StatePanelOpen(this);
        contentPane.add(statePanelOpen, BorderLayout.EAST);
        
        isStatePanelOpen = true;

        statePanelClosed = new StatePanelClosed(this);
        
        
    }

    /**
     * This method will switch between the StatePanelOpen and the
     * StatePanelClosed. Only one of them will be shown at a time.
     * 
     * @see StatePanelOpen
     * @see StatePanelClosed
     */
    public void switchStatePanel() {
        if (isStatePanelOpen == true) {
            remove(statePanelOpen);
            add(statePanelClosed, BorderLayout.EAST);
            isStatePanelOpen = false;
        } else {
            remove(statePanelClosed);
            add(statePanelOpen, BorderLayout.EAST);
            isStatePanelOpen = true;
        }
        
        revalidate();
        repaint();
    }

    /**
     * This method will return the InputPanel.
     * 
     * @return The InputPanel
     * @see InputPanel
     */
    public InputPanel getInputPanel() {
        return inputPanel;
    }

    /**
     * This method will return the ControlPanel.
     * 
     * @return The ControlPanel
     * @see ControlPanel
     */
    public ControlPanel getControlPanel() {
        return controlPanel;
    }

    /**
     * This method will return the StatePanelOpen.
     * 
     * @return The StatePanelOpen
     * @see StatePanelOpen
     */
    public StatePanelOpen getStatePanelOpen() {
        return statePanelOpen;
    }
    
    public File getCompilerPath() {
        JFileChooser pathChooser = new JFileChooser(System.getProperty("java.home"));
        pathChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = JFileChooser.CANCEL_OPTION;
        while (returnVal == JFileChooser.CANCEL_OPTION || returnVal == JFileChooser.ERROR_OPTION) {
            returnVal = pathChooser.showOpenDialog(this);
        }
        return pathChooser.getSelectedFile();
    }
}
