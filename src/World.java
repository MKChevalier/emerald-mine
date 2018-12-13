// Author: Manon Chevalier, i6138957 (based on code by Cameron Browne (based on code by Marc Lanctot)).
// Purpose: create the basic elements of the Emerald Mine game using object oriented programming.
// The goal for the player (p) is to collect a given amount of emeralds (e) (or diamonds (d), worth 3 emeralds),
// without leaving the map or getting killed by the alien (a). You can move up, down, left or right by one by pressing
// u, d, l or r respectively. The alien can move up, down left or right by one. If he collects an emerald, it is lost.
// World: creates the world class and all the methods necessary.

import java.util.Random;

//-------------------------------------------------WORLD CLASS----------------------------------------------------------
class World {

    //Simple record of a [row,col] coordinate.
    class RC {
        int row = -1;
        int col = -1;

        public RC(final int row, final int col) {
            this.row = row;
            this.col = col;
        }

        public void set(final int r, final int c) {
            row = r;
            col = c;
        }

        public boolean matches(final RC other)
        { return row == other.row && col == other.col; }
    }

    //MEMBER VARIABLES
    private int rows, cols;                                     // dimension of world in rows and columns
    private WorldObject[][] world;                              // 2D array representing the world map
    private int emeraldsRemaining;                              // number of emeralds still needed to win
    private int stolenEmeralds = 0;                             // number of emeralds stolen by the alien
    private int placedDiamonds = 3;                             // number of diamonds placed initially
    private int placedRocks = 5;                                // number of rocks placed initially
    //private int placedEmeralds = emeraldsRemaining - (3*placedDiamonds) + 1;
    public static final int Playing         = 0;                // game in progress
    public static final int Win             = 1;                // player win
    public static final int LossAlien       = 2;                // alien killed player
    public static final int LossEmeralds    = 3;                // alien stole too many emeralds
    public static final int LossLimits      = 4;                // player crossed the world limits
    public int status;                                         // status of the game, see above
    private RC playerAt = null;                                 // keeps track of players position
    private RC alienAt  = null ;                                // keeps track of aliens position
    private final Random rng = new Random();                    // random number generator

    //-------------------------------------------------CONSTRUCTOR------------------------------------------------------

    public World(final int rows, final int cols, final int goalEmeralds)
    {
        this.rows = rows;
        this.cols = cols;
        this.emeraldsRemaining = goalEmeralds;
        init();
    }

    //---------------------------------------------------METHODS--------------------------------------------------------

    // returns status of game: PAUSED / PLAYING / WIN / LOSS.
    public int status()
    { return status; }


    // World creation.
    private void init()
    {
        // Initialise the map with all dirt
        world = new WorldObject[rows][cols];
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                world[r][c] = new Dirt();

        // places the player in a random position
        int randomRowP = rng.nextInt(rows);
        int randomColP = rng.nextInt(cols);
        // makes sure that the player is placed at a new position(one that was '#' before)
        while (world[randomRowP][randomColP].toString().charAt(0) != '#') {
            randomRowP = rng.nextInt(rows);
            randomColP = rng.nextInt(cols);
        }
        world[randomRowP][randomColP] = new Player();
        playerAt = new RC(randomRowP, randomColP);

        // place the alien in a random position
        int randomRowA = rng.nextInt(rows);
        int randomColA = rng.nextInt(cols);
        // makes sure that the player is placed at a new position(one that was '#' before)
        while (world[randomRowA][randomColA].toString().charAt(0) != '#') {
            randomRowA = rng.nextInt(rows);
            randomColA = rng.nextInt(cols);
        }
        world[randomRowA][randomColA] = new Alien();
        alienAt  = new RC(randomRowA, randomColA);

        // inserts the diamonds
        for (int i = 0; i < placedDiamonds; i++) {
            int randomRowD = rng.nextInt(rows);
            int randomColD = rng.nextInt(cols);
            // makes sure that all diamonds are placed at different positions (those that were '#' before)
            while (world[randomRowD][randomColD].toString().charAt(0) != '#') {
                randomRowD = rng.nextInt(rows);
                randomColD = rng.nextInt(cols);
            }
            world[randomRowD][randomColD] = new Diamond();
        }

        // inserts the emeralds in random positions on the map
        for (int i = 0; i < emeraldsRemaining-9+1; i++) {
            int randomRowE = rng.nextInt(rows);
            int randomColE = rng.nextInt(cols);
            // makes sure that all emeralds are placed at different positions (those that were '#' before)
            while (world[randomRowE][randomColE].toString().charAt(0) != '#') {
                randomRowE = rng.nextInt(rows);
                randomColE = rng.nextInt(cols);
            }
            world[randomRowE][randomColE] = new Emerald();
        }

        // inserts the rocks
        for (int i = 0; i < placedRocks; i++) {
            int randomRowR = rng.nextInt(rows);
            int randomColR = rng.nextInt(cols);
            // makes sure that all rocks are placed at different positions (those that were '#' before)
            while (world[randomRowR][randomColR].toString().charAt(0) != '#') {
                randomRowR = rng.nextInt(rows);
                randomColR = rng.nextInt(cols);
            }
            world[randomRowR][randomColR] = new Rock();
        }


        status = Playing;  // game is now active
    }


    // returns whether coordinates [row,col] is in bounds.
    public boolean inBounds(int row, int col)
    {
        return (row >= 0 && row < rows && col >= 0 && col < cols);
    }


    // returns whether char input is a valid move.
    public boolean validMove(final char ch)
    {
        return ch == 'u' || ch == 'd' || ch == 'l' || ch == 'r';
    }


    // applies the player and alien moves
    public void applyMove(final char move) {
        playerMove(move);
        alienMove();
    }

    //
    public char getMove()
    {
        return world[playerAt.row][playerAt.col].getMove();
    }

    // Makes a player move in specified direction.
    public void playerMove(final char move) {
        // Check the player's destination
        int nextRow = playerAt.row;
        int nextCol = playerAt.col;
        switch (move) {
            case 'u':
                nextRow--;
                break;
            case 'd':
                nextRow++;
                break;
            case 'l':
                nextCol--;
                break;
            case 'r':
                nextCol++;
                break;
            default:
                System.out.println("Unexpected char '" + move + "'.");
        }

        //checks if players new position is in bounds
        if (inBounds(nextRow, nextCol)) {

            // if the new position has a rock, don't move player and print message.
            if (!world[nextRow][nextCol].isEdible() && !world[nextRow][nextCol].canMove()) {
                System.out.println("Illegal move, there is a rock in the way. Try another direction.");
            }
            // else move the player accordingly
            else {

                // if player meets an alien, he loses
                if (world[nextRow][nextCol].canMove() && !world[nextRow][nextCol].isPlayer()) {
                    // move player from old to new position (so visual on map matches situation)
                    world[nextRow][nextCol] = world[playerAt.row][playerAt.col];
                    world[playerAt.row][playerAt.col] = new Space();
                    status = LossAlien;  // player dies
                    return;
                }

                // if the player meets anything edible, collect the emerald value
                int value = world[nextRow][nextCol].getEmeraldValue();
                if (world[nextRow][nextCol].isEdible()) {
                    emeraldsRemaining -= value;
                }

                // make sure emeraldsRemaining doesn't go below zero
                if (emeraldsRemaining <= 0) {
                    emeraldsRemaining = 0;
                }

                // place player on new position
                world[nextRow][nextCol] = world[playerAt.row][playerAt.col];
                // remove the player from the old position
                world[playerAt.row][playerAt.col] = new Space();
                // update playerAt and move the player
                playerAt.set(nextRow, nextCol);
            }

        } else if (!inBounds(nextRow, nextCol)) {
            // move player from old to new position (so visual on map matches situation)
            world[playerAt.row][playerAt.col] = new Space();
            status = LossLimits;  // player dies
            return;
        }

        // if the player collected all emeralds, he wins
        if (emeraldsRemaining == 0)
            status = Win;
    }

    // Makes a random alien move.
    public void alienMove() {
        // checks if alien is off the board, no longer active
        if (alienAt == null)
            return;

        // Move the alien in a random direction
        int nextRow = alienAt.row;
        int nextCol = alienAt.col;

        char move = world[alienAt.row][alienAt.col].getMove();

        switch (move) {
            case 'u':
                nextRow++;
                break;
            case 'd':
                nextRow--;
                break;
            case 'r':
                nextCol++;
                break;
            case 'l':
                nextCol--;
                break;
        }


        // if the alien stays within the world, apply move
        if (inBounds(nextRow, nextCol)) {

            //only does something if the next element is edible or can move (doesn't move the alien if it is a rock)
            if (world[nextRow][nextCol].isEdible() || world[nextRow][nextCol].canMove()) {


                // if alien meets player, alien kills player
                if (alienAt.matches(playerAt)) {
                    status = LossAlien;
                }
                // else if alien meets something edible, alien eats it
                else if (world[alienAt.row][alienAt.col].isEdible()) {
                    int stolenValue = world[alienAt.row][alienAt.col].getEmeraldValue();
                    stolenEmeralds += stolenValue;
                    // if alien ate more emeralds than the number of extra emeralds,
                    // then player can never win, so player loses
                    if (stolenEmeralds > 1) {
                        status = LossEmeralds;
                    }
                }

                // place alien on new position
                world[nextRow][nextCol] = world[alienAt.row][alienAt.col];
                // remove the alien from the old position
                world[alienAt.row][alienAt.col] = new Space();
                // update alienAt and move the player
                alienAt.set(nextRow, nextCol);
            }
        }
                // else, (if the alien leaves the world) alien exists the game
        else {
            System.out.println("\nAlien died! \n");
            alienAt = null;
        }
    }


    // transforms the map into a string form that later can be printed
    public String toString()
    {
        int emeraldsAvailable = emeraldsRemaining + 1;
        String str = "Emeralds available: " + emeraldsAvailable + "\n" +
                "Emeralds required to win: " + emeraldsRemaining + "\n" +
                "Emeralds stolen by alien: " + stolenEmeralds + "\n";
        StringBuilder sb = new StringBuilder(str + "\nWorld Map: \n");

        // goes through all rows and columns and concatenates the characters to the string str
        for (int row = 0; row < rows; row++)
        {
            for (int col = 0; col < cols; col++) {
                sb.append(world[row][col].toString());
            }
            // separates all rows with a newline
            sb.append("\n");
        }
        return sb.toString();
    }
}