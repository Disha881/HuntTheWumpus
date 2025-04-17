package Model;

public interface IPlayer {
    ICave getCurrentCave();
    void setCurrentCave(ICave cave);
    int getArrows();
    boolean hasArrows();
    void useArrow();
}
