import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

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


//----------------------------------------------------------------------------------------------------------------------

/**
 * Main GUI class.
 */
class GUI
{
    /**
     * Default constructor.
     */
    public GUI(){}

    /**
     * Initialise the GUI.
     */
    public void init()
    {
        final boolean READWORLDFROMFILE = false;

        JFrame frame;
        JLabel rules;
        JPanel wrapper;
        EMPanel panel;
        ScorePanel score;

        World world;


        if (READWORLDFROMFILE) {

            // Create the EM board
            try {
                System.out.println("Working Directory = " +
                        System.getProperty("user.dir"));
                String fileName = "./WorldDefinitions/Board2.txt";
                System.out.println("Testing file: " + fileName + "...");
                world = new World(fileName);  // !!!!!!! ERROR: This constructor fails at initializing the world properly => crash the game during play
                System.out.println("Successfully loaded world.");
                // catch possible exceptions from the creation of a world from a file
            } catch (BadFileFormatException e) {
                world = null;
                System.out.println(e);
            } catch (IOException e) {
                world = null;
                e.printStackTrace();
            }
        }
        else {
            world = new World(20, 20, 50);
        }

        if (world!=null)
        {
            // create new frame with a title and minimum 640x640 dimensions
            frame = new JFrame("Welcome to Emerald Mine, PRA 2003 edition.");
            frame.setPreferredSize(new Dimension(650, 710));
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Create JLabel for the rules
            rules = new JLabel("<html>Here are the rules: <br>You are the player. Your aim is to collect " + world.getEmeraldsRemaining()+
                    " emeralds without leaving the map or getting killed by the alien. <br>" +
                    "You can also collect diamonds, each diamond is worth three emeralds. <br>" +
                    "The alien can move up, down left or right by one and he can steal emeralds. <br>" +
                    "You can move up, down, left or right by one by pressing the arrow keys. </html>");


            // Create the panel for the board and wrap it to fit inside the overall BorderLayout
            panel = new EMPanel(world);
            panel.setSize(645, 650);
            wrapper = new JPanel();
            wrapper.add(panel);

            // Create a score board
            score = new ScorePanel(world);

            // Add the 3 components (rules, wrapped panel, score) to the frame in a Border layout
            frame.add(rules, BorderLayout.PAGE_START);
            frame.add(wrapper, BorderLayout.CENTER);
            frame.add(score, BorderLayout.PAGE_END);

            frame.add(panel);


            // Add the mouse event trapper to the frame
            frame.addKeyListener(new KeyEventTrapper(panel, score, world));
            // frame.addKeyListener(new KeyEventTrapper(panel, world));
            frame.pack();
            frame.setVisible(true);

        }


    }


//----------------------------------------------------------------------------------------------------------------------

    /**
     * Main entry point.
     */
    public static void main(final String[] args)
    {
        GUI gui = new GUI();
        gui.init();
    }


//----------------------------------------------------------------------------------------------------------------------

/**
 * Key Listener.
 */
class KeyEventTrapper implements KeyListener
{
    // variables
    private EMPanel panel;
    private ScorePanel score;
    private World world;
    private int outcome = 0;

    //constructor
    KeyEventTrapper(EMPanel panel, ScorePanel score, World world){
        this.panel = panel;
        this.score = score;
        this.world = world;
    }

    KeyEventTrapper(EMPanel panel, World world){
        this.panel = panel;
        this.world = world;
    }

        //methods
    @Override
    public void keyPressed(KeyEvent e) {
        char ch;
        // read the key pressed and transform it into a character
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                ch = 'u';
                break;
            case KeyEvent.VK_RIGHT:
                ch = 'r';
                break;
            case KeyEvent.VK_DOWN:
                ch = 'd';
                break;
            case KeyEvent.VK_LEFT:
                ch = 'l';
                break;
            default:
                ch = 'z';
                System.out.println("Invalid key");
                break;
        }
        if (outcome == 0) {
            if (world.validMove(ch)) {
                outcome = world.applyMove(ch);
                panel.redrawWorld();
                score.redrawScore();
            }

        /* switch (outcome) {
            case 1:
                System.out.println("You win!");
                break;
            case 2:
                System.out.println("Bad luck, you lose.");
                break;
            default:
                System.out.println("Keep playing.");
                break;
        }*/
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}


//-----------------------------------------------------------------------------

/**
 * Custom panel for EM game.
 */
class EMPanel extends JPanel
{
    private final JLabel[][] labels;
    private World world;
    private int rows, cols;

    /**
     * Constructor.
     */
    public EMPanel(World world)
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

    public void redrawWorld()
    {
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
            {
                labels[r][c].setIcon(world.getWorld()[r][c].getImageIcon());
                repaint();
            }
    }

}

/**
 * Custom scoreboard for out EM.
 */
class ScorePanel extends JPanel
{
    private final JLabel[] scores;
    private World world;
    /**
     * Constructor.
     */
    public ScorePanel(World world)
    {
        this.world = world;

        final GridLayout layout2 = new GridLayout(1, 3, 0, 0);
        setLayout(layout2);

        scores = new JLabel[3];

        // Create the 3 text areas
        scores[0] = new JLabel();
        scores[1] = new JLabel();
        scores[2] = new JLabel();

        // Add the 3 text areas
        add(scores[0]);
        add(scores[1]);
        add(scores[2]);

        redrawScore();
    }


    public void redrawScore()
    {
        String statusString;
        int emeraldsRemaining;
        int emeraldsStolen;
        int outcome;

        // Get the necessary data to put in the score panel
        outcome = world.getStatus();
        switch (outcome) {
            case 0:
                statusString = "Keep playing!";
                break;
            case 1:
                statusString = "You Win!";
                break;
            case 2:
                statusString = "Bad luck, you loose!";
                break;
            default:
                statusString = "Unexpected outcome...";
        }
        emeraldsRemaining = world.getEmeraldsRemaining();
        emeraldsStolen = world.getEmeraldsStolen();

        scores[0].setText("Status: " + statusString);
        scores[1].setText("Emeralds remaining to win: " + emeraldsRemaining);
        scores[2].setText("Emeralds stolen by alien: " + emeraldsStolen);
        // repaint();

    }

}


}