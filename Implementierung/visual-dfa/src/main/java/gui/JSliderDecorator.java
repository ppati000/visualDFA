package gui;

import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;

/**
 * A utility class to set standard properties for {@code JSliders}.
 * 
 * @author Michael
 * 
 * @see JSlider
 */
public class JSliderDecorator {

    private JComponentDecorator componentDecorator;

    /**
     * Pass a {@code JComponentDecorator} to set standard {@code JComponent}
     * properties.
     * 
     * @param componentDecorator
     * @see JComponentDecorator
     * @see JComponent
     */
    public JSliderDecorator(JComponentDecorator componentDecorator) {
        this.componentDecorator = componentDecorator;
    }

    /**
     * Set standard properties for a {@code JSlider}. First calls the decorate
     * method on the {@code JComponentDecorator}, then adds {@code JSlider}
     * specific properties.
     * 
     * @param comp
     *            {@code JSlider} on which the properties are set
     * @param maxValue
     *            the maximum value for the {@code JSlider}
     * @param minorTickspacing
     * @param majorTickspacing
     * @param l
     *            the {@code ChangeListener} for the {@code JSlider}
     * 
     * @see JSlider
     * @see JComponentDecorator
     * @see ChangeListener
     */
    public void decorateSlider(JSlider comp, int maxValue, int minorTickspacing, int majorTickspacing,
            ChangeListener l) {
        componentDecorator.decorate(comp);
        comp.setMaximum(maxValue);
        comp.setMinimum(0);
        comp.setMajorTickSpacing(majorTickspacing);
        comp.setMinorTickSpacing(minorTickspacing);
        comp.addChangeListener(l);
    }
}
