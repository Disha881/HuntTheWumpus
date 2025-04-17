package Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Cave implements ICave {
    private int id;
    private List<ICave> neighbors;
    private boolean hasPit;
    private boolean hasWumpus;
    private boolean hasBat;

    public Cave(int id) {
        this.id = id;
        this.neighbors = new ArrayList<>();
        this.hasPit = false;
        this.hasWumpus = false;
        this.hasBat = false;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void addNeighbor(ICave cave) {
        if (!neighbors.contains(cave)) {
            neighbors.add(cave);
            // Avoid infinite recursion by checking if the other cave already has this as a neighbor
            if (!cave.getNeighbors().contains(this)) {
                cave.addNeighbor(this);
            }
        }
    }

    @Override
    public List<ICave> getNeighbors() {
        return new ArrayList<>(neighbors);
    }

    @Override
    public boolean hasPit() {
        return hasPit;
    }

    @Override
    public void setHasPit(boolean hasPit) {
        this.hasPit = hasPit;
    }

    @Override
    public boolean hasWumpus() {
        return hasWumpus;
    }

    @Override
    public void setHasWumpus(boolean hasWumpus) {
        this.hasWumpus = hasWumpus;
    }

    @Override
    public boolean hasBat() {
        return hasBat;
    }

    @Override
    public void setHasBat(boolean hasBat) {
        this.hasBat = hasBat;
    }

    @Override
    public boolean isDangerous() {
        return hasPit || hasWumpus;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Cave cave = (Cave) obj;
        return id == cave.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
