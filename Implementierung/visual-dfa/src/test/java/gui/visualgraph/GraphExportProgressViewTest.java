package gui.visualgraph;

import gui.Colors;
import gui.visualgraph.GraphExportProgressView;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.junit.Assert.assertEquals;

public class GraphExportProgressViewTest {
    private GraphExportProgressView view;
    private JPanel panel;
    private Component[] components;
    private JLabel viewLabel;
    private JProgressBar viewProgressBar;
    private String testPath = "test/path";

    @Before
    public void initProgressView() {
        view = new GraphExportProgressView(testPath);
        panel = (JPanel) view.getRootPane().getContentPane().getComponents()[0];
        components = panel.getComponents();
        viewLabel = (JLabel) components[0];
        viewProgressBar = (JProgressBar) components[1];
    }

    @Test
    public void shouldHaveCorrectInitialState() {
        assertEquals(true, view.isVisible());

        assertEquals(2, components.length);
        assertEquals("Exporting to " + testPath, viewLabel.getText());

        assertEquals(0, viewProgressBar.getValue());

        assertEquals(Colors.LIGHT_TEXT.getColor(), viewLabel.getForeground());
        assertEquals(Colors.BACKGROUND.getColor(), panel.getBackground());
    }

    @Test
    public void shouldHandleCallbackMethods() throws InterruptedException {
        view.setMaxStep(10);

        assertEquals(0, viewProgressBar.getValue());
        assertEquals(10, viewProgressBar.getMaximum());

        view.setExportStep(5);
        view.onImageExported(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB));

        assertEquals(5, viewProgressBar.getValue());
        assertEquals(10, viewProgressBar.getMaximum());

        view.done();

        assertEquals(10, viewProgressBar.getValue());
        assertEquals(10, viewProgressBar.getMaximum());
        assertEquals("Exporting to " + testPath + " – Done.", viewLabel.getText());

        Thread.sleep(1500);

        assertEquals(false, view.isVisible());
    }
}
