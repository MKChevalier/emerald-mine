/**
 * PRA2003 Assignment 4 sample solution.
 * @author cambolbro (based on code from previous years).
 *
 * Note: if you use this code, put your name and ID in this header!
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

//-----------------------------------------------------------------------------

/**
 * Main GUI panel.
 */
class EmeraldMinePanel extends JPanel 
{
    private JLabel[][] labels;
    private int rows, cols;

    /**
     * Constructor.
     */
    public EmeraldMinePanel(final int rows, final int cols) 
    {
        this.rows = rows;
        this.cols = cols;

        // make a 3-by-3 grid layout
        GridLayout layout = new GridLayout(rows, cols, 0, 0);
        setLayout(layout);

        // create a label for each cell
        labels = new JLabel[rows][cols];
        for (int row = 0; row < rows; row++)
            for (int col = 0; col < cols; col++) 
            {
                labels[row][col] = new JLabel();  // create the label
                add(labels[row][col]);  // add to grid: gridLayout handles layout
            }
    }

    /**
     * Redraw the screen by updating the labels.
     */
    public void redrawWorld(final World world) 
    {
        //labels[row][col].setIcon(new ImageIcon("doge-" + player + ".png"));
       for (int row = 0; row < rows; row++)
            for (int col = 0; col < cols; col++) 
                 labels[row][col].setIcon(new ImageIcon(world.objectAt(row, col).getIconFile()));
         repaint();
    }
}

/**
 * The GUI class!
 */
class GUI 
{
    private JFrame frame;
    private EmeraldMinePanel panel;
    private World world;

    /**
     * Constructor.
     */
    public GUI(final World world) 
    {
        this.world = world;
    }

    /**
     * Initialise the GUI. 
     */
    public void init() 
    {
        frame = new JFrame("Emerald Mine GUI pefect edition!");

        // Each cell is 32x32
        final int imageSize = 32;
        
        // Add 5 pixels for outer edge and 10 pixels for bar at top
        int pixWidth  = world.rows() * imageSize + 5;
        int pixHeight = world.cols() * imageSize + 10;

        frame.setPreferredSize(new Dimension(pixWidth, pixHeight));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create the panel
        panel = new EmeraldMinePanel(world.rows(), world.cols());
        panel.setSize(pixWidth, pixHeight);

        // Add the panel to the frame
        frame.add(panel);
        panel.redrawWorld(world);

        // Add the key event trapper to the frame, not panel
        frame.addKeyListener(new KeyEventTrapper(panel, world));

        frame.pack();
        frame.setVisible(true);
    }

}
