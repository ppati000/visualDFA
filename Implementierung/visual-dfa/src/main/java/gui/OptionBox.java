package gui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * {@code DialogBox} for displaying messages, which leave options for the user.
 * The decision the user made can be obtained.
 * 
 * @author Michael
 *
 * @see DialogBox
 */
public class OptionBox extends DialogBox {

    private static final long serialVersionUID = 1L;
    
    private Option option;
    private JTextArea messageArea;
    private String message;

    /**
     * Display the {@code OptionBox}. Stop execution of this {@code Thread}
     * until user closes the {@code Dialog}.
     * 
     * @param owner
     *            the {@code Frame}, which owns the {@code OptionBox}
     * @param title
     *            the title of the {@code OptionBox}
     * 
     * @see Frame
     * @see javax.swing.JDialog
     */
    public OptionBox(Frame owner, String title) {
        super(owner);
        this.message = null;
        option = Option.CANCEL_OPTION;
        init(title);
        pack();
        setVisible(true);
    }

    /**
     * Display the {@code OptionBox}. Stop execution of this {@code Thread}
     * until user closes the {@code Dialog}.
     * 
     * @param owner
     *            the {@code Frame}, which owns the {@code OptionBox}
     * @param title
     *            the title of the {@code OptionBox}
     * @param message
     *            the message for the {@code OptionBox}
     * 
     * @see Frame
     * @see javax.swing.JDialog
     */
    public OptionBox(Frame owner, String title, String message) {
        super(owner);
        this.message = message;
        init(title);
        pack();
        setVisible(true);
    }

    /**
     * Set layout and content of the ContentPanel for the {@code OptionBox}.
     */
    @Override
    protected void initContentPanel() {
        
        contentPanel.setBackground(Colors.BACKGROUND.getColor());
        
        if (message != null) {
            
            contentPanel.setLayout(new BorderLayout());

            messageArea = new JTextArea();
            new JComponentDecorator().decorate(messageArea);
            int i = message.length();
            // This makes boxes with more text larger than boxes with fewer text
            if ((i / 25) > 4) {
                messageArea.setColumns(30);
                messageArea.setRows(15);
            } else {
                messageArea.setColumns(25);
                messageArea.setRows(4);
            }
            messageArea.setEditable(false);
            messageArea.setLineWrap(true);
            messageArea.setWrapStyleWord(true);
            messageArea.setText(message);
            JScrollPane messagePane = new JScrollPane(messageArea);
            messagePane.setBorder(null);
            contentPanel.add(messagePane, BorderLayout.CENTER);
        }

    }

    /**
     * Set layout and content of the ButtonPane for the {@code OptionBox}.
     */
    @Override
    protected void initButtonPane() {
        buttonPane.setBackground(Colors.BACKGROUND.getColor());
        GridBagLayout gbl_Button = new GridBagLayout();
        gbl_Button.columnWidths = new int[] { 0, 0, 0, 0, 0 };
        gbl_Button.columnWeights = new double[] { 0.5, 0.5, 0.5, 0.5, 0.5 };
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
     * Returns which {@code Option} the user selected while closing the Dialog.
     * 
     * @return The selected {@code Option}.
     * @see Option
     */
    public Option getOption() {
        return option;
    }

    /**
     * The Listener for the Yes-Button.
     * 
     * @author Michael
     *
     */
    private class YesListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            option = Option.YES_OPTION;
            setVisible(false);

        }

    }

    /**
     * The Listener for the No-Button.
     * 
     * @author Michael
     *
     */
    private class NoListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            option = Option.NO_OPTION;
            setVisible(false);

        }

    }

    /**
     * The Listener for the Cancel-Button.
     * 
     * @author Michael
     *
     */
    private class CancelListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            option = Option.CANCEL_OPTION;
            setVisible(false);

        }

    }
}
