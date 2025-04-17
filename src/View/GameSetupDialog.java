package View;

import javax.swing.*;
import java.awt.*;

public class GameSetupDialog extends JDialog {
    private JSpinner caveSpinner;
    private JSpinner pitSpinner;
    private JSpinner batSpinner;
    private JSpinner arrowSpinner;
    private JButton okButton;
    private JButton cancelButton;
    private boolean confirmed = false;

    public GameSetupDialog(JFrame parent) {
        super(parent, "Game Setup", true);

        // Create spinner components with default values
        // Use 20 as default and ensure values that work with pentagon layout (multiples of 5)
        caveSpinner = new JSpinner(new SpinnerNumberModel(20, 20, 50, 5));
        pitSpinner = new JSpinner(new SpinnerNumberModel(2, 1, 10, 1));
        batSpinner = new JSpinner(new SpinnerNumberModel(2, 1, 10, 1));
        arrowSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 10, 1));

        okButton = new JButton("OK");
        cancelButton = new JButton("Cancel");

        // Set up layout
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("Number of Caves:"));
        panel.add(caveSpinner);
        panel.add(new JLabel("Number of Pits:"));
        panel.add(pitSpinner);
        panel.add(new JLabel("Number of Bats:"));
        panel.add(batSpinner);
        panel.add(new JLabel("Number of Arrows:"));
        panel.add(arrowSpinner);
        panel.add(okButton);
        panel.add(cancelButton);

        // Add tooltip about cave count
        JLabel caveTip = new JLabel("(Must be a multiple of 5 for pentagon layout)");
        caveTip.setFont(new Font(caveTip.getFont().getName(), Font.ITALIC, 10));

        // Create a new panel for the tip
        JPanel tipPanel = new JPanel(new BorderLayout());
        tipPanel.add(caveTip, BorderLayout.CENTER);

        // Create a new container panel with BorderLayout
        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.add(panel, BorderLayout.CENTER);
        containerPanel.add(tipPanel, BorderLayout.SOUTH);

        // Add action listeners
        okButton.addActionListener(e -> {
            confirmed = true;
            dispose();
        });

        cancelButton.addActionListener(e -> dispose());

        setContentPane(containerPanel);
        pack();
        setLocationRelativeTo(parent);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public int getNumCaves() {
        return (Integer) caveSpinner.getValue();
    }

    public int getNumPits() {
        return (Integer) pitSpinner.getValue();
    }

    public int getNumBats() {
        return (Integer) batSpinner.getValue();
    }

    public int getNumArrows() {
        return (Integer) arrowSpinner.getValue();
    }
}