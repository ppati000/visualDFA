package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JTextArea;

/**
 * DialogBox for displaying messages, which leave options for the user. The
 * decision the user made can be obtained.
 * 
 * @author Michael
 *
 * @see DialogBox
 */
public class OptionBox extends DialogBox {

    private Option option;
    private JTextArea messageArea;

    /**
     * Display the OptionBox. Stop execution of this Thread until user closes
     * the Dialog.
     * 
     * @param owner
     *            The Frame, which owns the OptionBox.
     * @param title
     *            The title of the OptionBox.
     * 
     * @see Frame
     * @see javax.swing.JDialog
     */
    public OptionBox(Frame owner, String title) {
        super(owner, title);
        messageArea.setText(null);
        pack();
    }
    
    public OptionBox(Frame owner, String title, String message) {
        super(owner, title);
        messageArea.setText(message);
        pack();
        setVisible(true);
    }

    /**
     * Set layout and content of the ContentPanel for the OptionBox.
     */
    @Override
    protected void initContentPanel() {
        contentPanel.setBackground(Colors.BACKGROUND.getColor());
        contentPanel.setLayout(new BorderLayout());
        
        messageArea = new JTextArea();
        new JComponentDecorator().decorate(messageArea);
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        contentPanel.add(messageArea, BorderLayout.CENTER);

    }

    /**
     * Set layout and content of the ButtonPane for the OptionBox.
     */
    @Override
    protected void initButtonPane() {
        buttonPane.setBackground(Colors.BACKGROUND.getColor());
        GridBagLayout gbl_Button = new GridBagLayout();
        gbl_Button.columnWidths = new int[] {0, 0, 0, 0, 0};
        gbl_Button.columnWeights = new double[] {0.5, 0.5, 0.5, 0.5, 0.5};
        buttonPane.setLayout(gbl_Button);
        JButtonDecorator buttonDecorator = new JButtonDecorator(new JComponentDecorator());
        JButton btnYes = new JButton();
        buttonDecorator.decorateBorderButton(btnYes, new YesListener(), "Yes", Colors.GREY_BORDER.getColor());
        btnYes.setBackground(Colors.WHITE_BACKGROUND.getColor());
        btnYes.setForeground(Colors.DARK_TEXT.getColor());
        GridBagConstraints gbc_btnYes = GridBagConstraintFactory.getStandardGridBagConstraints(4, 0, 1, 1);
        buttonPane.add(btnYes, gbc_btnYes);
        
        JButton btnNo = new JButton();
        buttonDecorator.decorateBorderButton(btnNo, new NoListener(), "No", Colors.GREY_BORDER.getColor());
        btnNo.setBackground(Colors.WHITE_BACKGROUND.getColor());
        btnNo.setForeground(Colors.DARK_TEXT.getColor());
        GridBagConstraints gbc_btnNo = GridBagConstraintFactory.getStandardGridBagConstraints(2, 0, 1, 1);
        buttonPane.add(btnNo, gbc_btnNo);
        
        JButton btnCancel = new JButton();
        buttonDecorator.decorateBorderButton(btnCancel, new CancelListener(), "Cancel", Colors.GREY_BORDER.getColor());
        GridBagConstraints gbc_btnCancel = GridBagConstraintFactory.getStandardGridBagConstraints(0, 0, 1, 1);
        buttonPane.add(btnCancel, gbc_btnCancel);
    }

    /**
     * Returns which option the user selected while closing the Dialog.
     * 
     * @return The selected option.
     * @see Option
     */
    public Option getOption() {
        return option;
    }

    private class YesListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            option = Option.YES_OPTION;
            setVisible(false);
            
        }
        
    }
    
    private class NoListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            option = Option.NO_OPTION;
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
}
