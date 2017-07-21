package gui;

import java.awt.Frame;
import java.util.List;

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
        super(owner, "Select Method");
    }

    /**
     * Set layout and content of the ContentPanel for the MethodSelectionBox.
     */
    @Override
    protected void initContentPanel() {
        // TODO Auto-generated method stub

    }

    /**
     * Set layout and content of the ButtonPane for the MethodSelectionBox.
     */
    @Override
    protected void initButtonPane() {
        // TODO Auto-generated method stub

    }

    /**
     * Look up, which method is selected and return its name.
     * 
     * @return The name of the selected method.
     */
    public String getSelectedMethod() {
        return selectedMethod;
    }
}
