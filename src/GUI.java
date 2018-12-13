import javax.swing.*;
import java.awt.*;

/**
 * PRA2003: Emerald Mine
 * @author Manon Chevalier i6138957 based on code from Cameron Browne (based on code from previous PRA2003 years).
 * Purpose: create the basic elements of the Emerald Mine game using object oriented programming and a GUI
 * The goal for the player is to collect a given amount of emeralds (or diamonds, worth 3 emeralds),
 * without leaving the map or getting killed by the alien or by a heavy object falling.
 * The player can move up, down, left or right by one by pressing the arrow keys.
 * The alien can move up, down left or right by one. If he collects an emerald, it is lost.
 * GUI.java: creates a GUI and plays the game. (has main method)
 *
 * NB: The game can be played with the GUI using the initial world constructor (using input).
 * However it does not work when loaded from a file (game crashes because of a problem with the RCs)
 * Also, there is sometimes errors in the gravity.
 * I did not correct these problems as they were already present in the Solution of Assignment 3, found online.
 */

/**
 * Main GUI class.
 */
class GUI
{

    private World world;
    private Score score;

    /**
     * Constructor.
     */
    public GUI(World world, Score score){
        this.world = world;
        this.score = score;
    }

    /**
     * Initialise the GUI.
     */
    public void init()
    {

        JFrame frame;
        JLabel rulesLabel;
        WorldPanel worldPanel;
        ScorePanel scorePanel;

        // create new frame with a title and minimum 640x640 dimensions
        frame = new JFrame("Welcome to Emerald Mine, PRA 2003 edition.");
        frame.setPreferredSize(new Dimension(675, 785));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create JLabel for the rules
        rulesLabel = new JLabel("<html>Here are the rules: <br>You are the player. Your aim is to collect " + world.getEmeraldsRemaining()+
                " emeralds without leaving the map or getting killed by the alien. <br>" +
                "You can also collect diamonds, each diamond is worth three emeralds. <br>" +
                "The alien can move up, down left or right by one and he can steal emeralds. <br>" +
                "You can move up, down, left or right by one by pressing the arrow keys. </html>");


        // Create the panel for the board and wrap it to fit inside the overall BorderLayout
        worldPanel = new WorldPanel(world);

        // Create a score board
        scorePanel = new ScorePanel(score);

        // Add the 3 components (rulesLabel, worldPanel, scorePanel) to the frame in a Border layout
        frame.add(rulesLabel, BorderLayout.PAGE_START);
        frame.add(worldPanel, BorderLayout.CENTER);
        frame.add(scorePanel, BorderLayout.PAGE_END);

        // Add the mouse event trapper to the frame
        frame.addKeyListener(new KeyEventTrapper(world, worldPanel, score, scorePanel));
        frame.pack();
        frame.setVisible(true);

     }

}