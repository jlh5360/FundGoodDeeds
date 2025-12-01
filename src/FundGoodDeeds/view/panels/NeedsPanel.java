// ==========================
// NeedsPanel.java
// ==========================
package FundGoodDeeds.view.panels;

import FundGoodDeeds.controller.MasterController;
import FundGoodDeeds.model.Bundle;
import FundGoodDeeds.model.Need;
import FundGoodDeeds.model.NeedComponent;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.stream.Collectors;

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

@SuppressWarnings("deprecation")
public class NeedsPanel extends JPanel implements Observer {

    // We talk to the MasterController instead of the repositories directly.
    // That keeps all domain logic out of the Swing layer.
    private final MasterController master;

    // Core UI widgets for this panel.
    private JTable needsTable; // show bundles and needs
    private DefaultTableModel tableModel; // backing model for the table
 
    public NeedsPanel(MasterController master) {
        // Save controller reference so we can delegate actions.
        // The panel itself should not be doing business logic.
    
        this.master = master;
        master.registerObservers(this);
        
        // BorderLayout works nicely here: table in CENTER, buttons at SOUTH.
        setLayout(new BorderLayout(5, 5));
        
        // Table Setup
        String[] columnNames = {"Type", "Name", "Total Cost ($)", "Components"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {                            
                // We only edit via dialogs and buttons, not directly in cells.
                return false; // All cells are read-only
            }
        };
        needsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(needsTable);
        add(scrollPane, BorderLayout.CENTER);

        // Button Panel Setup
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBorder(BorderFactory.createTitledBorder("Catalog Actions (List Needs)"));

        JButton addNeedButton = new JButton("Add Need");
        addNeedButton.addActionListener(e -> addNeed());
        
        JButton addBundleButton = new JButton("Add Bundle");
        addBundleButton.addActionListener(e -> addBundle());
        
        JButton editNeedButton = new JButton("Edit Need Total");
        editNeedButton.addActionListener(e -> editNeedTotal());
        
        JButton editBundleButton = new JButton("Edit Bundle Components");
        editBundleButton.addActionListener(e -> editBundleComponents());
        
        JButton deleteNeedButton = new JButton("Delete Need/Bundle");
        deleteNeedButton.addActionListener(e -> deleteNeed());

        buttonPanel.add(addNeedButton);
        buttonPanel.add(addBundleButton);
        buttonPanel.add(editNeedButton);
        buttonPanel.add(editBundleButton);
        buttonPanel.add(deleteNeedButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Initial load
        updateTable();
    }

   
     /**
     * Simple dialog flow for creating a new single Need.
     * We only collect the minimal fields and let the controller validate.
     */
    private void addNeed() {
        String name = JOptionPane.showInputDialog(this, "Enter Need Name:", "Add New Need", JOptionPane.PLAIN_MESSAGE);
        if (name == null || name.trim().isEmpty()) return;

        String totalStr = JOptionPane.showInputDialog(this, "Enter Unit Cost ($):", "Add New Need", JOptionPane.PLAIN_MESSAGE);
        if (totalStr == null || totalStr.trim().isEmpty()) return;
        
        try {
            double total = Double.parseDouble(totalStr.trim());
            master.getNeedsController().addNeed(name.trim(), total);
            JOptionPane.showMessageDialog(this, "Need '" + name.trim() + "' added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid cost entered.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            // Controller enforces business rules; we just surface the message.
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Creates a new Bundle and lets the user attach existing Needs/components.
     * This is intentionally basic but shows how bundles group multiple needs.
     */
    private void addBundle() {
        String name = JOptionPane.showInputDialog(this, "Enter Bundle Name:", "Add New Bundle", JOptionPane.PLAIN_MESSAGE);
        if (name == null || name.trim().isEmpty()) return;
        
        // For simplicity, a simple input for components is used.
        String componentsStr = JOptionPane.showInputDialog(this, 
            "<html>Enter components (e.g., *NeedName*:*Quantity*, *Need2*:*Qty2*).<br>Use only existing Needs/Bundles.</html>", 
            "Add New Bundle Components", JOptionPane.PLAIN_MESSAGE);
        
        if (componentsStr == null || componentsStr.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Bundle must contain at least one part.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Map<NeedComponent, Integer> parts = parseBundleComponents(componentsStr.trim());
            master.getNeedsController().addBundle(name.trim(), parts);
            JOptionPane.showMessageDialog(this, "Bundle '" + name.trim() + "' added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding Bundle: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Allows editing the total cost of a single Need.
     * Bundles are handled differently, so check what type is selected.
     */
        private void editNeedTotal() {
        int selectedRow = needsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a Need (not a Bundle) to edit.", "Selection Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String name = (String) tableModel.getValueAt(selectedRow, 1);
        String type = (String) tableModel.getValueAt(selectedRow, 0);

        //prevent editing bundles this way, keep model consistent
        if ("Bundle".equals(type)) {
             JOptionPane.showMessageDialog(this, "Cannot edit the total of a Bundle this way. Edit its components instead.", "Input Error", JOptionPane.ERROR_MESSAGE);
             return;
        }

        String newTotalStr = JOptionPane.showInputDialog(this, 
            "Enter new unit cost for '" + name + "':", 
            "Edit Need Total", 
            JOptionPane.PLAIN_MESSAGE);
        
        if (newTotalStr != null && !newTotalStr.trim().isEmpty()) {
            try {
                double newTotal = Double.parseDouble(newTotalStr.trim());
                master.getNeedsController().editNeedTotal(name, newTotal);
                JOptionPane.showMessageDialog(this, "Total for Need '" + name + "' updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid cost entered.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * For Bundles, pop a small dialog to adjust the description of components.
     */    
    private void editBundleComponents() {
        int selectedRow = needsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a Bundle to edit.", "Selection Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String bundleName = (String) tableModel.getValueAt(selectedRow, 1);
        String type = (String) tableModel.getValueAt(selectedRow, 0);

        if (!"Bundle".equals(type)) {
             JOptionPane.showMessageDialog(this, "Selected item is not a Bundle.", "Input Error", JOptionPane.ERROR_MESSAGE);
             return;
        }
        
        // Options include Add Component, Remove Component Units, Update Component Units
        String[] options = {"Add Component", "Remove Component Units", "Update Component Units"};
        int choice = JOptionPane.showOptionDialog(this, 
            "Choose an action for Bundle '" + bundleName + "':", 
            "Edit Bundle", 
            JOptionPane.DEFAULT_OPTION, 
            JOptionPane.QUESTION_MESSAGE, 
            null, 
            options, 
            options[0]);

        if (choice == 0) { 
            // Add Component
            String componentName = JOptionPane.showInputDialog(this, "Enter component name to add:", "Add Component", JOptionPane.PLAIN_MESSAGE);
            if (componentName == null) return;

            String quantityStr = JOptionPane.showInputDialog(this, "Enter quantity:", "Add Component Quantity", JOptionPane.PLAIN_MESSAGE);
            try {
                int quantity = Integer.parseInt(quantityStr.trim());
                master.getNeedsController().addComponentToBundle(bundleName, componentName, quantity);
                JOptionPane.showMessageDialog(this, "Component added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }

        } else if (choice == 1) { 
            // Remove Component Units
            String componentName = JOptionPane.showInputDialog(this, "Enter component name to remove units from:", "Remove Component Units", JOptionPane.PLAIN_MESSAGE);
            if (componentName == null) return;
            String quantityStr = JOptionPane.showInputDialog(this, "Enter quantity to remove:", "Remove Component Quantity", JOptionPane.PLAIN_MESSAGE);
            try {
                int quantity = Integer.parseInt(quantityStr.trim());
                int removed = master.getNeedsController().removeComponentFromBundle(bundleName, componentName, quantity);
                JOptionPane.showMessageDialog(this, removed + " units of " + componentName + " removed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }

        } else if (choice == 2) { 
            // Update Component Units
            String componentName = JOptionPane.showInputDialog(this, "Enter component name to update quantity for:", "Update Component Units", JOptionPane.PLAIN_MESSAGE);
            if (componentName == null) return;
            String quantityStr = JOptionPane.showInputDialog(this, "Enter new total quantity:", "Update Component Quantity", JOptionPane.PLAIN_MESSAGE);
            try {
                int newQuantity = Integer.parseInt(quantityStr.trim());
                master.getNeedsController().updateBundleComponentUnits(bundleName, componentName, newQuantity);
                JOptionPane.showMessageDialog(this, "Units for " + componentName + " updated to " + newQuantity + ".", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Deletes either a Need or a Bundle based on the selected row.
     * We confirm with the user first so accidental deletes are less likely.
     */    private void deleteNeed() {
        int selectedRow = needsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a Need or Bundle to delete.", "Selection Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String name = (String) tableModel.getValueAt(selectedRow, 1);
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete '" + name + "'?", 
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // The controller exposes its repository here;
                // we call removeNeedComponent by name to support both Needs and Bundles.
                master.getNeedsController().getNeedsRepository().removeNeedComponent(name);
                JOptionPane.showMessageDialog(this, "'" + name + "' deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting item: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Helper method to turn the "NeedName:Quantity" comma-separated string
     * into a typed map of NeedComponent to quantity.
     *
     * This method also validates:
     *  - format of each pair
     *  - quantity is a positive integer
     *  - each component name actually exists in the catalog
     */   
     private Map<NeedComponent, Integer> parseBundleComponents(String componentsStr) throws IllegalArgumentException {
        Map<NeedComponent, Integer> parts = new LinkedHashMap<>();
        String[] pairs = componentsStr.split(",");
        
        for (String pair : pairs) {
            String[] partsArray = pair.trim().split(":");
            if (partsArray.length != 2) {
                throw new IllegalArgumentException("Component entry must be in the format 'NeedName:Quantity'.");
            }
            String componentName = partsArray[0].trim();
            int quantity;
            try {
                quantity = Integer.parseInt(partsArray[1].trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Quantity must be an integer: " + partsArray[1].trim());
            }
            
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be positive.");
            }

            NeedComponent component = master.getNeedsController().getNeedByName(componentName);
            if (component == null) {
                throw new IllegalArgumentException("Component Need or Bundle not found in catalog: " + componentName);
            }
            
            parts.put(component, quantity);
        }
        return parts;
    }

    /**
     * Rebuilds the table from the current state of Needs and Bundles.
     * This gets called on startup and whenever the model notifies us.
     */
    private void updateTable() {
        // Clear existing rows
        tableModel.setRowCount(0);

        List<NeedComponent> catalog = master.getNeedsController().getNeedsCatalog();
        for (NeedComponent component : catalog) {
            String type = (component instanceof Bundle) ? "Bundle" : "Need";
            String componentsDesc = "";

            // For Bundles, we show each component with its quantity.
            if (component instanceof Bundle bundle) {
                componentsDesc = bundle.getComponents().stream()
                    .map(c -> c.getName() + " (" + bundle.getComponentCount(c.getName()) + ")")
                    .collect(Collectors.joining(", "));
            }
            
            tableModel.addRow(new Object[]{
                type,
                component.getName(),
                String.format("%.2f", component.getTotal()),
                componentsDesc
            });
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        updateTable();
    }
}