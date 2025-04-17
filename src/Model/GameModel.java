package Model;

import java.util.*;

public class GameModel implements IGameModel {
    private IMaze maze;
    private IPlayer player;
    private ICave wumpusCave;
    private List<ICave> pitCaves;
    private List<ICave> batCaves;
    private Random random;
    private boolean gameOver;
    private String gameStatus; // "ongoing", "won", "lost"

    // For observer pattern
    private List<GameModelListener> listeners;

    public GameModel() {
        random = new Random();
        listeners = new ArrayList<>();
        gameStatus = "ongoing";
        pitCaves = new ArrayList<>();
        batCaves = new ArrayList<>();
    }

    @Override
    public void initialize(int numCaves, int numPits, int numBats, int numArrows) {
        maze = new Maze(numCaves);
        pitCaves = new ArrayList<>();
        batCaves = new ArrayList<>();
        gameOver = false;
        gameStatus = "ongoing";

        // Place Wumpus
        wumpusCave = maze.getRandomEmptyCave();
        wumpusCave.setHasWumpus(true);

        // Place pits
        for (int i = 0; i < numPits; i++) {
            ICave pitCave = maze.getRandomEmptyCave();
            pitCave.setHasPit(true);
            pitCaves.add(pitCave);
        }

        // Place bats
        for (int i = 0; i < numBats; i++) {
            ICave batCave = maze.getRandomEmptyCave();
            batCave.setHasBat(true);
            batCaves.add(batCave);
        }

        // Place player
        ICave playerCave = maze.getRandomEmptyCave();
        player = new Player(playerCave, numArrows);

        notifyListeners();
    }

    @Override
    public boolean movePlayer(ICave targetCave) {
        if (!player.getCurrentCave().getNeighbors().contains(targetCave)) {
            return false; // Can't move to non-adjacent cave
        }

        player.setCurrentCave(targetCave);

        // Check for hazards
        if (targetCave.hasWumpus()) {
            gameStatus = "lost";
            gameOver = true;
            notifyListeners();
            return true;
        }

        if (targetCave.hasPit() && targetCave.hasBat()) {
            // 50% chance for each hazard
            if (random.nextBoolean()) {
                handlePit();
            } else {
                handleBat();
            }
        } else if (targetCave.hasPit()) {
            handlePit();
        } else if (targetCave.hasBat()) {
            handleBat();
        }

        checkArrows();
        notifyListeners();
        return true;
    }

    private void handlePit() {
        gameStatus = "lost";
        gameOver = true;
    }

    private void handleBat() {
        ICave randomCave = maze.getAllCaves().get(random.nextInt(maze.getAllCaves().size()));
        player.setCurrentCave(randomCave);

        // Check if randomly moved to hazard
        if (randomCave.hasWumpus()) {
            gameStatus = "lost";
            gameOver = true;
        } else if (randomCave.hasPit()) {
            handlePit();
        }
    }

    @Override
    public boolean shootArrow(ICave targetCave) {
        if (!player.hasArrows()) {
            return false;
        }

        if (!player.getCurrentCave().getNeighbors().contains(targetCave)) {
            return false; // Can't shoot non-adjacent cave
        }

        player.useArrow();

        if (targetCave.hasWumpus()) {
            gameStatus = "won";
            gameOver = true;
        }

        checkArrows();
        notifyListeners();
        return true;
    }

    private void checkArrows() {
        if (!player.hasArrows() && !gameOver) {
            gameStatus = "lost";
            gameOver = true;
        }
    }

    @Override
    public boolean canSmellWumpus() {
        return player.getCurrentCave().getNeighbors().stream()
                .anyMatch(ICave::hasWumpus);
    }

    @Override
    public boolean canFeelDraft() {
        return player.getCurrentCave().getNeighbors().stream()
                .anyMatch(ICave::hasPit);
    }

    @Override
    public boolean canHearBats() {
        return player.getCurrentCave().getNeighbors().stream()
                .anyMatch(ICave::hasBat);
    }

    @Override
    public boolean isGameOver() {
        return gameOver;
    }

    @Override
    public String getGameStatus() {
        return gameStatus;
    }

    @Override
    public IPlayer getPlayer() {
        return player;
    }

    @Override
    public IMaze getMaze() {
        return maze;
    }

    // Observer pattern for view updates
    @Override
    public void addListener(GameModelListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(GameModelListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (GameModelListener listener : listeners) {
            listener.gameStateChanged();
        }
    }

    // Extra credit: Check if game is winnable
    @Override
    public boolean isWinnable() {
        // If player is already in a dangerous location, game is unwinnable
        if (player.getCurrentCave().isDangerous()) {
            return false;
        }

        // We need to find if there is a safe path to any cave adjacent to the Wumpus
        // Use BFS to find all reachable safe caves from player's current position

        Set<ICave> reachableSafeCaves = new HashSet<>();
        Queue<ICave> queue = new LinkedList<>();
        Set<ICave> visited = new HashSet<>();

        queue.add(player.getCurrentCave());
        visited.add(player.getCurrentCave());

        while (!queue.isEmpty()) {
            ICave current = queue.poll();
            reachableSafeCaves.add(current);

            for (ICave neighbor : current.getNeighbors()) {
                if (!visited.contains(neighbor) && !neighbor.isDangerous()) {
                    queue.add(neighbor);
                    visited.add(neighbor);
                }
            }
        }

        // Check if any cave adjacent to Wumpus is safe and reachable
        for (ICave cave : wumpusCave.getNeighbors()) {
            if (reachableSafeCaves.contains(cave)) {
                return true;
            }
        }

        return false;
    }
}
