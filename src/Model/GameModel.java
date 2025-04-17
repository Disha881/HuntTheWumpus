// Model/GameModel.java
package Model;

import java.util.List;
import java.util.Map;

public interface GameModel {
    Map<Integer, Cave> getCaves();
    int getPlayerLocation();
    List<Integer> getAdjacentCaves(int caveId);
    String movePlayer(int targetCaveId);
    String shootArrow(int targetCaveId);
    String getPercepts();
    GameState getGameState();
    int getArrows();
    boolean isWumpusAlive();
    List<Integer> getPlayerLocationHistory();
    void restartGame(); // Method to reset the game
    boolean isWinnable(); // For the extra credit
    void checkAndRestartIfUnwinnable(); // For the extra credit
}