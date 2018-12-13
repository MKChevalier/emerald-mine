/**
 * PRA2003: Assignment 2 sample solution.
 * Author: Cameron Browne (based on code by Marc Lanctot).
 *
 * Note: if you use this code, add your name and ID to this header!
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * World class for Emerald Mine game.
 */
class World
{
    /**
     * Simple record of a [row,col] coordinate.
     */
    class RC
    {
        int row = -1;
        int col = -1;

        public RC(final int row, final int col)
        {
            this.row = row;
            this.col = col;
        }

        public void set(final int r, final int c)
        {
            row = r;
            col = c;
        }

        public boolean matches(final RC other)
        {
            return row == other.row && col == other.col;
        }
    }

    private int rows, cols, emeraldsRemaining;
    private WorldObject[][] world;
    private final Random rng = new Random();

    private RC playerAt = null;           // player location
    private RC alienAt  = null;           // alien location

    public static final int Playing = 0;  // game in progress
    public static final int Win     = 1;  // player win
    public static final int Loss    = 2;  // player loss
    private int status;

    public static final int Off = -1;     // off-board cell

    //-------------------------------------------------------------------------

    /**
     * Constructor.
     */
    public World(int rows, int cols, int emeralds)
    {
        this.rows = rows;
        this.cols = cols;
        this.emeraldsRemaining = emeralds;
        init();
    }

    //-------------------------------------------------------------------------

    /**
     * World creation.
     */
    private void init()
    {
        // Create objects, shuffle and add to world
        // Including an optimisation by Felix Quinque: add elements then shuffle
        world = new WorldObject[rows][cols];
        final List<WorldObject> objects = new ArrayList<WorldObject>();

        // Add the player
        objects.add(new Player());

        // Add the alien
        objects.add(new Alien());

        // Add the emeralds
        for (int e = 0; e < emeraldsRemaining+1; e++)
            objects.add(new Emerald());

        // Add the diamonds
        final int numDiamonds = 3;
        emeraldsRemaining += 3 * numDiamonds;
        for (int d = 0; d < numDiamonds; d++)
            objects.add(new Diamond());

        // Add some rocks
        final int numRocks = 4 + rng.nextInt(3);
        for (int r = 0; r < numRocks; r++)
            objects.add(new Rock());

        // Fill the rest with dirt
        while (objects.size() < rows * cols)
            objects.add(new Dirt());

        // Shuffle objects and put in world array
        Collections.shuffle(objects);
        for (int n = 0; n < objects.size(); n++)
        {
            final int row = n / cols;
            final int col = n % cols;

            world[row][col] = objects.get(n);

            if (world[row][col].isPlayer())
                playerAt = new RC(row, col);
            else if (world[row][col].isMonster())
                alienAt = new RC(row, col);
        }

        status = Playing;  // game is now active
    }

    //-------------------------------------------------------------------------

    /**
     * @return Whether coordinate [row,col] is in bounds.
     */
    public boolean inBounds(final int row, final int col)
    {
        return (row >= 0 && row < rows && col >= 0 && col < cols);
    }

    //-------------------------------------------------------------------------

    /**
     * @return Character entered by player. 
     */
    public char getMove()
    {
        System.out.print("Where to? ");
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();

        if (line.length() == 0)
            return ' ';

        return line.charAt(0);
    }

    /**
     * @return Whether char input is a valid move.
     */
    public boolean validMove(final char ch)
    {
        return ch == 'u' || ch == 'd' || ch == 'l' || ch == 'r';
    }

    //-------------------------------------------------------------------------

    /**
     * @return Result of suggested move: Playing / Win / Loss.
     */
    public int applyMove(final char ch)
    {
        playerMove(ch);
        if (alienAt != null)
            alienMove(world[alienAt.row][alienAt.col].getMove());
        return status;
    }

    //-------------------------------------------------------------------------

    /**
     * Make a player move in the specified direction.
     */
    public void playerMove(final char ch)
    {
        final RC playerNext = stepTo(playerAt, ch);
        if (!inBounds(playerNext.row, playerNext.col) || world[playerNext.row][playerNext.col].isMonster())
        {
            // Player dies
            status = Loss;
            world[playerAt.row][playerAt.col] = new Space();  // remove the player from this world
            return;
        }

        if (world[playerNext.row][playerNext.col].isEdible())
        {
            // Stepping into an edible cell, decrease by its point value
            emeraldsRemaining -= world[playerNext.row][playerNext.col].getEmeraldValue();

            // Do not go below 0
            if (emeraldsRemaining <= 0)
                emeraldsRemaining = 0;

            // Check for a win
            if (emeraldsRemaining == 0)
                status = Win;

            // Move the player
            world[playerAt.row][playerAt.col] = new Space();
            playerAt.set(playerNext.row, playerNext.col);
            world[playerAt.row][playerAt.col] = new Player();
        }
        else
        {
            // Can't stepe into an inedible object
            System.out.println("There is an obstacle in the way.");
        }
    }

    //-------------------------------------------------------------------------

    /**
     * Make a random alien move.
     */
    public void alienMove(final char ch)
    {
        if (alienAt == null)
            return;  // alien is off the board and no longer active

        final RC alienNext = stepTo(alienAt, ch);
        world[alienAt.row][alienAt.col] = new Space();

        if (!inBounds(alienNext.row, alienNext.col))
        {
            alienAt = null;  // alien exits the game
        }
        else if (world[alienNext.row][alienNext.col].isEdible())
        {
            // Alien can move there
            if (world[alienNext.row][alienNext.col].isPlayer())
                status = Loss;  // alien will step onto player

            alienAt.set(alienNext.row, alienNext.col);
            world[alienAt.row][alienAt.col] = new Alien();
        }
    }

    //-------------------------------------------------------------------------

    /**
     * @return Coordinate of step in the specified direction.
     */
    public RC stepTo(final RC from, final char dirn)
    {
        int dRow = 0;
        int dCol = 0;
        switch (dirn)
        {
            case 'u': dRow--; break;
            case 'd': dRow++; break;
            case 'l': dCol--; break;
            case 'r': dCol++; break;
            default: System.out.println("** World.stepTo(): Unexpected dirn '" + dirn + "'.");
        }
        return new RC(from.row+dRow, from.col+dCol);
    }

    //-------------------------------------------------------------------------

    @Override
    public String toString()
    {
        String str = "Emeralds remaining: " + emeraldsRemaining + "\n";
        for (int r = 0; r < rows; r++)
        {
            for (int c = 0; c < cols; c++)
                str += world[r][c];
            str += "\n";
        }
        return str;
    }
}