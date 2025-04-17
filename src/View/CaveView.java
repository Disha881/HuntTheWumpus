package View;

import Model.ICave;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CaveView extends JPanel {
    private ICave cave;
    private boolean isPlayerHere;
    private boolean isRevealed;
    private int size = 50; // Smaller size to match the first image
    private ActionListener moveListener;
    private ActionListener shootListener;

    public CaveView(ICave cave) {
        this.cave = cave;
        this.isPlayerHere = false;
        this.isRevealed = false;

        setPreferredSize(new Dimension(size, size));
        setOpaque(false); // Make panel transparent

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e) && moveListener != null) {
                    moveListener.actionPerformed(new ActionEvent(CaveView.this, ActionEvent.ACTION_PERFORMED, "move"));
                } else if (SwingUtilities.isRightMouseButton(e) && shootListener != null) {
                    shootListener.actionPerformed(new ActionEvent(CaveView.this, ActionEvent.ACTION_PERFORMED, "shoot"));
                }
            }
        });
    }

    public void setMoveListener(ActionListener listener) {
        this.moveListener = listener;
    }

    public void setShootListener(ActionListener listener) {
        this.shootListener = listener;
    }

    public void setPlayerHere(boolean isPlayerHere) {
        this.isPlayerHere = isPlayerHere;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Enable anti-aliasing for smoother graphics
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw cave - no square border, just the circle
        if (cave.hasBat() && isRevealed) {
            // Magenta circle for bats (as seen in the first image)
            g2d.setColor(Color.MAGENTA);
            g2d.fillOval(5, 5, size - 10, size - 10);
        } else {
            // Yellow circle for normal caves
            g2d.setColor(Color.YELLOW);
            g2d.fillOval(5, 5, size - 10, size - 10);
        }

        // Draw outline of circle
        g2d.setColor(Color.BLACK);
        g2d.drawOval(5, 5, size - 10, size - 10);

        // Draw hazards if revealed
        if (isRevealed) {
            if (cave.hasWumpus()) {
                // Red circle for Wumpus (as in the first image)
                g2d.setColor(Color.RED);
                g2d.fillOval(15, 15, 30, 30);
                g2d.setColor(Color.BLACK);
                g2d.drawOval(15, 15, 30, 30);
            } else if (cave.hasPit()) {
                // Black circle for pit
                g2d.setColor(Color.BLACK);
                g2d.fillOval(15, 15, 30, 30);
            }
        }

        // Draw player indicator (triangle) if player is here
        if (isPlayerHere) {
            // Triangle for player's current location (as in the first image)
            g2d.setColor(Color.YELLOW);
            int[] xPoints = {size/2, size/4, 3*size/4};
            int[] yPoints = {size/4, 3*size/4, 3*size/4};
            g2d.fillPolygon(xPoints, yPoints, 3);

            // Draw outline
            g2d.setColor(Color.BLACK);
            g2d.drawPolygon(xPoints, yPoints, 3);
        }

        // Draw cave ID in the center
        g2d.setColor(Color.BLACK);
        String idStr = String.valueOf(cave.getId());
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(idStr);
        int textHeight = fm.getHeight();
        g2d.drawString(idStr, (size - textWidth) / 2, (size + textHeight / 2) / 2);
    }

    public ICave getCave() {
        return cave;
    }

    public void setRevealed(boolean revealed) {
        this.isRevealed = revealed;
        repaint();
    }

    /**
     * Reset this cave view to its initial state
     */
    public void reset() {
        this.isPlayerHere = false;
        this.isRevealed = false;
        repaint();
    }
}