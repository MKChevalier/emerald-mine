/**
 * PRA2003 Assignment 4 sample solution.
 * @author cambolbro (based on code from previous years).
 */

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Key event trapper for handling keypresses in GUI.
 */
class KeyEventTrapper implements KeyListener 
{
    private final EmeraldMinePanel panel;
    private final World world;

    /**
     * Constructor.
     */
    public KeyEventTrapper(final EmeraldMinePanel panel, final World world) 
    {
        this.panel = panel;
        this.world = world;
    }

    @Override
    public void keyPressed(KeyEvent e) 
    {
        //System.out.println("Key pressed: " + e.getKeyCode());
        int outcome = World.Playing;
        switch (e.getKeyCode())
        {
        case KeyEvent.VK_UP:  	outcome = world.applyMove('u'); break;
        case KeyEvent.VK_RIGHT: outcome = world.applyMove('r'); break;
        case KeyEvent.VK_DOWN: 	outcome = world.applyMove('d'); break;
        case KeyEvent.VK_LEFT:  outcome = world.applyMove('l'); break;
        default: 				outcome = world.applyMove('?');
        }
        panel.redrawWorld(world);
        System.out.println("Emeralds remaining: " + world.emeraldsRemaining());

        if (outcome == World.Win) 
        {
            System.out.println("You win!");
            System.exit(-1);
        }
        else if (outcome == World.Loss) 
        {
            System.out.println("Bad luck, you lose.");
            System.exit(-1);
        }
    }

    public void keyTyped(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}
}
