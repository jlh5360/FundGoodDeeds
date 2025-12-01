// ==========================
// SummaryPanel.java
// ==========================
package FundGoodDeeds.view.panels;

import FundGoodDeeds.controller.MasterController;
import FundGoodDeeds.model.Day;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

/**
 * SummaryPanel (V2)
 * - Displays the daily financial snapshot
 * - Mirrors CLI summary header visually
 * - Pulls all summary data through MasterController.getDaySummary()
 */

@SuppressWarnings("deprecation")
public class SummaryPanel extends JPanel implements Observer {
    
    // Central entry point for all data this panel needs.
    private final MasterController master;

    // These labels act like "slots" where we drop the summary numbers.
    private final JLabel snapshotDateLabel;
    private final JLabel fundsLabel;
    private final JLabel thresholdLabel;
    private final JLabel dailyProgressLabel;
    private final JLabel needCostsLabel;
    private final JLabel incomeLabel;
    private final JLabel netCostLabel;
    private final JLabel exceededLabel;
    
    // Buttons for system-level actions (reload/save CSVs).
    // These mirror the CLI "load" and "save" options but in UI form.
    private final JButton reloadButton;
    private final JButton saveButton;

    // Keep money and dates formatting consistent across the app.
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
    private final DateTimeFormatter YMD = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public SummaryPanel(MasterController master) {
        this.master = master;
        master.registerObservers(this); // subscribe to panel so it updates with changes 

        // BorderLayout:
        // - CENTER = grid of summary values
        // - SOUTH  = system buttons (reload/save)
        setLayout(new BorderLayout(10, 10));
        
        // --- Summary Grid Panel ---
        // 8 rows, 2 columns: left = label text, right = actual value
        JPanel summaryGrid = new JPanel(new GridLayout(8, 2, 5, 5));
        summaryGrid.setBorder(BorderFactory.createTitledBorder("Daily & Total Summary"));

        snapshotDateLabel = new JLabel();
        fundsLabel = new JLabel();
        thresholdLabel = new JLabel();
        dailyProgressLabel = new JLabel();
        needCostsLabel = new JLabel();
        incomeLabel = new JLabel();
        netCostLabel = new JLabel();
        exceededLabel = new JLabel();

        summaryGrid.add(new JLabel("Snapshot Date:")); summaryGrid.add(snapshotDateLabel);
        summaryGrid.add(new JLabel("Funds (for date):")); summaryGrid.add(fundsLabel);
        summaryGrid.add(new JLabel("Threshold (for date):")); summaryGrid.add(thresholdLabel);
        summaryGrid.add(new JLabel("Daily Net Cost (Spent - Received):")); summaryGrid.add(dailyProgressLabel); 
        summaryGrid.add(new JLabel("Total Needs Cost (Catalog):")); summaryGrid.add(needCostsLabel);
        summaryGrid.add(new JLabel("Total Income (Catalog):")); summaryGrid.add(incomeLabel);
        summaryGrid.add(new JLabel("Net Cost (Catalog):")); summaryGrid.add(netCostLabel);
        summaryGrid.add(new JLabel("Daily Threshold Exceeded?")); summaryGrid.add(exceededLabel);

        // --- System Controls Panel ---
        JPanel systemPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        reloadButton = new JButton("Reload CSVs");
        reloadButton.addActionListener(e -> reloadData());
        
        saveButton = new JButton("Save CSVs");
        saveButton.addActionListener(e -> saveData());
        
        systemPanel.add(reloadButton);
        systemPanel.add(saveButton);
        
        // Add components to the main panel
        add(summaryGrid, BorderLayout.CENTER);
        add(systemPanel, BorderLayout.SOUTH);

        // Initial update
        update(null, null);
    }
    
    /**
     * Reloads all CSV-based data through the MasterController.
     * This gives the user an easy way to "reset" from disk without restarting the app.
     */
    private void reloadData() {
        try {
            master.loadAll();
            JOptionPane.showMessageDialog(this, "Data successfully reloaded from CSVs.", "Reload Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error reloading data: " + ex.getMessage(), "Reload Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Saves the current in-memory state back to CSVs.
     * Basically the GUI version of a "Save" command in the CLI.
     */
    private void saveData() {
        try {
            master.saveAll();
            JOptionPane.showMessageDialog(this, "Data successfully saved to CSVs.", "Save Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving data: " + ex.getMessage(), "Save Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

     /**
     * Observer callback.
     * Anytime the model notifies observers (date change, ledger update, etc.),
     * we rebuild the summary view from the MasterController.
     */
    @Override
    public void update(Observable o, Object arg) {
        // Pull the "Day" snapshot for the currently selected date.
        Day daySummary = master.getDaySummary(master.getSelectedDate());
        
        // These totals are more "global" and come straight off MasterController.
        double totalNeeds = master.getTotalNeedCost();
        double totalIncome = master.getTotalIncome();
        double netCost = master.getNetCost();
        double dailyNetCost = master.getNetDayCost();
        double threshold = master.getLedgerController().getThreshold(master.getSelectedDate());

        // Date at the top – ties the snapshot to whatever date is active.
        snapshotDateLabel.setText(master.getSelectedDate().format(YMD));
        
        // Funds available for that day (from the Day snapshot).
        fundsLabel.setText(currencyFormatter.format(daySummary.getFunds()));
        
        // Threshold for that day – used to flag if we're overspending.
        thresholdLabel.setText(currencyFormatter.format(threshold));

        // Daily net cost (what we spent - what we brought in today).
        dailyProgressLabel.setText(currencyFormatter.format(dailyNetCost));
        
        // Catalog-level.
        needCostsLabel.setText(currencyFormatter.format(totalNeeds));
        incomeLabel.setText(currencyFormatter.format(totalIncome));

        netCostLabel.setText(currencyFormatter.format(netCost));
        
        // Simple YES/NO indicator with color to draw attention if we blow the threshold.
        String exceededText = master.isDailyThresholdExceeded() ? "YES (Net Day Cost > Threshold)" : "NO";
        exceededLabel.setText(exceededText);
        exceededLabel.setForeground(master.isDailyThresholdExceeded() ? Color.RED : Color.GREEN);
    }
}