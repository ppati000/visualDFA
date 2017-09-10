package gui.visualgraph;

import java.awt.image.BufferedImage;

public interface GraphExportCallback {
    void setMaxStep(int step);

    void setExportStep(int step);

    void onImageExported(BufferedImage image);

    void done();
}
