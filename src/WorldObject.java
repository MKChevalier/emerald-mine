// Author: Manon Chevalier, i6138957 (based on code by Cameron Browne (based on code by Marc Lanctot)).
// Purpose: create the basic elements of the Emerald Mine game using object oriented programming.
// The goal for the player (p) is to collect a given amount of emeralds (e) (or diamonds (d), worth 3 emeralds),
// without leaving the map or getting killed by the alien (a). You can move up, down, left or right by one by pressing
// u, d, l or r respectively. The alien can move up, down left or right by one. If he collects an emerald, it is lost.
// WorldObject: creates the classes of all elements (WorldObjects) that go into the game and the methods necessary.

import java.util.Random;
import java.util.Scanner;

//-----------------------------------------------ABSTRACT CLASSES-------------------------------------------------------

// WorldObject (includes all possible elements of the game)
// has abstract methods and default implementations
public abstract class WorldObject {
    public abstract boolean isEdible();
    public abstract boolean hasMass();
    public abstract boolean isVulnerable();
    public boolean canMove() { return false; }
    public boolean isPlayer(){ return false; }
    public char getMove(){ return '?'; }
    public int getEmeraldValue(){ return 0; }
}

// EdibleObject (includes all edible elements of the game)
abstract class EdibleObject extends WorldObject{
    public boolean isEdible() { return true; }
}

// Moveable (includes all moveable elements of the game)
abstract class Moveable extends WorldObject {
    public boolean canMove() { return true; }
}

//---------------------------------------------CONCRETE CLASSES---------------------------------------------------------
// implement the abstract methods in WorldObject, override default implementations when necessary, override toString

class Space extends WorldObject {
    public boolean isEdible() { return true; }
    public boolean hasMass() { return false; }
    public boolean isVulnerable() { return true; }
    public String toString(){ return "."; }
}

class Rock extends WorldObject {
    public boolean isEdible() { return false; }
    public boolean hasMass() { return true; }
    public boolean isVulnerable() { return false; }
    public String toString(){ return "r"; }
}

class Dirt extends EdibleObject {
    public boolean hasMass() { return false; }
    public boolean isVulnerable() { return false; }
    public String toString(){ return "#"; }
}

class Emerald extends EdibleObject{
    public boolean hasMass() { return true; }
    public boolean isVulnerable() { return false; }
    public int getEmeraldValue() { return 1; }
    public String toString(){ return "e"; }
}

class Diamond extends EdibleObject{
    public boolean hasMass() { return true; }
    public boolean isVulnerable() { return true; }
    public int getEmeraldValue() { return 3; }
    public String toString(){ return "d"; }
}

class Alien extends Moveable {
    public boolean isEdible() { return false; }
    public boolean hasMass() { return true; }
    public boolean isVulnerable() { return true; }
    public String toString(){ return "a"; }

    // returns a random char move (u/d/l/r)
    public char getMove() {
        Random rand = new Random();
        char move;
        switch (rand.nextInt(4))
        {
        case 0: move = 'u'; break;
        case 1: move = 'd'; break;
        case 2: move = 'l'; break;
        case 3: move = 'r'; break;
        default: move = 'z';                    // should never happen
        }
        return move;
    }
}

class Player extends Moveable{
    public boolean isEdible() {  return false; }
    public boolean hasMass() { return false; }
    public boolean isVulnerable() { return true; }
    public String toString(){ return "p"; }
    public boolean isPlayer() { return true; }

    // asks the user to input a move, returns a char (normally: u/d/l/r)
    public char getMove() {
        System.out.println("Where to?");
        final Scanner scan = new Scanner(System.in);
        String line = scan.nextLine();
        if (line.length() == 0)
            return ' ';
        return line.charAt(0);
    }
}
