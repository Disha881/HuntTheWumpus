// Controller/UserInputListener.java
package Controller;

public interface UserInputListener {
    void movePlayer(int targetCaveId);
    void shootArrow(int targetCaveId);
    void restartGame();
}