package view;

import controller.TransactionController;

import javax.swing.*;
import java.awt.*;

/**
 * Root application window.  Hosts four tab panels.
 */
public class MainFrame extends JFrame {

    private final TransactionController controller = new TransactionController();

    public MainFrame() {
        setTitle("Personal Finance Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 620);
        setLocationRelativeTo(null);
        setResizable(true);

        // Tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Arial", Font.BOLD, 13));

        DashboardPanel    dashboard    = new DashboardPanel(controller);
        TransactionForm   form         = new TransactionForm(controller, dashboard);
        TransactionTable  table        = new TransactionTable(controller, dashboard);
        ForecastPanel     forecast     = new ForecastPanel(controller);

        tabs.addTab("📊 Dashboard",        dashboard);
        tabs.addTab("➕ Add Transaction",  form);
        tabs.addTab("📋 Transactions",     table);
        tabs.addTab("🔮 Forecast",         forecast);

        // Refresh dashboard whenever the user switches back to it
        tabs.addChangeListener(e -> {
            int idx = tabs.getSelectedIndex();
            if (idx == 0) dashboard.refresh();
            if (idx == 2) table.refresh();
        });

        add(tabs);
        setVisible(true);
    }
}
