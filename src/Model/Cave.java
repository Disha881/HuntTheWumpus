// Model/Cave.java
package Model;

import java.util.ArrayList;
import java.util.List;

public class Cave {
    private int id;
    private List<Cave> adjacentCaves;
    private boolean hasWumpus;
    private boolean hasPit;
    private boolean hasBats;

    public Cave(int id) {
        this.id = id;
        this.adjacentCaves = new ArrayList<>();
        this.hasWumpus = false;
        this.hasPit = false;
        this.hasBats = false;
    }

    public int getId() {
        return id;
    }

    public List<Cave> getAdjacentCaves() {
        return adjacentCaves;
    }

    public void addAdjacentCave(Cave cave) {
        if (!adjacentCaves.contains(cave)) {
            adjacentCaves.add(cave);
            cave.addAdjacentCave(this); // Caves are connected bidirectionally
        }
    }

    public boolean hasWumpus() {
        return hasWumpus;
    }

    public void setHasWumpus(boolean hasWumpus) {
        this.hasWumpus = hasWumpus;
    }

    public boolean hasPit() {
        return hasPit;
    }

    public void setHasPit(boolean hasPit) {
        this.hasPit = hasPit;
    }

    public boolean hasBats() {
        return hasBats;
    }

    public void setHasBats(boolean hasBats) {
        this.hasBats = hasBats;
    }
}