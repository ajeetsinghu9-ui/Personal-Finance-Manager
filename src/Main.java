import view.MainFrame;

import javax.swing.*;

/**
 * Application entry point.
 */
public class Main {
    public static void main(String[] args) {
        // Use system look-and-feel for a native appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Launch on the Event Dispatch Thread
        SwingUtilities.invokeLater(MainFrame::new);
    }
}
