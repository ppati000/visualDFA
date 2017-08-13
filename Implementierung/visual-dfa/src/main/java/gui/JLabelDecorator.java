package gui;

import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JLabel;

/**
 * Utility class to set standard properties for {@code JLabels}.
 * 
 * @author Michael
 *
 * @see JLabel
 */
public class JLabelDecorator {

    private JComponentDecorator componentDecorator;

    /**
     * Pass a {@code JComponentDecorator} to set standard {@code JComponent}
     * properties.
     * 
     * @param componentDecorator
     * @see JComponentDecorator
     * @see JComponent
     */
    public JLabelDecorator(JComponentDecorator componentDecorator) {
        this.componentDecorator = componentDecorator;
    }

    /**
     * Set standard properties for a {@code JLabel}. First calls the decorate
     * method of the {@code JComponentDecorator}, then adds {@code JLabel}
     * specific properties.
     * 
     * @param comp
     *            {@code JLabel} on which standard properties are set
     * @param text
     *            the text for the {@code JLabel}
     * 
     * @see JComponentDecorator
     * @see JLabel
     */
    public void decorateLabel(JLabel comp, String text) {
        componentDecorator.decorate(comp);
        comp.setText(text);
    }

    /**
     * Set standard properties for a {@code JLabel} which is used as a title.
     * First calls the decorate method of the {@code JComponentDecorator}, then
     * adds {@code JLabel} specific properties.
     * 
     * @param comp
     *            {@code JLabel} on which standard properties are set
     * @param text
     *            the text for the {@code JLabel}
     * 
     * @see JComponentDecorator
     * @see JLabel
     */
    public void decorateTitle(JLabel comp, String text) {
        componentDecorator.decorate(comp);
        comp.setFont(new Font(comp.getFont().getName(), Font.BOLD, 24));
        comp.setText(text);
    }
}
