
/**
 * PRA2003: Emerald Mine
 * @author Manon Chevalier i6138957 based on code from Cameron Browne (based on code from previous PRA2003 years).
 * Purpose: create the basic elements of the Emerald Mine game using object oriented programming and a GUI
 * The goal for the player is to collect a given amount of emeralds (or diamonds, worth 3 emeralds),
 * without leaving the map or getting killed by the alien or by a heavy object falling.
 * The player can move up, down, left or right by one by pressing the arrow keys.
 * The alien can move up, down left or right by one. If he collects an emerald, it is lost.
 * WorldObject.java: Creation of all WorldObjects
 */

import javax.swing.*;
import java.util.Random;

//-----------------------------------------------------------------------------

/**
 * An object that can exist in the world.
 * Every cell in the world array will contain an object.
 */
abstract class WorldObject 
{

    // the ImageIcon
    public abstract ImageIcon getImageIcon();

    // returns true if this object is edible, false otherwise
    public abstract boolean isEdible();

    // returns true if the object can fall (due to gravity), false otherwise
    public abstract boolean hasMass();

    //returns true if the object gets destroyed if a rock falls on it, false otherwise
    public abstract boolean isVulnerable();

    //-------------------------------------------------------------------------
    
    // can this object move?
    public boolean canMove() 
    {
        return false; // immobile by default, must be overridden
    }

    // get the move
    public char getMove() 
    {
        return '?';
    }

    // is this object the player?
    public boolean isPlayer() 
    {
        return false; // by default, no.. must be overridden
    }

    // is this object a monster?
    public boolean isMonster() 
    {
        return (canMove() && !isPlayer());
    }

    // how much is this object worth, in emeralds?
    public int getEmeraldValue()
    {
        return 0;  // 0 by default, must be overridden
    }

    // is this space or dirt?
    public boolean isDirt()
    {
        return false;
    }
	
	// change the direction (override for bug and spaceship
    public void changeDirection() {}

    //-------------------------------------------------------------------------
   
    /**
     * Create world object from char, else null if no match.
     */
    public static WorldObject createFromChar(final char ch) 
    {
     	// Note: This is not good design! Object characters are maintained 
    	// in two locations, which increases the chance of errors.
    	// A better (but more complex) approach would be to check each 
    	// subclass's toString() and return the one that matches ch.
    	switch(ch) 
    	{
            case '.': return new Space();
            case '#': return new Dirt();
            case 'e': return new Emerald();
            case 'd': return new Diamond();
            case 'r': return new Rock();
            case 'a': return new Alien();
            case 'p': return new Player();
			case 'b': return new Bug();
            case 's': return new Spaceship();
        }
        return null;
    }
}

//-----------------------------------------------------------------------------

/**
 * Moveable object.
 */
abstract class Moveable extends WorldObject 
{
    public boolean canMove() { return true; }
}

/**
 * Edible object.
 */
abstract class EdibleObject extends WorldObject 
{
    public boolean isEdible() { return true; }
}

//-----------------------------------------------------------------------------

/**
 * Instances of space.
 */
class Space extends WorldObject 
{
    public boolean isEdible() 		{ return true; }
    public boolean hasMass() 		{ return false; }
    public boolean isVulnerable() 	{ return true; }
    public String toString() 		{ return "."; }
    public ImageIcon getImageIcon() {
    return new ImageIcon("./images/space.png");
}
}

/**
 * Instances of rock.
 */
class Rock extends WorldObject 
{
    public boolean isEdible() 		{ return false; }
    public boolean hasMass() 		{ return true; }
    public boolean isVulnerable() 	{ return false; }
    public String toString() 		{ return "r"; }
    public ImageIcon getImageIcon() {
        return new ImageIcon("./images/rock.png");
    }
}

/**
 * Instances of dirt.
 */
class Dirt extends EdibleObject 
{
    public boolean isDirt() 		{ return true; }
    public boolean hasMass() 		{ return false; }
    public boolean isVulnerable() 	{ return false; }
    // public int getEmeraldValue() 	{ return 0; }
    public String toString() 		{ return "#"; }
    public ImageIcon getImageIcon() {
        return new ImageIcon("./images/dirt.png");
    }
}

/**
 * Instances of emeralds.
 */
class Emerald extends EdibleObject 
{
    public static int emeraldValue = 1;

    public boolean hasMass() 		{ return true; }
    public boolean isVulnerable() 	{ return false; }
    public int getEmeraldValue() 	{ return emeraldValue; }
    public String toString() 		{ return "e"; }
    public ImageIcon getImageIcon() {
        return new ImageIcon("./images/emerald.png");
    }
}

/**
 * Instances of diamonds.
 */
class Diamond extends EdibleObject 
{
    public static int emeraldValue = 3;

    public boolean hasMass() 		{ return true; }
    public boolean isVulnerable() 	{ return true; }
    public int getEmeraldValue() 	{ return emeraldValue; }
    public String toString() 		{ return "d"; }
    public ImageIcon getImageIcon() {
        return new ImageIcon("./images/diamond.png");
    }
}

/**
 * Instances of aliens.
 */
class Alien extends Moveable 
{
    // best to create a single rand. number generator at the start
    private static Random rng = new Random();

    public boolean isEdible() 		{ return false; }
    public boolean hasMass() 		{ return false; }
    public boolean isVulnerable() 	{ return true; }
    public char getMove() 
    {
        switch (rng.nextInt(4))
        {

        case 0: return 'u';
        case 1: return 'r';
        case 2: return 'd';
        case 3:  return 'l';
        default: return '?';
        }
    }
    public String toString() { return "a"; }
    public ImageIcon getImageIcon() {
        return new ImageIcon("./images/alien.png");
    }
}

/**
 * Instances of bugs.
 */
class Bug extends Moveable {
    // 1 = up, 2 = right, 3 = down, 4 = left
    private int direction;

    public Bug() {
        direction = 1;
    }

    public boolean isEdible() {
        return false;
    }

    public boolean hasMass() {
        return false;
    }

    public boolean isVulnerable() {
        return true;
    }

    public ImageIcon getImageIcon() {
        String number = Integer.toString(direction);
        return new ImageIcon("./images/bug" + number + ".png");
    }

    public char getMove() {
        switch (direction) {
            case 1:
                return 'u';
            case 2:
                return 'r';
            case 3:
                return 'd';
            case 4:
                return 'l';
            default:
                return '?';
        }
    }

    public void changeDirection() {
        switch (direction) {
            case 1: direction = 2;
                break;
            case 2: direction = 3;
                break;
            case 3: direction = 4;
                break;
            case 4: direction = 1;
                break;
        }
    }

    public String toString() {
        return "b";
    }
}

/**
* Instances of Spaceship.
*/

class Spaceship extends Moveable {
    // 1 = up, 2 = right, 3 = down, 4 = left
    private int direction;

    public Spaceship() {
        direction = 1;
    }

    public boolean isEdible() {
        return false;
    }

    public boolean hasMass() {
        return false;
    }

    public boolean isVulnerable() {
        return true;
    }

    public ImageIcon getImageIcon() {
        String number = Integer.toString(direction);
        return new ImageIcon("./images/spaceship" + number + ".png");
    }

    public char getMove() {
        switch (direction) {
            case 1:
                return 'u';
            case 2:
                return 'r';
            case 3:
                return 'd';
            case 4:
                return 'l';
            default:
                return '?';
        }
    }

    public void changeDirection() {
        switch (direction) {
            case 1:
                direction = 4;
                break;
            case 2:
                direction = 1;
                break;
            case 3:
                direction = 2;
                break;
            case 4:
                direction = 3;
                break;
        }
    }

    public String toString() {
        return "s";
    }
}

/**
 * Instances of the player.
 */
class Player extends Moveable 
{
    public boolean isPlayer() 		{ return true; }
    public boolean isEdible() 		{ return false; }
    public boolean hasMass() 		{ return false; }
    public boolean isVulnerable() 	{ return true; }

    public String toString() { return "p"; }
    public ImageIcon getImageIcon() {
        return new ImageIcon("./images/player.png");
    }
}
