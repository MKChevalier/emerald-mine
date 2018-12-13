import javax.swing.*;
import java.awt.*;

/**
 * Custom scoreboard for out EM.
 */
class ScorePanel extends JPanel
{
    private final JLabel[] scores;
    //private World world;
    Score score;

    /**
     * Constructor.
     */
    public ScorePanel(Score score)
    {
        this.score = score;

        final GridLayout layout2 = new GridLayout(1, 3, 0, 0);
        setLayout(layout2);

        scores = new JLabel[3];

        // Create the 3 text areas
        scores[0] = new JLabel(); // Game status
        scores[1] = new JLabel(); // Emeralds remaining to win
        scores[2] = new JLabel(); // Emeralds stolen by Alien

        // Add the 3 text areas
        add(scores[0]);
        add(scores[1]);
        add(scores[2]);

        redrawScore();
    }


    public void redrawScore()
    {
        GameStatus gameStatus;
        String statusString;

        // Get the necessary data to put in the score panel
        gameStatus = score.gameStatus;
        switch (gameStatus) {
            case PLAYING:
                statusString = "Keep playing!";
                break;
            case WON:
                statusString = "You Win!";
                break;
            case LOST:
                statusString = "Bad luck, you loose!";
                break;
            default:
                statusString = "Unexpected outcome...";
        }

        scores[0].setText("Status: " + statusString);
        scores[1].setText("Emeralds remaining to win: " + score.emeraldsRemaining);
        scores[2].setText("Emeralds stolen by alien: " + score.emeraldsStolen);
        // repaint();

    }

}