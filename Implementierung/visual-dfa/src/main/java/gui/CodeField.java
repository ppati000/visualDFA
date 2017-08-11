package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * A class to display code in a JScrollPane with line numbers to the left.
 * 
 * @author Michael
 * 
 * @see JScrollPane
 */
public class CodeField extends JScrollPane {
    
    private JPanel contentPanel;
    private JTextArea lineArea;
    private JTextArea codeArea;
    private int lineAreaStart;

    /**
     * Constructor for the CodeField. Can be set editable, so the user can write Code in the JTextArea-component or
     * non-editable, so code can be displayed to the user.
     * 
     * @param editable
     */
    public CodeField(boolean editable) {
        super();
        
        contentPanel =  new JPanel();
        contentPanel.setLayout(new BorderLayout());
        
        lineArea = new JTextArea("1");
        lineAreaStart = 1;
        lineArea.setEditable(false);
        //TODO Color
        contentPanel.add(lineArea, BorderLayout.WEST);
        
        codeArea = new JTextArea();
        codeArea.setEditable(editable);
        codeArea.setTabSize(4);
        codeArea.getDocument().addDocumentListener(new TextChangeListener());
        
        
        contentPanel.add(codeArea, BorderLayout.CENTER);
        
        setViewportView(contentPanel);
        
         setPreferredSize(new Dimension(1, Integer.MAX_VALUE));
        
        
        
        
    }
    
    public void setLineAreaStart(int start) {
        lineAreaStart = start;
    }
    
    /**
     * Update the line numbers in the lineArea every time the codeArea has changed.
     */
    private void updateLineArea() {
        int lines = codeArea.getLineCount();
        lineArea.setText(null);
        for (int i = 0; i < lines; i++) {
            lineArea.append(lineAreaStart + i + System.getProperty("line.separator"));
        }
    }
    
    /**
     * Return the content of the codeArea.
     * @return A String with the content of the codeArea.
     */
    public String getCode() {
        return codeArea.getText();
    }
    
    /**
     * Set the content of the codeArea.
     * @param code A String with the content for the codeArea.
     */
    public void setCode(String code) {
        codeArea.setText(code);
    }
    
    public void setEnabled(boolean b) {
        super.setEnabled(b);
        lineArea.setEnabled(b);
        codeArea.setEnabled(b);
    }
    
    /**
     * The TextChangeListener gets called every time the text in the codeArea has changed.
     * Call updateLineArea on every change.
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
