package gui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;

import javax.swing.JDialog;
import javax.swing.JPanel;

/**
 * Superclass for different DialogBoxes. Includes a title, a contentPanel and a
 * buttonPane. Can be customized by subclasses. Layout and content of
 * contentPanel and buttonPane have to be set by subclasses. Blocks user input
 * on top-level windows.
 * 
 * @author Michael
 *
 */

public abstract class DialogBox extends JDialog {

    private final JPanel contentPanel = new JPanel();
    private final JPanel buttonPane = new JPanel();
    private final JPanel titlePane = new JPanel();

    /**
     * Create the dialog. Set owner and make modal, so that top-level windows
     * block user Input.
     * 
     * @param owner
     *            The Frame, which is the owner of this DialogBox.
     * @param title
     *            The title of this DialogBox.
     * 
     * @see Frame
     *
     */
    public DialogBox(Frame owner, String title) {
        super(owner, true);
        init(title);
    }

    /**
     * Create the dialog. Set owner and make modal, so that top-level windows
     * block user Input.
     * 
     * @param owner
     *            The Dialog which is the owner of this DialogBox.
     * @param title
     *            The title of this DialogBox.
     * 
     * @see Dialog
     *
     */
    public DialogBox(Dialog owner, String title) {
        super(owner, true);
        init(title);
    }

    /**
     * Initialize the titlePane, contentPanel and the buttonPane. Automatically
     * size the DialogBox.
     * 
     * @param title
     *            The title of the DialogBox.
     */
    private final void init(String title) {
        getContentPane().setLayout(new BorderLayout());

        initContentPanel();
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        initTitlePane(title);
        getContentPane().add(titlePane, BorderLayout.NORTH);
        initButtonPane();
        getContentPane().add(buttonPane, BorderLayout.SOUTH);

    }

    /**
     * Set the layout and content of the titlePane.
     * 
     * @param title
     *            The title, which is displayed in the titlePane.
     */
    private void initTitlePane(String title) {

    }

    /**
     * Is to be implemented by subclasses. Set the layout and content of the
     * contentPanel.
     */
    protected abstract void initContentPanel();

    /**
     * Is to be implemented by subclasses. Set the layout and content of the
     * buttonPane.
     */
    protected abstract void initButtonPane();
}
