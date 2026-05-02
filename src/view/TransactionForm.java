package view;

import controller.TransactionController;
import model.Category;
import model.Transaction;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * "Add Transaction" tab.  Also used internally by TransactionTable for editing.
 */
public class TransactionForm extends JPanel {

    private final TransactionController controller;
    private final DashboardPanel        dashboard;

    // Form fields
    private final JComboBox<String>   cmbType        = new JComboBox<>(new String[]{"Income", "Expense"});
    private final JComboBox<Category> cmbCategory    = new JComboBox<>();
    private final JTextField          txtAmount      = new JTextField(15);
    private final JTextField          txtDate        = new JTextField("yyyy-MM-dd", 15);
    private final JTextField          txtDescription = new JTextField(15);
    private final JButton             btnSave        = new JButton("💾 Save Transaction");
    private final JButton             btnClear       = new JButton("🔄 Clear");

    private Integer editingId = null;   // null = new, non-null = edit mode

    public TransactionForm(TransactionController controller, DashboardPanel dashboard) {
        this.controller = controller;
        this.dashboard  = dashboard;
        buildUI();
        loadCategories();
    }

    private void buildUI() {
        setLayout(new GridBagLayout());
        setBackground(new Color(250, 252, 255));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets  = new Insets(10, 15, 5, 15);
        gc.fill    = GridBagConstraints.HORIZONTAL;
        gc.anchor  = GridBagConstraints.WEST;

        // Heading
        JLabel heading = new JLabel("Add / Edit Transaction");
        heading.setFont(new Font("Arial", Font.BOLD, 18));
        heading.setForeground(new Color(30, 60, 120));
        gc.gridx = 0; gc.gridy = 0; gc.gridwidth = 2;
        gc.insets = new Insets(20, 15, 15, 15);
        add(heading, gc);

        gc.gridwidth = 1;
        gc.insets    = new Insets(8, 15, 5, 10);

        addRow(gc, "Type:",        cmbType,        1);
        addRow(gc, "Category:",    cmbCategory,    2);
        addRow(gc, "Amount (₹):",  txtAmount,      3);
        addRow(gc, "Date:",        txtDate,        4);
        addRow(gc, "Description:", txtDescription, 5);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnPanel.setBackground(new Color(250, 252, 255));
        styleButton(btnSave, new Color(39, 174, 96));
        styleButton(btnClear, new Color(127, 140, 141));
        btnPanel.add(btnSave);
        btnPanel.add(btnClear);

        gc.gridx = 0; gc.gridy = 6; gc.gridwidth = 2;
        gc.insets = new Insets(20, 15, 15, 15);
        add(btnPanel, gc);

        // Wire listeners
        btnSave .addActionListener(e -> saveTransaction());
        btnClear.addActionListener(e -> clearForm());
    }

    private void addRow(GridBagConstraints gc, String labelText, JComponent field, int row) {
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Arial", Font.PLAIN, 13));
        gc.gridx = 0; gc.gridy = row; gc.weightx = 0.3;
        add(lbl, gc);

        if (field instanceof JTextField) ((JTextField)field).setFont(new Font("Arial", Font.PLAIN, 13));
        gc.gridx = 1; gc.weightx = 0.7;
        add(field, gc);
    }

    private void styleButton(JButton btn, Color color) {
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(180, 38));
    }

    private void loadCategories() {
        cmbCategory.removeAllItems();
        List<Category> cats = controller.getCategories();
        for (Category c : cats) cmbCategory.addItem(c);
    }

    // ---- Save -------------------------------------------------------

    private void saveTransaction() {
        try {
            String type        = (String) cmbType.getSelectedItem();
            Category category  = (Category) cmbCategory.getSelectedItem();
            double amount      = Double.parseDouble(txtAmount.getText().trim());
            Date date          = new SimpleDateFormat("yyyy-MM-dd").parse(txtDate.getText().trim());
            String description = txtDescription.getText().trim();

            if (category == null) {
                JOptionPane.showMessageDialog(this, "Please select a category.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Transaction t = new Transaction(type, category.getId(), amount, date, description);

            boolean ok;
            if (editingId != null) {
                t.setId(editingId);
                ok = controller.updateTransaction(t);
            } else {
                ok = controller.addTransaction(t);
            }

            if (ok) {
                JOptionPane.showMessageDialog(this, "Transaction saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                dashboard.refresh();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save transaction.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount.", "Validation", JOptionPane.WARNING_MESSAGE);
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, "Date format must be yyyy-MM-dd.", "Validation", JOptionPane.WARNING_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void clearForm() {
        editingId = null;
        cmbType.setSelectedIndex(0);
        if (cmbCategory.getItemCount() > 0) cmbCategory.setSelectedIndex(0);
        txtAmount.setText("");
        txtDate.setText("yyyy-MM-dd");
        txtDescription.setText("");
        btnSave.setText("💾 Save Transaction");
    }

    /** Called from TransactionTable to pre-fill the form for editing. */
    public void loadForEdit(Transaction t) {
        editingId = t.getId();
        cmbType.setSelectedItem(t.getType());
        // Select matching category
        for (int i = 0; i < cmbCategory.getItemCount(); i++) {
            if (cmbCategory.getItemAt(i).getId() == t.getCategoryId()) {
                cmbCategory.setSelectedIndex(i);
                break;
            }
        }
        txtAmount.setText(String.valueOf(t.getAmount()));
        txtDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(t.getDate()));
        txtDescription.setText(t.getDescription());
        btnSave.setText("✏️ Update Transaction");
    }
}
