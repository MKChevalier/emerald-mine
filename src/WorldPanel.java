import javax.swing.*;
import java.awt.*;

/**
 * Custom panel for EM game.
 */
class WorldPanel extends JPanel
{
    private final JLabel[][] labels;
    private World world;
    private int rows, cols;

    /**
     * Constructor.
     */
    public WorldPanel(World world)
    {
        this.world = world;
        rows = world.getRows();
        cols = world.getCols();

        // Make a 3-by-3 grid layout
        final GridLayout layout1 = new GridLayout(rows, cols, 0, 0);
        setLayout(layout1);

        // Create a label for each cell
        labels = new JLabel[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                labels[r][c] = new JLabel();
                // labels[r][c].setIcon(world.getWorld()[r][c].getImageIcon());

                // Add the label. the layout chooses where it is placed
                // the gridlayout places them in reading order
                add(labels[r][c]);
            }
        }
        redrawWorld();
    }

    public synchronized void redrawWorld()
    {
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
            {
                labels[r][c].setIcon(world.getWorld()[r][c].getImageIcon());
                repaint();
            }
    }

}