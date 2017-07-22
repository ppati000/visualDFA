package gui;

import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;

/**
 * A utility class to set standard properties for JSliders.
 * 
 * @author Michael
 * 
 * @see JSlider
 */
public class JSliderDecorator {

    private JComponentDecorator componentDecorator;
    
    /**
     * Pass a JComponentDecorator to set standard JComponent properties.
     * 
     * @param componentDecorator
     * @see JComponentDecorator
     * @see JComponent
     */
    public JSliderDecorator(JComponentDecorator componentDecorator) {
        this.componentDecorator = componentDecorator;
    }
    
    /**
     * Set standard properties for a JSlider.
     * First calls the decorate method on the JComponentDecorator, then adds JSlider specific properties.
     * 
     * @param comp JSlider on which the properties are set.
     * @param maxValue The maximum value for the JSlider.
     * @param minorTickspacing 
     * @param majorTickspacing
     * @param l The ChangeListener for the JSlider.
     * 
     * @see JSlider
     * @see JComponentDecorator
     * @see ChangeListener
     */
    public void decorateSlider(JSlider comp, int maxValue, int minorTickspacing, int majorTickspacing, ChangeListener l) {
        componentDecorator.decorate(comp);
        comp.setMaximum(maxValue);
        comp.setMinimum(0);
        comp.setMajorTickSpacing(majorTickspacing);
        comp.setMinorTickSpacing(minorTickspacing);
        comp.addChangeListener(l);
    }
}
