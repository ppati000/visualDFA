package gui;

import java.awt.GridBagConstraints;
import java.awt.Insets;

/**
 * Utility class to create GridBagConstraints with standard properties set.
 * 
 * @author Michael
 *
 * @see GridBagConstraints
 */
public class GridBagConstraintFactory {
    
    private GridBagConstraintFactory(){
        
    }

    /**
     * Creates standard GridBagConstraints with a given position and size.
     * 
     * @param x The horizontal placement in the grid.
     * @param y The vertical placement in the grid.
     * @param width The number of cells the component should take up horizontally.
     * @param height The number of cells the component should take up vertically.
     * @return GridBagConstraints with standard properties.
     * 
     * @see GridBagConstraints
     */
    public static GridBagConstraints getStandardGridBagConstraints(int x, int y, int width, int height) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.gridheight = height;
        return gbc;
    }
}
