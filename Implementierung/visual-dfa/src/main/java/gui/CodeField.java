package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * A class to display code in a {@code JScrollPane} with line numbers to the
 * left.
 * 
 * @author Michael
 * 
 * @see JScrollPane
 */
public class CodeField extends JScrollPane {

    private static final long serialVersionUID = 1L;
    
    private JPanel contentPanel;
    private JTextArea lineArea;
    private JTextArea codeArea;
    private int lineAreaStart;

    /**
     * Constructor for the {@code CodeField}. Can be set editable, so the user
     * can write Code in the {@code JTextArea}-component or non-editable, so
     * code can be displayed to the user.
     * 
     * @param editable
     *            determine if {@code CodeArea} should be editable
     */
    public CodeField(boolean editable) {
        super();

        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());

        lineArea = new JTextArea("1");
        lineAreaStart = 1;
        lineArea.setBorder(new EmptyBorder(0, 2, 0, 5));
        lineArea.setEditable(false);
        lineArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, lineArea.getFont().getSize()));
        lineArea.setBackground(Colors.LIGHT_BACKGROUND.getColor());

        contentPanel.add(lineArea, BorderLayout.WEST);

        codeArea = new JTextArea();
        codeArea.setEditable(editable);
        codeArea.setTabSize(2);
        codeArea.setBorder(new EmptyBorder(0, 5, 0, 0));
        codeArea.getDocument().addDocumentListener(new TextChangeListener());
        codeArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, codeArea.getFont().getSize()));

        contentPanel.add(codeArea, BorderLayout.CENTER);

        setViewportView(contentPanel);

        
    }

    /**
     * Determine at which number the {@code lineArea} should start counting.
     * 
     * @param start
     *            the first number in the {@code lineArea}
     */
    public void setLineAreaStart(int start) {
        lineAreaStart = start;
    }

    /**
     * Update the line numbers in the {@code lineArea} every time the
     * {@code codeArea} has changed.
     */
    private void updateLineArea() {
        int lines = codeArea.getLineCount();
        lineArea.setText(null);
        for (int i = 0; i < lines; i++) {
            lineArea.append(lineAreaStart + i + System.getProperty("line.separator"));
        }
    }

    /**
     * Return the content of the {@code codeArea}.
     * 
     * @return A {@code String} with the content of the {@code codeArea}.
     */
    public String getCode() {
        return codeArea.getText();
    }

    /**
     * Set the content of the {@code codeArea}.
     * 
     * @param code
     *            a {@code String} with the content for the {@code codeArea}
     */
    public void setCode(String code) {
        codeArea.setText(code);
    }

    /**
     * Enable or disable the {@code CodeField}.
     * 
     * @param b
     *            enable if {@code true}, disable if {@code false}
     */
    public void setEnabled(boolean b) {
        super.setEnabled(b);
        lineArea.setEnabled(b);
        codeArea.setEnabled(b);
    }

    /**
     * The {@code TextChangeListener} gets called every time the text in the
     * {@code codeArea} has changed. Calls {@code updateLineArea} on every
     * change.
     * 
     * @author Michael
     *
     */
    private class TextChangeListener implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent e) {
            updateLineArea();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            updateLineArea();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            updateLineArea();
        }

    }

}
