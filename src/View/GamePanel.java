// View/GamePanel.java
package View;

import Model.GameState;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GamePanel extends JPanel {
    Map<Integer, Point2D> caveLocations;
    private Map<Integer, List<Integer>> caveConnections;
    int playerLocation;
    int arrows;
    GameState gameState;
    private Map<Integer, Boolean> hasWumpus;
    private Map<Integer, Boolean> hasPit;
    private Map<Integer, Boolean> hasBats;
    private List<Integer> highlightedCaves;

    public GamePanel() {
        setPreferredSize(new Dimension(600, 600));
        setBackground(Color.WHITE);
        caveLocations = new HashMap<>();
        caveConnections = new HashMap<>();
        hasWumpus = new HashMap<>();
        hasPit = new HashMap<>();
        hasBats = new HashMap<>();
        highlightedCaves = null;

        // Create pentagon layout for caves
        setupPentagonLayout();
    }

    private void setupPentagonLayout() {
        // Center point
        int centerX = 300;
        int centerY = 300;

        // Define pentagon layers
        createPentagonLayer(centerX, centerY, 240, 1, 5); // Outer pentagon (caves 1-5)
        createPentagonLayer(centerX, centerY, 160, 6, 5); // Middle pentagon (caves 6-10)
        createPentagonLayer(centerX, centerY, 80, 11, 5); // Inner pentagon (caves 11-15)

        // Add center cave
        caveLocations.put(16, new Point2D.Double(centerX, centerY));

        // Add connections between pentagons (caves 17-20)
        double angleOffset = Math.PI / 5; // Offset for better positioning
        for (int i = 0; i < 4; i++) {
            double angle = 2 * Math.PI * i / 4 + angleOffset;
            double radius = 120; // Between inner and outer pentagons
            int x = centerX + (int)(radius * Math.cos(angle));
            int y = centerY + (int)(radius * Math.sin(angle));
            caveLocations.put(17 + i, new Point2D.Double(x, y));
        }
    }

    private void createPentagonLayer(int centerX, int centerY, int radius, int startId, int count) {
        for (int i = 0; i < count; i++) {
            double angle = 2 * Math.PI * i / count - Math.PI / 2; // Start from top
            int x = centerX + (int)(radius * Math.cos(angle));
            int y = centerY + (int)(radius * Math.sin(angle));
            caveLocations.put(startId + i, new Point2D.Double(x, y));
        }
    }

    public void setCaveConnections(Map<Integer, List<Integer>> connections) {
        this.caveConnections = connections;
        repaint();
    }

    public void updateDisplay(int playerLocation, Map<Integer, Boolean> hasWumpus,
                              Map<Integer, Boolean> hasPit, Map<Integer, Boolean> hasBats,
                              int arrows, GameState gameState) {
        this.playerLocation = playerLocation;
        this.hasWumpus = hasWumpus;
        this.hasPit = hasPit;
        this.hasBats = hasBats;
        this.arrows = arrows;
        this.gameState = gameState;
        repaint();
    }

    public void highlightAdjacentCaves(List<Integer> adjacentCaves) {
        this.highlightedCaves = adjacentCaves;
        repaint();
    }

    public void clearHighlighting() {
        this.highlightedCaves = null;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw connections
        g2d.setColor(Color.GRAY);
        g2d.setStroke(new BasicStroke(1.5f));
        for (Map.Entry<Integer, List<Integer>> entry : caveConnections.entrySet()) {
            int caveId1 = entry.getKey();
            Point2D p1 = caveLocations.get(caveId1);
            if (p1 != null) {
                for (int caveId2 : entry.getValue()) {
                    Point2D p2 = caveLocations.get(caveId2);
                    if (p2 != null && caveId2 > caveId1) { // Avoid drawing connections twice
                        g2d.drawLine((int) p1.getX(), (int) p1.getY(), (int) p2.getX(), (int) p2.getY());
                    }
                }
            }
        }

        // Draw caves as pentagons
        for (Map.Entry<Integer, Point2D> entry : caveLocations.entrySet()) {
            int caveId = entry.getKey();
            Point2D center = entry.getValue();

            // Create pentagon shape for each cave
            Polygon pentagon = createPentagonShape((int)center.getX(), (int)center.getY(), 25);

            // Determine cave color
            Color caveColor = Color.LIGHT_GRAY;

            if (highlightedCaves != null && highlightedCaves.contains(caveId)) {
                caveColor = Color.CYAN; // Highlight adjacent caves
            }

            if (caveId == playerLocation) {
                caveColor = Color.BLUE; // Player location
            }

            g2d.setColor(caveColor);
            g2d.fill(pentagon);

            // Draw cave outline
            g2d.setColor(Color.BLACK);
            g2d.draw(pentagon);

            // Add cave number
            g2d.drawString(String.valueOf(caveId), (int)center.getX() - 4, (int)center.getY() + 4);

            // If this is the player's location, draw a player marker
            if (caveId == playerLocation) {
                g2d.setColor(Color.WHITE);
                g2d.fillOval((int)center.getX() - 8, (int)center.getY() - 8, 16, 16);
            }

            // Draw icons for hazards if game is over or debugging
            if (gameState != GameState.PLAYING) {
                if (hasWumpus.getOrDefault(caveId, false)) {
                    g2d.setColor(Color.RED);
                    g2d.drawString("W", (int)center.getX() - 12, (int)center.getY() - 8);
                }
                if (hasPit.getOrDefault(caveId, false)) {
                    g2d.setColor(Color.BLACK);
                    g2d.drawString("P", (int)center.getX() + 4, (int)center.getY() - 8);
                }
                if (hasBats.getOrDefault(caveId, false)) {
                    g2d.setColor(Color.ORANGE);
                    g2d.drawString("B", (int)center.getX() - 4, (int)center.getY() + 12);
                }
            }
        }

        // Display arrows information
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("Arrows: " + arrows, 20, 30);

        // Display game state if not playing
        if (gameState != null && gameState != GameState.PLAYING) {
            g2d.setColor(Color.RED);
            g2d.drawString("Game State: " + gameState.toString(), 20, 60);
        }
    }

    private Polygon createPentagonShape(int centerX, int centerY, int radius) {
        Polygon pentagon = new Polygon();
        for (int i = 0; i < 5; i++) {
            double angle = 2 * Math.PI * i / 5 - Math.PI / 2; // Start from top
            int x = centerX + (int)(radius * Math.cos(angle));
            int y = centerY + (int)(radius * Math.sin(angle));
            pentagon.addPoint(x, y);
        }
        return pentagon;
    }
}