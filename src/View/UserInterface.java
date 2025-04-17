// View/UserInterface.java
package View;

import Controller.GameController;
import Model.Game;
import Model.GameModel;
import Model.GameState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.Map;

public class UserInterface extends JFrame implements GameView {
    private GamePanel gamePanel;
    private MessageDisplay messageDisplay;
    private JPanel controlPanel;
    private JButton restartButton;
    private JLabel arrowsLabel;
    private GameController controller; // Reference to the Controller
    private Map<Integer, Point2D> caveLocations;

    public UserInterface() {
        setTitle("Hunt the Wumpus - Pentagon Edition");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create components
        gamePanel = new GamePanel();
        messageDisplay = new MessageDisplay();
        setupControlPanel();

        // Add components to frame
        add(gamePanel, BorderLayout.CENTER);
        add(messageDisplay, BorderLayout.SOUTH);
        add(controlPanel, BorderLayout.NORTH);

        // Setup mouse listener for cave interaction
        setupMouseListener();

        // Configure frame
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        caveLocations = gamePanel.caveLocations;
    }

    private void setupControlPanel() {
        controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        // Create restart button
        restartButton = new JButton("Restart Game");
        restartButton.addActionListener(e -> {
            if (controller != null) {
                controller.restartGame();
            }
        });

        // Create arrows label
        arrowsLabel = new JLabel("Arrows: 3");

        // Add components to control panel
        controlPanel.add(restartButton);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(arrowsLabel);

        // Add game instructions
        JLabel instructionsLabel = new JLabel("Left-click: Move | Right-click: Shoot Arrow");
        controlPanel.add(Box.createHorizontalStrut(40));
        controlPanel.add(instructionsLabel);
    }

    private void setupMouseListener() {
        gamePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (controller != null) {
                    Point clickedPoint = e.getPoint();
                    int clickedCaveId = getClickedCave(clickedPoint);
                    if (clickedCaveId != -1) {
                        if (SwingUtilities.isLeftMouseButton(e)) {
                            controller.movePlayer(clickedCaveId);
                        } else if (SwingUtilities.isRightMouseButton(e)) {
                            controller.shootArrow(clickedCaveId);
                        }
                    }
                }
            }
        });
    }

    public void setController(GameController controller) {
        this.controller = controller;
    }

    private int getClickedCave(Point point) {
        for (Map.Entry<Integer, Point2D> entry : caveLocations.entrySet()) {
            int caveId = entry.getKey();
            Point2D center = entry.getValue();
            double distance = point.distance(center);
            if (distance <= 25) { // Pentagon radius
                return caveId;
            }
        }
        return -1;
    }

    @Override
    public void updateDisplay(int playerLocation, Map<Integer, Boolean> hasWumpus,
                              Map<Integer, Boolean> hasPit, Map<Integer, Boolean> hasBats,
                              int arrows, GameState gameState) {
        gamePanel.updateDisplay(playerLocation, hasWumpus, hasPit, hasBats, arrows, gameState);
    }

    @Override
    public void displayMessage(String message) {
        messageDisplay.displayMessage(message);
    }

    @Override
    public void setCaveConnections(Map<Integer, List<Integer>> connections) {
        gamePanel.setCaveConnections(connections);
    }

    @Override
    public void highlightAdjacentCaves(List<Integer> adjacentCaves) {
        gamePanel.highlightAdjacentCaves(adjacentCaves);
    }

    @Override
    public void clearHighlighting() {
        gamePanel.clearHighlighting();
    }

    @Override
    public void updateArrows(int arrows) {
        arrowsLabel.setText("Arrows: " + arrows);
        gamePanel.arrows = arrows;
        gamePanel.repaint();
    }

    @Override
    public void updateGameState(GameState gameState) {
        gamePanel.gameState = gameState;
        gamePanel.repaint();

        // Show game over dialog if needed
        if (gameState != GameState.PLAYING) {
            String message;
            String title;
            int messageType;

            if (gameState == GameState.WON) {
                message = "Congratulations! You killed the Wumpus and won the game!";
                title = "Victory!";
                messageType = JOptionPane.INFORMATION_MESSAGE;
            } else {
                String reason = "";
                switch (gameState) {
                    case LOST_PIT:
                        reason = "You fell into a bottomless pit!";
                        break;
                    case LOST_WUMPUS:
                        reason = "You were eaten by the Wumpus!";
                        break;
                    case LOST_NO_ARROWS:
                        reason = "You ran out of arrows!";
                        break;
                }
                message = "Game Over! " + reason;
                title = "Game Over";
                messageType = JOptionPane.WARNING_MESSAGE;
            }

            // Show game over dialog
            JOptionPane.showMessageDialog(this, message, title, messageType);
        }
    }

    @Override
    public void updatePlayerLocation(int playerLocation) {
        gamePanel.playerLocation = playerLocation;
        gamePanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Create the game with 20 caves, 2 pits, 2 bats, and 3 arrows
            GameModel model = new Game(20, 2, 2, 3);
            UserInterface view = new UserInterface();
            GameController controller = new GameController(model, view);
            view.setController(controller);
        });
    }
}