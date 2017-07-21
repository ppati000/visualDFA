package gui;

import java.awt.Frame;

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
        // TODO Auto-generated constructor stub
    }

    /**
     * Set layout and content of the ContentPanel for the OptionBox.
     */
    @Override
    protected void initContentPanel() {
        // TODO Auto-generated method stub

    }

    /**
     * Set layout and content of the ButtonPane for the OptionBox.
     */
    @Override
    protected void initButtonPane() {
        // TODO Auto-generated method stub

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

}
