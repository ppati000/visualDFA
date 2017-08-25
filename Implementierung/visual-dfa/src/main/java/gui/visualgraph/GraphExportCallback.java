package gui.visualgraph;

import java.awt.image.BufferedImage;
import java.util.List;

public interface GraphExportCallback {
    void setMaxStep(int step);

    void setExportStep(int step);

    void onImageExported(BufferedImage image);

    void done();
}
