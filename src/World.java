
/**
 * PRA2003: Emerald Mine
 * @author Manon Chevalier i6138957 based on code from Cameron Browne (based on code from previous PRA2003 years).
 * Purpose: create the basic elements of the Emerald Mine game using object oriented programming and a GUI
 * The goal for the player is to collect a given amount of emeralds (or diamonds, worth 3 emeralds),
 * without leaving the map or getting killed by the alien or by a heavy object falling.
 * The player can move up, down, left or right by one by pressing the arrow keys.
 * The alien can move up, down left or right by one. If he collects an emerald, it is lost.
 * World.java: Creation of World Class and necessary methods
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

//-----------------------------------------------------------------------------

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

	
    private int rows, cols, emeraldsRemaining, initialEmeralds, emeraldsStolen, goalEmeralds;
    private WorldObject[][] world;
    private final Random rng = new Random();

    private RC playerAt = null;           // player location
    private RC alienAt  = null;           // alien location
    private RC supported = null;          // player supports this cell
    
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

   /**
    * Constructor to load from file.
    * @throws BadFileFormatException
    * @throws IOException
    */
    public World(final String fileName) throws BadFileFormatException, IOException
    {
    	loadFromFile(fileName);
    }

    //-------------------------------------------------------------------------

    /**
     * Getters.
     */

    public int getRows(){
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public WorldObject[][] getWorld() {
        return world;
    }

    public int getEmeraldsRemaining() {
        return emeraldsRemaining;
    }

    public int getStatus() {
        return status;
    }

    public int getEmeraldsStolen() {
        return emeraldsStolen;
    }


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
		// keep track of how many emeralds were initially placed and are initially required
        goalEmeralds = emeraldsRemaining;
        initialEmeralds = emeraldsRemaining+1;


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
     * Constructor load from file.
     * @param filename
     * @throws BadFileFormatException
     * @throws IOException
     */
     private void loadFromFile(final String filename) throws BadFileFormatException, IOException 
     {
        final BufferedReader in = 
        		new BufferedReader(new InputStreamReader(new FileInputStream(filename)));

        // Assume that first three lines are properly formatted (as per Assignment 3).
        this.rows = Integer.parseInt(in.readLine());
        this.cols = Integer.parseInt(in.readLine());
        this.emeraldsRemaining = Integer.parseInt(in.readLine());

        world = new WorldObject[rows][cols];

        int row = 0;
        int emeraldsInWorld = 0;

        String line = in.readLine();
        while (line != null) 
        {
        	// Check whether we are still within world dimensions
            if (row >= rows)
                throw new BadFileFormatException("Too many rows", row, -1);

            if (line.length() < cols)
                throw new BadFileFormatException("Too few columns (" + line.length() + ")", row, -1);
            
            if (line.length() > cols)
                throw new BadFileFormatException("Too many columns (" + line.length() + ")", row, -1);

            // Parse this line of text
            for (int col = 0; col < cols; col++) 
            {
                // Create the object at this grid cell
                final char ch = line.charAt(col);
                world[row][col] = WorldObject.createFromChar(ch);
                if (world[row][col] == null)
                    throw new BadFileFormatException("Invalid character: " + ch, row, col);
                
                
                // Everything good so far, update the emerald count (if any)
                emeraldsInWorld += world[row][col].getEmeraldValue();
            }
            line = in.readLine();
            row++;
        }

        // Do a final check of the world dimensions
        if (row != rows)
            throw new BadFileFormatException("Not enough rows (" + rows + ")", -1, -1);

        if (emeraldsInWorld < emeraldsRemaining)
            throw new BadFileFormatException("Not enough emeralds in the world: " + emeraldsInWorld, -1, -1);

        // keep these two values (they will never change)
        initialEmeralds = emeraldsInWorld;
        goalEmeralds = emeraldsRemaining;
        in.close();
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
     *  Not used in Assignment 4.
     * public char getMove()
    {
        System.out.print("Where to? ");
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        
        if (line.length() == 0)
            return ' ';
        
        return line.charAt(0);
    }
    */

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
    	{
    		final char chAlien = world[alienAt.row][alienAt.col].getMove();
    		//System.out.println("Alien steps '" + chAlien + "'.");
    		alienMove(chAlien);
    	}

    	// if alien stole too many emeralds to win, player loses.
    	if (initialEmeralds - emeraldsStolen < emeraldsRemaining )
        {
            status = Loss;
        }

    	// Apply gravity
        if 
        (
        	applyGravity
        	(
        		supported == null ? -1 : supported.row, 
        		supported == null ? -1 : supported.col
       		)
        )
        	status = Loss;  // player killed by something dropping on them
        
    	return status;
    }
   
    //-------------------------------------------------------------------------

    /**
     * Make a player move in the specified direction.
     */
    public void playerMove(final char ch) 
    {
    	supported = null;
    	
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
        	
        	// Check whether player supports an object with their head
            final int rowAbove = playerAt.row - 1;
            if (inBounds(rowAbove, playerAt.col) && world[rowAbove][playerAt.col].hasMass()) 
            {
            	// Player supports the object above
                supported = new RC(rowAbove, playerAt.col);
            }
    	}
        else
        {
        	// Can't step into an inedible object
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
    	  
        if (!inBounds(alienNext.row, alienNext.col)) 
        {
        	world[alienAt.row][alienAt.col] = new Space();
        	alienAt = null;  // alien exits the game
        }
        else if (world[alienNext.row][alienNext.col].isEdible())
        {
            // Alien can move there
        	if (world[alienNext.row][alienNext.col].isPlayer()) 
        		status = Loss;  // alien will step onto player

            // counts the emeralds the alien steals
            emeraldsStolen += world[alienAt.row][alienAt.col].getEmeraldValue();
        	world[alienAt.row][alienAt.col] = new Space();
        	alienAt.set(alienNext.row, alienNext.col);
        	world[alienAt.row][alienAt.col] = new Alien();
        }
    }

    //-------------------------------------------------------------------------

    /**
     * @return Whether an object with mass falls on the player.
     */
    public boolean applyGravity(int exceptRow, int exceptCol) 
    {
        boolean fellOnPlayer = false;  // whether an object with mass fell on the player

        // Note: Gravity should really be a loop, so that objects fall as far as
        // they need to each turn. But this was not specified in Assignment 3.
        
        // Apply gravity from the bottom up! 
        // So if there are two rocks, one on top of the other, they both fall.
        // Start at rows-2 because gravity does not apply to objects on bottom row.
        for (int row = rows-2; row >= 0; row--) 
            for (int col = 0; col < cols; col++) 
            {
                if (row == exceptRow && col == exceptCol) 
                    continue;  // do not apply gravity here
               
                int rowBelow = row+1;  // because rows are indexed bottom up

                if (world[row][col].hasMass() && world[rowBelow][col].isVulnerable()) 
                {
                	// Object with mass drops a row
                    if (world[rowBelow][col].isPlayer())
                    {
                    	fellOnPlayer = true;  // player squashed
                    }
                    else if (world[rowBelow][col].isMonster())
                    {
                    	System.out.println("Alien squashed!");
                    	alienAt = null;  // remove the alien from the game
                    }
                    
                    // Move this object down a row
                    world[rowBelow][col] = world[row][col];
                    world[row][col] = new Space();    
                }
            }
        return fellOnPlayer;
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

    /**
     * @returns the string representation of the world-
     */
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
