// Author: Manon Chevalier, i6138957
// Purpose: create the basic elements of the Emerald Mine game. The goal for the player (p) is to collect all except one
// emeralds (e) without leaving the map or getting killed by the alien (a). The alien can move up, down left or right by
// one. If he collects an emerald, it is lost. You can move up, down, left or right by one by pressing u, d, l or r
// respectively.

import java.util.*;

public class World {

    //-----------------------------------------------MEMBER VARIABLES---------------------------------------------------

    private int nRows;                          //number of rows
    private int nColumns;                       //number of columns
    private int rEmeralds;                      //number of emeralds required for the player to win
    private int stolenEmeralds = 0;             //emeralds stolen by alien
    private char[][] elements;                  //2D character array representing the world map
    private int rowPositionP;                   //row position of the player
    private int colPositionP;                   //column position of the player
    private int rowPositionA;                   //row position of the alien
    private int colPositionA;                   //column position of the alien


    //-------------------------------------------------CONSTRUCTOR------------------------------------------------------

    World(int rows, int columns, int emeralds){
        nRows = rows;
        nColumns = columns;
        rEmeralds = emeralds;
        int N = rEmeralds + 1;                  //one more emerald on map, than are required for the player to win
        elements = new char[nRows][nColumns];
        Random rand = new Random();

        // changes all zeros to dots (open spaces)
        for (int i = 0; i < nRows; i++) {
            for (int j = 0; j < nColumns; j++) {
                elements[i][j] = '.';
            }
        }

        // inserts the emeralds (one more than required to win), in random positions on the map
        for (int i = 0; i < N; i++) {
                int randomRowE = rand.nextInt(nRows - 1);
                int randomColE = rand.nextInt(nColumns - 1);
            // makes sure that all emeralds are placed at different positions (those that were '.' before)
            while (elements[randomRowE][randomColE] != '.') {
                randomRowE = rand.nextInt(nRows - 1);
                randomColE = rand.nextInt(nColumns - 1);
            }
            elements[randomRowE][randomColE] = 'e';
        }

        // places the player in a random position
        int randomRowP = rand.nextInt(nRows - 1);
        int randomColP = rand.nextInt(nColumns - 1);
        // makes sure that the player is placed at a different position than the emeralds (one that was '.' before)
        while (elements[randomRowP][randomColP] != '.') {
            randomRowP = rand.nextInt(nRows - 1);
            randomColP = rand.nextInt(nColumns - 1);
        }
        rowPositionP = randomRowP;
        colPositionP = randomColP;
        elements[randomRowP][randomColP] = 'p';

        // place the alien in a random position
        int randomRowA = rand.nextInt(nRows - 1);
        int randomColA = rand.nextInt(nColumns - 1);
        // makes sure that the player is placed at a different position than the emeralds or player ( was '.' before)
        while (elements[randomRowA][randomColA] != '.') {
            randomRowA = rand.nextInt(nRows - 1);
            randomColA = rand.nextInt(nColumns - 1);
        }
        rowPositionA = randomRowA;
        colPositionA = randomColA;
        elements[randomRowA][randomColA] = 'a';
    }


    //-----------------------------------------------------METHODS------------------------------------------------------

    // reads a char from the keyboard to get the move (u, d, l, r)
    public char getMove() {
        System.out.println("Where to?");
        Scanner scan = new Scanner(System.in);
        return scan.next().charAt(0);
    }

    // moves the player according to getMove, executes all applicable rules/exceptions, moves alien
    public int applyMove(char move) {

    //PLAYER
       // 4 options: u, d, l, r
        if (move == 'u') {
            // make the old position a dot
            elements[rowPositionP][colPositionP] = '.';
            // move the player up one row
            rowPositionP--;
        } else if (move == 'd') {
            // make the old position a dot
            elements[rowPositionP][colPositionP] = '.';
            // move the player down one row
            rowPositionP++;
        } else if (move == 'l') {
            // make the old position a dot
            elements[rowPositionP][colPositionP] = '.';
            // move the player one column to the left
            colPositionP--;
        } else if (move == 'r') {
            // make the old position a dot
            elements[rowPositionP][colPositionP] = '.';
            // move the player one column to the right
            colPositionP++;
        // if input is anything else
        } else {
            System.out.println("Incorrect input. Press u to move up, d to move down," +
                    "l to move left and r to move right.");
        }

        // if player stays within the world limits, check if he encounters an emerald/an alien/nothing
        if (rowPositionP >= 0 && (rowPositionP <= nRows - 1) &&
                (colPositionP >= 0) && (colPositionP <= nColumns - 1)) {
            // if the new position contains an emerald, decrease required emeralds by one
            if (elements[rowPositionP][colPositionP] == 'e') {
                rEmeralds--;
            }
            // if the new position contains an alien, player loses
            else if (elements[rowPositionP][colPositionP] == 'a') {
                return 2;
            }
            // then, move player to his new position
            elements[rowPositionP][colPositionP] = 'p';
        // if player moves out of the world limits, player loses
        } else {
            return 2;
        }

    //ALIEN
        // check if alien is still within the world limits (he could have died in the round before)
        // do nothing if he is dead
        if (rowPositionA >= 0 && (rowPositionA <= nRows - 1) &&
                (colPositionA >= 0) && (colPositionA <= nColumns - 1)) {

            // remove the alien from its old position
            elements[rowPositionA][colPositionA] = '.';

            // picks a random move for the alien
            Random rand = new Random();
            int randomMoveA = rand.nextInt(3);
            if (randomMoveA == 0) { //up
                rowPositionA--;
            } else if (randomMoveA == 1) { //down
                rowPositionA++;
            } else if (randomMoveA == 2) { //left
                colPositionA--;
            } else if (randomMoveA == 3) { //right
                colPositionA++;
            }
        }

        // check if alien is still inside the world map after his move
        // do nothing if he left/died
        if (rowPositionA >= 0 && (rowPositionA <= nRows - 1) &&
               (colPositionA >= 0) && (colPositionA <= nColumns - 1)) {

            // if the alien runs into the player, player loses
            if (elements[rowPositionA][colPositionA] == 'p') {
                return 2;

            // if the alien runs into an emerald, increase the count of stolen emeralds
            } else if (elements[rowPositionA][colPositionA] == 'e') {
                stolenEmeralds++;
            }

            // alien is moved to the new position on the map (if it was an emerald, it is therefore lost)
            elements[rowPositionA][colPositionA] = 'a';
        }

    //RESULT
        // if the player collected all required emeralds: player wins (1)
        if (rEmeralds == 0) {
            return 1;

        // if the alien stole more than 1 emerald, player can never win: player loses (2)
        } else if (stolenEmeralds > 1) {
            return 2;

        // if player didn't win or lose yet: keep playing (0)
        } else {
            return 0;
        }
    }

    // transforms the map into a string form that later can be printed
    public String toString() {
        String worldMap;
        StringBuilder sb = new StringBuilder("\nWorld Map: \n");
        // goes through all rows and columns and concatenates the characters to the string worldMap
        for (int i = 0; i < nRows ; i++) {
            for (int j = 0; j < nColumns; j++) {
                char charToPrint = elements[i][j];
                sb.append(charToPrint);
            }
            // separates all rows with a newline
            sb.append("\n");
        }
        worldMap = sb.toString();
        return worldMap;
    }

    //-----------------------------------------------------MAIN---------------------------------------------------------
    public static void main(String[] args) {
        // declares and initializes the rows/columns/emeralds for the game
        int rows = 5;
        int columns = 4;
        int emeralds = 7;
        // creates a world with the given variables
        World WorldA = new World(rows, columns, emeralds);
        // prints out a welcome message and the rules for this specific game
        System.out.println("Welcome to Emerald Mine, PRA 2003 edition.");
        System.out.println("Here are the rules:");
        System.out.println("You are the player (p). \n" + "Your aim is to collect " + emeralds +
                " emeralds (e) without leaving the map or getting killed by the alien (a). \n" +
                "The alien can move up, down left or right by one. If he collects an emerald, it is lost. \n" +
                "You can move up, down, left or right by one by pressing u, d, l or r respectively.");

        // prints the world map
        System.out.println(WorldA.toString());

        // plays until game ends (player wins(result=1) or loses(result=2))
        int result;
        char move;
        do {
            move = WorldA.getMove();
            result = WorldA.applyMove(move);
            System.out.println(WorldA.toString());
        } while (result == 0);

        // prints the result as a message(win or lose)
       if (result == 2) {
           System.out.println("You lost!");
       } else if (result == 1) {
           System.out.println("You won!");
       }
    }
}

// QUESTION: Why are these methods non-static?
//      Because an object (world) has to be created before the methods can be used.
//      The methods are useless without a world first.