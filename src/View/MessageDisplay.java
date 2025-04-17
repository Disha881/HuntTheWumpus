// View/MessageDisplay.java
package View;

import javax.swing.*;
import java.awt.*;

public class MessageDisplay extends JLabel {
    public MessageDisplay() {
        super("Welcome to Hunt the Wumpus!");
        setPreferredSize(new Dimension(600, 30));
        setHorizontalAlignment(SwingConstants.CENTER);
        setFont(new Font("Arial", Font.PLAIN, 14));
    }

    public void displayMessage(String message) {
        setText(message);
    }
}