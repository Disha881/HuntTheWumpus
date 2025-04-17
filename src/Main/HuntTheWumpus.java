package Main;

import Controller.GameSetupController;
import Model.GameModel;
import Model.IGameModel;
import View.GameView;

import javax.swing.*;

public class HuntTheWumpus {
    public static void main(String[] args) {
        // Use SwingUtilities.invokeLater for thread safety
        SwingUtilities.invokeLater(() -> {
            // Create model
            IGameModel model = new GameModel();

            // Initialize model with default values - 20 caves for pentagon layout
            model.initialize(20, 2, 2, 3);

            // Create view
            GameView view = new GameView(model);

            // Create controller and connect to view
            GameSetupController controller = new GameSetupController(model, view);

            // Start an initial game
            controller.startNewGame();
        });
    }
}