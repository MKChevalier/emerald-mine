import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Key Listener.
 */
public class KeyEventTrapper implements KeyListener
{
    // variables
    private World world;
    private WorldPanel worldPanel;

    private Score score;
    private ScorePanel scorePanel;

    private GameStatus gameStatus = GameStatus.PLAYING;

    //constructor
    KeyEventTrapper(World world, WorldPanel worldPanel, Score score, ScorePanel scorePanel){
        this.world      = world;
        this.worldPanel = worldPanel;
        this.score      = score;
        this.scorePanel = scorePanel;
    }

    //methods
    @Override
    public void keyPressed(KeyEvent e) {
        if (score.gameStatus == GameStatus.PLAYING) {
            updateWorld(e);
            worldPanel.redrawWorld();
            score.Update();
            scorePanel.redrawScore();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    private void updateWorld(KeyEvent e){
        // read the key pressed and transform it into a character
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                gameStatus = world.applyMove('u');
                break;
            case KeyEvent.VK_RIGHT:
                gameStatus = world.applyMove('r');
                break;
            case KeyEvent.VK_DOWN:
                gameStatus = world.applyMove('d');
                break;
            case KeyEvent.VK_LEFT:
                gameStatus = world.applyMove('l');
                break;
            default:
                gameStatus = world.applyMove('?');
                System.out.println("Invalid key");
                break;
        }
    }

}
