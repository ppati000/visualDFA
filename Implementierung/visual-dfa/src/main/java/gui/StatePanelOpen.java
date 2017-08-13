package gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * The {@code StatePanelOpen} Class contains UI-elements to let the user see the
 * state of the data flow analysis.
 * 
 * @author Michael
 *
 */
public class StatePanelOpen extends JPanel {

    private ProgramFrame frame;
    private JButton btnClose;
    private JLabel lblTitle;
    private JLabel lblPosition;
    private JLabel lblInput;
    private JLabel lblOutput;
    private CodeField lineField;
    private JTextArea inputArea;
    private JTextArea outputArea;
    private static final String STATEPANEL_TITLE = "Results";
    private static final String POSITION = "Selected position: ";

    /**
     * Create the panel. Set the {@code ProgramFrame}, so the StatePanel can be
     * switched.
     * 
     * @param frame
     *            the {@code ProgramFrame} which contains the StatePanel
     * @see ProgramFrame
     */
    public StatePanelOpen(ProgramFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());

        JComponentDecorator compDecorator = new JComponentDecorator();
        JButtonDecorator btnDecorator = new JButtonDecorator(compDecorator);
        JLabelDecorator lblDecorator = new JLabelDecorator(compDecorator);

        JPanel titleBar = new JPanel();
        titleBar.setBackground(Colors.BACKGROUND.getColor());
        titleBar.setBorder(new LineBorder(Colors.GREY_BORDER.getColor()));

        btnClose = new JButton();
        btnDecorator.decorateIconButton(btnClose, "icons/close.png", 0.1, new CloseListener(), null);
        titleBar.add(btnClose);

        lblTitle = new JLabel();
        lblDecorator.decorateLabel(lblTitle, STATEPANEL_TITLE);
        titleBar.add(lblTitle);

        add(titleBar, BorderLayout.NORTH);

        JPanel statePanel = new JPanel();
        GridBagLayout gbl_StatePanel = new GridBagLayout();
        gbl_StatePanel.rowWeights = new double[] { 0.1, 0.5, 0.1, 0.5, 0.1, 0.5 };
        statePanel.setLayout(gbl_StatePanel);
        statePanel.setBackground(Colors.BACKGROUND.getColor());
        statePanel.setBorder(
                new CompoundBorder(new LineBorder(Colors.GREY_BORDER.getColor()), new EmptyBorder(5, 5, 5, 5)));

        lblPosition = new JLabel();
        lblDecorator.decorateLabel(lblPosition, POSITION + "none");
        GridBagConstraints gbc_lblPosition = GridBagConstraintFactory.getStandardGridBagConstraints(0, 0, 1, 1);
        statePanel.add(lblPosition, gbc_lblPosition);

        lineField = new CodeField(false);
        GridBagConstraints gbc_lineField = GridBagConstraintFactory.getStandardGridBagConstraints(0, 1, 1, 1);
        statePanel.add(lineField, gbc_lineField);

        lblInput = new JLabel();
        lblDecorator.decorateLabel(lblInput, "Input");
        GridBagConstraints gbc_lblInput = GridBagConstraintFactory.getStandardGridBagConstraints(0, 2, 1, 1);
        statePanel.add(lblInput, gbc_lblInput);

        inputArea = new JTextArea();
        inputArea.setColumns(20);
        inputArea.setEditable(false);
        GridBagConstraints gbc_inputArea = GridBagConstraintFactory.getStandardGridBagConstraints(0, 3, 1, 1);
        JScrollPane inputPane = new JScrollPane(inputArea);
        statePanel.add(inputPane, gbc_inputArea);

        lblOutput = new JLabel();
        lblDecorator.decorateLabel(lblOutput, "Output");
        GridBagConstraints gbc_lblOutput = GridBagConstraintFactory.getStandardGridBagConstraints(0, 4, 1, 1);
        statePanel.add(lblOutput, gbc_lblOutput);

        outputArea = new JTextArea();
        outputArea.setColumns(20);
        outputArea.setEditable(false);
        GridBagConstraints gbc_outputArea = GridBagConstraintFactory.getStandardGridBagConstraints(0, 5, 1, 1);
        JScrollPane outputPane = new JScrollPane(outputArea);
        statePanel.add(outputPane, gbc_outputArea);

        JPanel fillPanel = new JPanel();
        fillPanel.setBackground(Colors.BACKGROUND.getColor());
        GridBagConstraints gbc_fillPanel = GridBagConstraintFactory.getStandardGridBagConstraints(0, 6, 1, 1);
        statePanel.add(fillPanel, gbc_fillPanel);

        add(statePanel, BorderLayout.CENTER);

    }

    /**
     * Activate or deactivate the Panel and its contents.
     * 
     * @param b
     *            activate {@code true} or deactivate {@code false} the panel
     */
    public void setActivated(boolean b) {
        btnClose.setEnabled(b);
        lblTitle.setEnabled(b);
        lblPosition.setEnabled(b);
        lineField.setEnabled(b);
        lblInput.setEnabled(b);
        inputArea.setEnabled(b);
        lblOutput.setEnabled(b);
        outputArea.setEnabled(b);
    }

    /**
     * Set the content of the input {@code TextArea}.
     * 
     * @param latticeElement
     *            the {@code String} to be displayed
     */
    public void setIn(String latticeElement) {

        inputArea.setText(latticeElement);
    }

    /**
     * Set the content of the output {@code TextArea}.
     * 
     * @param latticeElement
     *            the {@code String} to be displayed
     */
    public void setOut(String latticeElement) {
        outputArea.setText(latticeElement);
    }

    /**
     * Set the content of the {@code lineField}.
     * 
     * @param s
     *            the code to be displayed
     * @param blockNumber
     *            the number of the block the code is in
     * @param lineNumber
     *            the number of the first line of the given code
     */
    public void setSelectedLine(String s, int blockNumber, int lineNumber) {
        lblPosition.setText(POSITION + "Block " + blockNumber);
        lineField.setLineAreaStart(lineNumber);
        lineField.setCode(s);

    }

    /**
     * Resets all the content of the {@code StatePanelOpen} so it is empty
     * again.
     */
    public void reset() {
        lblPosition.setText(POSITION + "none");
        lineField.setLineAreaStart(1);
        lineField.setCode(null);
        inputArea.setText(null);
        outputArea.setText(null);

    }

    /**
     * The Listener for the Close-Button. Changes {@code StatePanelOpen} to
     * {@code StatePanelClosed}.
     * 
     * @author Michael
     *
     */
    private class CloseListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            frame.switchStatePanel();

        }

    }
}
