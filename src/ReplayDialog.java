import javax.swing.*;

public class ReplayDialog {

    public ReplayDialog() {
    }

    public int AskReplayQuestion() {
        //Custom button text
        Object[] options = {"Play again", "Quit game"};
        int n = JOptionPane.showOptionDialog(null,
                "Would you like to play again?",
                "Game Over",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]);

        return n;

    }


}