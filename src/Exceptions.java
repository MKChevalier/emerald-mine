// Author: Manon Chevalier, i6138957 (based on code by Cameron Browne (based on code by Marc Lanctot)).
// Purpose: create the basic elements of the Emerald Mine game using object oriented programming.
// The goal for the player (p) is to collect a given amount of emeralds (e) (or diamonds (d), worth 3 emeralds),
// without leaving the map or getting killed by the alien (a). You can move up, down, left or right by one by pressing
// u, d, l or r respectively. The alien can move up, down left or right by one. If he collects an emerald, it is lost.
// Exceptions: Creates custom exception BadFileFormatException for the formatting of the input file.

public class Exceptions {
    public static class BadFileFormatException extends Exception{

        //variables
        private String message;
        private int row = -1;
        private int col = -1;

        //constructor 1: when rows and columns are necessary
        BadFileFormatException(String message, int row, int col){
            this.message = message;
            this.row = row;
            this.col = col;
        }

        //constructor 2: when only rows are necessary
        BadFileFormatException(String message, int rows){
            this.message = message;
            this.row = rows;
        }

        //constructor 3: when the exception applies to the world in general
        BadFileFormatException(String message){
            this.message = message;
        }

        // prints the a string message for each exception with necessary info about rows and columns
        public String toString() {
            return "BadFileFormatException: " + message
                    + ( row != -1 ? " at row " + row : "")
                    + ( col != -1 ? " and at col " + col : "");
        }
    }
}
