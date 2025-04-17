package View;

import javax.swing.*;
import java.awt.*;

public class StatusView extends JPanel {
    private JLabel arrowsLabel;
    private JLabel sensoryLabel;
    private JLabel gameStatusLabel;
    private JLabel winnableLabel;

    public StatusView() {
        setLayout(new GridLayout(4, 1));
        setBorder(BorderFactory.createTitledBorder("Status"));

        arrowsLabel = new JLabel("Arrows: 3");
        sensoryLabel = new JLabel("You sense: nothing unusual.");
        gameStatusLabel = new JLabel("Game in progress...");
        winnableLabel = new JLabel("Maze is winnable: Yes");

        add(arrowsLabel);
        add(sensoryLabel);
        add(gameStatusLabel);
        add(winnableLabel);

    }

    public void updateArrows(int arrows) {
        arrowsLabel.setText("Arrows: " + arrows);
    }

    public void updateSensory(boolean smellWumpus, boolean feelDraft, boolean hearBats) {
        StringBuilder sb = new StringBuilder("You sense: ");
        boolean noSensation = true;

        if (smellWumpus) {
            sb.append("a foul smell (Wumpus nearby) ");
            noSensation = false;
        }

        if (feelDraft) {
            sb.append("a draft (Pit nearby) ");
            noSensation = false;
        }

        if (hearBats) {
            sb.append("rustling (Bats nearby)");
            noSensation = false;
        }

        if (noSensation) {
            sb.append("nothing unusual.");
        }

        sensoryLabel.setText(sb.toString());
    }

    public void updateGameStatus(String status) {
        switch (status) {
            case "won":
                gameStatusLabel.setText("You won! You killed the Wumpus!");
                break;
            case "lost":
                gameStatusLabel.setText("Game over! You lost.");
                break;
            default:
                gameStatusLabel.setText("Game in progress...");
        }
    }

    public void updateWinnableStatus(boolean isWinnable) {
        winnableLabel.setText("Maze is winnable: " + (isWinnable ? "Yes" : "No"));
    }
}
