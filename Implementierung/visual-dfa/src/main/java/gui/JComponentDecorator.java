package gui;

import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.border.EmptyBorder;

/**
 * Utility class to set standard properties for {@code JComponents}.
 * 
 * @author Michael
 *
 */
public class JComponentDecorator {

    /**
     * Set standard properties for the given {@code jComponent}.
     * 
     * @param comp
     *            set properties on this component
     * @see JComponent
     */
    public void decorate(JComponent comp) {
        comp.setBackground(Colors.BACKGROUND.getColor());
        comp.setBorder(new EmptyBorder(5, 5, 5, 5));
        comp.setForeground(Colors.WHITE_BACKGROUND.getColor());
        comp.setFont(new Font("Trebuchet MS", Font.BOLD, 16));
        comp.setOpaque(true);
    }

}
