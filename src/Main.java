/**
 * PRA2003 Assignment 4 sample solution.
 * @author cambolbro (based on code from previous years).
 */

/**
 * Main app class.
 */
class Main 
{
	/**
	 * Main game loop.
	 */
    public static void mainLoop() 
    {
    	// Load the world
        World world;
        try 
        {
        	// Check the example
            world = new World("example.txt");
        }
        catch(Exception e) 
        {
            e.printStackTrace();
            return;
        }
        world = new World(12, 12, 16);

        // Create and initialize the GUI
        GUI gui = new GUI(world);
        gui.init();

        // Start the game loop
        int outcome = World.Playing;
        while (outcome == World.Playing) 
        {
             char move = world.getMove();
            outcome = world.applyMove(move);
        }
        System.out.println(world);
        System.out.println("");

        if (outcome == World.Win)
            System.out.println("Way to go!");
        else
            System.out.println(":(  better luck next time...");
    }

    /**
     * Main entry point.
     */
    public static void main(String[] args) 
    {
        System.out.println("Starting main game loop...");
        mainLoop();
    }
}

