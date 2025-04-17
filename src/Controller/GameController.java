// Controller/GameController.java
package Controller;

import Model.GameModel;
import View.GameView;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GameController implements UserInputListener {
    private GameModel model;
    private GameView view;

    public GameController(GameModel model, GameView view) {
        this.model = model;
        this.view = view;
        initializeView();
        updateView();
    }

    private void initializeView() {
        Map<Integer, List<Integer>> connections = model.getCaves().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().getAdjacentCaves().stream() // Get adjacent caves list and then stream
                                .map(Model.Cave::getId)
                                .collect(Collectors.toList())
                ));
        view.setCaveConnections(connections);
    }

    private void updateView() {
        int playerLocation = model.getPlayerLocation();
        Map<Integer, Boolean> hasWumpus = model.getCaves().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().hasWumpus()));
        Map<Integer, Boolean> hasPit = model.getCaves().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().hasPit()));
        Map<Integer, Boolean> hasBats = model.getCaves().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().hasBats()));
        int arrows = model.getArrows();
        Model.GameState gameState = model.getGameState();

        view.updateDisplay(playerLocation, hasWumpus, hasPit, hasBats, arrows, gameState);
        view.displayMessage(model.getPercepts());
        view.highlightAdjacentCaves(model.getAdjacentCaves(playerLocation));
        view.updateArrows(arrows);
        view.updateGameState(gameState);
        view.updatePlayerLocation(playerLocation);

        if (gameState != Model.GameState.PLAYING) {
            view.clearHighlighting();
        }
    }

    @Override
    public void movePlayer(int targetCaveId) {
        if (model.getGameState() == Model.GameState.PLAYING) {
            String result = model.movePlayer(targetCaveId);
            view.displayMessage(result);
            updateView();
        } else {
            view.displayMessage("The game is over.");
        }
    }

    @Override
    public void shootArrow(int targetCaveId) {
        if (model.getGameState() == Model.GameState.PLAYING) {
            String result = model.shootArrow(targetCaveId);
            view.displayMessage(result);
            updateView();
        } else {
            view.displayMessage("The game is over.");
        }
    }

    @Override
    public void restartGame() {
        model.restartGame();
        updateView();
    }
}