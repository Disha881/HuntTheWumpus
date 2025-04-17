package Model;

public class Player implements IPlayer {
    private ICave currentCave;
    private int arrows;

    public Player(ICave startingCave, int initialArrows) {
        this.currentCave = startingCave;
        this.arrows = initialArrows;
    }

    @Override
    public ICave getCurrentCave() {
        return currentCave;
    }

    @Override
    public void setCurrentCave(ICave cave) {
        this.currentCave = cave;
    }

    @Override
    public int getArrows() {
        return arrows;
    }

    @Override
    public boolean hasArrows() {
        return arrows > 0;
    }

    @Override
    public void useArrow() {
        if (arrows > 0) {
            arrows--;
        }
    }
}
