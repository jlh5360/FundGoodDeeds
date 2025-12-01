// ==========================
// FundingPanel.java
// =========================
package FundGoodDeeds.view.panels;

import FundGoodDeeds.controller.MasterController;
import FundGoodDeeds.model.FundingSource;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * FundingPanel (V2)
 * - Swing version of CLI Funding Source management
 * - Allows:
 *   -List Sources
 *   -Add Source
 *   -Edit Source
 *   -Delete Source
 *   -Refresh
 */
@SuppressWarnings("deprecation")
public class FundingPanel extends JPanel implements Observer {

    private final MasterController master;
    private JTable fundingTable;
    private DefaultTableModel tableModel;
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);

    // Constructor 
    public FundingPanel(MasterController master) {
        this.master = master;
        master.registerObservers(this);

        setLayout(new BorderLayout(5, 5));
        
        // Table Setup
    
        // Simple two-column table: funding source name + amount.
        // Each row = one FundingSource from the repository.
        String[] columnNames = {"Name", "Initial Amount ($)"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        fundingTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(fundingTable);
        add(scrollPane, BorderLayout.CENTER);

        // Button Panel Setup
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBorder(BorderFactory.createTitledBorder("Funding Source Actions (List Funding Sources)"));

        JButton addSourceButton = new JButton("Add Source");
        addSourceButton.addActionListener(e -> addFundingSource());
        
        JButton editSourceButton = new JButton("Edit Source Amount");
        editSourceButton.addActionListener(e -> editFundingSource());
        
        JButton deleteSourceButton = new JButton("Delete Source");
        deleteSourceButton.addActionListener(e -> deleteFundingSource());

        buttonPanel.add(addSourceButton);
        buttonPanel.add(editSourceButton);
        buttonPanel.add(deleteSourceButton);
        
        add(buttonPanel, BorderLayout.SOUTH);

        updateTable();
    }

    // Add Funding Source — asks for name + amount, then delegates.
    private void addFundingSource() {
        // Basic two-input dialog flow: name to amount
        String name = JOptionPane.showInputDialog(this, "Enter Funding Source Name:", "Add Funding Source", JOptionPane.PLAIN_MESSAGE);
        if (name == null || name.trim().isEmpty()) return;

        String amountStr = JOptionPane.showInputDialog(this, "Enter Initial Amount ($):", "Add Funding Source", JOptionPane.PLAIN_MESSAGE);
        if (amountStr == null || amountStr.trim().isEmpty()) return;
        
        try {
            double amount = Double.parseDouble(amountStr.trim());
            // Controller handles validation + adding the object.
            master.getFundingController().addFundingSource(name.trim(), amount);
            
            JOptionPane.showMessageDialog(this, "Funding Source '" + name.trim() + "' added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid amount entered.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Edit Funding Source — changes only the amount field.
    private void editFundingSource() {
        int selectedRow = fundingTable.getSelectedRow();
        if (selectedRow == -1) {
            // no selected rows = cant edit anything
            JOptionPane.showMessageDialog(this, "Please select a Funding Source to edit.", "Selection Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Grab existing name (names are unique identifiers in this design).
        String name = (String) tableModel.getValueAt(selectedRow, 0);
        
        // prompt for new amount
        String newAmountStr = JOptionPane.showInputDialog(this, 
            "Enter new initial amount for '" + name + "':", 
            "Edit Funding Source", 
            JOptionPane.PLAIN_MESSAGE);
        
        if (newAmountStr != null && !newAmountStr.trim().isEmpty()) {
            try {
                double newAmount = Double.parseDouble(newAmountStr.trim());
                
                // Pass the update request through the controller.
                master.getFundingController().updateFundingSource(name, newAmount);
                
                JOptionPane.showMessageDialog(this, "Amount for '" + name + "' updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount entered.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Delete Funding Source — remove it entirely.
    private void deleteFundingSource() {
        int selectedRow = fundingTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a Funding Source to delete.", "Selection Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String name = (String) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete '" + name + "'?", 
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                master.getFundingController().deleteFundingSource(name);
                JOptionPane.showMessageDialog(this, "'" + name + "' deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting item: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Rebuild the funding table — Observer callback + manual refresh.
    private void updateTable() {
        tableModel.setRowCount(0);

        List<FundingSource> sources = master.getFundingController().getAll();
        for (FundingSource source : sources) {
            tableModel.addRow(new Object[]{
                source.getName(),
                currencyFormatter.format(source.getAmount())
            });
        }
    }

    // Every time model updates → refresh view automatically.
    @Override
    public void update(Observable o, Object arg) {
        updateTable();
    }
}