package gui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class GenericBox extends DialogBox {

    private static final String NEVER_SHOW_AGAIN = "Never show again";
    private Option option;
    private JTextArea messageArea;
    private String message;
    private String yesButtonText;
    private String noButtonText;
    private String cancelButtonText;
    private boolean hasNotShowCheckbox;
    private JCheckBox notShowCheckbox;
    

    public GenericBox(Frame owner, String title, String yesButtonText, String noButtonText, String cancelButtonText,
            boolean hasNotShowCheckbox, Option stdOption) {
        super(owner);
        this.yesButtonText = yesButtonText;
        this.noButtonText = noButtonText;
        this.cancelButtonText = cancelButtonText;
        this.message = null;
        option = stdOption;
        this.hasNotShowCheckbox = hasNotShowCheckbox;
        init(title);
        pack();
        setVisible(true);
    }

    public GenericBox(Frame owner, String title, String message, String yesButtonText, String noButtonText,
            String cancelButtonText, boolean hasNotShowCheckbox, Option stdOption) {
        super(owner);
        this.yesButtonText = yesButtonText;
        this.noButtonText = noButtonText;
        this.cancelButtonText = cancelButtonText;
        this.message = message;
        option = stdOption;
        this.hasNotShowCheckbox = hasNotShowCheckbox;
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
        contentPanel.setLayout(new BorderLayout());

        if (message != null) {

            

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
        if (hasNotShowCheckbox) {
            notShowCheckbox = new JCheckBox(NEVER_SHOW_AGAIN);
            new JComponentDecorator().decorate(notShowCheckbox);
            contentPanel.add(notShowCheckbox, BorderLayout.SOUTH);
        }

    }

    protected void initButtonPane() {
        buttonPane.setBackground(Colors.BACKGROUND.getColor());
        GridBagLayout gbl_Button = new GridBagLayout();
        gbl_Button.columnWidths = new int[] { 0, 0, 0, 0, 0 };
        gbl_Button.columnWeights = new double[] { 0.5, 0.5, 0.5, 0.5, 0.5 };
        buttonPane.setLayout(gbl_Button);

        JButtonDecorator buttonDecorator = new JButtonDecorator(new JComponentDecorator());

        if (yesButtonText != null) {
            JButton btnYes = new JButton();
            buttonDecorator.decorateBorderButton(btnYes, new YesListener(), yesButtonText,
                    Colors.GREY_BORDER.getColor());
            btnYes.setBackground(Colors.WHITE_BACKGROUND.getColor());
            btnYes.setForeground(Colors.DARK_TEXT.getColor());
            GridBagConstraints gbc_btnYes = GridBagConstraintFactory.getStandardGridBagConstraints(4, 0, 1, 1);
            buttonPane.add(btnYes, gbc_btnYes);
        }

        if (noButtonText != null) {
            JButton btnNo = new JButton();
            buttonDecorator.decorateBorderButton(btnNo, new NoListener(), noButtonText, Colors.GREY_BORDER.getColor());
            btnNo.setBackground(Colors.WHITE_BACKGROUND.getColor());
            btnNo.setForeground(Colors.DARK_TEXT.getColor());
            GridBagConstraints gbc_btnNo = GridBagConstraintFactory.getStandardGridBagConstraints(2, 0, 1, 1);
            buttonPane.add(btnNo, gbc_btnNo);
        }

        if (cancelButtonText != null) {
            JButton btnCancel = new JButton();
            buttonDecorator.decorateBorderButton(btnCancel, new CancelListener(), cancelButtonText,
                    Colors.GREY_BORDER.getColor());
            GridBagConstraints gbc_btnCancel = GridBagConstraintFactory.getStandardGridBagConstraints(0, 0, 1, 1);
            buttonPane.add(btnCancel, gbc_btnCancel);
        }
    }
    
    public boolean showAgain() {
        if (hasNotShowCheckbox) {
            return notShowCheckbox.isSelected();
        } else {
            return true;
        }
        
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
