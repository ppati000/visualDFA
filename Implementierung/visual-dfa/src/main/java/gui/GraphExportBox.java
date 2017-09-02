package gui;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * {@code DialogBox} for graph-exporting. User can set export settings and start or
 * cancel exporting.
 * 
 * @author Michael
 *
 * @see DialogBox
 */
public class GraphExportBox extends DialogBox {

    private Quality quality;
    private Option option;
    private final String GRAPHEXPORT_TITLE = "Export Graph as PNG";
    private JButton btn_Low;
    private JButton btn_Standard;
    private JButton btn_High;
    private JCheckBox cb_BatchExport;
    private JCheckBox cb_LineStates;

    /**
     * Display the GraphExportBox. Stop execution of this Thread until user
     * closes the Dialog.
     * 
     * @param owner
     *            the Frame, which is the owner of this Dialog
     * 
     * @see Frame
     * @see javax.swing.JDialog
     */
    public GraphExportBox(Frame owner) {
        super(owner);
        init(GRAPHEXPORT_TITLE);
        option = Option.CANCEL_OPTION;
        quality = Quality.STANDARD;
        pack();
        setVisible(true);
    }

    /**
     * Set layout and content of the contentPanel for the GraphExportBox.
     */
    @Override
    protected void initContentPanel() {
        contentPanel.setBackground(Colors.BACKGROUND.getColor());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        GridBagLayout gbl_Content = new GridBagLayout();
        gbl_Content.columnWeights = new double[] { 0.5, 0.5, 0.5 };
        gbl_Content.rowWeights = new double[] { 0.5, 0.5, 0.5, 0.5 };
        contentPanel.setLayout(gbl_Content);

        JComponentDecorator compDecorator = new JComponentDecorator();
        JButtonDecorator btnDecorator = new JButtonDecorator(compDecorator);
        JLabelDecorator lblDecorator = new JLabelDecorator(compDecorator);

        JLabel lblResolution = new JLabel();
        lblDecorator.decorateLabel(lblResolution, "Resolution");
        GridBagConstraints gbc_lblResolution = GridBagConstraintFactory.getStandardGridBagConstraints(0, 0, 3, 1);
        contentPanel.add(lblResolution, gbc_lblResolution);

        btn_Low = new JButton();
        btnDecorator.decorateBorderButton(btn_Low, new ResolutionChangeListener(), "Low",
                Colors.GREY_BORDER.getColor());
        btn_Low.setActionCommand("low");
        btn_Low.setBackground(Colors.WHITE_BACKGROUND.getColor());
        btn_Low.setForeground(Colors.DARK_TEXT.getColor());
        GridBagConstraints gbc_btnLow = GridBagConstraintFactory.getStandardGridBagConstraints(0, 1, 1, 1);
        gbc_btnLow.insets.set(gbc_btnLow.insets.top, gbc_btnLow.insets.left, gbc_btnLow.insets.bottom, 0);
        contentPanel.add(btn_Low, gbc_btnLow);

        btn_Standard = new JButton();
        btnDecorator.decorateBorderButton(btn_Standard, new ResolutionChangeListener(), "Standard",
                Colors.GREY_BORDER.getColor());
        btn_Standard.setActionCommand("standard");
        btn_Standard.setBackground(Colors.LIGHT_BACKGROUND.getColor());
        btn_Standard.setForeground(Colors.DARK_TEXT.getColor());
        GridBagConstraints gbc_btnStandard = GridBagConstraintFactory.getStandardGridBagConstraints(1, 1, 1, 1);
        gbc_btnStandard.insets.set(gbc_btnStandard.insets.top, 0, gbc_btnStandard.insets.bottom, 0);
        contentPanel.add(btn_Standard, gbc_btnStandard);

        btn_High = new JButton();
        btnDecorator.decorateBorderButton(btn_High, new ResolutionChangeListener(), "High",
                Colors.GREY_BORDER.getColor());
        btn_High.setActionCommand("high");
        btn_High.setBackground(Colors.WHITE_BACKGROUND.getColor());
        btn_High.setForeground(Colors.DARK_TEXT.getColor());
        GridBagConstraints gbc_btnHigh = GridBagConstraintFactory.getStandardGridBagConstraints(2, 1, 1, 1);
        gbc_btnHigh.insets.set(gbc_btnHigh.insets.top, 0, gbc_btnHigh.insets.bottom, gbc_btnHigh.insets.right);
        contentPanel.add(btn_High, gbc_btnHigh);

        cb_BatchExport = new JCheckBox();
        compDecorator.decorate(cb_BatchExport);
        cb_BatchExport.setText("Batch export all graph states");
        cb_BatchExport.addChangeListener(new ChangeListener() {
            
            @Override
            public void stateChanged(ChangeEvent e) {
                if (cb_BatchExport.isSelected()) {
                    cb_LineStates.setEnabled(true);
                } else {
                    cb_LineStates.setEnabled(false);
                }
                
            }
        });
        GridBagConstraints gbc_cbBatch = GridBagConstraintFactory.getStandardGridBagConstraints(0, 2, 3, 1);
        contentPanel.add(cb_BatchExport, gbc_cbBatch);

        cb_LineStates = new JCheckBox();
        compDecorator.decorate(cb_LineStates);
        cb_LineStates.setEnabled(false);
        cb_LineStates.setText("Include line steps");
        GridBagConstraints gbc_cbLineStates = GridBagConstraintFactory.getStandardGridBagConstraints(0, 3, 3, 1);
        contentPanel.add(cb_LineStates, gbc_cbLineStates);

    }

    /**
     * Set layout and content of the ButtonPane for the GraphExportBox.
     */
    @Override
    protected void initButtonPane() {
        buttonPane.setBackground(Colors.BACKGROUND.getColor());
        buttonPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        GridBagLayout gbl_Button = new GridBagLayout();
        gbl_Button.columnWidths = new int[] { 0, 0, 0 };
        gbl_Button.columnWeights = new double[] { 0.5, 0.5, 0.5 };
        buttonPane.setLayout(gbl_Button);
        JButtonDecorator buttonDecorator = new JButtonDecorator(new JComponentDecorator());

        JButton btnOK = new JButton();
        buttonDecorator.decorateBorderButton(btnOK, new OKListener(), "Export", Colors.GREY_BORDER.getColor());
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
     * Look up whether the user wants to include the LineSteps in the export.
     * 
     * @return True if the user has selected the according JCheckBox, false
     *         otherwise.
     */
    public boolean includeLineStates() {
        return cb_LineStates.isSelected();
    }

    /**
     * Look up whether the user wants a batch export of the data flow analysis.
     * 
     * @return [true] if batch export is selected, [false] if not.
     */
    public boolean isBatchExport() {
        return cb_BatchExport.isSelected();
    }

    /**
     * Look up with which option the user has closed the dialog.
     * 
     * @return The Option according to the users choice.
     */
    public Option getOption() {
        return option;
    }

    /**
     * Look up which Quality the user has selected for the export.
     * 
     * @return The selected Quality.
     */
    public Quality getQuality() {
        return quality;
    }

    /**
     * Listener class for the OK-Button.
     * 
     * @author Michael
     *
     * @see JButton
     */
    private class OKListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            option = Option.YES_OPTION;
            setVisible(false);
        }

    }

    /**
     * Listener class for the Cancel Button
     * 
     * @author Michael
     *
     * @see JButton
     */
    private class CancelListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            option = Option.CANCEL_OPTION;
            setVisible(false);
        }

    }

    /**
     * Listener Class for the resolution buttons. Changes the color of the
     * buttons and sets the Quality accordingly.
     * 
     * @author Michael
     * 
     * @see JButton
     *
     */
    private class ResolutionChangeListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (e.getActionCommand()) {
            case "low":
                btn_Low.setBackground(Colors.LIGHT_BACKGROUND.getColor());
                btn_Standard.setBackground(Colors.WHITE_BACKGROUND.getColor());
                btn_High.setBackground(Colors.WHITE_BACKGROUND.getColor());
                quality = Quality.LOW;
                break;
            case "standard":

                btn_Low.setBackground(Colors.WHITE_BACKGROUND.getColor());
                btn_Standard.setBackground(Colors.LIGHT_BACKGROUND.getColor());
                btn_High.setBackground(Colors.WHITE_BACKGROUND.getColor());
                quality = Quality.STANDARD;
                break;
            case "high":

                btn_Low.setBackground(Colors.WHITE_BACKGROUND.getColor());
                btn_Standard.setBackground(Colors.WHITE_BACKGROUND.getColor());
                btn_High.setBackground(Colors.LIGHT_BACKGROUND.getColor());
                quality = Quality.HIGH;
                break;

            default:

                btn_Low.setBackground(Colors.WHITE_BACKGROUND.getColor());
                btn_Standard.setBackground(Colors.LIGHT_BACKGROUND.getColor());
                btn_High.setBackground(Colors.WHITE_BACKGROUND.getColor());
                quality = Quality.STANDARD;
                break;
            }

        }

    }

}
