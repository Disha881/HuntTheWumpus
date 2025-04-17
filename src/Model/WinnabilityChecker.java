package Model;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class WinnabilityChecker {
    /**
     * Determines if the current state of the game is winnable.
     * A game is winnable if there exists a safe path from the player's current position
     * to a position where the player can shoot the Wumpus.
     *
     * @param model The game model
     * @return true if the game is winnable, false otherwise
     */
    public static boolean isGameWinnable(IGameModel model) {
        IMaze maze = model.getMaze();
        IPlayer player = model.getPlayer();

        // If the player is already in danger, game is unwinnable
        if (player.getCurrentCave().isDangerous()) {
            return false;
        }

        // Find the Wumpus cave
        ICave wumpusCave = findWumpusCave(maze);
        if (wumpusCave == null) {
            return false; // Should never happen
        }

        // Use BFS to find all reachable safe caves from player's position
        Set<ICave> reachableSafeCaves = findReachableSafeCaves(player.getCurrentCave());

        // Check if any cave adjacent to Wumpus is safe and reachable
        for (ICave cave : wumpusCave.getNeighbors()) {
            if (!cave.isDangerous() && reachableSafeCaves.contains(cave)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Finds the cave containing the Wumpus.
     *
     * @param maze The game maze
     * @return The cave containing the Wumpus, or null if not found
     */
    private static ICave findWumpusCave(IMaze maze) {
        for (ICave cave : maze.getAllCaves()) {
            if (cave.hasWumpus()) {
                return cave;
            }
        }
        return null;
    }

    /**
     * Finds all caves that are safely reachable from the starting cave.
     * A cave is safely reachable if there exists a path from the starting cave
     * to that cave that does not go through any dangerous caves.
     *
     * @param startCave The starting cave
     * @return A set of all safely reachable caves
     */
    private static Set<ICave> findReachableSafeCaves(ICave startCave) {
        Set<ICave> reachableCaves = new HashSet<>();
        Queue<ICave> queue = new LinkedList<>();
        Set<ICave> visited = new HashSet<>();

        queue.add(startCave);
        visited.add(startCave);

        while (!queue.isEmpty()) {
            ICave current = queue.poll();
            reachableCaves.add(current);

            for (ICave neighbor : current.getNeighbors()) {
                if (!visited.contains(neighbor) && !neighbor.isDangerous()) {
                    queue.add(neighbor);
                    visited.add(neighbor);
                }
            }
        }

        return reachableCaves;
    }
}
