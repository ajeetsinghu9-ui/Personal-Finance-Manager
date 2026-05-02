package view;

import controller.TransactionController;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Dashboard tab – shows total income, expense and current balance.
 */
public class DashboardPanel extends JPanel {

    private final TransactionController controller;

    private final JLabel lblIncome  = new JLabel("₹ 0.00");
    private final JLabel lblExpense = new JLabel("₹ 0.00");
    private final JLabel lblBalance = new JLabel("₹ 0.00");

    public DashboardPanel(TransactionController controller) {
        this.controller = controller;
        buildUI();
        refresh();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 248, 255));

        // Title
        JLabel title = new JLabel("Personal Finance Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(new Color(30, 60, 120));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // Cards panel
        JPanel cards = new JPanel(new GridLayout(1, 3, 20, 0));
        cards.setBackground(new Color(245, 248, 255));
        cards.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        cards.add(buildCard("Total Income",  lblIncome,  new Color(39, 174, 96)));
        cards.add(buildCard("Total Expense", lblExpense, new Color(231, 76,  60)));
        cards.add(buildCard("Balance",       lblBalance, new Color(41, 128, 185)));

        add(cards, BorderLayout.CENTER);

        // Tip footer
        JLabel tip = new JLabel("Tip: Use the Add Transaction tab to record income or expenses.",
                                SwingConstants.CENTER);
        tip.setFont(new Font("Arial", Font.ITALIC, 12));
        tip.setForeground(Color.GRAY);
        tip.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        add(tip, BorderLayout.SOUTH);
    }

    private JPanel buildCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker(), 1),
            BorderFactory.createEmptyBorder(20, 10, 20, 10)
        ));

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0; gc.gridy = 0;

        JLabel titleLbl = new JLabel(title, SwingConstants.CENTER);
        titleLbl.setFont(new Font("Arial", Font.BOLD, 14));
        titleLbl.setForeground(Color.WHITE);
        card.add(titleLbl, gc);

        gc.gridy = 1;
        gc.insets = new Insets(10, 0, 0, 0);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 26));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(valueLabel, gc);

        return card;
    }

    /** Called whenever data changes – refreshes all summary labels. */
    public void refresh() {
        double income  = controller.getTotalIncome();
        double expense = controller.getTotalExpense();
        double balance = controller.getBalance();

        lblIncome .setText(String.format("₹ %,.2f", income));
        lblExpense.setText(String.format("₹ %,.2f", expense));
        lblBalance.setText(String.format("₹ %,.2f", balance));

        // Colour balance label based on positive/negative
        lblBalance.setForeground(balance >= 0 ? Color.WHITE : new Color(255, 200, 200));
    }
}
