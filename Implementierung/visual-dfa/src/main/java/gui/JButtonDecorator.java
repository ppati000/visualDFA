package gui;

import java.awt.Color;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.border.LineBorder;

/**
 * A utility class to set standard properties for a jButton.
 * 
 * @author Michael
 *
 * @see JButton
 */
public class JButtonDecorator {
    private JComponentDecorator componentDecorator;
    
    /**
     * Pass a JComponentDecorator to set standard JComponent properties.
     * 
     * @param componentDecorator
     * @see JComponentDecorator
     * @see JComponent
     */
    public JButtonDecorator(JComponentDecorator componentDecorator) {
        this.componentDecorator = componentDecorator;
    }
    
    /**
     * Set standard properties for a JButton with an icon.
     * First calls the decorate method of the JComponentDecorator
     * then adds JButton specific properties.
     * 
     * @param comp JButton on which the properties are set.
     * @param iconPath Path to the icon resource.
     * @param scale Scale of the icon.
     * @param actListener ActionListener for the JButton.
     * @param text Text on the JButton.
     * @see JButton
     * @see JComponentDecorator
     * @see IconLoader
     * @see ActionListener
     */
    public void decorateIconButton(JButton comp, String iconPath, double scale, ActionListener actListener, String text) {
        decorateButton(comp, actListener, text);
        comp.setIcon(IconLoader.loadIcon(iconPath, scale));
    }
    
    /**
     * Set standard properties for a JButton without an icon.
     * First calls the decorate method of the JComponentDecorator
     * then adds JButton specific properties.
     * 
     * @param comp JButton on which the properties are set.
     * @param actListener ActionListener for the JButton.
     * @param text TExt on the JButton.
     * @see JButton
     * @see JComponentDecorator
     * @see ActionListener
     */
    public void decorateButton(JButton comp, ActionListener actListener, String text) {
        componentDecorator.decorate(comp);
        comp.setText(text);
        comp.addActionListener(actListener);
        comp.getInputMap().put(KeyStroke.getKeyStroke("SPACE"), "none");
        comp.setFocusPainted(false);
    }
    
    public void decorateBorderButton(JButton comp, ActionListener actListener, String text, Color BorderColor) {
        decorateButton(comp, actListener, text);
        comp.setBorder(new LineBorder(BorderColor));
    }
}
