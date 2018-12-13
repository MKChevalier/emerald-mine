// Author: Manon Chevalier, i6138957 (based on code by Cameron Browne (based on code by Marc Lanctot)).
// Purpose: create the basic elements of the Emerald Mine game using object oriented programming.
// The goal for the player (p) is to collect a given amount of emeralds (e) (or diamonds (d), worth 3 emeralds),
// without leaving the map or getting killed by the alien (a). You can move up, down, left or right by one by pressing
// u, d, l or r respectively. The alien can move up, down left or right by one. If he collects an emerald, it is lost.
// World: creates the world class and all the methods necessary.

import java.util.Random;
import java.io.*;


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

        public boolean matches(final RC other) {
            return row == other.row && col == other.col;
        }
    }

    //MEMBER VARIABLES
    private int rows, cols;                                     // dimension of world in rows and columns
    private WorldObject[][] world;                              // 2D array representing the world map
    private int emeraldsRemaining;                              // number of emeralds still needed to win
    private int stolenEmeralds = 0;                             // number of emeralds stolen by the alien
    private int emeraldsAvailable = 0;                          // numbers of emeralds by value on board (3 for diamond)
    private int placedDiamonds = 3;                             // number of diamonds placed initially
    private int placedRocks = 5;                                // number of rocks placed initially
    public static final int Playing = 0;                        // game in progress
    public static final int Win = 1;                            // player win
    public static final int LossAlienMeet = 2;                  // player met alien
    public static final int LossAlienAttack = 3;                // alien killed player
    public static final int LossEmeralds = 4;                   // alien stole too many emeralds
    public static final int LossLimits = 5;                     // player crossed the world limits
    public static final int LossGravity = 6;                    // massive object fell on player
    public int status;                                          // status of the game, see above
    public static final int Invalid = 0;                        // world is invalid
    public static final int Valid = 1;                          // world is valid
    public int validity;                                        // validity of the world, see above
    private RC playerAt = null;                                 // keeps track of players position
    private RC alienAt = null;                                  // keeps track of aliens position
    private char lastObject;                                    // keeps track of where the player last went(for gravity)
    private final Random rng = new Random();                    // random number generator

    //-------------------------------------------------CONSTRUCTORS-----------------------------------------------------

    public World(final int rows, final int cols, final int emeraldsRemaining) {
        this.rows = rows;
        this.cols = cols;
        this.emeraldsRemaining = emeraldsRemaining;
        init();
    }

    public World(String InFileName) throws Exception{
            parse(InFileName);
    }


    //---------------------------------------------------METHODS--------------------------------------------------------

    // World creation from scratch
    private void init() {
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
        alienAt = new RC(randomRowA, randomColA);

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
            emeraldsAvailable += 3;
        }

        // inserts the emeralds in random positions on the map
        for (int i = 0; i < emeraldsRemaining - 9 + 1; i++) {
            int randomRowE = rng.nextInt(rows);
            int randomColE = rng.nextInt(cols);
            // makes sure that all emeralds are placed at different positions (those that were '#' before)
            while (world[randomRowE][randomColE].toString().charAt(0) != '#') {
                randomRowE = rng.nextInt(rows);
                randomColE = rng.nextInt(cols);
            }
            world[randomRowE][randomColE] = new Emerald();
            emeraldsAvailable += 1;
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

        // game is now active
        validity = Valid;
        status = Playing;
    }


    // World creation using an input file
    public void parse(String inFileName) throws Exception {
            // Open the input stream using the input file name
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(inFileName)));

            // read the 3 first lines (number of rows, columns and required emeralds) and create array world
            rows = Integer.parseInt(in.readLine());
            cols = Integer.parseInt(in.readLine());
            emeraldsRemaining = Integer.parseInt(in.readLine());
            world = new WorldObject[rows][cols];

            // create counters, to later check all conditions are fulfilled
            int playerCount = 0;
            int alienCount = 0;

            // start reading the world map line by line from line 4
            for (int r = 0; r < rows; r++) {
                String line = in.readLine();

                // check that there is a line to read, if not: not enough rows
                if (line == null) {
                    validity = Invalid;
                    throw new Exceptions.BadFileFormatException("Missing rows", r+1);
                } else {
                    // read character by character in the specified line
                    for (int c = 0; c < cols; c++) {
                        // check that there are enough columns
                        if (line.length() < cols) {
                            validity = Invalid;
                            int problemCol = line.length()+1;
                            throw new Exceptions.BadFileFormatException("Missing columns", r+1, problemCol);
                        } else {
                            char current = line.charAt(c);
                            // fill up the array world with correct elements
                            switch (current) {
                                case '#':
                                    world[r][c] = new Dirt();
                                    break;
                                case '.':
                                    world[r][c] = new Space();
                                    break;
                                case 'r':
                                    world[r][c] = new Rock();
                                    break;
                                case 'e':
                                    world[r][c] = new Emerald();
                                    emeraldsAvailable++;
                                    break;
                                case 'd':
                                    world[r][c] = new Diamond();
                                    emeraldsAvailable+= 3;
                                    break;
                                case 'a':
                                    world[r][c] = new Alien();
                                    alienCount++;
                                    break;
                                case 'p':
                                    world[r][c] = new Player();
                                    playerCount++;
                                    // if several players are placed, throw exception
                                    if (playerCount > 1) {
                                        validity = Invalid;
                                        throw new Exceptions.BadFileFormatException("Extra player", r+1, c+1);
                                    }
                                    break;
                                default:
                                    validity = Invalid;
                                    throw new Exceptions.BadFileFormatException("Unexpected element", r+1, c+1);
                            }
                        }
                    }
                    // if the row is longer than the number of columns, too many columns
                    if (line.length() > cols) {
                        validity = Invalid;
                        throw new Exceptions.BadFileFormatException("Extra columns", r+1, cols);
                    }
                }
            }
            // check that the line after the last row is empty, if not: too many rows
            if (in.readLine() != null) {
                validity = Invalid;
                throw new Exceptions.BadFileFormatException("Extra rows", rows);
            }

            // check if enough emeralds were placed in the world, if not: exception
            if (emeraldsAvailable < emeraldsRemaining) {
                validity = Invalid;
                throw new Exceptions.BadFileFormatException("Not enough emeralds");
            }

            // close input file
            in.close();

            // game is now active
            status = Playing;
    }


    // returns status of game: PAUSED / PLAYING / WIN / LOSS.
    public int status() {
        return status;
    }

    // returns validity of world created: VALID/INVALID
    public int validity() {
        return validity;
    }


    // prints out a welcome message and the rules for this specific game
    public void printRules() {
        System.out.println("Welcome to Emerald Mine, PRA 2003 edition.");
        System.out.println("Here are the rules:");
        System.out.println("You are the player (p). \n" + "Your aim is to collect " + emeraldsRemaining +
                " emeralds (e) without leaving the map or getting killed by the alien (a). \n" +
                "You can also collect diamonds (d), each diamond is worth three emeralds. \n" +
                "The alien can move up, down left or right by one and he can steal emeralds. \n" +
                "You can move up, down, left or right by one by pressing u, d, l or r respectively. \n");
    }

    // returns whether coordinates [row,col] is in bounds.
    public boolean inBounds(int row, int col) {
        return (row >= 0 && row < rows && col >= 0 && col < cols);
    }


    // returns whether char input is a valid move.
    public boolean validMove(final char ch) {
        return ch == 'u' || ch == 'd' || ch == 'l' || ch == 'r';
    }


    // applies the player and alien moves
    public void applyMove(final char move) {
        playerMove(move);
        alienMove();

        //apply gravity and check if something fell on player
        boolean fellOnPlayer;
        //if player moved to dirt/emerald (not vulnerable): exception (player is protected)
        if (lastObject == '#' || lastObject == 'e') {
            fellOnPlayer = applyGravity(playerAt.row, playerAt.col);
        } else {
            //if p moved to space/diamond (vulnerable) or stayed in the same spot: not exception (player can be hurt)
            fellOnPlayer = applyGravity(-1, -1);
        }
        if (fellOnPlayer){
            status = LossGravity;
        }
    }

    public char getMove() {
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
                System.out.println("There is a rock in the way. You stay in the same place.");
                lastObject = '.';
            }
            // else move the player accordingly
            else {

                // if player meets an alien, he loses
                if (world[nextRow][nextCol].canMove() && !world[nextRow][nextCol].isPlayer()) {
                    // move player from old to new position (so visual on map matches situation)
                    world[nextRow][nextCol] = world[playerAt.row][playerAt.col];
                    world[playerAt.row][playerAt.col] = new Space();
                    status = LossAlienMeet;  // player dies
                    return;
                }

                // if the player meets anything edible, collect the emerald value
                int value = world[nextRow][nextCol].getEmeraldValue();
                if (world[nextRow][nextCol].isEdible()) {
                    emeraldsRemaining -= value;
                    emeraldsAvailable -= value;
                    if (value == 1) {
                        lastObject = 'e';
                    }
                    else if (value == 3){
                        lastObject = 'd';
                    }
                    else if (value == 0){
                        lastObject = '#';
                    }
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
                    status = LossAlienAttack;
                }
                // else if alien meets something edible, alien eats it
                else if (world[alienAt.row][alienAt.col].isEdible()) {
                    int stolenValue = world[alienAt.row][alienAt.col].getEmeraldValue();
                    stolenEmeralds += stolenValue;
                    emeraldsAvailable -= stolenValue;
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


    // Returns true if an object with mass falls on the player,  false otherwise
    private boolean applyGravity(int exceptRow, int exceptCol) {
        //as a default, we say it didn't fall on the player
        boolean fellOnPlayer = false;
        // going through all elements from the bottom up (skipping the last row)
        for (int r = rows-2; r >= 0; r--) {
            for (int c = 0; c < cols; c++) {
                // if the element has mass it can fall
                if (world[r][c].hasMass()){
                    // if the element below is vulnerable the element above will fall
                    if (world[r+1][c].isVulnerable()){
                        // apply gravity except if it is the protected cell (player on # or e)
                        // no cell will be -1 -1 so in that case no cell is protected
                        if ( (r+1 != exceptRow) || (c != exceptCol) ) {
                            if (world[r+1][c].isPlayer()) {
                                fellOnPlayer = true;

                            }
                            world[r+1][c] = world[r][c];
                            world[r][c] = new Space();
                        }
                    }
                }
            }
        }

        return fellOnPlayer;
    }


    // transforms the map into a string form that later can be printed
    public String toString() {
        String worldString = "";
        // only print the world if it is created correctly (i.e. valid)
        if (validity == Valid) {
            String str01 = "Emeralds available: " + emeraldsAvailable + "\n" +
                    "Emeralds required to win: " + emeraldsRemaining + "\n" +
                    "Emeralds stolen by alien: " + stolenEmeralds + "\n";
            StringBuilder sb = new StringBuilder(str01 + "\nWorld Map: \n");

            // goes through all rows and columns and concatenates the characters to the string str
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    sb.append(world[row][col].toString());
                }
                // separates all rows with a newline
                sb.append("\n");
            }
            worldString = sb.toString();
        }
        return worldString;
    }
}
