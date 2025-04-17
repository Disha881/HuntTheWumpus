package Controller;

import Model.IGameModel;
import View.CaveView;
import View.GameSetupDialog;
import View.GameView;

public class GameSetupController {
    private IGameModel model;
    private GameView view;
    private int numCaves = 20;  // Default set to 20 for pentagon layout
    private int numPits = 2;
    private int numBats = 2;
    private int numArrows = 3;

    public GameSetupController(IGameModel model, GameView view) {
        this.model = model;
        this.view = view;

        // Set up action listeners
        view.getNewGameButton().addActionListener(e -> startNewGame());
        view.getSetupButton().addActionListener(e -> showSetupDialog());

        // Set up cave listeners
        setupCaveListeners();
    }

    private void setupCaveListeners() {
        // Set move listeners (left-click)
        view.getMazeView().setMoveListeners(e -> {
            if (model.isGameOver()) return;

            CaveView source = (CaveView) e.getSource();
            model.movePlayer(source.getCave());
        });

        // Set shoot listeners (right-click)
        view.getMazeView().setShootListeners(e -> {
            if (model.isGameOver()) return;

            CaveView source = (CaveView) e.getSource();
            model.shootArrow(source.getCave());
        });
    }

    public void startNewGame() {
        // Initialize the model with new game settings
        model.initialize(numCaves, numPits, numBats, numArrows);

        // Reset the view to create a completely new maze view
        view.resetGame();

        // Set up listeners for the new cave views
        setupCaveListeners();

        // Extra credit: Check if game is winnable, if not restart
        if (!model.isWinnable()) {
            System.out.println("Generated an unwinnable maze. Restarting with a new configuration...");
            startNewGame();
        }
    }

    private void showSetupDialog() {
        GameSetupDialog dialog = new GameSetupDialog(view);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            // Update configuration
            numCaves = dialog.getNumCaves();
            numPits = dialog.getNumPits();
            numBats = dialog.getNumBats();
            numArrows = dialog.getNumArrows();

            // Start a new game with updated configuration
            startNewGame();
        }
    }

    // Setters for game configuration
    public void setNumCaves(int numCaves) {
        this.numCaves = numCaves;
    }

    public void setNumPits(int numPits) {
        this.numPits = numPits;
    }

    public void setNumBats(int numBats) {
        this.numBats = numBats;
    }

    public void setNumArrows(int numArrows) {
        this.numArrows = numArrows;
    }
}