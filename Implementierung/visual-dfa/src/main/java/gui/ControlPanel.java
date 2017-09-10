package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Hashtable;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import controller.Controller;
import java.awt.GridBagLayout;

import javax.swing.JSlider;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import java.awt.GridBagConstraints;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;

import javax.swing.border.EmptyBorder;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;

/**
 * The {@code ControlPanel} Class contains UI-elements to let the user control
 * the control flow graph.
 * 
 * @author Michael
 *
 */
public class ControlPanel extends JPanel {

    private static final long serialVersionUID = 1L;

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

    private boolean isPlayButtonAdded;

    /**
     * Create the panel. Set the {@code Controller}, so the
     * {@code ActionListener}s can access it.
     * 
     * @param ctrl
     *            the {@code Controller} to be accessed in case of events
     * 
     * @see controller.Controller
     * @see ActionListener
     */
    public ControlPanel(Controller ctrl) {

        setBorder(
                new CompoundBorder(new BevelBorder(BevelBorder.RAISED, Colors.GREY_BORDER.getColor(), null, null, null),
                        new EmptyBorder(5, 5, 5, 5)));
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

        btnStopAnalysis = new JButton();
        jBuDecorator.decorateIconButton(btnStopAnalysis, "icons/rounded-white-square-shape.png", 0.3,
                new StopListener(), null);
        btnStopAnalysis.setHorizontalTextPosition(SwingConstants.CENTER);
        btnStopAnalysis.setVerticalTextPosition(SwingConstants.BOTTOM);
        btnStopAnalysis.setToolTipText("Stop Analysis");
        GridBagConstraints gbc_btnStopAnalysis = GridBagConstraintFactory.getStandardGridBagConstraints(0, 1, 1, 3);
        add(btnStopAnalysis, gbc_btnStopAnalysis);

        btnPreviousBlock = new JButton();
        jBuDecorator.decorateIconButton(btnPreviousBlock, "icons/rewind-button.png", 0.3, new PreviousBlockListener(),
                null);
        btnPreviousBlock.setToolTipText("Previous Block");
        GridBagConstraints gbc_btnPreviousBlock = GridBagConstraintFactory.getStandardGridBagConstraints(2, 1, 1, 3);
        add(btnPreviousBlock, gbc_btnPreviousBlock);

        btnPreviousLine = new JButton();
        jBuDecorator.decorateIconButton(btnPreviousLine, "icons/step-backward.png", 0.3, new PreviousLineListener(),
                null);
        btnPreviousLine.setToolTipText("Previous Line");
        GridBagConstraints gbc_btnPreviousLine = GridBagConstraintFactory.getStandardGridBagConstraints(3, 1, 1, 3);
        add(btnPreviousLine, gbc_btnPreviousLine);

        btnPlay = new JButton();
        jBuDecorator.decorateIconButton(btnPlay, "icons/play-button.png", 0.35, new PlayListener(), null);
        btnPlay.setToolTipText("Play");
        
        btnPause = new JButton();
        jBuDecorator.decorateIconButton(btnPause, "icons/pause-symbol.png", 0.35, new PauseListener(), null);
        btnPause.setToolTipText("Pause");
        
        gbc_btnPlayPause = GridBagConstraintFactory.getStandardGridBagConstraints(4, 1, 1, 3);
        add(btnPlay, gbc_btnPlayPause);
        isPlayButtonAdded = true;

        btnNextLine = new JButton();
        jBuDecorator.decorateIconButton(btnNextLine, "icons/step-forward.png", 0.3, new NextLineListener(), null);
        btnNextLine.setToolTipText("Next Line");
        
        GridBagConstraints gbc_btnNextLine = GridBagConstraintFactory.getStandardGridBagConstraints(5, 1, 1, 3);
        add(btnNextLine, gbc_btnNextLine);

        btnNextBlock = new JButton();
        jBuDecorator.decorateIconButton(btnNextBlock, "icons/fast-forward-arrows.png", 0.3, new NextBlockListener(),
                null);
        btnNextBlock.setToolTipText("Next Block");
        
        GridBagConstraints gbc_btnNextBlock = GridBagConstraintFactory.getStandardGridBagConstraints(6, 1, 1, 3);
        add(btnNextBlock, gbc_btnNextBlock);

        delaySlider = new JSlider();
        jSliDecorator.decorateSlider(delaySlider, 19, 1, 1, null);

        // Put Labels to the DelaySlider
        Hashtable<Integer, JLabel> delaySliderLabelTable = new Hashtable<>();
        JLabel tickLabel_1 = new JLabel();
        jLaDecorator.decorateLabel(tickLabel_1, "" + delaySlider.getMinimum());
        JLabel tickLabel_2 = new JLabel();
        jLaDecorator.decorateLabel(tickLabel_2, "0.5");
        JLabel tickLabel_3 = new JLabel();
        jLaDecorator.decorateLabel(tickLabel_3, "1");
        JLabel tickLabel_4 = new JLabel();
        jLaDecorator.decorateLabel(tickLabel_4, "10");

        delaySliderLabelTable.put(new Integer(delaySlider.getMinimum()), tickLabel_1);
        delaySliderLabelTable.put(new Integer(5), tickLabel_2);
        delaySliderLabelTable.put(new Integer(10), tickLabel_3);
        delaySliderLabelTable.put(new Integer(19), tickLabel_4);

        delaySlider.setLabelTable(delaySliderLabelTable);
        delaySlider.setPaintLabels(true);
        delaySlider.setValue(10);
        GridBagConstraints gbc_delaySlider = GridBagConstraintFactory.getStandardGridBagConstraints(7, 2, 2, 2);
        add(delaySlider, gbc_delaySlider);

        setShortcuts();
        
    }

    private void setShortcuts() {
        Action keyAction = new AbstractAction() {

            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {

                JButton source = (JButton) e.getSource();
                source.doClick();

            }
        };

        btnPreviousBlock.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.SHIFT_DOWN_MASK, true), "PrevBlock");
        btnPreviousBlock.getActionMap().put("PrevBlock", keyAction);

        btnPreviousLine.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, true), "PreviousLine");
        btnPreviousLine.getActionMap().put("PreviousLine", keyAction);

        btnPlay.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false),
                "PLAY");
        btnPlay.getActionMap().put("PLAY", keyAction);

        btnPause.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false),
                "PAUSE");
        btnPause.getActionMap().put("PAUSE", keyAction);

        btnNextLine.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, true), "NextLine");
        btnNextLine.getActionMap().put("NextLine", keyAction);
        
        btnNextBlock.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.SHIFT_DOWN_MASK, true), "NextBlock");
        btnNextBlock.getActionMap().put("NextBlock", keyAction);
        
        btnStopAnalysis.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, true), "StopAnalysis");
        btnStopAnalysis.getActionMap().put("StopAnalysis", keyAction);
        
    }

    /**
     * Set the step on the step-slider, which should be coherent with the state
     * of the control flow graph.
     * 
     * @param step
     *            the step to be set
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
     *            the total number of steps
     */
    public void setTotalSteps(int steps) {
        stepSlider.removeChangeListener(stepListener);
        stepSlider.setValue(0);
        stepSlider.setMaximum(steps - 1);
        stepSlider.addChangeListener(stepListener);
    }

    /**
     * Set the activity state of the {@code ControlPanel}. {@code ACTIVATED}:
     * All UI-elements are activated. {@code PLAYING}: Only the
     * {@code DelaySlider} and the {@code PauseButton} are activated.
     * {@code DEACTIVATED}: All UI-elements are deactivated.
     * 
     * @param cps
     *            an ENUM which defines the activity state of the
     *            {@code ControlPanel}
     * 
     * @see ControlPanelState
     */
    public void setActivated(ControlPanelState cps) {
        switch (cps) {
        case ACTIVATED:
            btnNextBlock.setEnabled(true);
            btnNextLine.setEnabled(true);
            btnPause.setEnabled(false);
            btnPlay.setEnabled(true);
            btnPreviousBlock.setEnabled(true);
            btnPreviousLine.setEnabled(true);
            btnStopAnalysis.setEnabled(true);
            stepSlider.setEnabled(true);
            delaySlider.setEnabled(true);
            lblDelayInSeconds.setEnabled(true);
            if (isPlayButtonAdded == false) {
                remove(btnPause);
                add(btnPlay, gbc_btnPlayPause);
                revalidate();
                repaint();
                isPlayButtonAdded = true;
            }
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
            btnPlay.setEnabled(false);
            btnPreviousBlock.setEnabled(false);
            btnPreviousLine.setEnabled(false);
            btnStopAnalysis.setEnabled(true);
            stepSlider.setEnabled(false);
            delaySlider.setEnabled(true);
            lblDelayInSeconds.setEnabled(true);
            if (isPlayButtonAdded == true) {
                remove(btnPlay);
                add(btnPause, gbc_btnPlayPause);
                revalidate();
                repaint();
                isPlayButtonAdded = false;
            }
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
     * Look up the current position of the {@code DelaySlider} and return it.
     * 
     * 
     * @return The position of the {@code DelaySlider}.
     */
    public double getDelay() {
        int value = delaySlider.getValue();
        if (value <= 10) {
            // returns values 0, 0.1 ... 0.9, 1
            double delay = (double) value / 10.0;
            return delay;
        } else {
            // returns values 2, 3, ... 10 since the value of the slider is
            // shifted to the right because of the delay-values between 0 and 1
            return value - 9;
        }
    }

    /**
     * Look up the current position of the {@code StepSlider} and return it.
     * 
     * @return The position of the {@code StepSlider}.
     */
    public int getSliderStep() {
        return stepSlider.getValue();
    }

    /**
     * Implementation of an {@code ActionListener} which informs the
     * {@code Controller}, when the Stop button has been clicked.
     * 
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
     * Implementation of an {@code ActionListener} which informs the
     * {@code Controller}, when the PreviousBlock button has been clicked.
     * 
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
     * Implementation of an {@code ActionListener} which informs the
     * {@code Controller}, when the PreviousLine button has been clicked.
     * 
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
     * Implementation of an {@code ActionListener} which informs the
     * {@code Controller}, when the NextLine button has been clicked.
     * 
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
     * Implementation of an {@code ActionListener} which informs the
     * {@code Controller}, when the NextBlock button has been clicked.
     * 
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
     * Implementation of a {@code ChangeListener} which informs the
     * {@code Controller}, when the position of the StepSlider has changed.
     * 
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
     * Implementation of an {@code ActionListener} which informs the
     * {@code Controller}, when the Play button has been clicked. Furthermore
     * exchanges the Play button with the Pause button
     * 
     * @author Michael
     *
     * @see ActionListener
     */
    private class PlayListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            ctrl.play();
        }

    }

    /**
     * Implementation of an {@code ActionListener} which informs the
     * {@code Controller}, when the Pause button has been clicked. Furthermore
     * exchanges the Pause button for the Play button.
     * 
     * @author Michael
     *
     * @see ActionListener
     */
    private class PauseListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            ctrl.pause();

        }

    }

}
