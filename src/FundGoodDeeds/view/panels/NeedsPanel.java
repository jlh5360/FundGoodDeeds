// ==========================
// NeedsPanel.java
// ==========================
package FundGoodDeeds.view.panels;

import FundGoodDeeds.controller.MasterController;
import FundGoodDeeds.model.NeedComponent;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * NeedsPanel
 * - Shows the Needs/Bundle catalog (names only for now)
 * - Provides buttons to:
 *   - Refresh list
 *   - Add Need
 *   - Add Bundle
 *
 * This mirrors the CLI options:
 *  1) List Needs
 *  2) Add Need
 *  3) Add Bundle
 */
public class NeedsPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final MasterController master;
    private final DefaultListModel<String> model = new DefaultListModel<>();
    private final JList<String> list = new JList<>(model);

    private final JButton refreshBtn = new JButton("Refresh");
    private final JButton addNeedBtn = new JButton("Add Need");
    private final JButton addBundleBtn = new JButton("Add Bundle");

    public NeedsPanel(MasterController master) {
        this.master = master;

        setLayout(new BorderLayout(8, 8));

        // Center: catalog list
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(list), BorderLayout.CENTER);

        // South: controls
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.add(refreshBtn);
        controls.add(addNeedBtn);
        controls.add(addBundleBtn);
        add(controls, BorderLayout.SOUTH);

        // Wire up listeners
        installListeners();

        // Initial load
        refresh();
    }

    private void installListeners() {
        refreshBtn.addActionListener(e -> refresh());

        addNeedBtn.addActionListener(e -> showAddNeedDialog());

        addBundleBtn.addActionListener(e -> showAddBundleDialog());
    }

    /** Reload the list of needs/bundles from the model. */
    public void refresh() {
        model.clear();
        for (NeedComponent nc : master.getNeedsController().getNeedsCatalog()) {
            model.addElement(nc.getName());
        }
    }

    // ------------------------------------------------------
    // Dialogs / Actions
    // ------------------------------------------------------

    private void showAddNeedDialog() {
        JTextField nameField  = new JTextField(15);
        JTextField fixedField = new JTextField(8);
        JTextField varField   = new JTextField(8);
        JTextField feesField  = new JTextField(8);

        JPanel panel = new JPanel(new GridLayout(0, 2, 4, 4));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Fixed cost:"));
        panel.add(fixedField);
        panel.add(new JLabel("Variable cost:"));
        panel.add(varField);
        panel.add(new JLabel("Fees:"));
        panel.add(feesField);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Add Need",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) return;

        try {
            String name  = nameField.getText().trim();
            double fixed = Double.parseDouble(fixedField.getText().trim());
            double var   = Double.parseDouble(varField.getText().trim());
            double fees  = Double.parseDouble(feesField.getText().trim());
            double total = fixed + var + fees;

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name cannot be empty.");
                return;
            }

            master.getNeedsController().addNeed(name, total, fixed, var, fees);
            refresh();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for the costs.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding need: " + ex.getMessage());
        }
    }

    private void showAddBundleDialog() {
        String bundleName = JOptionPane.showInputDialog(this, "Bundle name:");
        if (bundleName == null || bundleName.trim().isEmpty()) {
            return;
        }
        bundleName = bundleName.trim();

        Map<NeedComponent, Integer> parts = new LinkedHashMap<>();

        boolean more = true;
        while (more) {
            String cname = JOptionPane.showInputDialog(this,
                    "Component name (blank to finish):");
            if (cname == null) return; // user hit Cancel
            cname = cname.trim();
            if (cname.isEmpty()) break;

            NeedComponent nc = master.getNeedsController().getNeedByName(cname);
            if (nc == null) {
                JOptionPane.showMessageDialog(this,
                        "Component '" + cname + "' not found in catalog.");
                continue;
            }

            String qtyStr = JOptionPane.showInputDialog(this,
                    "Quantity for " + cname + ":");
            if (qtyStr == null) return;
            qtyStr = qtyStr.trim();
            try {
                int qty = Integer.parseInt(qtyStr);
                if (qty <= 0) {
                    JOptionPane.showMessageDialog(this,
                            "Quantity must be a positive integer.");
                    continue;
                }
                parts.put(nc, parts.getOrDefault(nc, 0) + qty);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a valid integer quantity.");
            }

            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Add another component?",
                    "Add More?",
                    JOptionPane.YES_NO_OPTION
            );
            more = (choice == JOptionPane.YES_OPTION);
        }

        if (parts.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Bundle must contain at least one component.");
            return;
        }

        try {
            master.getNeedsController().addBundle(bundleName, parts);
            refresh();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error adding bundle: " + ex.getMessage());
        }
    }
}