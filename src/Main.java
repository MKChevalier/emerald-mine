// Author: Manon Chevalier, i6138957 (based on code by Cameron Browne (based on code by Marc Lanctot)).
// Purpose: create the basic elements of the Emerald Mine game using object oriented programming.
// The goal for the player (p) is to collect a given amount of emeralds (e) (or diamonds (d), worth 3 emeralds),
// without leaving the map or getting killed by the alien (a). You can move up, down, left or right by one by pressing
// u, d, l or r respectively. The alien can move up, down left or right by one. If he collects an emerald, it is lost.
// Main: creates a world and plays the game.

class Main {
    // main entry point.
    public static void main(final String[] args) {
        int rows = 10;
        int columns = 10;
        int goalEmeralds = 16; //7 emeralds + 3 diamonds
        final World world = new World(rows, columns, goalEmeralds);

        // prints out a welcome message and the rules for this specific game
        System.out.println("Welcome to Emerald Mine, PRA 2003 edition.");
        System.out.println("Here are the rules:");
        System.out.println("You are the player (p). \n" + "Your aim is to collect " + goalEmeralds +
                " emeralds (e) without leaving the map or getting killed by the alien (a). \n" +
                "You can also collect diamonds (d), each diamond is worth three emeralds. \n" +
                "The alien can move up, down left or right by one and he can steal emeralds. \n" +
                "You can move up, down, left or right by one by pressing u, d, l or r respectively. \n");

        // Play the game
        while (world.status() == World.Playing) {
            System.out.println(world);
            char move = world.getMove();
            if (!world.validMove(move))
                System.out.println("Not a valid move. Try one of: u,d,l,r. \n");
            else
                world.applyMove(move);
        }

        // Show the result
        System.out.println(world);
        switch (world.status()) {
            case World.Win:
                System.out.println("You win!");
                break;
            case World.LossAlien:
                System.out.println("Alien killed you. Bad luck. You lose.");
                break;
            case World.LossEmeralds:
                System.out.println("Alien stole too many emeralds. Bad luck. You lose.");
                break;
            case World.LossLimits:
                System.out.println("You crossed the world limits. Bad luck. You lose.");
                break;
            default:
                System.out.println("** Unexpected game outcome " + world.status() + ".");
        }
    }
}
