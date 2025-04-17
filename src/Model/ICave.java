package Model;

import java.util.List;

public interface ICave {
    int getId();
    void addNeighbor(ICave cave);
    List<ICave> getNeighbors();
    boolean hasPit();
    void setHasPit(boolean hasPit);
    boolean hasWumpus();
    void setHasWumpus(boolean hasWumpus);
    boolean hasBat();
    void setHasBat(boolean hasBat);
    boolean isDangerous();
}
