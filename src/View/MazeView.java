package View;

import Model.ICave;
import Model.IMaze;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class MazeView extends JPanel {
    private Map<ICave, CaveView> caveViewMap;
    private Map<ICave, Point> cavePositions;

    public MazeView(IMaze maze) {
        caveViewMap = new HashMap<>();
        cavePositions = new HashMap<>();

        setLayout(null); // Use absolute positioning
        setPreferredSize(new Dimension(600, 600));

        // Create views for each cave
        initializeCaveViews(maze);

        // Position caves in a pentagon pattern
        positionCavesInPentagon(maze);
    }

    private void initializeCaveViews(IMaze maze) {
        for (ICave cave : maze.getAllCaves()) {
            CaveView caveView = new CaveView(cave);
            caveViewMap.put(cave, caveView);
            add(caveView);
        }
    }

    private void positionCavesInPentagon(IMaze maze) {
        List<ICave> caves = maze.getAllCaves();
        int centerX = 300;
        int centerY = 300;

        // If we don't have enough caves, fallback to circle layout
        if (caves.size() < 20) {
            positionCavesInCircle(maze);
            return;
        }

        // Calculate positions based on layers
        int outerRadius = 250;  // Outer pentagon (5 caves)
        int middleRadius = 150; // Middle circle (10 caves)
        int innerRadius = 80;   // Inner pentagon (5 caves)

        // Position outer pentagon (5 caves)
        for (int i = 0; i < 5; i++) {
            double angle = 2 * Math.PI * i / 5 - Math.PI / 2; // Start from top
            int x = (int) (centerX + outerRadius * Math.cos(angle));
            int y = (int) (centerY + outerRadius * Math.sin(angle));

            CaveView caveView = caveViewMap.get(caves.get(i));
            x -= caveView.getPreferredSize().width / 2;
            y -= caveView.getPreferredSize().height / 2;

            caveView.setBounds(x, y, caveView.getPreferredSize().width, caveView.getPreferredSize().height);
            cavePositions.put(caves.get(i), new Point(x, y));
        }

        // Position middle circle (10 caves) - caves 5-14
        for (int i = 0; i < 10; i++) {
            double angle = 2 * Math.PI * i / 10 - Math.PI / 2; // Start from top
            int x = (int) (centerX + middleRadius * Math.cos(angle));
            int y = (int) (centerY + middleRadius * Math.sin(angle));

            CaveView caveView = caveViewMap.get(caves.get(i + 5));
            x -= caveView.getPreferredSize().width / 2;
            y -= caveView.getPreferredSize().height / 2;

            caveView.setBounds(x, y, caveView.getPreferredSize().width, caveView.getPreferredSize().height);
            cavePositions.put(caves.get(i + 5), new Point(x, y));
        }

        // Position inner pentagon (5 caves) - caves 15-19
        for (int i = 0; i < 5; i++) {
            double angle = 2 * Math.PI * i / 5 - Math.PI / 2; // Start from top
            int x = (int) (centerX + innerRadius * Math.cos(angle));
            int y = (int) (centerY + innerRadius * Math.sin(angle));

            CaveView caveView = caveViewMap.get(caves.get(i + 15));
            x -= caveView.getPreferredSize().width / 2;
            y -= caveView.getPreferredSize().height / 2;

            caveView.setBounds(x, y, caveView.getPreferredSize().width, caveView.getPreferredSize().height);
            cavePositions.put(caves.get(i + 15), new Point(x, y));
        }

        // If there are more caves, position them in the center
        if (caves.size() > 20) {
            for (int i = 20; i < caves.size(); i++) {
                CaveView caveView = caveViewMap.get(caves.get(i));
                int x = centerX - caveView.getPreferredSize().width / 2;
                int y = centerY - caveView.getPreferredSize().height / 2;

                caveView.setBounds(x, y, caveView.getPreferredSize().width, caveView.getPreferredSize().height);
                cavePositions.put(caves.get(i), new Point(x, y));
            }
        }
    }

    private void positionCavesInCircle(IMaze maze) {
        // Fallback method for fewer caves
        List<ICave> caves = maze.getAllCaves();
        int centerX = 300;
        int centerY = 300;
        int radius = 250;

        for (int i = 0; i < caves.size(); i++) {
            ICave cave = caves.get(i);
            CaveView caveView = caveViewMap.get(cave);

            double angle = 2 * Math.PI * i / caves.size();
            int x = (int) (centerX + radius * Math.cos(angle)) - caveView.getPreferredSize().width / 2;
            int y = (int) (centerY + radius * Math.sin(angle)) - caveView.getPreferredSize().height / 2;

            caveView.setBounds(x, y, caveView.getPreferredSize().width, caveView.getPreferredSize().height);
            cavePositions.put(cave, new Point(x, y));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Draw connections between caves
        for (Map.Entry<ICave, CaveView> entry : caveViewMap.entrySet()) {
            ICave cave = entry.getKey();
            CaveView caveView = entry.getValue();

            Point p1 = new Point(
                    caveView.getX() + caveView.getWidth() / 2,
                    caveView.getY() + caveView.getHeight() / 2
            );

            // Draw lines to all neighbors
            for (ICave neighbor : cave.getNeighbors()) {
                CaveView neighborView = caveViewMap.get(neighbor);
                if (neighborView != null) {
                    Point p2 = new Point(
                            neighborView.getX() + neighborView.getWidth() / 2,
                            neighborView.getY() + neighborView.getHeight() / 2
                    );

                    g2d.setColor(Color.DARK_GRAY);
                    g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
                }
            }
        }
    }

    public void updatePlayerLocation(ICave playerCave) {
        // Clear previous player location
        for (CaveView caveView : caveViewMap.values()) {
            caveView.setPlayerHere(false);
        }

        // Set new player location
        CaveView playerCaveView = caveViewMap.get(playerCave);
        if (playerCaveView != null) {
            playerCaveView.setPlayerHere(true);
        }
    }

    public void revealAllCaves() {
        for (CaveView caveView : caveViewMap.values()) {
            caveView.setRevealed(true);
        }
    }

    /**
     * Reset all cave views
     */
    public void reset() {
        for (CaveView caveView : caveViewMap.values()) {
            caveView.reset();
        }
        repaint();
    }

    public CaveView getCaveViewFor(ICave cave) {
        return caveViewMap.get(cave);
    }

    public void setMoveListeners(ActionListener listener) {
        for (CaveView caveView : caveViewMap.values()) {
            caveView.setMoveListener(listener);
        }
    }

    public void setShootListeners(ActionListener listener) {
        for (CaveView caveView : caveViewMap.values()) {
            caveView.setShootListener(listener);
        }
    }
}