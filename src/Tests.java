// Author: Manon Chevalier, i6138957 (based on code by Cameron Browne (based on code by Marc Lanctot)).
// Purpose: create the basic elements of the Emerald Mine game using object oriented programming.
// The goal for the player (p) is to collect a given amount of emeralds (e) (or diamonds (d), worth 3 emeralds),
// without leaving the map or getting killed by the alien (a). You can move up, down, left or right by one by pressing
// u, d, l or r respectively. The alien can move up, down left or right by one. If he collects an emerald, it is lost.
// Tests: tests different file formats and catches exceptions when applicable

public class Tests {
    public static void main(String[] args) {
        /* all tests to make, to check all possible errors
        file naming: Board<insert number from list below>.txt
        0: non existing file
        1: correct
        2: too many rows
        3: too few rows
        4: too many columns
        5: too few columns
        6: too few emeralds
        7: too many players
        8: unexpected character
         */

        // tries to create a world from a text file and catches exceptions when applicable
        // change number in the inFileName, to test different boards
        try {
            final World world = new World("Board1.txt");
            System.out.println(world.toString());
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        System.out.println("No exception was caught. Correct file format.");
    }
}
