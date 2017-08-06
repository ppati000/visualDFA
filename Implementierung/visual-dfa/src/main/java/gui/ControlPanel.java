package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import controller.Controller;
import java.awt.GridBagLayout;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

import java.awt.GridBagConstraints;
import javax.swing.JButton;
import javax.swing.JLabel;

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

    private JButton btnStopAnalysis;
    private JButton btnPreviousBlock;
    private JButton btnPreviousLine;
    private JButton btnPlay;
    private JButton btnPause;
    private JButton btnNextLine;
    private JButton btnNextBlock;

    private JSlider stepSlider;
    private JSlider delaySlider;

    private GridBagConstraints gbc_btnPlayPause;

    private JLabel lblDelayInSeconds;

    private StepSliderChangeListener stepListener;

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

        setBorder(new CompoundBorder(new BevelBorder(BevelBorder.RAISED, Colors.GREY_BORDER.getColor(), null, null, null), new EmptyBorder(5, 5, 5, 5)));
        setBackground(Colors.BACKGROUND.getColor());

        this.ctrl = ctrl;

        JComponentDecorator jCompDecorator = new JComponentDecorator();
        JButtonDecorator jBuDecorator = new JButtonDecorator(jCompDecorator);
        JSliderDecorator jSliDecorator = new JSliderDecorator(jCompDecorator);
        JLabelDecorator jLaDecorator = new JLabelDecorator(jCompDecorator);

        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0 };
        gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0 };
        gridBagLayout.columnWeights = new double[] { 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5 };
        gridBagLayout.rowWeights = new double[] { 0.5, 0.5, 0.5, 0.5 };
        setLayout(gridBagLayout);

        stepListener = new StepSliderChangeListener();
        stepSlider = new JSlider();
        jSliDecorator.decorateSlider(stepSlider, 1, 1, 1, stepListener);
        GridBagConstraints gbc_stepSlider = GridBagConstraintFactory.getStandardGridBagConstraints(0, 0, 9, 1);
        add(stepSlider, gbc_stepSlider);

        lblDelayInSeconds = new JLabel();
        jLaDecorator.decorateLabel(lblDelayInSeconds, "Step delay (seconds)");
        GridBagConstraints gbc_lblDelayInSeconds = GridBagConstraintFactory.getStandardGridBagConstraints(7, 1, 2, 1);
        add(lblDelayInSeconds, gbc_lblDelayInSeconds);
        
        JButton btnStopAnalysis = new JButton("Stop");
        jBuDecorator.decorateIconButton(btnStopAnalysis, "icons/rounded-black-square-shape.png", 0.5, null, "Stop");
        btnStopAnalysis.setBackground(Colors.WHITE_BACKGROUND.getColor());
        btnStopAnalysis.setForeground(Colors.DARK_TEXT.getColor());

        btnStopAnalysis.setHorizontalTextPosition(SwingConstants.CENTER);
        btnStopAnalysis.setVerticalTextPosition(SwingConstants.BOTTOM);
        GridBagConstraints gbc_btnStopAnalysis = GridBagConstraintFactory.getStandardGridBagConstraints(0, 1, 1, 3);
        add(btnStopAnalysis, gbc_btnStopAnalysis);

        btnPreviousBlock = new JButton();
        jBuDecorator.decorateIconButton(btnPreviousBlock, "icons/rewind-button.png", 0.5, new PreviousBlockListener(),
                null);

        GridBagConstraints gbc_btnPreviousBlock = GridBagConstraintFactory.getStandardGridBagConstraints(2, 1, 1, 3);
        add(btnPreviousBlock, gbc_btnPreviousBlock);

        btnPreviousLine = new JButton();
        jBuDecorator.decorateIconButton(btnPreviousLine, "icons/step-backward.png", 0.5, new PreviousLineListener(),
                null);
        GridBagConstraints gbc_btnPreviousLine = GridBagConstraintFactory.getStandardGridBagConstraints(3, 1, 1, 3);
        add(btnPreviousLine, gbc_btnPreviousLine);

        btnPlay = new JButton();
        jBuDecorator.decorateIconButton(btnPlay, "icons/play-button.png", 0.6, new PlayListener(), null);
        btnPause = new JButton();
        jBuDecorator.decorateIconButton(btnPause, "icons/pause-symbol.png", 0.6, new PauseListener(), null);
        gbc_btnPlayPause = GridBagConstraintFactory.getStandardGridBagConstraints(4, 1, 1, 3);
        add(btnPlay, gbc_btnPlayPause);

        btnNextLine = new JButton();
        jBuDecorator.decorateIconButton(btnNextLine, "icons/step-forward.png", 0.5, new NextLineListener(), null);
        GridBagConstraints gbc_btnNextLine = GridBagConstraintFactory.getStandardGridBagConstraints(5, 1, 1, 3);
        add(btnNextLine, gbc_btnNextLine);

        btnNextBlock = new JButton();
        jBuDecorator.decorateIconButton(btnNextBlock, "icons/fast-forward-arrows.png", 0.5, new NextBlockListener(),
                null);
        GridBagConstraints gbc_btnNextBlock = GridBagConstraintFactory.getStandardGridBagConstraints(6, 1, 1, 3);
        add(btnNextBlock, gbc_btnNextBlock);

        delaySlider = new JSlider();
        jSliDecorator.decorateSlider(delaySlider, 10, 1, 1, null);
        Hashtable<Integer, JLabel> delaySliderLabelTable = new Hashtable<>();
        JLabel tickLabel_1 = new JLabel();
        jLaDecorator.decorateLabel(tickLabel_1, "" + delaySlider.getMaximum());
        JLabel tickLabel_2 = new JLabel();
        jLaDecorator.decorateLabel(tickLabel_2, "" + delaySlider.getMaximum() / 10);
        JLabel tickLabel_3 = new JLabel();
        jLaDecorator.decorateLabel(tickLabel_3, "" + delaySlider.getMinimum());
        delaySliderLabelTable.put(new Integer(delaySlider.getMaximum()), tickLabel_1);
        delaySliderLabelTable.put(new Integer(delaySlider.getMaximum() / 10), tickLabel_2);
        delaySliderLabelTable.put(new Integer(delaySlider.getMinimum()), tickLabel_3);
        delaySlider.setLabelTable(delaySliderLabelTable);
        delaySlider.setPaintLabels(true);
        GridBagConstraints gbc_delaySlider = GridBagConstraintFactory.getStandardGridBagConstraints(7, 2, 2, 2);
        add(delaySlider, gbc_delaySlider);
    }

    /**
     * Set the step on the step-slider, which should be coherent with the state
     * of the control flow graph.
     * 
     * @param step
     *            The step to be set.
     */
    public void setSliderStep(int step) {
        stepSlider.removeChangeListener(stepListener);
        stepSlider.setValue(step);
        stepSlider.addChangeListener(stepListener);
    }

    /**
     * Set the total number of steps on the step-slider. Should be coherent to
     * the total number of steps in the control flow graph.
     * 
     * @param steps
     *            The total number of steps.
     */
    public void setTotalSteps(int steps) {
        stepSlider.setMaximum(steps);
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
        switch (cps) {
        case ACTIVATED:
            btnNextBlock.setEnabled(true);
            btnNextLine.setEnabled(true);
            btnPause.setEnabled(true);
            btnPlay.setEnabled(true);
            btnPreviousBlock.setEnabled(true);
            btnPreviousLine.setEnabled(true);
            btnStopAnalysis.setEnabled(true);
            stepSlider.setEnabled(true);
            delaySlider.setEnabled(true);
            lblDelayInSeconds.setEnabled(true);
            break;
        case PRECALCULATING:
            btnNextBlock.setEnabled(false);
            btnNextLine.setEnabled(false);
            btnPause.setEnabled(false);
            btnPlay.setEnabled(false);
            btnPreviousBlock.setEnabled(false);
            btnPreviousLine.setEnabled(false);
            btnStopAnalysis.setEnabled(true);
            stepSlider.setEnabled(false);
            delaySlider.setEnabled(false);
            lblDelayInSeconds.setEnabled(false);
            break;
        case PLAYING:
            btnNextBlock.setEnabled(false);
            btnNextLine.setEnabled(false);
            btnPause.setEnabled(true);
            btnPlay.setEnabled(true);
            btnPreviousBlock.setEnabled(false);
            btnPreviousLine.setEnabled(false);
            btnStopAnalysis.setEnabled(true);
            stepSlider.setEnabled(false);
            delaySlider.setEnabled(true);
            lblDelayInSeconds.setEnabled(true);
            break;
        case DEACTIVATED:
            btnNextBlock.setEnabled(false);
            btnNextLine.setEnabled(false);
            btnPause.setEnabled(false);
            btnPlay.setEnabled(false);
            btnPreviousBlock.setEnabled(false);
            btnPreviousLine.setEnabled(false);
            btnStopAnalysis.setEnabled(false);
            stepSlider.setEnabled(false);
            delaySlider.setEnabled(false);
            lblDelayInSeconds.setEnabled(false);
            break;
        default:
            btnNextBlock.setEnabled(false);
            btnNextLine.setEnabled(false);
            btnPause.setEnabled(false);
            btnPlay.setEnabled(false);
            btnPreviousBlock.setEnabled(false);
            btnPreviousLine.setEnabled(false);
            btnStopAnalysis.setEnabled(false);
            stepSlider.setEnabled(false);
            delaySlider.setEnabled(false);
            lblDelayInSeconds.setEnabled(false);
            break;
        }
    }

    /**
     * Look up the current position of the DelaySlider and return it.
     * 
     * @return The position of the DelaySlider.
     */
    public int getDelaySliderPosition() {
        return delaySlider.getValue();
    }

    /**
     * Look up the current position of the StepSlider and return it.
     * 
     * @return The position of the StepSlider.
     */
    public int getSliderStep() {
        return stepSlider.getValue();
    }
    
    /**
     * Implementation of an ActionListener which informs the controller,
     * when the Stop button has been clicked.
     * @author Michael
     *
     * @see ActionListener 
     */
    private class StopListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            ctrl.stopAnalysis();

        }

    }

    /**
     * Implementation of an ActionListener which informs the controller,
     * when the PreviousBlock button has been clicked.
     * @author Michael
     *
     * @see ActionListener 
     */
    private class PreviousBlockListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            ctrl.previousBlock();

        }

    }

    /**
     * Implementation of an ActionListener which informs the controller,
     * when the PreviousLine button has been clicked.
     * @author Michael
     *
     * @see ActionListener 
     */
    private class PreviousLineListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            ctrl.previousLine();

        }

    }

    /**
     * Implementation of an ActionListener which informs the controller,
     * when the NextLine button has been clicked.
     * @author Michael
     *
     * @see ActionListener 
     */
    private class NextLineListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            ctrl.nextLine();

        }

    }

    /**
     * Implementation of an ActionListener which informs the controller,
     * when the NextBlock button has been clicked.
     * @author Michael
     *
     * @see ActionListener 
     */
    private class NextBlockListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            ctrl.nextBlock();
        }

    }

    /**
     * Implementation of a ChangeListener which informs the controller,
     * when the position of the StepSlider has changed.
     * @author Michael
     *
     * @see ChangeListener
     */
    private class StepSliderChangeListener implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            ctrl.jumpToStep(getSliderStep());
        }

    }

    /**
     * Implementation of an ActionListener which informs the controller,
     * when the Play button has been clicked. Furthermore exchanges the Play button
     * with the Pause button
     * @author Michael
     *
     * @see ActionListener 
     */
    private class PlayListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            remove(btnPlay);
            add(btnPause, gbc_btnPlayPause);
            revalidate();
            repaint();
            ctrl.play();
        }

    }

    /**
     * Implementation of an ActionListener which informs the controller,
     * when the Pause button has been clicked. Furthermore exchanges the Pause button
     * for the Play button.
     * @author Michael
     *
     * @see ActionListener 
     */
    private class PauseListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            remove(btnPause);
            add(btnPlay, gbc_btnPlayPause);
            revalidate();
            repaint();
            ctrl.pause();

        }

    }

}
