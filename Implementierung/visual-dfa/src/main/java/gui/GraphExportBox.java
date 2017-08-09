package gui;

import java.awt.Frame;

/**
 * DialogBox for graph-exporting. User can set export settings and start or
 * cancel exporting.
 * 
 * @author Michael
 *
 * @see DialogBox
 */
public class GraphExportBox extends DialogBox {

    private boolean hasLineStatesIncluded;
    private boolean isBatchExport;
    private Quality quality;
    private final String GRAPHEXPORT_TITLE = "Export Graph as PNG";

    /**
     * Display the GraphExportBox. Stop execution of this Thread until user
     * closes the Dialog.
     * 
     * @param owner
     *            The Frame, which is the owner of this Dialog
     * 
     * @see Frame
     * @see javax.swing.JDialog
     */
    public GraphExportBox(Frame owner) {
        super(owner);
        init(GRAPHEXPORT_TITLE);
        // TODO Auto-generated constructor stub
    }

    /**
     * Set layout and content of the contentPanel for the GraphExportBox.
     */
    @Override
    protected void initContentPanel() {
        // TODO Auto-generated method stub

    }

    /**
     * Set layout and content of the ButtonPane for the GraphExportBox.
     */
    @Override
    protected void initButtonPane() {
        // TODO Auto-generated method stub

    }

    /**
     * Look up whether the user wants line states in the Graph export.
     * 
     * @return [true], if line states should be included, [false] if not.
     */
    public boolean hasLineStatesIncluded() {
        return hasLineStatesIncluded;
    }

    /**
     * Look up whether the user wants a batch export of the data flow analysis.
     * 
     * @return [true] if batch export is selected, [false] if not.
     */
    public boolean isBatchExport() {
        return isBatchExport;
    }

}
