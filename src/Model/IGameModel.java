package Model;

public interface IGameModel {
    void initialize(int numCaves, int numPits, int numBats, int numArrows);
    boolean movePlayer(ICave targetCave);
    boolean shootArrow(ICave targetCave);
    boolean canSmellWumpus();
    boolean canFeelDraft();
    boolean canHearBats();
    boolean isGameOver();
    String getGameStatus();
    IPlayer getPlayer();
    IMaze getMaze();
    void addListener(GameModelListener listener);
    void removeListener(GameModelListener listener);
    boolean isWinnable();
}
