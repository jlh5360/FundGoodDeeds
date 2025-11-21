// ==========================
// FundingPanel.java
// ==========================
package FundGoodDeeds.view.panels;

import FundGoodDeeds.controller.MasterController;
import FundGoodDeeds.model.FundingSource;

import javax.swing.*;
import java.awt.*;

/**
 * FundingPanel (V2)
 * - Swing version of CLI Funding Source management
 * - Allows:
 *   ✓ List Sources
 *   ✓ Add Source
 *   ✓ Edit Source
 *   ✓ Delete Source
 *   ✓ Refresh
 */
public class FundingPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final MasterController master;

    private final DefaultListModel<String> model = new DefaultListModel<>();
    private final JList<String> list = new JList<>(model);

    private final JButton refreshBtn = new JButton("Refresh");
    private final JButton addBtn     = new JButton("Add");
    private final JButton editBtn    = new JButton("Edit");
    private final JButton deleteBtn  = new JButton("Delete");

    public FundingPanel(MasterController master) {
        this.master = master;

        setLayout(new BorderLayout(8, 8));

        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(list), BorderLayout.CENTER);

        // Controls
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.add(refreshBtn);
        controls.add(addBtn);
        controls.add(editBtn);
        controls.add(deleteBtn);

        add(controls, BorderLayout.SOUTH);

        // Event listeners
        installListeners();

        refresh();
    }

    private void installListeners() {
        refreshBtn.addActionListener(e -> refresh());
        addBtn.addActionListener(e -> showAddDialog());
        editBtn.addActionListener(e -> showEditDialog());
        deleteBtn.addActionListener(e -> deleteSelected());
    }

    /** Reload funding sources from model. */
    public void refresh() {
        model.clear();
        for (FundingSource fs : master.getFundingController().getAll()) {
            model.addElement(fs.toString());
        }
    }

    // ====================================================
    // Add Funding Source
    // ====================================================
    private void showAddDialog() {
        JTextField nameField = new JTextField(12);
        JTextField amountField = new JTextField(8);

        JPanel panel = new JPanel(new GridLayout(0, 2, 6, 6));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("$/Unit:"));
        panel.add(amountField);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Add Funding Source",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) return;

        try {
            String name = nameField.getText().trim();
            double amt = Double.parseDouble(amountField.getText().trim());
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name cannot be empty.");
                return;
            }
            master.getFundingController().addFundingSource(name, amt);
            refresh();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid input.\n" + e.getMessage());
        }
    }

    // ====================================================
    // Edit Funding Source
    // ====================================================
    private void showEditDialog() {
        String selected = list.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a funding source first.");
            return;
        }

        // Extract name from "Name ($X.XX)" display string
        String oldName = selected.contains("(")
                ? selected.substring(0, selected.indexOf("(")).trim()
                : selected.trim();

        JTextField amountField = new JTextField(8);

        JPanel panel = new JPanel(new GridLayout(0, 2, 6, 6));
        panel.add(new JLabel("New $/Unit:"));
        panel.add(amountField);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Edit Funding Source: " + oldName,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) return;

        try {
            double amt = Double.parseDouble(amountField.getText().trim());
            master.getFundingController().updateFundingSource(oldName, amt);
            refresh();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid input.\n" + e.getMessage());
        }
    }

    // ====================================================
    // Delete Funding Source
    // ====================================================
    private void deleteSelected() {
        String selected = list.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a source to delete.");
            return;
        }

        String name = selected.contains("(")
                ? selected.substring(0, selected.indexOf("(")).trim()
                : selected.trim();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete '" + name + "'?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            master.getFundingController().deleteFundingSource(name);
            refresh();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error deleting source:\n" + e.getMessage());
        }
    }
}
