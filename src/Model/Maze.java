package Model;

import java.util.*;
import java.util.stream.Collectors;

public class Maze implements IMaze {
    private List<ICave> caves;
    private Map<Integer, ICave> caveMap;
    private Random random;

    public Maze(int numCaves) {
        caves = new ArrayList<>();
        caveMap = new HashMap<>();
        random = new Random();

        // Create caves
        for (int i = 0; i < numCaves; i++) {
            ICave cave = new Cave(i);
            caves.add(cave);
            caveMap.put(i, cave);
        }

        // Connect caves in a pentagon structure
        connectPentagonStructure();
    }

    private void connectPentagonStructure() {
        // If we don't have enough caves, fallback to simple connection
        if (caves.size() < 20) {
            connectSimple();
            return;
        }

        // Clear any existing connections first
        // Note: This is a bit of a hack since we don't have a clean way to remove neighbors

        // Connect outer pentagon (first 5 caves)
        for (int i = 0; i < 5; i++) {
            int nextIndex = (i + 1) % 5;
            ICave cave = caves.get(i);
            ICave nextCave = caves.get(nextIndex);
            cave.addNeighbor(nextCave);
        }

        // Connect middle layer (caves 5-14) in a circle
        for (int i = 0; i < 10; i++) {
            int nextIndex = (i + 1) % 10 + 5;
            ICave cave = caves.get(i + 5);
            ICave nextCave = caves.get(nextIndex);
            cave.addNeighbor(nextCave);
        }

        // Connect inner pentagon (caves 15-19)
        for (int i = 0; i < 5; i++) {
            int nextIndex = (i + 1) % 5 + 15;
            ICave cave = caves.get(i + 15);
            ICave nextCave = caves.get(nextIndex);
            cave.addNeighbor(nextCave);
        }

        // Connect outer pentagon to middle layer
        for (int i = 0; i < 5; i++) {
            // Each vertex of outer pentagon connects to 2 caves in middle layer
            ICave outerCave = caves.get(i);

            // Connect to middle layer caves
            int middleIndex1 = (i * 2) % 10 + 5;
            int middleIndex2 = (i * 2 + 1) % 10 + 5;

            outerCave.addNeighbor(caves.get(middleIndex1));
            outerCave.addNeighbor(caves.get(middleIndex2));
        }

        // Connect middle layer to inner pentagon
        for (int i = 0; i < 10; i++) {
            // Each middle layer cave connects to 1 inner pentagon cave
            ICave middleCave = caves.get(i + 5);

            // Connect to inner pentagon cave
            int innerIndex = (i / 2) % 5 + 15;
            middleCave.addNeighbor(caves.get(innerIndex));
        }

        // Ensure each cave has exactly 3 connections
        ensureThreeConnections();
    }

    private void ensureThreeConnections() {
        for (ICave cave : caves) {
            List<ICave> neighbors = cave.getNeighbors();

            if (neighbors.size() < 3) {
                // Add connections until we have exactly 3
                int connectionsNeeded = 3 - neighbors.size();

                // Find caves that are not already connected
                List<ICave> possibleConnections = new ArrayList<>(caves);
                possibleConnections.remove(cave);  // Can't connect to self
                possibleConnections.removeAll(neighbors);  // Can't connect to existing neighbors

                // Sort by proximity (using index as a proxy)
                possibleConnections.sort((c1, c2) -> {
                    int id1 = c1.getId();
                    int id2 = c2.getId();
                    int caveId = cave.getId();

                    int dist1 = Math.abs(id1 - caveId);
                    int dist2 = Math.abs(id2 - caveId);

                    // Consider wraparound
                    if (dist1 > caves.size() / 2) {
                        dist1 = caves.size() - dist1;
                    }
                    if (dist2 > caves.size() / 2) {
                        dist2 = caves.size() - dist2;
                    }

                    return Integer.compare(dist1, dist2);
                });

                // Add the needed connections
                for (int i = 0; i < connectionsNeeded && i < possibleConnections.size(); i++) {
                    cave.addNeighbor(possibleConnections.get(i));
                }
            }

            // If we have more than 3 connections, do nothing
            // This could happen from mutual connections being made
        }
    }

    private void connectCaves(int id1, int id2) {
        ICave cave1 = caveMap.get(id1);
        ICave cave2 = caveMap.get(id2);
        if (cave1 != null && cave2 != null) {
            cave1.addNeighbor(cave2);
        }
    }

    private void connectSimple() {
        // For simplicity, connect each cave to 3 others
        for (int i = 0; i < caves.size(); i++) {
            ICave cave = caves.get(i);

            // Connect to the next 3 caves (wrapping around)
            for (int j = 1; j <= 3; j++) {
                int neighborIndex = (i + j) % caves.size();
                cave.addNeighbor(caves.get(neighborIndex));
            }
        }
    }

    @Override
    public ICave getRandomEmptyCave() {
        List<ICave> emptyCaves = caves.stream()
                .filter(c -> !c.hasPit() && !c.hasWumpus() && !c.hasBat())
                .collect(Collectors.toList());

        if (emptyCaves.isEmpty()) {
            return caves.get(random.nextInt(caves.size()));
        }

        return emptyCaves.get(random.nextInt(emptyCaves.size()));
    }

    @Override
    public List<ICave> getAllCaves() {
        return new ArrayList<>(caves);
    }

    @Override
    public ICave getCaveById(int id) {
        return caveMap.get(id);
    }

}