/**
 * PRA2003: Assignment 3 sample solution.
 * Author: cambolbro (based on code by Marc Lanctot).
 * 
 * Note: if you use this code, add your name and ID to this header!
 */

import java.util.Random;

//-----------------------------------------------------------------------------

/**
 * An object that can exist in the world.
 * Every cell in the world array will contain an object.
 */
abstract class WorldObject 
{
    // Returns true if this object is edible, false otherwise
    public abstract boolean isEdible();

    // Returns true if the object can fall (due to gravity), false otherwise
    public abstract boolean hasMass();

    // Returns true if the object gets destroyed if a rock falls on it, false otherwise
    public abstract boolean isVulnerable();

    // Returns the name of the file for the icon;
     public String getIconFile() { return ""; }

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
    public boolean isOpen() 
    {
        return false;
    }

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

/**
 * Instances of space.
 */
class Space extends WorldObject 
{
    public boolean isOpen() 		{ return true; }
    public boolean isEdible() 		{ return true; }
    public boolean hasMass() 		{ return false; }
    public boolean isVulnerable() 	{ return true; }
    public String toString() 		{ return "."; }
    public String getIconFile() 	{ return "space.png"; }
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
    public String getIconFile() 	{ return "rock.png"; }
}

/**
 * Instances of dirt.
 */
class Dirt extends EdibleObject 
{
    public boolean isOpen() 		{ return true; }
    public boolean hasMass() 		{ return false; }
    public boolean isVulnerable() 	{ return false; }
    public int getEmeraldValue() 	{ return 0; }
    public String toString() 		{ return "#"; }
    public String getIconFile() 	{ return "dirt.png"; }
}

/**
 * Instances of emeralds.
 */
class Emerald extends EdibleObject 
{
    public boolean hasMass() 		{ return true; }
    public boolean isVulnerable() 	{ return false; }
    public int getEmeraldValue() 	{ return 1; }
    public String toString() 		{ return "e"; }
    public String getIconFile() 	{ return "emerald.png"; }
}

/**
 * Instances of diamonds.
 */
class Diamond extends EdibleObject 
{
    public boolean hasMass() 		{ return true; }
    public boolean isVulnerable() 	{ return true; }
    public int getEmeraldValue() 	{ return 3; }
    public String toString() 		{ return "d"; }
    public String getIconFile() 	{ return "diamond.png"; }
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
        case 0:  return 'u';
        case 1:  return 'r';
        case 2:  return 'd';
        case 3:  return 'l';
        default: return '?';
        }
    }
    
    public String toString() 		{ return "a"; }
    public String getIconFile() 	{ return "alien.png"; }
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
    public String toString() 		{ return "p"; }
    public String getIconFile() 	{ return "player.png"; }
}
