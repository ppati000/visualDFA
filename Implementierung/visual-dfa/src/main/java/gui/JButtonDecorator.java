package gui;

import java.awt.Color;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * A utility class to set standard properties for a {@code JButton}.
 * 
 * @author Michael
 *
 * @see JButton
 */
public class JButtonDecorator {
    private JComponentDecorator componentDecorator;

    /**
     * Pass a {@code JComponentDecorator} to set standard {@code JComponent}
     * properties.
     * 
     * @param componentDecorator
     *            decorates standard properties for {@code JComponents}
     * @see JComponentDecorator
     * @see JComponent
     */
    public JButtonDecorator(JComponentDecorator componentDecorator) {
        this.componentDecorator = componentDecorator;
    }

    /**
     * Set standard properties for a {@code JButton} with an icon. First calls
     * the decorate method of the {@code JComponentDecorator} then adds
     * {@code JButton} specific properties.
     * 
     * @param comp
     *            {@code JButton} on which the properties are set
     * @param iconPath
     *            path to the icon resource
     * @param scale
     *            scale of the icon
     * @param actListener
     *            {@code ActionListener} for the {@code JButton}
     * @param text
     *            text on the {@code JButton}
     * @see JButton
     * @see JComponentDecorator
     * @see IconLoader
     * @see ActionListener
     */
    public void decorateIconButton(JButton comp, String iconPath, double scale, ActionListener actListener,
            String text) {
        decorateButton(comp, actListener, text);
        comp.setIcon(IconLoader.loadIcon(iconPath, scale));
    }

    /**
     * Set standard properties for a {@code JButton} without an icon. First
     * calls the decorate method of the {@code JComponentDecorator} then adds
     * {@code JButton} specific properties.
     * 
     * @param comp
     *            {@code JButton} on which the properties are set
     * @param actListener
     *            {@code ActionListener} for the {@code JButton}
     * @param text
     *            text on the {@code JButton}
     * @see JButton
     * @see JComponentDecorator
     * @see ActionListener
     */
    public void decorateButton(JButton comp, ActionListener actListener, String text) {
        componentDecorator.decorate(comp);
        comp.setText(text);
        comp.addActionListener(actListener);
        
        //Remove the Space keyStroke from the standard InputMap so custom key - inputs can work
        comp.getInputMap().put(KeyStroke.getKeyStroke("SPACE"), "none");
        comp.setFocusPainted(false);
    }

    /**
     * Set standard properties for a {@code JButton} with a border. First calls
     * the decorate method of the {@code JComponentDecorator} then adds
     * {@code JButton} specific properties.
     * 
     * @param comp
     *            {@code JButton} on which the properties are set
     * @param actListener
     *            {@code ActionListener} for the {@code JButton}
     * @param text
     *            text on the {@code JButton}
     * @param BorderColor
     *            Color for the border of the {@code JButton}
     * @see JButton
     * @see JComponentDecorator
     * @see ActionListener
     */
    public void decorateBorderButton(JButton comp, ActionListener actListener, String text, Color BorderColor) {
        decorateButton(comp, actListener, text);
        comp.setBorder(new CompoundBorder(new LineBorder(BorderColor), new EmptyBorder(3, 3, 3, 3)));
    }
}
