package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.io.File;

import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

import controller.Controller;
import gui.visualgraph.VisualGraphPanel;

/**
 * The {@code JFrame} which contains all panels.
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
    private static final Dimension MIN_SIZE = new Dimension(1200, 800);
    private static final Rectangle STANDARD_BOUNDS = new Rectangle(0, 0, 1600, 800);

    private Controller ctrl;

    /**
     * Creates a {@code JFrame} and its content. Sets theLayout of the
     * ContentPane.
     * 
     * @param ctrl
     *            the {@code Controller} with which the {@code actionListeners}
     *            will correspond
     * 
     * @see Controller
     * @see java.awt.event.ActionListener
     */

    public ProgramFrame(Controller ctrl) {

        this.ctrl = ctrl;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        setBounds(STANDARD_BOUNDS);
        setExtendedState(getExtendedState() | Frame.MAXIMIZED_BOTH);
        setMinimumSize(MIN_SIZE);
        contentPane = new JPanel();

        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        inputPanel = new InputPanel(ctrl);
        contentPane.add(inputPanel, BorderLayout.WEST);

        JPanel centerPan = new JPanel();
        contentPane.add(centerPan, BorderLayout.CENTER);
        centerPan.setLayout(new BorderLayout(0, 0));

        visualGraphPanel = ctrl.getVisualGraphPanel();
        centerPan.add(visualGraphPanel, BorderLayout.CENTER);

        controlPanel = new ControlPanel(ctrl);
        centerPan.add(controlPanel, BorderLayout.SOUTH);

        statePanelOpen = new StatePanelOpen(this);
        contentPane.add(statePanelOpen, BorderLayout.EAST);

        isStatePanelOpen = true;

        statePanelClosed = new StatePanelClosed(this);

    }

    /**
     * This method will switch between the {@code StatePanelOpen} and the
     * {@code StatePanelClosed}. Only one of them will be shown at a time.
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
     * This method will return the {@code InputPanel}.
     * 
     * @return The {@code InputPanel}.
     * @see InputPanel
     */
    public InputPanel getInputPanel() {
        return inputPanel;
    }

    /**
     * This method will return the {@code ControlPanel}.
     * 
     * @return The {@code ControlPanel}.
     * @see ControlPanel
     */
    public ControlPanel getControlPanel() {
        return controlPanel;
    }

    /**
     * This method will return the {@code StatePanelOpen}.
     * 
     * @return The {@code StatePanelOpen}.
     * @see StatePanelOpen
     */
    public StatePanelOpen getStatePanelOpen() {
        return statePanelOpen;
    }
    
    /**
     * Opens a {@code JFileChooser}, so the user can set the path to his JDK.
     * 
     * @return The path chosen by the user.
     * @see JFileChooser
     */
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
