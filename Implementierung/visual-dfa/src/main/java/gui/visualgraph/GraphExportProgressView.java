package gui.visualgraph;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import gui.Colors;
import gui.JComponentDecorator;
import gui.JLabelDecorator;

public class GraphExportProgressView extends JDialog implements GraphExportCallback {

    private static final long serialVersionUID = 1L;
    
    private JProgressBar progressBar;
    private JLabel label;

    public GraphExportProgressView(String path) {
        JLabelDecorator labelDecorator = new JLabelDecorator(new JComponentDecorator());

        setSize(500, 100);
        setMinimumSize(new Dimension(500, 100));
        setAlwaysOnTop(true);

        JPanel panel = new JPanel();
        panel.setSize(500, 100);
        panel.setBackground(Colors.BACKGROUND.getColor());
        panel.setLayout(new BorderLayout());

        progressBar = new JProgressBar();
        label = new JLabel();
        labelDecorator.decorateLabel(label, "Exporting to " + path);

        panel.add(label, BorderLayout.NORTH);
        panel.add(progressBar, BorderLayout.CENTER);
        panel.setVisible(true);
        add(panel);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void setExportStep(int step) {
        progressBar.setValue(step);
    }

    @Override
    public void onImageExported(BufferedImage image) {
        // Do nothing - implemented by subclass if necessary.
    }

    public void setMaxStep(int step) {
        progressBar.setMaximum(step);
    }

    public void done() {
        progressBar.setValue(progressBar.getMaximum());
        label.setText(label.getText() + " \u2013 Done.");

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            // Ignored.
        }

        dispose();
    }
}
