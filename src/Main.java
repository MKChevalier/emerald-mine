/**
 * PRA2003: Assignment 2 sample solution.
 * Author: Cameron Browne (based on code by Marc Lanctot).
 *
 * Note: if you use this code, add your name and ID to this header!
 */

/**
 * Main class.
 */
class Main
{
    /**
     * Main entry point.
     */
    public static void main(final String[] args)
    {
        final World world = new World(10, 10, 7);

        // Play the game
        int outcome = 0;
        while (outcome == 0)
        {
            System.out.println(world);
            final char ch = world.getMove();
            if (!world.validMove(ch))
                System.out.println("Not a valid move. Try one of: u,d,l,r.");
            else
                outcome = world.applyMove(ch);
        }

        // Show the result
        System.out.println(world + "\n");
        if (outcome == 1)
            System.out.println("Way to go!");
        else
            System.out.println(":(  better luck next time...");
    }
}