// Model/Game.java
package Model;

import java.util.*;

public class Game implements GameModel {
    private World world;
    private int numberOfCaves = 20; // Using 20 caves for a pentagon layout
    private int initialArrows = 3; // Default value, can be configurable
    private int numberOfPits = 2;   // Default value, can be configurable
    private int numberOfBats = 2;   // Default value, can be configurable

    public Game(int numberOfCaves, int numberOfPits, int numberOfBats, int initialArrows) {
        this.numberOfCaves = Math.max(numberOfCaves, 16); // Minimum 16 caves for pentagon layout
        this.numberOfPits = numberOfPits;
        this.numberOfBats = numberOfBats;
        this.initialArrows = initialArrows;
        setupWorld();
    }

    public Game(int numberOfCaves) {
        this.numberOfCaves = Math.max(numberOfCaves, 16); // Minimum 16 caves for pentagon layout
        setupWorld();
    }

    @Override
    public Map<Integer, Cave> getCaves() {
        return world.getCaves();
    }

    @Override
    public int getPlayerLocation() {
        return world.getPlayerLocation();
    }

    @Override
    public List<Integer> getAdjacentCaves(int caveId) {
        return world.getAdjacentCaves(caveId);
    }

    @Override
    public String movePlayer(int targetCaveId) {
        if (!world.getAdjacentCaves(world.getPlayerLocation()).contains(targetCaveId)) {
            return "You cannot move to that cave. It is not adjacent.";
        }

        world.setPlayerLocation(targetCaveId);
        Cave currentCave = world.getCaves().get(targetCaveId);

        if (currentCave.hasWumpus()) {
            world.setGameState(GameState.LOST_WUMPUS);
            return "You have stumbled upon the Wumpus! Game Over.";
        } else if (currentCave.hasPit()) {
            if (currentCave.hasBats() && new Random().nextBoolean()) {
                return handleBatEncounter();
            } else {
                world.setGameState(GameState.LOST_PIT);
                return "You have fallen into a bottomless pit! Game Over.";
            }
        } else if (currentCave.hasBats()) {
            return handleBatEncounter();
        } else {
            return "You have moved to cave " + targetCaveId + ".";
        }
    }

    private String handleBatEncounter() {
        Random random = new Random();
        List<Integer> allCaves = new ArrayList<>(world.getCaves().keySet());
        int newLocation = allCaves.get(random.nextInt(allCaves.size()));
        world.setPlayerLocation(newLocation);

        // After being transported, check the new cave for hazards
        Cave newCave = world.getCaves().get(newLocation);
        if (newCave.hasWumpus()) {
            world.setGameState(GameState.LOST_WUMPUS);
            return "Superbats carried you to cave " + newLocation + " where the Wumpus ate you! Game Over.";
        } else if (newCave.hasPit()) {
            world.setGameState(GameState.LOST_PIT);
            return "Superbats carried you to cave " + newLocation + " where you fell into a pit! Game Over.";
        } else {
            return "Superbats have picked you up and dropped you in cave " + newLocation + ".";
        }
    }

    @Override
    public String shootArrow(int targetCaveId) {
        if (world.getArrows() <= 0) {
            world.setGameState(GameState.LOST_NO_ARROWS);
            return "You have no arrows left! Game Over.";
        }
        world.decrementArrows();

        if (!world.getAdjacentCaves(world.getPlayerLocation()).contains(targetCaveId)) {
            return "You cannot shoot an arrow into that cave. It is not adjacent.";
        }

        if (world.getCaves().get(targetCaveId).hasWumpus()) {
            world.setWumpusAlive(false);
            world.setGameState(GameState.WON);
            return "You have shot and killed the Wumpus! You Win!";
        } else {
            if (world.getArrows() <= 0) {
                world.setGameState(GameState.LOST_NO_ARROWS);
                return "Your arrow missed! You have no arrows left. Game Over.";
            }
            return "Your arrow missed! You have " + world.getArrows() + " arrows left.";
        }
    }

    @Override
    public String getPercepts() {
        int playerLocation = world.getPlayerLocation();
        StringBuilder percepts = new StringBuilder();

        for (int adjacentCaveId : world.getAdjacentCaves(playerLocation)) {
            Cave adjacentCave = world.getCaves().get(adjacentCaveId);
            if (adjacentCave.hasWumpus()) {
                percepts.append("You smell a terrible stench. ");
            }
            if (adjacentCave.hasPit()) {
                percepts.append("You feel a draft. ");
            }
            if (adjacentCave.hasBats()) {
                percepts.append("You hear the rustling of bats. ");
            }
        }

        if (percepts.length() == 0) {
            percepts.append("All is quiet... ");
        }

        percepts.append("You have " + world.getArrows() + " arrows left.");
        return percepts.toString().trim();
    }

    @Override
    public GameState getGameState() {
        return world.getGameState();
    }

    @Override
    public int getArrows() {
        return world.getArrows();
    }

    @Override
    public boolean isWumpusAlive() {
        return world.isWumpusAlive();
    }

    @Override
    public List<Integer> getPlayerLocationHistory() {
        return world.getPlayerLocationHistory();
    }

    @Override
    public void restartGame() {
        setupWorld();
    }

    @Override
    public boolean isWinnable() {
        int wumpusLocation = -1;
        // Find wumpus location
        for (Map.Entry<Integer, Cave> entry : world.getCaves().entrySet()) {
            if (entry.getValue().hasWumpus()) {
                wumpusLocation = entry.getKey();
                break;
            }
        }

        if (wumpusLocation == -1) return false;

        int playerStartLocation = world.getPlayerLocation();
        List<Integer> adjacentToWumpus = world.getAdjacentCaves(wumpusLocation);

        for (int targetCave : adjacentToWumpus) {
            if (isReachableAndSafe(playerStartLocation, targetCave)) {
                return true;
            }
        }
        return false;
    }

    private boolean isReachableAndSafe(int start, int end) {
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();

        queue.offer(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            int currentCaveId = queue.poll();

            if (currentCaveId == end) {
                Cave targetCave = world.getCaves().get(end);
                return !targetCave.hasPit() && !targetCave.hasWumpus();
            }

            for (int neighborId : world.getAdjacentCaves(currentCaveId)) {
                if (!visited.contains(neighborId)) {
                    Cave neighborCave = world.getCaves().get(neighborId);
                    if (!neighborCave.hasPit() && !neighborCave.hasWumpus()) {
                        visited.add(neighborId);
                        queue.offer(neighborId);
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void checkAndRestartIfUnwinnable() {
        if (!isWinnable() && getGameState() == GameState.PLAYING) {
            System.out.println("Maze is unwinnable. Restarting game with a new setup.");
            setupWorld();
        }
    }

    private void setupWorld() {
        Map<Integer, Cave> caves = createPentagonCaves();
        world = new World(caves, initialArrows);
        placeEntities();
    }

    private Map<Integer, Cave> createPentagonCaves() {
        Map<Integer, Cave> caves = new HashMap<>();

        // Create caves
        for (int i = 1; i <= numberOfCaves; i++) {
            caves.put(i, new Cave(i));
        }

        // Create pentagon structure - outer pentagon (1-5)
        connectCaves(caves, 1, 2);
        connectCaves(caves, 2, 3);
        connectCaves(caves, 3, 4);
        connectCaves(caves, 4, 5);
        connectCaves(caves, 5, 1);

        // Middle pentagon (6-10)
        connectCaves(caves, 6, 7);
        connectCaves(caves, 7, 8);
        connectCaves(caves, 8, 9);
        connectCaves(caves, 9, 10);
        connectCaves(caves, 10, 6);

        // Inner pentagon (11-15)
        connectCaves(caves, 11, 12);
        connectCaves(caves, 12, 13);
        connectCaves(caves, 13, 14);
        connectCaves(caves, 14, 15);
        connectCaves(caves, 15, 11);

        // Connect outer to middle pentagon
        connectCaves(caves, 1, 6);
        connectCaves(caves, 2, 7);
        connectCaves(caves, 3, 8);
        connectCaves(caves, 4, 9);
        connectCaves(caves, 5, 10);

        // Connect middle to inner pentagon
        connectCaves(caves, 6, 11);
        connectCaves(caves, 7, 12);
        connectCaves(caves, 8, 13);
        connectCaves(caves, 9, 14);
        connectCaves(caves, 10, 15);

        // Center cave (16) connects to inner pentagon
        if (caves.containsKey(16)) {
            connectCaves(caves, 16, 11);
            connectCaves(caves, 16, 12);
            connectCaves(caves, 16, 13);
            connectCaves(caves, 16, 14);
            connectCaves(caves, 16, 15);
        }

        // Additional connector caves (17-20) if they exist
        if (caves.containsKey(17)) {
            // Connect diagonal caves
            connectCaves(caves, 17, 1);
            connectCaves(caves, 17, 6);
            connectCaves(caves, 17, 11);
            connectCaves(caves, 17, 16);
        }

        if (caves.containsKey(18)) {
            connectCaves(caves, 18, 3);
            connectCaves(caves, 18, 8);
            connectCaves(caves, 18, 13);
            connectCaves(caves, 18, 16);
        }

        if (caves.containsKey(19)) {
            connectCaves(caves, 19, 5);
            connectCaves(caves, 19, 10);
            connectCaves(caves, 19, 15);
            connectCaves(caves, 19, 16);
        }

        if (caves.containsKey(20)) {
            connectCaves(caves, 20, 2);
            connectCaves(caves, 20, 7);
            connectCaves(caves, 20, 12);
            connectCaves(caves, 20, 16);
        }

        return caves;
    }

    private void connectCaves(Map<Integer, Cave> caves, int id1, int id2) {
        Cave cave1 = caves.get(id1);
        Cave cave2 = caves.get(id2);
        if (cave1 != null && cave2 != null) {
            cave1.addAdjacentCave(cave2);
        }
    }

    private void placeEntities() {
        Random random = new Random();
        List<Integer> availableCaves = new ArrayList<>(world.getCaves().keySet());

        // Place player
        int playerStart = availableCaves.remove(random.nextInt(availableCaves.size()));
        world.setPlayerLocation(playerStart);

        // Place wumpus
        int wumpusStart;
        do {
            wumpusStart = availableCaves.remove(random.nextInt(availableCaves.size()));
        } while (wumpusStart == playerStart);
        world.getCaves().get(wumpusStart).setHasWumpus(true);
        world.setWumpusLocation(wumpusStart);

        // Place pits
        List<Integer> pitLocations = new ArrayList<>();
        for (int i = 0; i < numberOfPits && !availableCaves.isEmpty(); i++) {
            int pitLocation = availableCaves.remove(random.nextInt(availableCaves.size()));
            world.getCaves().get(pitLocation).setHasPit(true);
            pitLocations.add(pitLocation);
        }
        world.setPitLocations(pitLocations);

        // Place bats - allow overlapping with pits for 50/50 chance
        List<Integer> batLocations = new ArrayList<>();
        List<Integer> allCavesForBats = new ArrayList<>(world.getCaves().keySet());
        allCavesForBats.remove(playerStart);
        allCavesForBats.remove(wumpusStart);

        for (int i = 0; i < numberOfBats && !allCavesForBats.isEmpty(); i++) {
            int batLocation = allCavesForBats.remove(random.nextInt(allCavesForBats.size()));
            world.getCaves().get(batLocation).setHasBats(true);
            batLocations.add(batLocation);
        }
        world.setBatLocations(batLocations);

        // Check if the maze is winnable
        checkAndRestartIfUnwinnable();
    }

    public World getWorld() {
        return world;
    }
}