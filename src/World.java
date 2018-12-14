
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


//-----------------------------------------------------------------------------

/**
 * World class for Emerald Mine game.
 */
class World
{
	/**
	 * Simple record of a [row,col] coordinate.
	 */
	class Coordinates
	{
		int row = -1;
		int col = -1;
		
		public Coordinates(final int row, final int col)
		{
			this.row = row;
			this.col = col;
		}
		
		public void set(final int r, final int c)
		{
			row = r;
			col = c;
		}
		
		public boolean matches(final Coordinates other)
		{
			return row == other.row && col == other.col;
		}
	}

	
    private int rows, cols;
	// private int initialEmeralds;
	// int emeraldsRemaining;
	// int emeraldsStolen;

	private int remainingEmeraldsInWorld;
	private int remainingEmeraldsToWin;

    private WorldObject[][] world;
    private final Random rng = new Random();

    private Coordinates playerAt = null;           // player location
    private Coordinates alienAt  = null;           // alien location

    private Coordinates supported = null;          // player supports this cell

    private GameStatus status;
 	
	// public static final int Off = -1;     // off-board cell

    //-------------------------------------------------------------------------
    
    /**
     * Constructor to create a random world.
     */
    public World(int rows, int cols, int emeralds, int diamonds, GameDifficulty gameDifficulty)
    {
        this.rows = rows;
        this.cols = cols;

        // TODO: check that the number of emeralds & diamonds matches the world size (rows*cols)

        // This should decrease every time a diamond/emerald is collected or crushed
        this.remainingEmeraldsInWorld =  emeralds*Emerald.emeraldValue + diamonds * Diamond.emeraldValue;

        // This should decrease every time the player collects a diamond/emerald
        this.remainingEmeraldsToWin = remainingEmeraldsInWorld - gameDifficulty.getAcceptableEmeraldLosses();

        createRandomly(emeralds, diamonds);
    }

   /**
    * Constructor to load world definition from file.
    * @throws BadFileFormatException
    * @throws IOException
    */
    public World(final String fileName, GameDifficulty gameDifficulty) throws BadFileFormatException, IOException
    {
    	createFromFile(fileName, gameDifficulty);
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

    public GameStatus getStatus() {
        return status;
    }

    public int getRemainingEmeraldsInWorld() {
        return remainingEmeraldsInWorld;
    }

    public int getRemainingEmeraldsToWin() {
        return remainingEmeraldsToWin;
    }


    /**
     * World creation.
     */
    private void createRandomly(int emeralds, int diamonds)
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
		for (int e = 0; e < emeralds; e++)
			objects.add(new Emerald());
		
		// Add the diamonds
		for (int d = 0; d < diamonds; d++)
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

		// Register  Player & Alien coordinates
		for (int n = 0; n < objects.size(); n++)
		{
			final int row = n / cols;
			final int col = n % cols;
		
			world[row][col] = objects.get(n);
		
			if (world[row][col].isPlayer())
				playerAt = new Coordinates(row, col);
			else if (world[row][col].isMonster())
				alienAt = new Coordinates(row, col);
		}

        // game is now active
		status = GameStatus.PLAYING;
    }

    //-------------------------------------------------------------------------

    /**
     * Create world from a file.
     * @param filename
     * @throws BadFileFormatException
     * @throws IOException
     */
     private void createFromFile(final String filename, GameDifficulty gameDifficulty) throws BadFileFormatException, IOException
     {
         // TODO: need to fix createFromFile methods
     }
 /*    {
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
        in.close();
    }*/

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
     * @return Result of suggested move: Playing / Won / Lost.
     */
    public GameStatus applyMove(final char ch)
    {

        synchronized (this) {

            playerMove(ch);

            //    	if (alienAt != null)
            //    	{
            //    		final char chAlien = world[alienAt.row][alienAt.col].getMove();
            //    		alienMove(chAlien);
            //    	}

            // Apply gravity
            if (applyGravity()) {
                status = GameStatus.LOST;  // player killed by something dropping on them
            }

            // If there is less emerald value in World than what is still required to have a chance to win, game is over
            if (remainingEmeraldsInWorld < remainingEmeraldsToWin) {
                status = GameStatus.LOST;
            }
        }

    	return status;
    }


    //-------------------------------------------------------------------------

    /**
     * @return Result of suggested move: Playing / Win / Loss.
     */
    public GameStatus moveMonsters()
    {
        synchronized (this) {

            if (alienAt != null) {
                final char chAlien = world[alienAt.row][alienAt.col].getMove();
                alienMove(chAlien);
            }

            // Apply gravity
            if (applyGravity()) {
                status = GameStatus.LOST;  // player killed by something dropping on them
            }

            // If there is less emerald value in World than what is still required to have a chance to win, game is over
            if (remainingEmeraldsInWorld < remainingEmeraldsToWin) {
                status = GameStatus.LOST;
            }
        }

        return status;
    }

    //-------------------------------------------------------------------------

    /**
     * Make a player move in the specified direction.
     */
    public void playerMove(final char ch) 
    {
    	supported = null;
    	
    	final Coordinates playerNext = stepTo(playerAt, ch);

    	// Player tries to move outside the board
        if (!inBounds(playerNext.row, playerNext.col))
        {
        	// Player dies
        	// status = GameStatus.LOST;
        	// world[playerAt.row][playerAt.col] = new Space();  // remove the player from this world
            System.out.println("Player cannot leave the world");
            return;
        }

        // Player tries to move into monster
        if (world[playerNext.row][playerNext.col].isMonster())
        {
            // Player dies
            status = GameStatus.LOST;
            world[playerAt.row][playerAt.col] = new Space();  // remove the player from this world
            return;
        }

        // Player eats an object
        if (world[playerNext.row][playerNext.col].isEdible()) 
        {
        	// Stepping into an edible cell, decrease by its point value
            remainingEmeraldsInWorld -= world[playerNext.row][playerNext.col].getEmeraldValue();
            remainingEmeraldsToWin -= world[playerNext.row][playerNext.col].getEmeraldValue();

            // Do not go below 0
            if (remainingEmeraldsToWin <= 0)
                remainingEmeraldsToWin = 0;

            // Check for a win
            if (remainingEmeraldsToWin <= 0)
            	status = GameStatus.WON;
         
            // Move the player
            world[playerAt.row][playerAt.col] = new Space();
            playerAt.set(playerNext.row, playerNext.col);
        	world[playerAt.row][playerAt.col] = new Player();
        	
        	// Check whether player supports an object with their head
            final int rowAbove = playerAt.row - 1;
            if (inBounds(rowAbove, playerAt.col) && world[rowAbove][playerAt.col].hasMass()) 
            {
            	// Player supports the object above
                supported = new Coordinates(rowAbove, playerAt.col);
            }

            return;
    	}

       	// Can't step into an inedible object
       	System.out.println("There is an obstacle in the way.");

    }
    
    //-------------------------------------------------------------------------

    /**
     * Make a random alien move.
     */
    public void alienMove(final char ch) 
    {
        // Alien is dead and no longer active
    	if (alienAt == null)
    		return;
    	
    	final Coordinates alienNext = stepTo(alienAt, ch);

        // Alien tries to move outside the board
        if (!inBounds(alienNext.row, alienNext.col)) 
        {
        	// world[alienAt.row][alienAt.col] = new Space();
        	// alienAt = null;  // alien exits the game
            return;
        }

        // Alien moves in an edible object
        if (world[alienNext.row][alienNext.col].isEdible())
        {
            // Alien moves into player
        	if (world[alienNext.row][alienNext.col].isPlayer()) {
                status = GameStatus.LOST;
                world[alienAt.row][alienAt.col] = new Space();
                alienAt.set(alienNext.row, alienNext.col);
                world[alienAt.row][alienAt.col] = new Alien();
                return;
            }

        	// Counts the emeralds the alien steals
            remainingEmeraldsInWorld -= world[alienNext.row][alienNext.col].getEmeraldValue();
        	world[alienAt.row][alienAt.col] = new Space();
        	alienAt.set(alienNext.row, alienNext.col);
        	world[alienAt.row][alienAt.col] = new Alien();
        }
    }

    //-------------------------------------------------------------------------

    /**
     * @return Whether an object with mass falls on the player.
     */
    public boolean applyGravity()
    {

        boolean fellOnPlayer = false;  // whether an object with mass fell on the player

        //synchronized(this) {

            int row;
            int rowMass;
            WorldObject object;
            WorldObject crushedObject;

            // Check gravity one column at the time
            for (int col = 0; col < cols; col++) {

                // Start at rows-2 because gravity does not apply to objects on bottom row.
                row = rows - 2;

                // Check gravity one row at the time, bottom up
                while (row >= 0) {

                    object = world[row][col];

                    // Check if gravity can be applied to object
                    if (object.hasMass() && !isObjectSupported(row, col)) {

                        rowMass = row;

                        // While objects below are vulnerable, fall through
                        while (world[rowMass + 1][col].isVulnerable()) {

                            // Move falling object down one level
                            world[rowMass][col] = new Space();
                            rowMass++;
                            crushedObject = world[rowMass][col];  // Store the object that got crushed
                            world[rowMass][col] = object;

                            // Check which object got crushed and change game status/score accordingly
                            if (crushedObject.isPlayer()) {
                                fellOnPlayer = true;
                            } else if (crushedObject.isMonster()) {
                                System.out.println("Alien squashed!");
                                alienAt = null;  // remove the alien from the game
                            } else {
                                remainingEmeraldsInWorld -= crushedObject.getEmeraldValue();
                            }

                            // If mass has reached the bottom of the board, stop falling (exit while loop)
                            if (rowMass == rows - 1) {
                                break;
                            }
                        }

                        // The falling object is at its lowest level, look for the gravity one row above
                        row--;

                    } else {
                        // Object cannot fall, look for the gravity one row above
                        row--;
                    }
                }
            }

        //}

        return fellOnPlayer;

    }
    
    //-------------------------------------------------------------------------

    /**
      * @return Coordinate of step in the specified direction.
     */
    public Coordinates stepTo(final Coordinates from, final char dirn)
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
        return new Coordinates(from.row+dRow, from.col+dCol);
    }
    
    //-------------------------------------------------------------------------

    /**
     * @returns the string representation of the world-
     */
    @Override
    public String toString() 
    {
        String str;

        str = "Emeralds remaining in game: " + remainingEmeraldsInWorld + "\n";
        str += "Emeralds to collect to win: " + remainingEmeraldsToWin + "\n";

        for (int r = 0; r < rows; r++) 
        {
            for (int c = 0; c < cols; c++) 
                str += world[r][c];
            str += "\n";
        }
        return str;
    }

    //-------------------------------------------------------------------------

    private boolean isObjectSupported(int row, int col) {
        if (supported!=null && row == supported.row && col == supported.col){
            return true;
        }
        return false;
    }
    
}
