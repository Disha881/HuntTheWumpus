// Model/World.java
package Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class World {
    private Map<Integer, Cave> caves;
    private List<Integer> playerLocationHistory;
    private int playerLocation;
    private int wumpusLocation;
    private List<Integer> pitLocations;
    private List<Integer> batLocations;
    private int arrows;
    private boolean wumpusAlive;
    private GameState gameState;

    public World(Map<Integer, Cave> caves, int initialArrows) {
        this.caves = caves;
        this.playerLocationHistory = new ArrayList<>();
        this.arrows = initialArrows;
        this.wumpusAlive = true;
        this.gameState = GameState.PLAYING;
        // Locations will be set during game setup
    }

    public Map<Integer, Cave> getCaves() {
        return caves;
    }

    public int getPlayerLocation() {
        return playerLocation;
    }

    public void setPlayerLocation(int playerLocation) {
        this.playerLocation = playerLocation;
        this.playerLocationHistory.add(playerLocation);
    }

    public int getWumpusLocation() {
        return wumpusLocation;
    }

    public void setWumpusLocation(int wumpusLocation) {
        this.wumpusLocation = wumpusLocation;
    }

    public List<Integer> getPitLocations() {
        return pitLocations;
    }

    public void setPitLocations(List<Integer> pitLocations) {
        this.pitLocations = pitLocations;
    }

    public List<Integer> getBatLocations() {
        return batLocations;
    }

    public void setBatLocations(List<Integer> batLocations) {
        this.batLocations = batLocations;
    }

    public int getArrows() {
        return arrows;
    }

    public void decrementArrows() {
        this.arrows--;
        if (this.arrows < 0) {
            this.arrows = 0; // Ensure it doesn't go below zero
            if (gameState == GameState.PLAYING) {
                this.gameState = GameState.LOST_NO_ARROWS;
            }
        }
    }

    public boolean isWumpusAlive() {
        return wumpusAlive;
    }

    public void setWumpusAlive(boolean wumpusAlive) {
        this.wumpusAlive = wumpusAlive;
        if (!wumpusAlive && gameState == GameState.PLAYING) {
            this.gameState = GameState.WON;
        }
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public List<Integer> getAdjacentCaves(int caveId) {
        List<Integer> adjacentIds = new ArrayList<>();
        Cave currentCave = caves.get(caveId);
        if (currentCave != null) {
            for (Cave adjacentCave : currentCave.getAdjacentCaves()) {
                adjacentIds.add(adjacentCave.getId());
            }
        }
        return adjacentIds;
    }

    public boolean isAdjacent(int cave1Id, int cave2Id) {
        Cave cave1 = caves.get(cave1Id);
        if (cave1 != null) {
            for (Cave adjacentCave : cave1.getAdjacentCaves()) {
                if (adjacentCave.getId() == cave2Id) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<Integer> getPlayerLocationHistory() {
        return playerLocationHistory;
    }

}