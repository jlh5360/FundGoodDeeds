// ==========================
// LedgerPanel.java
// ==========================
package FundGoodDeeds.view.panels;

import FundGoodDeeds.controller.MasterController;
import FundGoodDeeds.model.LedgerEntity;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * LedgerPanel (V2)
 * - Shows daily ledger summary (same info as ConsoleView header)
 * - Provides buttons to:
 *   -Refresh summary
 *   -Add Need Fulfillment
 *   -Add Funding Income
 *
 * Deeper per-entry views can be added later if needed.
 */

@SuppressWarnings("deprecation")
public class LedgerPanel extends JPanel implements Observer {

    private final MasterController master;
    private JTable ledgerTable;
    private DefaultTableModel tableModel;
    
    // Shared date/money format so the UI is consistent with other panels.
    private final DateTimeFormatter YMD = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);

    public LedgerPanel(MasterController master) {
        this.master = master;
        master.registerObservers(this); //subscribe to panel so it updates with changes

        // Basic layout: table in the center, action buttons at the bottom.
        setLayout(new BorderLayout(5, 5));
        
        // Table Setup
        String[] columnNames = {"Index", "Date", "Type", "Name/Source", "Count/Units", "Amount/Total ($)"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Ledger is controlled by buttons + controller logic, not inline editing.
                return false;
            }
            @Override
            public Class<?> getColumnClass(int column) {
                return (column == 0) ? Integer.class : super.getColumnClass(column);
            }
        };
        ledgerTable = new JTable(tableModel);
        // Keep the index column narrow — we only need room for a small number.
        ledgerTable.getColumnModel().getColumn(0).setMaxWidth(50); // Set small width for Index column
        JScrollPane scrollPane = new JScrollPane(ledgerTable);
        add(scrollPane, BorderLayout.CENTER);

        // Button Panel Setup
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBorder(BorderFactory.createTitledBorder("Ledger Actions (Show All Entries)"));

        JButton addFulfillmentButton = new JButton("Add Need Fulfillment");
        addFulfillmentButton.addActionListener(e -> addNeedFulfillment());
        
        JButton addIncomeButton = new JButton("Add Funding Income");
        addIncomeButton.addActionListener(e -> addFundingIncome());
        
        JButton setFundsButton = new JButton("Set Funds");
        setFundsButton.addActionListener(e -> setFunds());
        
        JButton setGoalThresholdButton = new JButton("Set Goal/Threshold");
        setGoalThresholdButton.addActionListener(e -> setGoalThreshold());
        
        JButton deleteEntryButton = new JButton("Delete Selected Entry");
        deleteEntryButton.addActionListener(e -> deleteSelectedEntry());

        buttonPanel.add(addFulfillmentButton);
        buttonPanel.add(addIncomeButton);
        buttonPanel.add(setFundsButton);
        buttonPanel.add(setGoalThresholdButton);
        buttonPanel.add(deleteEntryButton);
        
        add(buttonPanel, BorderLayout.SOUTH);

        updateTable();
    }

    /**
     * Dialog flow for recording a Need fulfillment in the ledger.
     * Uses the currently-selected date as the date of the entry.
     */
    private void addNeedFulfillment() {
        LocalDate date = master.getSelectedDate();
        String needName = JOptionPane.showInputDialog(this, "Enter Need or Bundle Name:", "Add Need Fulfillment", JOptionPane.PLAIN_MESSAGE);
        if (needName == null || needName.trim().isEmpty()) return;

        String quantityStr = JOptionPane.showInputDialog(this, "Enter Quantity Fulfilled (e.g., 1.0):", "Add Need Fulfillment", JOptionPane.PLAIN_MESSAGE);
        if (quantityStr == null || quantityStr.trim().isEmpty()) return;
        
        try {
            double quantity = Double.parseDouble(quantityStr.trim());
            // This calls addEntry which performs the necessary validation and logging
            master.getLedgerController().addEntry(date, needName.trim(), quantity);
            JOptionPane.showMessageDialog(this, "Need Fulfillment recorded successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid quantity entered.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Dialog flow for recording Funding income into the ledger.
     * This is tied to the selected date as well.
     */    
    private void addFundingIncome() {
        LocalDate date = master.getSelectedDate();
        String sourceName = JOptionPane.showInputDialog(this, "Enter Funding Source Name:", "Add Funding Income", JOptionPane.PLAIN_MESSAGE);
        if (sourceName == null || sourceName.trim().isEmpty()) return;

        String unitsStr = JOptionPane.showInputDialog(this, "Enter Units Received (e.g., 1.0):", "Add Funding Income", JOptionPane.PLAIN_MESSAGE);
        if (unitsStr == null || unitsStr.trim().isEmpty()) return;

        try {
            double units = Double.parseDouble(unitsStr.trim());
            double amount = master.getFundingController().findFundByName(sourceName).getAmount();
            master.getLedgerController().addIncomeEntry(date, sourceName.trim(), units,amount);
            JOptionPane.showMessageDialog(this, "Funding Income recorded successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid number entered for units or amount.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Dialog for setting the total "Funds" amount for the active date.
     * Constraints (like only today/future) are enforced inside the controller.
     */
    private void setFunds() {
        LocalDate date = master.getSelectedDate();
        String amountStr = JOptionPane.showInputDialog(this, 
            "Enter new total Funds amount for " + date.format(YMD) + " ($):\n(Note: Funds can only be set for today or future dates)", 
            "Set Funds", JOptionPane.PLAIN_MESSAGE);
        
        if (amountStr != null && !amountStr.trim().isEmpty()) {
            try {
                double amount = Double.parseDouble(amountStr.trim());
                master.getLedgerController().setFunds(date, amount);
                JOptionPane.showMessageDialog(this, "Funds set successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount entered.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Dialog for setting either the "Goal" or "Threshold" value for the active date.
     * Same date rules as setFunds() — controller enforces business logic.
     */
    private void setGoalThreshold() {
        LocalDate date = master.getSelectedDate();
        
        String[] options = {"Set Daily Goal", "Set Daily Threshold"};
        int choice = JOptionPane.showOptionDialog(this, 
            "Choose a value to set for " + date.format(YMD) + ":\n(Note: Goal/Threshold can only be set for today or future dates)", 
            "Set Goal/Threshold", 
            JOptionPane.DEFAULT_OPTION, 
            JOptionPane.QUESTION_MESSAGE, 
            null, 
            options, 
            options[0]);

        if (choice == -1) return; // Closed dialog
        
        String valueType = (choice == 0) ? "Goal" : "Threshold";
        String amountStr = JOptionPane.showInputDialog(this, "Enter new " + valueType + " amount ($):", "Set " + valueType, JOptionPane.PLAIN_MESSAGE);
        
        if (amountStr != null && !amountStr.trim().isEmpty()) {
            try {
                double amount = Double.parseDouble(amountStr.trim());
                if (choice == 0) {
                    master.getLedgerController().setGoal(date, amount);
                } else {
                    master.getLedgerController().setThreshold(date, amount);
                }
                JOptionPane.showMessageDialog(this, valueType + " set successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount entered.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Deletes the currently-selected ledger entry by index.
     * Uses the row index in the table as the index into the underlying log list.
     */
    private void deleteSelectedEntry() {
        int selectedRow = ledgerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a ledger entry to delete.", "Selection Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete the selected ledger entry? (This cannot be undone)", 
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // The index in the table corresponds to the index in the underlying list.
            boolean success = master.getLedgerController().deleteEntryByIndex(selectedRow);
            if (success) {
                JOptionPane.showMessageDialog(this, "Entry deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error: Could not find or delete the entry.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Rebuilds the table rows from the current ledger log.
     * This is called on construction and whenever the model notifies observers.
     */    
    private void updateTable() {
        // Clear existing rows
        tableModel.setRowCount(0);

        List<LedgerEntity> log = master.getLedgerController().getLog();
        for (int i = 0; i < log.size(); i++) {
            LedgerEntity entry = log.get(i);
            // For NEED-type entries, name is the need/bundle name.
            // For FUND-type entries, name is the funding source.
            String nameOrSource = (entry.getNeedName() != null) ? entry.getNeedName() : "";
            
            // Always show the dollar amount in currency format.
            String amountOrTotal = currencyFormatter.format(entry.getAmount());
            
            // For FUND/GOAL/THRESHOLD, the "count/units" doesn’t make sense,
            // so we leave it blank. For NEED fulfilments, show the count.
            String countOrUnits = (entry.getType() == LedgerEntity.EntryType.FUND || entry.getType() == LedgerEntity.EntryType.GOAL || entry.getType() == LedgerEntity.EntryType.THRESHOLD)
                                  ? "" : String.format("%.2f", entry.getCount());
            
            tableModel.addRow(new Object[]{
                i, // Index for deletion
                entry.getDate().format(YMD), // date as yyyy-mm-dd
                entry.getType().toString(), //enum to string 
                nameOrSource, // need or funding name
                countOrUnits, //units or blank
                amountOrTotal //formatted currency
            });
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        // Update the table when any relevant model changes
        updateTable();
    }
}