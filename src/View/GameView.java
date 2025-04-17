// View/GameView.java
package View;

import Model.GameState;
import java.util.Map;

public interface GameView {
    void updateDisplay(int playerLocation, Map<Integer, Boolean> hasWumpus,
                       Map<Integer, Boolean> hasPit, Map<Integer, Boolean> hasBats,
                       int arrows, GameState gameState);
    void displayMessage(String message);
    void setCaveConnections(Map<Integer, java.util.List<Integer>> connections);
    void highlightAdjacentCaves(java.util.List<Integer> adjacentCaves);
    void clearHighlighting();
    void updateArrows(int arrows);
    void updateGameState(GameState gameState);
    void updatePlayerLocation(int playerLocation);
    // Potentially methods for handling user interactions (though these might be better in a listener)
}