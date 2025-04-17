package View;

import Model.GameModelListener;
import Model.IGameModel;

import javax.swing.*;
import java.awt.*;

public class GameView extends JFrame implements GameModelListener {
    private IGameModel model;
    private MazeView mazeView;
    private StatusView statusView;
    private JButton newGameButton;
    private JButton setupButton;
    private JLabel arrowsLabel;

    public GameView(IGameModel model) {
        this.model = model;
        model.addListener(this);

        // Set up the frame
        setTitle("Hunt the Wumpus");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create components
        mazeView = new MazeView(model.getMaze());
        statusView = new StatusView();

        // Create title and button panel
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Hunt The Wumpus", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        newGameButton = new JButton("New Game");
        setupButton = new JButton("Setup");
        buttonPanel.add(newGameButton);
        buttonPanel.add(setupButton);

        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.CENTER);

        // Create a panel for the maze and arrows display
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(mazeView, BorderLayout.CENTER);

        // Create arrows display on the right
        arrowsLabel = new JLabel("arrows " + model.getPlayer().getArrows());
        arrowsLabel.setHorizontalAlignment(JLabel.RIGHT);
        arrowsLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 20));
        arrowsLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(arrowsLabel, BorderLayout.NORTH);
        centerPanel.add(rightPanel, BorderLayout.EAST);

        // Add components to frame
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(statusView, BorderLayout.SOUTH);

        // Set size that matches the proportions in the first image
        setSize(700, 650);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void gameStateChanged() {
        // Update maze view
        mazeView.updatePlayerLocation(model.getPlayer().getCurrentCave());

        // Update arrows count in the corner
        arrowsLabel.setText("arrows " + model.getPlayer().getArrows());

        // Update status view
        statusView.updateArrows(model.getPlayer().getArrows());
        statusView.updateSensory(
                model.canSmellWumpus(),
                model.canFeelDraft(),
                model.canHearBats()
        );
        statusView.updateGameStatus(model.getGameStatus());
        statusView.updateWinnableStatus(model.isWinnable());

        // If game over, reveal all caves
        if (model.isGameOver()) {
            mazeView.revealAllCaves();

            // Display game result
            String message;
            if (model.getGameStatus().equals("won")) {
                message = "Congratulations! You killed the Wumpus!";
            } else {
                message = "Game Over! You lost.";
            }

            JOptionPane.showMessageDialog(this, message);
        }
    }

    /**
     * Fully resets the view when a new game is started.
     * This creates a completely new maze view.
     */
    /**
     * Fully resets the view when a new game is started.
     */
    public void resetGame() {
        // Reset the maze view (all cave views will be reset)
        mazeView.reset();

        // Update the player location
        mazeView.updatePlayerLocation(model.getPlayer().getCurrentCave());

        // Update the arrows display
        arrowsLabel.setText("arrows " + model.getPlayer().getArrows());

        // Update the status view
        statusView.updateArrows(model.getPlayer().getArrows());
        statusView.updateSensory(
                model.canSmellWumpus(),
                model.canFeelDraft(),
                model.canHearBats()
        );
        statusView.updateGameStatus("ongoing");
        statusView.updateWinnableStatus(model.isWinnable());

        // Repaint everything
        repaint();
    }

    private void setupCaveListeners() {
        // Set move listeners (left-click)
        mazeView.setMoveListeners(e -> {
            if (model.isGameOver()) return;

            CaveView source = (CaveView) e.getSource();
            model.movePlayer(source.getCave());
        });

        // Set shoot listeners (right-click)
        mazeView.setShootListeners(e -> {
            if (model.isGameOver()) return;

            CaveView source = (CaveView) e.getSource();
            model.shootArrow(source.getCave());
        });
    }

    public MazeView getMazeView() {
        return mazeView;
    }

    public JButton getNewGameButton() {
        return newGameButton;
    }

    public JButton getSetupButton() {
        return setupButton;
    }
}