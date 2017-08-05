import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author Patrick Petrovic
 *
 *         Utility methods for testing.
 */
public class TestUtils {
    /**
     * Simple BufferedImage pixel-by-pixel comparison (based on https://stackoverflow.com/a/15305092)
     *
     * HACK: mxGraph yields slightly different images on different operating systems. Thus, a soft comparison using the
     * {@code delta} parameter is needed.
     *
     * Returns empty string if soft comparison is fulfilled, string containing reason if not.
     *
     * @param expected
     *         a BufferedImage
     * @param actual
     *         another BufferedImage
     * @param delta
     *         by how many points each RGB value is allowed to differ
     *
     * @return empty string iff image sizes are the same AND not more than {@code allowedWrongPixels} pixels differ more
     * than {@code delta} in any RGBA value; else string containing reason
     */
    public static String bufferedImagesEqual(BufferedImage expected, BufferedImage actual, int delta, int allowedWrongPixels) {
        if (expected.getWidth() == actual.getWidth() && expected.getHeight() == actual.getHeight()) {
            for (int x = 0; x < expected.getWidth(); x++) {
                for (int y = 0; y < expected.getHeight(); y++) {
                    Color expectedColor = new Color(expected.getRGB(x, y));
                    Color actualColor = new Color(actual.getRGB(x, y));

                    if (Math.abs(expectedColor.getAlpha() - actualColor.getAlpha()) > delta) {
                        allowedWrongPixels -= 1;

                        if (allowedWrongPixels < 0) {
                            return "(" + x + ", " + y + "): Alpha values are too far apart: Expected " + expectedColor.getAlpha() + ", got " + actualColor.getAlpha();
                        }
                    } else if (Math.abs(expectedColor.getBlue() - actualColor.getBlue()) > delta) {
                        allowedWrongPixels -= 1;

                        if (allowedWrongPixels < 0) {
                            return "(" + x + ", " + y + "): Blue values are too far apart: Expected " + expectedColor.getBlue() + ", got " + actualColor.getBlue();
                        }
                    } else if (Math.abs(expectedColor.getRed() - actualColor.getRed()) > delta) {
                        allowedWrongPixels -= 1;

                        if (allowedWrongPixels < 0) {
                            return "(" + x + ", " + y + "): Red values are too far apart: Expected " + expectedColor.getRed() + ", got " + actualColor.getRed();
                        }
                    } else if (Math.abs(expectedColor.getGreen() - actualColor.getGreen()) > delta) {
                        allowedWrongPixels -= 1;

                        if (allowedWrongPixels < 0) {
                            return "(" + x + ", " + y + "): Green values are too far apart: Expected " + expectedColor.getGreen() + ", got " + actualColor.getGreen();
                        }
                    }
                }
            }
        } else {
            return "Image sizes don't match.";
        }
        return "";
    }
}
