package gui;

import java.awt.*;
import java.io.File;
import java.lang.reflect.Method;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

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
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        setTitle("Data-Flow Analysis");
        
        setBounds(STANDARD_BOUNDS);
        setExtendedState(getExtendedState() | Frame.MAXIMIZED_BOTH);
        setMinimumSize(MIN_SIZE);
        contentPane = new JPanel();

        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        inputPanel = new InputPanel(ctrl);

        JPanel centerPan = new JPanel();
        contentPane.add(centerPan, BorderLayout.CENTER);
        centerPan.setLayout(new BorderLayout(0, 0));
        
        JSplitPane inputGraphSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,  inputPanel, centerPan);
        inputGraphSplit.setDividerSize(5);
        inputGraphSplit.setDividerLocation(375);
        contentPane.add(inputGraphSplit, BorderLayout.CENTER);
        
        visualGraphPanel = ctrl.getVisualGraphPanel();
        centerPan.add(visualGraphPanel, BorderLayout.CENTER);

        controlPanel = new ControlPanel(ctrl);
        centerPan.add(controlPanel, BorderLayout.SOUTH);

        statePanelOpen = new StatePanelOpen(this);
        contentPane.add(statePanelOpen, BorderLayout.EAST);

        isStatePanelOpen = true;

        statePanelClosed = new StatePanelClosed(this);

        Image icon = IconLoader.loadIcon("./icons/app-icon.png", 1).getImage();
        setIconImage(icon);

        if (System.getProperty("os.name").toLowerCase().contains("mac") && getWindows().length != 0) {
            enableMacOSFullscreenAndSetDockIcon(getWindows()[0], icon);
        }
    }

    /**
     * This method will switch between the {@code StatePanelOpen} and the
     * {@code StatePanelClosed}. Only one of them will be shown at a time.
     * 
     * @see StatePanelOpen
     * @see StatePanelClosed
     */
    public void switchStatePanel() {
        if (isStatePanelOpen) {
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

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void enableMacOSFullscreenAndSetDockIcon(Window window, Image icon) {
        try {
            Class util = Class.forName("com.apple.eawt.FullScreenUtilities");
            Class params[] = new Class[]{Window.class, Boolean.TYPE};
            Method method = util.getMethod("setWindowCanFullScreen", params);
            method.invoke(util, window, true);

            Class application = Class.forName("com.apple.eawt.Application");
            Object applicationObject = application.newInstance().getClass().getMethod("getApplication")
                    .invoke(null);
            applicationObject.getClass().getMethod("setDockIconImage", java.awt.Image.class)
                    .invoke(applicationObject, icon);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("Failed to set macOS specific visual attributes. App icon or Fullscreen Mode may be unavailable.\n"
              + ex.getMessage());
        }
    }
}
