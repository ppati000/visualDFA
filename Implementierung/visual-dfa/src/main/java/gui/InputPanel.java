package gui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import controller.Controller;
import java.awt.GridBagLayout;
import javax.swing.JButton;
import javax.swing.JLabel;

import java.awt.GridBagConstraints;
import javax.swing.JComboBox;

/**
 * The InputPanel Class contains UI-elements to let the user start a new data
 * flow analysis.
 * 
 * @author Michael
 *
 */
public class InputPanel extends JPanel {

    private Controller ctrl;
    private CodeField codeField;

    private JButton btnOpen;
    private JButton btnSave;
    private JLabel lblAnalyses;
    private JComboBox<String> comboBox_Analyses;
    private JLabel lblWorklists;
    private JComboBox<String> comboBox_Worklists;
    private JButton btnStartAnalysis;


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
    public InputPanel(Controller ctrl) {
        this.ctrl = ctrl;

        setBorder(
                new CompoundBorder(new BevelBorder(BevelBorder.RAISED, Colors.GREY_BORDER.getColor(), null, null, null),
                        new EmptyBorder(5, 5, 5, 5)));
        setBackground(Colors.BACKGROUND.getColor());

        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
        gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        gridBagLayout.columnWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] { 0.5, 0.5, 0.5, 0.5, 0.0, 0.5, 0.0, 0.0, 0.5, 0.0, 0.0, 0.5, 0.1,
                0.1 };
        setLayout(gridBagLayout);

        JComponentDecorator jCompDecorator = new JComponentDecorator();
        JButtonDecorator jBuDecorator = new JButtonDecorator(jCompDecorator);
        JLabelDecorator jLaDecorator = new JLabelDecorator(jCompDecorator);

        codeField = new CodeField(true);
        GridBagConstraints gbc_codeField = GridBagConstraintFactory.getStandardGridBagConstraints(0, 0, 2, 4);
        add(codeField, gbc_codeField);

        btnOpen = new JButton();
        jBuDecorator.decorateIconButton(btnOpen, "icons/open-folder-outline.png", 0.2, null, "Open ...");
        btnOpen.setBackground(Colors.WHITE_BACKGROUND.getColor());
        btnOpen.setForeground(Colors.DARK_TEXT.getColor());
        GridBagConstraints gbc_btnOpen = GridBagConstraintFactory.getStandardGridBagConstraints(0, 4, 1, 1);
        gbc_btnOpen.insets.set(gbc_btnOpen.insets.top, gbc_btnOpen.insets.left, gbc_btnOpen.insets.bottom, 0);
        add(btnOpen, gbc_btnOpen);

        btnSave = new JButton();
        jBuDecorator.decorateIconButton(btnSave, "icons/save-file-option.png", 0.2, null, "Save ...");
        btnSave.setBackground(Colors.WHITE_BACKGROUND.getColor());
        btnSave.setForeground(Colors.DARK_TEXT.getColor());
        GridBagConstraints gbc_btnSave = GridBagConstraintFactory.getStandardGridBagConstraints(1, 4, 1, 1);
        gbc_btnSave.insets.set(gbc_btnSave.insets.top, 0, gbc_btnSave.insets.bottom, gbc_btnSave.insets.right);
        add(btnSave, gbc_btnSave);

        lblAnalyses = new JLabel();
        jLaDecorator.decorateLabel(lblAnalyses, "Analysis");
        GridBagConstraints gbc_lblAnalyses = GridBagConstraintFactory.getStandardGridBagConstraints(0, 6, 2, 1);
        add(lblAnalyses, gbc_lblAnalyses);

        comboBox_Analyses = new JComboBox<String>(ctrl.getAnalyses().toArray(new String[0]));
        GridBagConstraints gbc_comboBox_Analyses = GridBagConstraintFactory.getStandardGridBagConstraints(0, 7, 2, 1);
        gbc_comboBox_Analyses.fill = GridBagConstraints.HORIZONTAL;
        add(comboBox_Analyses, gbc_comboBox_Analyses);

        lblWorklists = new JLabel();
        jLaDecorator.decorateLabel(lblWorklists, "Worklist Algorithm");
        GridBagConstraints gbc_lblWorklists = GridBagConstraintFactory.getStandardGridBagConstraints(0, 9, 2, 1);
        add(lblWorklists, gbc_lblWorklists);


        comboBox_Worklists = new JComboBox<String>(ctrl.getWorklists().toArray(new String[0]));
        GridBagConstraints gbc_comboBox_Worklist = GridBagConstraintFactory.getStandardGridBagConstraints(0, 10, 2, 1);
        gbc_comboBox_Worklist.fill = GridBagConstraints.HORIZONTAL;
        add(comboBox_Worklists, gbc_comboBox_Worklist);

        btnStartAnalysis = new JButton();
        jBuDecorator.decorateButton(btnStartAnalysis, new StartAnalysisListener(), "Start Analysis");

        jBuDecorator.decorateButton(btnStartAnalysis, new StartAnalysisListener(), "Start Analysis");
        btnStartAnalysis.setBackground(Colors.GREEN_BACKGROUND.getColor());

        btnStartAnalysis.setFont(new Font("Trebuchet MS", Font.BOLD, 24));
        GridBagConstraints gbc_btnStartAnalysis = GridBagConstraintFactory.getStandardGridBagConstraints(0, 12, 2, 2);
        add(btnStartAnalysis, gbc_btnStartAnalysis);
    }

    /**
     * Activate or deactivate the InputPanel. If deactivated, all JComponents
     * which are children of this Panel are deactivated.
     * 
     * @param b
     *            Whether the panel should be activated [true] or deactivated
     *            [false].
     */
    public void setActivated(boolean b) {
        
        btnSave.setEnabled(b);
        btnOpen.setEnabled(b);
        lblAnalyses.setEnabled(b);
        comboBox_Analyses.setEnabled(b);
        lblWorklists.setEnabled(b);
        comboBox_Worklists.setEnabled(b);
        btnStartAnalysis.setEnabled(b);

        codeField.setEnabled(b);

    }

    /**
     * Look up the currently selected data flow analysis and return its name.
     * 
     * @return The name of the data flow analysis.
     */
    public String getAnalysis() {
        String analysisName = (String) comboBox_Analyses.getSelectedItem();
        return analysisName;
    }

    /**
     * Get the text from the CodeField and return it to the caller.
     * 
     * @return the text from the CodeField.
     *
     */
    public String getCode() {
        return codeField.getCode();
    }

    /**
     * Look up the currently selected worklist-algorithm and return its name.
     * 
     * @return The name of the worklist-algorithm.
     */
    public String getWorklist() {
        String worklistName = (String) comboBox_Worklists.getSelectedItem();
        return worklistName;
    }

    /**
     * Look up if a filter is selected and return this value.
     * 
     * @return [true] if a filter is selected, [false] if not.
     */
    public boolean isFilterSelected() {
        // TODO
        return false;
    }

    /**
     * Implementation of an ActionListener which informs the controller,
     * when the StartAnalysis button has been pressed.
     *
     * @author Michael
     * @see ActionListener
     */
    private class StartAnalysisListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            ctrl.startAnalysis();
        }

    }
}

