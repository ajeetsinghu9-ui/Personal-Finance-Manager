package view;

import controller.TransactionController;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * "Forecast" tab – shows basic average forecast AND trend-based forecast.
 */
public class ForecastPanel extends JPanel {

    private final TransactionController controller;

    // Basic forecast labels
    private final JLabel lblBasicIncome  = new JLabel("-");
    private final JLabel lblBasicExpense = new JLabel("-");
    private final JLabel lblBasicBalance = new JLabel("-");

    // Trend forecast labels
    private final JLabel lblTrendIncome  = new JLabel("-");
    private final JLabel lblTrendExpense = new JLabel("-");
    private final JLabel lblTrendBalance = new JLabel("-");

    public ForecastPanel(TransactionController controller) {
        this.controller = controller;
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 248, 255));
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Title
        JLabel title = new JLabel("Financial Forecast for Next Month", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(new Color(30, 60, 120));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        // Two forecast panels side by side
        JPanel center = new JPanel(new GridLayout(1, 2, 20, 0));
        center.setBackground(new Color(245, 248, 255));
        center.add(buildSection("📈 Basic Forecast (Monthly Average)",
                                "Based on average monthly income & expense.",
                                lblBasicIncome, lblBasicExpense, lblBasicBalance,
                                new Color(39, 174, 96)));
        center.add(buildSection("📉 Trend Forecast (Linear Regression)",
                                "Based on last 6 months trend (linear extrapolation).",
                                lblTrendIncome, lblTrendExpense, lblTrendBalance,
                                new Color(142, 68, 173)));
        add(center, BorderLayout.CENTER);

        // Run button
        JButton btnRun = new JButton("🔮 Run Forecast");
        btnRun.setBackground(new Color(41, 128, 185));
        btnRun.setForeground(Color.WHITE);
        btnRun.setFont(new Font("Arial", Font.BOLD, 14));
        btnRun.setFocusPainted(false);
        btnRun.setBorderPainted(false);
        btnRun.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRun.setPreferredSize(new Dimension(200, 42));
        btnRun.addActionListener(e -> runForecast());

        JPanel south = new JPanel();
        south.setBackground(new Color(245, 248, 255));
        south.add(btnRun);

        JLabel note = new JLabel(
            "<html><center><i>Note: Forecasts are estimates based on past data.</i></center></html>",
            SwingConstants.CENTER);
        note.setForeground(Color.GRAY);
        note.setFont(new Font("Arial", Font.ITALIC, 11));
        south.add(note);
        add(south, BorderLayout.SOUTH);
    }

    private JPanel buildSection(String title, String subtitle,
                                JLabel incLbl, JLabel expLbl, JLabel balLbl,
                                Color accentColor) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(accentColor, 2),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0; gc.gridy = 0; gc.gridwidth = 2;
        gc.insets = new Insets(0, 0, 5, 0);
        gc.anchor = GridBagConstraints.CENTER;

        JLabel ttl = new JLabel(title, SwingConstants.CENTER);
        ttl.setFont(new Font("Arial", Font.BOLD, 14));
        ttl.setForeground(accentColor);
        panel.add(ttl, gc);

        gc.gridy = 1;
        JLabel sub = new JLabel("<html><center>" + subtitle + "</center></html>", SwingConstants.CENTER);
        sub.setFont(new Font("Arial", Font.ITALIC, 11));
        sub.setForeground(Color.GRAY);
        panel.add(sub, gc);

        gc.gridwidth = 1; gc.anchor = GridBagConstraints.WEST;
        gc.insets = new Insets(12, 5, 4, 5);

        addRow(panel, gc, 2, "Predicted Income:",  incLbl, new Color(39, 174, 96));
        addRow(panel, gc, 3, "Predicted Expense:", expLbl, new Color(231, 76, 60));
        addRow(panel, gc, 4, "Predicted Balance:", balLbl, accentColor);

        return panel;
    }

    private void addRow(JPanel panel, GridBagConstraints gc, int row,
                        String labelText, JLabel valueLabel, Color color) {
        gc.gridx = 0; gc.gridy = row;
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Arial", Font.PLAIN, 13));
        panel.add(lbl, gc);

        gc.gridx = 1;
        valueLabel.setFont(new Font("Arial", Font.BOLD, 15));
        valueLabel.setForeground(color);
        panel.add(valueLabel, gc);
    }

    private void runForecast() {
        double[] basic = controller.basicForecast();
        lblBasicIncome .setText(String.format("₹ %,.2f", basic[0]));
        lblBasicExpense.setText(String.format("₹ %,.2f", basic[1]));
        lblBasicBalance.setText(String.format("₹ %,.2f", basic[2]));

        double[] trend = controller.trendForecast();
        lblTrendIncome .setText(String.format("₹ %,.2f", trend[0]));
        lblTrendExpense.setText(String.format("₹ %,.2f", trend[1]));
        lblTrendBalance.setText(String.format("₹ %,.2f", trend[2]));
    }
}
