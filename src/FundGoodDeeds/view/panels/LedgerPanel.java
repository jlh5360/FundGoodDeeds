// ==========================
// LedgerPanel.java
// ==========================
package FundGoodDeeds.view.panels;

import FundGoodDeeds.controller.MasterController;
import FundGoodDeeds.model.Day;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * LedgerPanel (V2)
 * - Shows daily ledger summary (same info as ConsoleView header)
 * - Provides buttons to:
 *   ✓ Refresh summary
 *   ✓ Add Need Fulfillment
 *   ✓ Add Funding Income
 *
 * Deeper per-entry views can be added later if needed.
 */
public class LedgerPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final MasterController master;
    private final JTextArea text = new JTextArea();

    private final JButton refreshBtn     = new JButton("Refresh");
    private final JButton addNeedBtn     = new JButton("Add Need Fulfillment");
    private final JButton addIncomeBtn   = new JButton("Add Funding Income");

    private final DateTimeFormatter YMD = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public LedgerPanel(MasterController master) {
        this.master = master;

        setLayout(new BorderLayout(8, 8));

        text.setEditable(false);
        text.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        add(new JScrollPane(text), BorderLayout.CENTER);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.add(refreshBtn);
        controls.add(addNeedBtn);
        controls.add(addIncomeBtn);

        add(controls, BorderLayout.SOUTH);

        installListeners();
        refresh();
    }

    private void installListeners() {
        refreshBtn.addActionListener(e -> refresh());
        addNeedBtn.addActionListener(e -> showAddNeedEntryDialog());
        addIncomeBtn.addActionListener(e -> showAddIncomeEntryDialog());
    }

    /** Refresh the daily ledger summary based on the active date. */
    public void refresh() {
        Day d = master.getDaySummary();
        StringBuilder sb = new StringBuilder();

        sb.append("Ledger Summary for ").append(d.getDate()).append("\n\n");
        sb.append("Funds:     $").append(String.format("%.2f", d.getFunds())).append("\n");
        sb.append("Threshold: $").append(String.format("%.2f", d.getThreshold())).append("\n");
        sb.append("Need Cost: $").append(String.format("%.2f", d.getTotalNeedCost())).append("\n");
        sb.append("Income:    $").append(String.format("%.2f", d.getTotalIncome())).append("\n");
        sb.append("Net:       $").append(String.format("%.2f", d.getNetCost())).append("\n");
        sb.append("Exceeded?  ").append(d.isThresholdExceeded() ? "YES" : "NO").append("\n");

        text.setText(sb.toString());
    }

    // ------------------------------------------------------
    // Dialogs for adding ledger entries
    // ------------------------------------------------------

    private void showAddNeedEntryDialog() {
        JTextField dateField  = new JTextField(master.getSelectedDate().format(YMD), 10);
        JTextField nameField  = new JTextField(12);
        JTextField unitsField = new JTextField(6);

        JPanel panel = new JPanel(new GridLayout(0, 2, 6, 6));
        panel.add(new JLabel("Date (yyyy-MM-dd):"));
        panel.add(dateField);
        panel.add(new JLabel("Need/Bundle name:"));
        panel.add(nameField);
        panel.add(new JLabel("Units:"));
        panel.add(unitsField);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Add Need Fulfillment",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) return;

        try {
            LocalDate date = LocalDate.parse(dateField.getText().trim(), YMD);
            String name = nameField.getText().trim();
            double units = Double.parseDouble(unitsField.getText().trim());

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name cannot be empty.");
                return;
            }

            master.getLedgerController().addEntry(date, name, units);
            master.setSelectedDate(date); // move active date if useful
            refresh();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding need entry:\n" + ex.getMessage());
        }
    }

    private void showAddIncomeEntryDialog() {
        JTextField dateField  = new JTextField(master.getSelectedDate().format(YMD), 10);
        JTextField srcField   = new JTextField(12);
        JTextField unitsField = new JTextField(6);

        JPanel panel = new JPanel(new GridLayout(0, 2, 6, 6));
        panel.add(new JLabel("Date (yyyy-MM-dd):"));
        panel.add(dateField);
        panel.add(new JLabel("Funding Source name:"));
        panel.add(srcField);
        panel.add(new JLabel("Units:"));
        panel.add(unitsField);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Add Funding Income",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) return;

        try {
            LocalDate date = LocalDate.parse(dateField.getText().trim(), YMD);
            String src = srcField.getText().trim();
            double units = Double.parseDouble(unitsField.getText().trim());

            if (src.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Source cannot be empty.");
                return;
            }

            master.getLedgerController().addIncomeEntry(date, src, units);
            master.setSelectedDate(date);
            refresh();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding income entry:\n" + ex.getMessage());
        }
    }
}
