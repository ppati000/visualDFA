package gui;

import java.awt.Frame;

/**
 * DialogBox for displaying messages. Can display a custom message.
 * 
 * @author Michael
 *
 * @see DialogBox
 */

public class MessageBox extends DialogBox {

    /**
     * Display the MessageBox. Stop execution of this Thread until user closes
     * the Dialog.
     * 
     * @param owner
     *            The Frame, which is the owner of the MessageBox.
     * @param title
     *            The title of the MessageBox.
     * @param message
     *            The message to be displayed by the MessageBox.
     * 
     * @see Frame
     * @see javax.swing.JDialog
     */
    public MessageBox(Frame owner, String title, String message) {
        super(owner, title);
        // TODO Auto-generated constructor stub
    }

    /**
     * Set layout and content of the contentPanel for the MessageBox.
     */
    @Override
    protected void initContentPanel() {
        // TODO Auto-generated method stub

    }

    /**
     * Set layout and content of the ButtonPane for the MessageBox.
     */
    @Override
    protected void initButtonPane() {
        // TODO Auto-generated method stub

    }

}
