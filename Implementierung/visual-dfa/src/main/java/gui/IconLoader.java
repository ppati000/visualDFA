package gui;

import java.awt.Image;
import java.net.URL;

import javax.swing.ImageIcon;

/**
 * Utility class for loading Icons and scaling them.
 * @author Michael
 *
 * @see ImageIcon
 */

public class IconLoader {
    
    private IconLoader() {
        
    }
    
    /**
     * Load an icon from the resource path and scale it as needed.
     * Returns null if resource is not found.
     * 
     * @param path The path to the resource for the icon.
     * @param scale The multiplier to scale the icon.
     * @return A scaled icon.
     * @see ImageIcon
     */
    static ImageIcon loadIcon(String path, double scale) {
        // TODO throw or try catch
        URL url = ClassLoader.getSystemResource(path);
        if (url == null) {
            return null;
        } else {
            ImageIcon icon = new ImageIcon(url);
            Image img = icon.getImage();
            img = img.getScaledInstance((int) (img.getHeight(null) * scale),
                    (int) (img.getWidth(null) * scale), Image.SCALE_SMOOTH);
            icon = new ImageIcon(img);
            return icon;
        }

    }
}
