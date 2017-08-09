package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;



/**
 * DialogBox for method selection. User can select one of several methods and
 * confirm or cancel the selection.
 * 
 * @author Michael
 * 
 * @see DialogBox
 */
public class MethodSelectionBox extends DialogBox {

    private List<String> methods;
    private String selectedMethod;
    private final String METHODBOX_TITLE = "Select Method";
    private Option option;

    /**
     * Display the MethodSelectionBox. Stop execution of this Thread until user
     * closes the Dialog.
     * 
     * @param owner
     *            The Frame, which owns this Dialog.
     * @param methods
     *            The names of the methods to choose from.
     * 
     * @see Frame
     * @see javax.swing.JDialog
     */

    public MethodSelectionBox(Frame owner, List<String> methods) {
        super(owner);
        this.methods = methods;
        init(METHODBOX_TITLE);
        pack();
        setVisible(true);
    }

    /**
     * Set layout and content of the ContentPanel for the MethodSelectionBox.
     */
    @Override
    protected void initContentPanel() {
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBackground(Colors.BACKGROUND.getColor());
        JScrollPane radioScrollPane = new JScrollPane();
        radioScrollPane.setPreferredSize(new Dimension(0, 200));
        JPanel radioPane = new JPanel();
        radioPane.setLayout(new BoxLayout(radioPane,  BoxLayout.PAGE_AXIS));
        radioPane.setBackground(Colors.BACKGROUND.getColor());
        ButtonGroup methodGroup = new ButtonGroup();
        JComponentDecorator compDecorator = new JComponentDecorator();
        boolean first = true;
        ButtonListener btnListener = new ButtonListener();
        for (String m : methods) {
            JRadioButton rb = new JRadioButton(m);
            rb.setActionCommand(m);
            rb.addActionListener(btnListener);
            if (first) {
                rb.setSelected(true);
                selectedMethod = m;
                first = false;
            }
            compDecorator.decorate(rb);
            methodGroup.add(rb);
            radioPane.add(rb);
            
        }
        radioScrollPane.setViewportView(radioPane);
        contentPanel.add(radioScrollPane, BorderLayout.CENTER);

    }

    /**
     * Set layout and content of the ButtonPane for the MethodSelectionBox.
     */
    @Override
    protected void initButtonPane() {
        buttonPane.setBackground(Colors.BACKGROUND.getColor());
        buttonPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        GridBagLayout gbl_Button = new GridBagLayout();
        gbl_Button.columnWidths = new int[] {0, 0, 0};
        gbl_Button.columnWeights = new double[] {0.5, 0.5, 0.5};
        buttonPane.setLayout(gbl_Button);
        JButtonDecorator buttonDecorator = new JButtonDecorator(new JComponentDecorator());
        
        JButton btnOK = new JButton();
        buttonDecorator.decorateBorderButton(btnOK, new OKListener(), "Run Analysis", Colors.GREY_BORDER.getColor());
        btnOK.setBackground(Colors.WHITE_BACKGROUND.getColor());
        btnOK.setForeground(Colors.DARK_TEXT.getColor());
        GridBagConstraints gbc_btnOK = GridBagConstraintFactory.getStandardGridBagConstraints(4, 0, 1, 1);
        buttonPane.add(btnOK, gbc_btnOK);
        
        JButton btnCancel = new JButton();
        buttonDecorator.decorateBorderButton(btnCancel, new CancelListener(), "Cancel", Colors.GREY_BORDER.getColor());
        GridBagConstraints gbc_btnCancel = GridBagConstraintFactory.getStandardGridBagConstraints(0, 0, 1, 1);
        buttonPane.add(btnCancel, gbc_btnCancel);

    }

    /**
     * Look up, which method is selected and return its name.
     * 
     * @return The name of the selected method.
     */
    public String getSelectedMethod() {
        return selectedMethod;
    }
    
    public Option getOption() {
        return option;
    }
    
    private class OKListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            option = Option.YES_OPTION; 
            setVisible(false);
        }
        
    }
    
    private class CancelListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            option = Option.CANCEL_OPTION;
            setVisible(false);
        }
        
    }
    
    private class ButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            selectedMethod = e.getActionCommand();
            
        }
        
    }
}
