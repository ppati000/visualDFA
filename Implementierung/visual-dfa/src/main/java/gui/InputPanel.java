package gui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

import controller.Controller;
import java.awt.GridBagLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;

import java.awt.GridBagConstraints;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;

/**
 * The {@code InputPanel} Class contains UI-elements to let the user start a new
 * data flow analysis.
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
    private JCheckBox cb_Filter;
    private static final String TEMPORARY_FOLDER = System.getProperty("user.home")
            + System.getProperty("file.separator") + "visualDfa";

    /**
     * Create the {@code JPanel}. Set the {@code Controller}, so the
     * {@code ActionListener}s can access it.
     * 
     * @param ctrl
     *            the {@code Controller} to be accessed in case of events
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
        gridBagLayout.columnWidths = new int[] { 0, 0 };
        gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        gridBagLayout.columnWeights = new double[] { 0.5, 0.5 };
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
        jBuDecorator.decorateIconButton(btnOpen, "icons/open-folder-outline.png", 0.2, new OpenListener(), "Open ...");
        btnOpen.setBackground(Colors.WHITE_BACKGROUND.getColor());
        btnOpen.setForeground(Colors.DARK_TEXT.getColor());
        GridBagConstraints gbc_btnOpen = GridBagConstraintFactory.getStandardGridBagConstraints(0, 4, 1, 1);
        gbc_btnOpen.insets.set(gbc_btnOpen.insets.top, gbc_btnOpen.insets.left, gbc_btnOpen.insets.bottom, 0);
        add(btnOpen, gbc_btnOpen);

        btnSave = new JButton();
        jBuDecorator.decorateIconButton(btnSave, "icons/save-file-option.png", 0.2, new SaveListener(), "Save ...");
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

        cb_Filter = new JCheckBox("Filter standard java methods");
        jCompDecorator.decorate(cb_Filter);
        GridBagConstraints gbc_cbFilter = GridBagConstraintFactory.getStandardGridBagConstraints(0, 11, 2, 1);
        add(cb_Filter, gbc_cbFilter);

        btnStartAnalysis = new JButton();
        jBuDecorator.decorateButton(btnStartAnalysis, new StartAnalysisListener(), "Start Analysis");
        btnStartAnalysis.setBackground(Colors.GREEN_BACKGROUND.getColor());
        btnStartAnalysis.setFont(new Font("Trebuchet MS", Font.BOLD, 24));
        GridBagConstraints gbc_btnStartAnalysis = GridBagConstraintFactory.getStandardGridBagConstraints(0, 12, 2, 2);
        add(btnStartAnalysis, gbc_btnStartAnalysis);
    }

    /**
     * Activate or deactivate the {@code InputPanel}. If deactivated, all
     * {@code JComponents} which are children of this Panel are deactivated.
     * 
     * @param b
     *            whether the panel should be activated {@code true} or
     *            deactivated {@code false}
     */
    public void setActivated(boolean b) {

        btnSave.setEnabled(b);
        btnOpen.setEnabled(b);
        lblAnalyses.setEnabled(b);
        comboBox_Analyses.setEnabled(b);
        lblWorklists.setEnabled(b);
        comboBox_Worklists.setEnabled(b);
        btnStartAnalysis.setEnabled(b);
        cb_Filter.setEnabled(b);
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
     * Get the text from the {@code CodeField} and return it to the caller.
     * 
     * @return the text from the {@code CodeField}.
     *
     */
    public String getCode() {
        return codeField.getCode();
    }

    /**
     * Set the code in the {@code CodeField}.
     * 
     * @param code
     *            the code to be set
     * @see CodeField
     */
    public void setCode(String code) {
        codeField.setCode(code);
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
     * @return {@code true} if a filter is selected, {@code false} if not.
     */
    public boolean isFilterSelected() {

        return cb_Filter.isSelected();
    }

    /**
     * Implementation of an {@code ActionListener} which informs the
     * {@code Controller}, when the StartAnalysis button has been pressed.
     *
     * @author Michael
     * @see ActionListener
     */
    private class StartAnalysisListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            ctrl.startAnalysis(null);
        }

    }

    /**
     * Listener class for the Open button. Opens a {@code JFileChooser} to open
     * .java files.
     * 
     * @author Michael
     * 
     * @see JFileChooser
     */
    private class OpenListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser openChooser = new JFileChooser(TEMPORARY_FOLDER);

            openChooser.setAcceptAllFileFilterUsed(false);
            openChooser.addChoosableFileFilter(new JavaFileFilter());
            openChooser.addChoosableFileFilter(new AllFileFilter());

            int returnVal = openChooser.showOpenDialog(btnOpen);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = openChooser.getSelectedFile();
                System.out.println(file.getAbsolutePath());
                try {
                    BufferedReader in = new BufferedReader(new FileReader(file));
                    String text = new String();
                    String input;
                    while ((input = in.readLine()) != null) {
                        text += input + System.getProperty("line.separator");
                    }
                    codeField.setCode(text);
                    in.close();
                } catch (FileNotFoundException e1) {

                    e1.printStackTrace();
                } catch (IOException e1) {

                    e1.printStackTrace();
                }
            }

        }

    }

    /**
     * Listener class for the Save Button. Opens a {@code JFileChooser} to save
     * .java files.
     * 
     * @author Michael
     *
     * @see JFileChooser
     */
    private class SaveListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            JFileChooser saveChooser = new JFileChooser(TEMPORARY_FOLDER);

            saveChooser.setAcceptAllFileFilterUsed(false);
            saveChooser.addChoosableFileFilter(new JavaFileFilter());

            saveChooser.addChoosableFileFilter(new AllFileFilter());

            int returnVal = saveChooser.showSaveDialog(btnSave);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = saveChooser.getSelectedFile();
                if (!file.getName().endsWith(".java")) {
                    file = new File(file.getAbsolutePath() + ".java");
                }
                try {
                    BufferedWriter out = new BufferedWriter(new FileWriter(file));
                    out.write(codeField.getCode());
                    out.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

    }
    
    private class JavaFileFilter extends FileFilter {
        @Override
        public String getDescription() {

            return ".java";
        }

        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }

            String ext = null;
            String s = f.getName();
            int i = s.lastIndexOf('.');

            if (i > 0 && i < s.length() - 1) {
                ext = s.substring(i + 1).toLowerCase();
                if (ext.equals("java")) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }

        }
    }
    
    private class AllFileFilter extends FileFilter {

        @Override
        public boolean accept(File f) {
            return true;
        }

        @Override
        public String getDescription() {
            return "*.*";
        }
        
    }

}
