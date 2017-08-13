package gui;

import java.awt.Color;

/**
 * Utility class with enums for colors for the UI.
 * 
 * @author Michael
 *
 */
public enum Colors {
    BACKGROUND(22, 50, 66), LIGHT_BACKGROUND(188, 230, 254), WHITE_BACKGROUND(251, 253, 255), DARK_TEXT(17, 37,
            48), LIGHT_TEXT(251, 253, 255), GREEN_BACKGROUND(114, 189, 44), GREY_BORDER(153, 204, 204);

    private Color color;

    private Colors(int r, int g, int b) {
        color = new Color(r, g, b);
    }

    /**
     * Return the rgb Color belonging to the enum.
     * 
     * @return The Color belonging to the enum.
     * @see Color
     */
    public Color getColor() {
        return color;
    }
}
