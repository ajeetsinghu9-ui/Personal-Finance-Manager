package view;

import controller.TransactionController;
import model.Transaction;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * "Transactions" tab – shows all transactions in a table with Delete button.
 */
public class TransactionTable extends JPanel {

    private final TransactionController controller;
    private final DashboardPanel        dashboard;

    private final String[] COLUMNS = {"ID", "Type", "Category", "Amount (₹)", "Date", "Description"};
    private final DefaultTableModel tableModel = new DefaultTableModel(COLUMNS, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(tableModel);

    public TransactionTable(TransactionController controller, DashboardPanel dashboard) {
        this.controller = controller;
        this.dashboard  = dashboard;
        buildUI();
        refresh();
    }

    private void buildUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(250, 252, 255));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Heading
        JLabel heading = new JLabel("Transaction History");
        heading.setFont(new Font("Arial", Font.BOLD, 18));
        heading.setForeground(new Color(30, 60, 120));
        add(heading, BorderLayout.NORTH);

        // Table
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setRowHeight(26);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);

        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(80);
        table.getColumnModel().getColumn(2).setPreferredWidth(110);
        table.getColumnModel().getColumn(3).setPreferredWidth(110);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.getColumnModel().getColumn(5).setPreferredWidth(250);

        add(new JScrollPane(table), BorderLayout.CENTER);

        // Buttons
        JButton btnDelete  = new JButton("🗑 Delete Selected");
        JButton btnRefresh = new JButton("🔄 Refresh");

        styleButton(btnDelete,  new Color(231, 76,  60));
        styleButton(btnRefresh, new Color(41, 128, 185));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(new Color(250, 252, 255));
        btnPanel.add(btnRefresh);
        btnPanel.add(btnDelete);
        add(btnPanel, BorderLayout.SOUTH);

        btnDelete .addActionListener(e -> deleteSelected());
        btnRefresh.addActionListener(e -> refresh());
    }

    private void styleButton(JButton btn, Color color) {
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    /** Reload data from the database. */
    public void refresh() {
        tableModel.setRowCount(0);
        List<Transaction> list = controller.getAllTransactions();
        SimpleDateFormat sdf   = new SimpleDateFormat("yyyy-MM-dd");
        for (Transaction t : list) {
            tableModel.addRow(new Object[]{
                t.getId(),
                t.getType(),
                t.getCategoryName(),
                String.format("%.2f", t.getAmount()),
                sdf.format(t.getDate()),
                t.getDescription()
            });
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a transaction to delete.");
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete transaction ID " + id + "?", "Confirm Delete",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            if (controller.deleteTransaction(id)) {
                JOptionPane.showMessageDialog(this, "Deleted successfully.");
                refresh();
                dashboard.refresh();
            } else {
                JOptionPane.showMessageDialog(this, "Delete failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
