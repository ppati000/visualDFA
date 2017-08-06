package gui;

import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JLabel;

/**
 * Utility class to set standard properties for JLabels.
 * 
 * @author Michael
 *
 * @see JLabel
 */
public class JLabelDecorator {
    
    private JComponentDecorator componentDecorator;
    
    /**
     * Pass a JComponentDecorator to set standard JComponent properties.
     * 
     * @param componentDecorator
     * @see JComponentDecorator
     * @see JComponent
     */
    public JLabelDecorator(JComponentDecorator componentDecorator) {
        this.componentDecorator = componentDecorator;
    }
    
    /**
     * Set standard properties for a JLabel.
     * First calls the decorate method of the JComponentDecorator then adds JLabel specific properties.
     * 
     * @param comp JLabel on which standard properties are set.
     * @param text The text for the JLabel.
     * 
     * @see JComponentDecorator
     * @see JLabel
     */
    public void decorateLabel(JLabel comp, String text) {
        componentDecorator.decorate(comp);
        comp.setText(text);
    }
    
    public void decorateTitle(JLabel comp, String text) {
        componentDecorator.decorate(comp);
        comp.setFont(new Font(comp.getFont().getName(), Font.BOLD, 24));
        comp.setText(text);
    }
}
