package gui;

import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.JPanel;

import controller.Controller;
import java.awt.GridBagLayout;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

import java.awt.GridBagConstraints;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.Color;

import javax.swing.border.EmptyBorder;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;

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
        //TODO Constants for colors?
        setBorder(new CompoundBorder(new BevelBorder(BevelBorder.RAISED, new Color(153, 204, 204), null, null, null), new EmptyBorder(5, 5, 5, 5)));
        setBackground(new Color(0, 0, 102));
        this.ctrl = ctrl;
        
        JComponentDecorator jCompDecorator = new JComponentDecorator();
        JButtonDecorator jBuDecorator = new JButtonDecorator(jCompDecorator);
        JSliderDecorator jSliDecorator = new JSliderDecorator(jCompDecorator);
        JLabelDecorator jLaDecorator = new JLabelDecorator(jCompDecorator);
        
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0};
        gridBagLayout.rowHeights = new int[]{0, 0, 0, 0};
        gridBagLayout.columnWeights = new double[]{0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5};
        gridBagLayout.rowWeights = new double[]{0.5, 0.5, 0.5, 0.5};
        setLayout(gridBagLayout);
        
        JSlider slider = new JSlider();
        jSliDecorator.decorateSlider(slider, 1, 1, 1, null);
        GridBagConstraints gbc_slider = GridBagConstraintFactory.getStandardGridBagConstraints(0, 0, 9, 1);
        add(slider, gbc_slider);
        
        JLabel lblDelayInSeconds = new JLabel();
        jLaDecorator.decorateLabel(lblDelayInSeconds, "Step delay (seconds)");
        GridBagConstraints gbc_lblDelayInSeconds = GridBagConstraintFactory.getStandardGridBagConstraints(7, 1, 2, 1);
        add(lblDelayInSeconds, gbc_lblDelayInSeconds);
        
        JButton btnStopAnalysis = new JButton("Stop");
        jBuDecorator.decorateIconButton(btnStopAnalysis, "icons/rounded-black-square-shape.png", 0.5, null, "Stop");
        btnStopAnalysis.setBackground(new Color(255,255,255));
        btnStopAnalysis.setForeground(new Color(0, 0, 0));
        btnStopAnalysis.setHorizontalTextPosition(SwingConstants.CENTER);
        btnStopAnalysis.setVerticalTextPosition(SwingConstants.BOTTOM);
        //TODO text below icon
        GridBagConstraints gbc_btnStopAnalysis = GridBagConstraintFactory.getStandardGridBagConstraints(0, 1, 1, 3);
        add(btnStopAnalysis, gbc_btnStopAnalysis);
        
        JButton btnPreviousBlock = new JButton();
        jBuDecorator.decorateIconButton(btnPreviousBlock, "icons/rewind-button.png", 0.5, null, null);
        GridBagConstraints gbc_btnPreviousBlock = GridBagConstraintFactory.getStandardGridBagConstraints(2, 1, 1, 3);
        add(btnPreviousBlock, gbc_btnPreviousBlock);
        
        JButton btnPreviousLine = new JButton();
        jBuDecorator.decorateIconButton(btnPreviousLine, "icons/step-backward.png", 0.5, null, null);        
        GridBagConstraints gbc_btnPreviousLine = GridBagConstraintFactory.getStandardGridBagConstraints(3, 1, 1, 3);
        add(btnPreviousLine, gbc_btnPreviousLine);
        
        JButton btnPlaypause = new JButton();
        jBuDecorator.decorateIconButton(btnPlaypause, "icons/play-button.png", 0.6, null, null);
        GridBagConstraints gbc_btnPlaypause = GridBagConstraintFactory.getStandardGridBagConstraints(4, 1, 1, 3);
        add(btnPlaypause, gbc_btnPlaypause);
        
        JButton btnNextLine = new JButton();
        jBuDecorator.decorateIconButton(btnNextLine, "icons/step-forward.png", 0.5, null, null);
        GridBagConstraints gbc_btnNextLine = GridBagConstraintFactory.getStandardGridBagConstraints(5, 1, 1, 3);
        add(btnNextLine, gbc_btnNextLine);
        
        JButton btnNextBlock = new JButton();
        jBuDecorator.decorateIconButton(btnNextBlock, "icons/fast-forward-arrows.png", 0.5, null, null);
        GridBagConstraints gbc_btnNextBlock = GridBagConstraintFactory.getStandardGridBagConstraints(6, 1, 1, 3);
        add(btnNextBlock, gbc_btnNextBlock);
        
        JSlider slider_1 = new JSlider();
        jSliDecorator.decorateSlider(slider_1, 10, 1, 1, null);
        Hashtable<Integer, JLabel> delaySliderLabelTable = new Hashtable<>();
        JLabel tickLabel_1 = new JLabel();
        jLaDecorator.decorateLabel(tickLabel_1, "" + slider_1.getMaximum());
        JLabel tickLabel_2 = new JLabel();
        jLaDecorator.decorateLabel(tickLabel_2, "" + slider_1.getMaximum() / 10);
        JLabel tickLabel_3 = new JLabel();
        jLaDecorator.decorateLabel(tickLabel_3, "" + slider_1.getMinimum());
        delaySliderLabelTable.put(new Integer(slider_1.getMaximum()), tickLabel_1);
        delaySliderLabelTable.put(new Integer(slider_1.getMaximum() / 10), tickLabel_2);
        delaySliderLabelTable.put(new Integer(slider_1.getMinimum()), tickLabel_3);
        slider_1.setLabelTable(delaySliderLabelTable);
        slider_1.setPaintLabels(true);
        GridBagConstraints gbc_slider_1 = GridBagConstraintFactory.getStandardGridBagConstraints(7, 2, 2, 2);
        add(slider_1, gbc_slider_1);
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
