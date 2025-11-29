package FundGoodDeeds.view;

import FundGoodDeeds.controller.MasterController;
import FundGoodDeeds.model.Bundle;
import FundGoodDeeds.model.Day;
import FundGoodDeeds.model.FundingSource;
import FundGoodDeeds.model.NeedComponent;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * ConsoleView (V2)
 * - Uses MasterController as the single entry point to the app
 * - Supports Needs/Bundles, Funding Sources, Ledger, Thresholds, and Date selection
 * - Keeps all persistence and business logic in controllers/repositories
 */
public class ConsoleView implements Observer {

    private final MasterController master;
    private final Scanner in = new Scanner(System.in);
    private final DateTimeFormatter YMD = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ConsoleView(MasterController master) {
        this.master = master;
        master.registerObservers(this);
    }

    /** Entry point for the CLI. Assumes data is already loaded by the App. */
    public void startup() {
        System.out.println("=== FundGoodDeeds CLI (V2) ===");

        //Set initial active date once
        LocalDate initialDate = askDate("Enter active date (yyyy-MM-dd) [blank=today]: ");
        master.setSelectedDate(initialDate);

        boolean run = true;
        while (run) {

            printSummaryHeader();

            System.out.println("\nCatalog");
            System.out.println(" 1) List Needs");
            System.out.println(" 2) Add Need");
            System.out.println(" 3) Add Bundle");
            System.out.println(" 4) Edit Need");         // NEW
            System.out.println(" 5) Delete Need");       // NEW
            System.out.println(" 6) Edit Bundle");       // NEW
            System.out.println(" 7) Delete Bundle");     // NEW

            System.out.println("\nFunding Sources");
            System.out.println(" 8) List Funding Sources");
            System.out.println(" 9) Add Funding Source");
            System.out.println(" 10) Edit Funding Source");
            System.out.println(" 11) Delete Funding Source");

            System.out.println("\nLedger");
            System.out.println("12) Show All Ledger Entries");
            System.out.println("13) Add Need Fulfillment");
            System.out.println("14) Add Funding Income");
            System.out.println("15) Set Funds");
            System.out.println("16) Set Goal / Threshold");
            System.out.println("17) Change Active Date");
            System.out.println("18) Show Daily Summary");

            System.out.println("\nSystem");
            System.out.println("19) Reload CSVs");
            System.out.println("20) Save CSVs");

            System.out.println("\n 0) Exit");
            System.out.print("\nSelect: ");

            switch (in.nextLine().trim()) {
                case "1" -> listNeeds();
                case "2" -> addNeed();
                case "3" -> addBundle();
                case "4" -> editNeed();
                case "5" -> deleteNeed();
                case "6" -> editBundle();
                case "7" -> deleteBundle();

                case "8" -> listFundingSources();
                case "9" -> addFundingSource();
                case "10" -> editFundingSource();
                case "11" -> deleteFundingSource();

                case "12" -> listLedgerEntries();
                case "13" -> addNeedEntry();
                // case "9" -> addNeedEntryLogic();
                case "14" -> addIncomeEntry();
                case "15" -> setFunds();
                case "16" -> setThreshold();
                case "17" -> changeDate();
                case "18" -> printSummaryHeader();

                case "19" -> master.loadAll();
                case "20" -> master.saveAll();
                case "0"  -> run = false;
                default   -> System.out.println("Invalid option.");
            }
        }

        if (askYesNo("Save before exit? (y/n): ")) {
            master.saveAll();
        }

        System.out.println("Goodbye!");
    }

    // ===========================================================
    // OBSERVER
    // ===========================================================
    @Override
    public void update(java.util.Observable o, Object arg) {
        System.out.println("[UPDATE] Data changed in model: " + o.getClass().getSimpleName());
    }

    // ===========================================================
    // DAILY SUMMARY HEADER
    // ===========================================================
    private void printSummaryHeader() {
        LocalDate date = master.getSelectedDate();
        Day d = master.getDaySummary(date);

        //Retrieve daily progress data
        double dailyDonations = master.getLedgerController().getTodayDonations(date);
        double dailyGoal = master.getLedgerController().getGoal(date);

        System.out.println("\n---------------------------------------------");
        System.out.println(" Active Date: " + master.getSelectedDate());
        System.out.println(" Snapshot Date: " + d.getDate());
        System.out.printf(" Funds: $%.2f | Threshold: $%.2f%n",
                d.getFunds(), d.getThreshold());
        // System.out.printf(" Need Costs: $%.2f | Income: $%.2f%n",
        //         d.getTotalNeedCost(), d.getTotalIncome());
        // System.out.printf(" Net Cost: $%.2f | Exceeded? %s%n",
        //         d.getNetCost(), d.isThresholdExceeded() ? "YES" : "NO");
        
        //Show Daily Progress against Goal
        System.out.printf(" Daily Progress: $%.2f / $%.2f%n",
                dailyDonations, dailyGoal);
        
        System.out.printf(" Need Costs: $%.2f | Income: $%.2f%n",
                master.getTotalNeedCost(), master.getTotalIncome());
        System.out.printf(" Net Cost: $%.2f | Exceeded? %s%n",
                master.getNetCost(), master.isThresholdExceeded() ? "YES" : "NO");
        System.out.println("---------------------------------------------");
    }

    // ===========================================================
    // CATALOG
    // ===========================================================
    private void listNeeds() {
        var list = master.getNeedsController().getNeedsCatalog();
        if (list.isEmpty()) {
            System.out.println("(catalog empty)");
            return;
        }
        // list.forEach(n -> System.out.println(" - " + n.getName()));
        //Logic to check type and print
        list.forEach(n -> {
            String type = "n";
            // Assuming you have access to Bundle.class (which you should)
            if (n instanceof FundGoodDeeds.model.Bundle) {
                type = "b";
            }
            System.out.printf(" - (%s) %s [$%.2f]%n", type, n.getName(), n.getTotal());
        });
    }

    private void addNeed() {
        // String name = ask("Name: ");
        String name = askForNonExistingNeedOrBundle("Need name: ");
        // double fixed = askDouble("Fixed cost: ");
        // double var   = askDouble("Variable cost: ");
        // double fees  = askDouble("Fees: ");
        // double total = fixed + var + fees;
        double total = askDouble("Total cost: $");

        master.getNeedsController().addNeed(name, total);
        // master.getNeedsController().addNeed(name, total, fixed, var, fees);
        System.out.println("Need added.");
    }

    private void addBundle() {
        // String name = ask("Bundle name: ");
        String name = askForNonExistingNeedOrBundle("Bundle Name: ");
        Map<NeedComponent, Integer> parts = new LinkedHashMap<>();

        boolean more = true;
        while (more) {
            String cname = askForNonExistingNeedOrBundle("Component name (blank=stop): ");
            if (cname.isBlank()) break;

            NeedComponent nc = master.getNeedsController().getNeedByName(cname);
            if (nc == null) {
                System.out.println("Not found.");
                continue;
            }

            // double qty = askDouble("Quantity: ");
            int qty = askInt("Quantity: ");
            parts.put(nc, qty);

            more = askYesNo("Add another component? (y/n): ");
        }

        // master.getNeedsController().addBundle(name, parts);
        // System.out.println("Bundle added.");

        if (!parts.isEmpty()) {
            master.getNeedsController().addBundle(name, parts);
            System.out.println("Bundle added.");
        }
        else {
            System.out.println("Bundle cancelled, no components added.");
        }
    }

    private void editNeed() {
        listNeeds();
        String name = askForExistingNeed("Editing Need (not Bundle) name: ");

        NeedComponent need = master.getNeedsController().getNeedByName(name);
        
        System.out.printf("Current Name: %s, Current Total Cost: $%.2f%n", need.getName(), need.getTotal());

        String newName = ask("Enter new name (blank to keep '" + need.getName() + "'): ");
        // Ensure new name is not a duplicate if provided
        if (!newName.isBlank() && !newName.equalsIgnoreCase(need.getName())) {
            if (master.getNeedsController().getNeedByName(newName) != null) {
                System.out.println("Error: New name '" + newName + "' already exists. Aborting edit.");
                return;
            }
        }

        double newTotal = -1.0; // Use -1.0 as a flag for "no change"
        String totalInput = ask("Enter new total cost (blank to keep $" + need.getTotal() + "): ");
        if (!totalInput.isBlank()) {
             try {
                newTotal = Double.parseDouble(totalInput);
                if (newTotal < 0) {
                    throw new IllegalArgumentException();
                }
            } catch (Exception e) {
                System.out.println("Invalid cost entered. Keeping old cost.");
                newTotal = -1.0; // Reset to no change flag
            }
        }
        
        // Pass the original name, and new values (newName or original name, newTotal or original total)
        master.getNeedsController().getNeedsRepository().editNeed(name, newName.isBlank() ? need.getName() : newName, newTotal);
        System.out.println("Need '" + name + "' updated.");
    }

    private void deleteNeed() {
        listNeeds();
        String name = askForExistingNeed("Deleting Need (not Bundle) name: ");

        // Check if the Need is part of a Bundle before removal
        if (master.getNeedsController().getNeedsRepository().isNeedComponentOfAnyBundle(name)) {
            System.out.println("Error: Need '" + name + "' is a component in one or more Bundles and cannot be deleted.");
            System.out.println("Please remove it from all bundles first.");
            return;
        }

        if (askYesNo("Are you sure you want to delete Need '" + name + "'? (y/n): ")) {
            master.getNeedsController().getNeedsRepository().removeNeed(name);
            System.out.println("Need '" + name + "' deleted.");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    private void editBundle() {
        listNeeds();
        String name = askForExistingBundle("Editing Bundle name: ");
        // NeedComponent is retrieved as a Bundle instance
        Bundle bundle = (Bundle) master.getNeedsController().getNeedByName(name);

        System.out.printf("Current Bundle Name: %s, Current Total Cost: $%.2f%n", bundle.getName(), bundle.getTotal());
        System.out.println("Components: " + String.join(", ", bundle.resolveAllNames()));

        // 1. Rename operation
        String newName = ask("Enter new name for Bundle (blank to keep '" + bundle.getName() + "'): ");
        if (!newName.isBlank() && !newName.equalsIgnoreCase(bundle.getName())) {
            if (master.getNeedsController().getNeedByName(newName) != null) {
                System.out.println("Error: New name '" + newName + "' already exists. Aborting rename.");
            } else {
                master.getNeedsController().getNeedsRepository().editBundleName(name, newName);
                System.out.println("Bundle renamed to '" + newName + "'.");
                name = newName; // Update name for subsequent operations
            }
        }
        
        // 2. Component Management
        boolean continueEditing = true;
        while(continueEditing) {
            // Re-fetch bundle to show updated components at the start of the loop
            bundle = (Bundle) master.getNeedsController().getNeedByName(name);
            if (bundle == null) {
                System.out.println("Error: Bundle not found after rename/edit operation. Stopping component edit.");
                return;
            }
            System.out.println("\nComponent Management for Bundle '" + name + "':");
            System.out.println("Current Components: " + String.join(", ", bundle.resolveAllNames()));
            System.out.println(" 1) Add/Increase Component Quantity");
            System.out.println(" 2) Remove Component Quantity");
            System.out.println(" 3) Remove Component Type Completely");
            System.out.println(" 0) Finish Editing Components");
            System.out.print("Select: ");
            
            String selection = in.nextLine().trim();
            switch (selection) {
                case "1" -> { 
                    String componentName = askForExistingNeedOrBundle("Component to add/increase: ");
                    NeedComponent component = master.getNeedsController().getNeedByName(componentName); 

                    if (component != null) {
                        // Only proceed if the component exists in the *main catalog*
                        int quantity = askInt("Quantity to add: ");

                        // 'name' is the name of the bundle being edited
                        master.getNeedsController().addComponentToBundle(name, componentName, quantity); 
                        
                        System.out.println(quantity + " units of '" + componentName + "' added/increased in '" + name + "'.");
                    } else {
                        // If the helper function (askForExistingNeedOrBundle) somehow failed, 
                        // print a clear message instead of using a flow control statement (like 'continue') 
                        // that may break the switch/while loop structure.
                        System.out.println("Error: Component not found in catalog or invalid input."); 
                    }
                }
                case "2" -> {
                    String componentName = ask("Component to remove quantity from: ");
                    NeedComponent component = master.getNeedsController().getNeedByName(componentName);
                    if (component == null || !bundle.getComponents().contains(component)) {
                        System.out.println("Component not in bundle or does not exist.");
                        break;
                    }
                    int quantity = askInt("Quantity to remove: ");
                    if (quantity > 0) {
                        int unitsRemoved = master.getNeedsController().removeComponentFromBundle(name, componentName, quantity);
                        System.out.println(unitsRemoved + " units of '" + componentName + "' removed from bundle.");
                    } else {
                        System.out.println("Quantity must be positive.");
                    }
                }
                case "3" -> {
                    String componentName = ask("Component type to remove completely: ");
                    NeedComponent component = master.getNeedsController().getNeedByName(componentName);
                    if (component == null || !bundle.getComponents().contains(component)) {
                        System.out.println("Component not in bundle or does not exist.");
                        break;
                    }
                    if (askYesNo("Are you sure you want to remove all units of '" + componentName + "' from the bundle? (y/n): ")) {
                        // master.getNeedsController().removeBundleComponentType(name, component);
                        master.getNeedsController().removeComponentFromBundle(name, componentName, bundle.getComponentCount(componentName));
                        System.out.println("Component type '" + componentName + "' removed completely from bundle.");
                    } else {
                        System.out.println("Removal cancelled.");
                    }
                }
                case "0" -> continueEditing = false;
                default -> System.out.println("Invalid option.");
            }
        }
        
        System.out.println("Bundle '" + name + "' fully updated.");
    }
    
    private void deleteBundle() {
        listNeeds();
        String name = askForExistingBundle("Deleting Bundle name: ");

        if (askYesNo("Are you sure you want to delete Bundle '" + name + "'? (y/n): ")) {
            master.getNeedsController().getNeedsRepository().removeBundle(name);
            System.out.println("Bundle '" + name + "' deleted.");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    // ===========================================================
    // FUNDING SOURCES
    // ===========================================================
    private void listFundingSources() {
        var all = master.getFundingController().getAll();
        if (all.isEmpty()) {
            System.out.println("(no funding sources)");
            return;
        }
        all.forEach(fs -> System.out.printf(" - (i) %s [$%.2f]%n", fs.getName(), fs.getAmount()));
    }

    private void addFundingSource() {
        // String name = ask("Funding name: ");
        String name = askForNonExistingFundingSource("Name: ");
        double amt = askDouble("Amount per unit: ");
        master.getFundingController().addFundingSource(name, amt);
        System.out.println("Added.");
    }

    private void editFundingSource() {
        // String name = ask("Editing which source? ");
        String name = askForExistingFundingSource("Editing which source? ");
        double amt = askDouble("New $/unit: ");
        master.getFundingController().updateFundingSource(name, amt);
        System.out.println("Updated.");
    }

    private void deleteFundingSource() {
        // String name = ask("Delete which? ");
        String name = askForExistingFundingSource("Delete which? ");
        // master.getFundingController().deleteFundingSource(name);
        // System.out.println("Deleted.");
        if (askYesNo("Confirm deletion of '" + name + "'? (y/n): ")) {
            master.getFundingController().deleteFundingSource(name);
            System.out.println("Source deleted.");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    // ===========================================================
    // LEDGER
    // ===========================================================
    // //V1
    // private void listLedgerEntries() {
    //     var list = master.getLedgerController().getLog();
        
    //     if (list.isEmpty()) {
    //         System.out.println("(ledger is empty)");
    //         return;
    //     }

    //     list.forEach(entry -> {
    //         String details;
            
    //         //Handle entries with names (NEED, INCOME) vs. amounts (FUND, GOAL, THRESHOLD)
    //         if (entry.getNeedName() != null) { 
    //             details = String.format("%s (x%.1f)", entry.getNeedName(), entry.getCount());
    //         }
    //         else {
    //             details = String.format("$%.1f", entry.getAmount());
    //         }
            
    //         System.out.printf(" - [%s] %s: %s%n", 
    //                           entry.getDate().format(YMD), 
    //                           entry.getType().toString(), 
    //                           details);
    //     });
    // }

    //V2
    private void listLedgerEntries() {
        var list = master.getLedgerController().getLog();

        if (list.isEmpty()) {
            System.out.println("(ledger is empty)");
            return;
        }
        
        // // Print header
        // System.out.println("Date\t\t| Type\t\t| Name\t\t\t| Count\t| Amount");
        // System.out.println("-----------------------------------------------------------------");

        //Define column widths for proper alignment
        //Date: 12 chars (yyyy-MM-dd is 10)
        //Type: 10 chars (e.g., THRESHOLD is 9, NEED is 4, INCOME is 6)
        //Name: 25 chars (Long enough for most names)
        //Count: 7 chars
        //Amount: 10 chars (e.g., $9999.00)
        final String FORMAT = "%-12s | %-10s | %-25s | %-7s | %-10s%n";
        final String SEPARATOR = "--------------------------------------------------------------------------------";

        // Print header using the same format string
        System.out.printf(FORMAT, "Date", "Type", "Name", "Count", "Amount");
        System.out.println(SEPARATOR);
        
        list.forEach(e -> {
            String date = e.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
            String type = switch (e.getType()) {
                case NEED -> "NEED";
                case FUND -> "FUND";
                case GOAL -> "GOAL";
                case THRESHOLD -> "THRESHOLD";
                case INCOME -> "INCOME";
                default -> "UNK";
            };
            String name = e.getNeedName() != null ? e.getNeedName() : "";
            // String count = e.getCount() != 0 ? String.valueOf(e.getCount()) : "";
            String count = e.getCount() != 0.0 ? String.format("%.1f", e.getCount()) : "";
            String amount = String.format("$%.2f", e.getAmount());

            // System.out.printf("%s\t| %s\t\t| %s\t\t| %s\t| %s%n", date, type, name, count, amount);
            System.out.printf(FORMAT, date, type, name, count, amount);
        });
        // System.out.println("-----------------------------------------------------------------");
        System.out.println(SEPARATOR);
    }

    private void addNeedEntry() {
        // listNeeds();

        // LocalDate d = askDate("Date (yyyy-MM-dd) [blank=today]: ");

        // String name = askForExistingNeedOrBundle("Need/Bundle: ");
        // double units = askDouble("Units: ");
        // addNeedEntryLogic(d, name, units);
        // // master.getLedgerController().addEntry(d, name, units);
        // System.out.println("Entry added.");

        
        listNeeds();

        LocalDate d = askDate("Date (yyyy-MM-dd) [blank=today]: ");

        String name = askForExistingNeedOrBundle("Fullfilling Need/Bundle: ");
        double units = askDouble("Units to fulfill: ");
        addNeedEntryLogic(d, name, units);
        // // master.getLedgerController().addEntry(d, name, units);
        // System.out.println("Entry added.");
        // master.getLedgerController().addEntry(master.getSelectedDate(), name, units);
        System.out.println(units + " unit(s) of '" + name + "' fulfilled for " + master.getSelectedDate());
    }

    /**
     * Modified logic for adding a Ledger Entry (specifically for a NEED).
     * This complex logic handles unit depletion from bundles first.
     */
    private void addNeedEntryLogic(LocalDate date, String name, double quantityDouble) {
        // LocalDate date = master.getSelectedDate();
        int quantity = (int) Math.round(quantityDouble); // Assuming units are integers

        List<Bundle> containingBundles = master.getLedgerController().getBundlesContainingNeed(name);
        
        if (!containingBundles.isEmpty()) {
            //Complex fulfillment --> Deplete units from bundles first
            int remainingQuantity = quantity;
            int fulfilledCount = 0;
            
            System.out.printf("\nFulfilling %d units of '%s' by depleting units from Bundles first.\n", quantity, name);

            while (remainingQuantity > 0) {
                
                int totalAvailableInBundles = master.getNeedsController().getNeedsRepository().getTotalBundleComponentCount(name);
                
                if (totalAvailableInBundles == 0) {
                    System.out.println("No more units of '" + name + "' are available in any bundle.");
                    break;
                }
                
                System.out.println("\nRemaining units to fulfill: " + remainingQuantity);
                
                //Re-fetch bundles to ensure list is current (in case a bundle component hit zero)
                containingBundles = master.getLedgerController().getBundlesContainingNeed(name);
                
                Bundle selectedBundle = selectBundleForFulfillment(containingBundles, name);
                
                if (selectedBundle == null) {
                    System.out.println("Fulfillment cancelled by user.");
                    break;
                }
                
                //Get the current component count in the selected bundle
                int bundleComponentCount = selectedBundle.getComponentsAndCounts().entrySet().stream()
                    .filter(e -> e.getKey().getName().equalsIgnoreCase(name))
                    .mapToInt(Map.Entry::getValue)
                    .sum();

                if (bundleComponentCount == 0) {
                    System.out.println("Selected bundle no longer contains units of '" + name + "'. Please select another.");
                    continue;
                }

                //Determine max units that can be fulfilled from this bundle right now
                int maxUnits = Math.min(remainingQuantity, bundleComponentCount);
                int unitsToFulfillFromThisBundle;
                
                while (true) {
                    unitsToFulfillFromThisBundle = askInt("Units to fulfill from '" + selectedBundle.getName() + 
                        "' (Max " + maxUnits + "): ");
                    
                    if (unitsToFulfillFromThisBundle > 0 && unitsToFulfillFromThisBundle <= maxUnits) {
                        break;
                    }
                    System.out.println("Invalid quantity. Must be between 1 and " + maxUnits);
                }
                
                //Call the new controller method to handle the fulfillment, update, and potential deletion
                master.getLedgerController().fulfillNeedFromBundle(date, name, selectedBundle, unitsToFulfillFromThisBundle);
                
                remainingQuantity -= unitsToFulfillFromThisBundle;
                fulfilledCount += unitsToFulfillFromThisBundle;
            }
            
            //Final reporting
            if (fulfilledCount > 0) {
                System.out.printf("Successfully recorded fulfillment of %d units of '%s'.\n", fulfilledCount, name);
            }
            if (remainingQuantity > 0) {
                System.out.printf("WARNING: Could not fulfill %d remaining units of '%s' due to insufficient units in bundles.\n", remainingQuantity, name);
            }

        }
        else {
            // Simple Need or Bundle fulfillment (use original simple logic)
            master.getLedgerController().addEntry(date, name, quantity);
            
            // Since we removed the auto-deletion from addEntry, re-implement it here for simple Needs/Bundles
            NeedComponent need = master.getNeedsController().getNeedByName(name);
            if ((need != null) && !(need instanceof Bundle)) { // If it's a basic Need, not a Bundle
                master.getNeedsController().getNeedsRepository().removeNeedComponent(name);
            }
        }
    }

    private void addIncomeEntry() {
        listFundingSources();
        LocalDate d = askDate("Date (yyyy-MM-dd) [blank=today]: ");
        // String src = ask("Funding Source: ");
        String src = askForExistingFundingSource("Funding Source: ");
        double units = askDouble("Units: ");
        
        var all = master.getFundingController().getAll();

        //Need to validate source for income entries
        if (all.isEmpty()) {
            System.out.println("(no funding sources)");
            return;
        }
        else {
            for (FundingSource fs : all) {
                // //DEBUGGING
                // System.out.printf("src --> '%s'%n", src);
                // System.out.printf("fs --> '%s'%n", fs.getName());
                if (src.equals(fs.getName())) {
                    // //DEBUGGING
                    // System.out.println("MATCHES");
                    master.getLedgerController().addIncomeEntry(d, src, units, fs.getAmount());
                    
                    System.out.println("Income added.");
                }
            }
        }
    }

    // private void addIncomeEntry() {
    //     listFundingSources();
    //     String name = askForExistingFundingSource("Income source name: ");
    //     int count = askInt("Number of units of income to add: ");
    //     master.getLedgerController().getLedgerRepository().addFundingIncome(name, count, master.getSelectedDate());
    //     System.out.println(count + " unit(s) of income from '" + name + "' added for " + master.getSelectedDate());
    // }

    // private void setFunds() {
    //     LocalDate d = askDate("Date (yyyy-MM-dd) [blank=today]: ");
    //     double amt = askDouble("Funds: ");
    //     master.getLedgerController().setFunds(d, amt);
    //     System.out.println("Funds updated.");
    // }

    private void setFunds() {
        double newFunds = askDouble("Set current funds to: $");
        master.getLedgerController().setFunds(master.getSelectedDate(), newFunds);
        System.out.println("Funds set to $" + newFunds);
    }

    // private void setThreshold() {
    //     LocalDate d = askDate("Date (yyyy-MM-dd) [blank=today]: ");
    //     double amt = askDouble("Threshold: ");
    //     master.getLedgerController().setGoal(d, amt);
    //     System.out.println("Threshold updated.");
    // }

    private void setThreshold() {
        // Now handles both Goal and Threshold
        System.out.println("Setting Goal and/or Threshold for " + master.getSelectedDate());
        
        double currentGoal = master.getLedgerController().getGoal(master.getSelectedDate());
        double newGoal = -1.0;
        String goalInput = ask("Enter new Daily Goal (blank to keep $" + currentGoal + "): ");
        if (!goalInput.isBlank()) {
             try {
                newGoal = Double.parseDouble(goalInput);
            } catch (Exception e) {
                System.out.println("Invalid cost entered. Keeping old goal.");
            }
        }
        
        double currentThreshold = master.getLedgerController().getThreshold(master.getSelectedDate());
        double newThreshold = -1.0;
        String thresholdInput = ask("Enter new System Threshold (blank to keep $" + currentThreshold + "): ");
        if (!thresholdInput.isBlank()) {
             try {
                newThreshold = Double.parseDouble(thresholdInput);
            } catch (Exception e) {
                System.out.println("Invalid cost entered. Keeping old threshold.");
            }
        }
        
        if (newGoal >= 0.0) {
            master.getLedgerController().setGoal(master.getSelectedDate() ,newGoal);
            System.out.println("Daily Goal set to $" + newGoal);
        }
        
        if (newThreshold >= 0.0) {
            master.getLedgerController().getLedgerRepository().setThreshold(master.getSelectedDate(), newThreshold);
            System.out.println("System Threshold set to $" + newThreshold);
        }
        
        if (newGoal < 0 && newThreshold < 0) {
            System.out.println("No changes made.");
        }
    }

    // private void changeDate() {
    //     LocalDate d = askDate("New active date: ");
    //     master.setSelectedDate(d);
    //     System.out.println("Active date set.");
    // }

    private void changeDate() {
        LocalDate newDate = askDate("Enter new active date (yyyy-MM-dd) [blank=today]: ");
        master.setSelectedDate(newDate);
        System.out.println("Active date set to " + master.getSelectedDate());
    }

    // ===========================================================
    // INPUT HELPERS
    // ===========================================================
    /**
     * Helper method to handle the user selection of a bundle.
     */
    private Bundle selectBundleForFulfillment(List<Bundle> bundles, String needName) {
        while (true) {
            System.out.println("\n--- Select Bundle to Fulfill '" + needName + "' ---");
            for (int i = 0; i < bundles.size(); i++) {
                Bundle b = bundles.get(i);
                // Get the current component count in the bundle
                int count = b.getComponentsAndCounts().entrySet().stream()
                    .filter(e -> e.getKey().getName().equalsIgnoreCase(needName))
                    .mapToInt(Map.Entry::getValue)
                    .sum();
                System.out.printf("  %d) %s (Contains %d units of %s)\n", i + 1, b.getName(), count, needName);
            }
            System.out.println("  0) Cancel Fulfillment");

            int choice = askInt("Enter bundle number: ");
            if (choice == 0) return null;
            if (choice > 0 && choice <= bundles.size()) {
                return bundles.get(choice - 1);
            }
            System.out.println("Invalid choice. Please try again.");
        }
    }

    /** Utility to ask for the name of a Need (not Bundle) that *must* exist. */
    private String askForExistingNeed(String p) {
        String name;
        NeedComponent nc;
        while (true) {
            name = askForExistingNeedOrBundle(p); // First ensure it exists
            nc = master.getNeedsController().getNeedByName(name);
            // Must not be a Bundle
            if (nc instanceof FundGoodDeeds.model.Bundle) {
                System.out.println("'" + name + "' is a Bundle. Please enter a simple Need.");
                continue;
            }
            return name;
        }
    }

    /** Utility to ask for the name of a Bundle that *must* exist. */
    private String askForExistingBundle(String p) {
        String name;
        NeedComponent nc;
        while (true) {
            name = askForExistingNeedOrBundle(p); // First ensure it exists
            nc = master.getNeedsController().getNeedByName(name);
            // Must be a Bundle
            if (nc instanceof FundGoodDeeds.model.Bundle) {
                return name;
            } else {
                System.out.println("'" + name + "' is a simple Need. Please enter a Bundle name.");
            }
        }
    }

    /**
     * Prompts the user for a Need or Bundle name and ensures it exists in the catalog.
     * Used for fulfillment, editing, and deleting.
     * @param prompt The message to display to the user.
     * @return The name of an existing Need or Bundle.
     */
    private String askForExistingNeedOrBundle(String prompt) {
        while (true) {
            String name = ask(prompt);
            if (name.isBlank()) {
                System.out.println("Name cannot be empty.");
                continue;
            }
            if (master.getNeedsController().getNeedByName(name) != null) {
                return name;
            }
            System.out.println("[ERROR] Need or Bundle not found: " + name);
        }
    }

    /**
     * Prompts the user for a Need or Bundle name and ensures it *does not* exist in the catalog.
     * Used for adding new Needs/Bundles.
     * @param prompt The message to display to the user.
     * @return A unique name for a new Need or Bundle.
     */
    private String askForNonExistingNeedOrBundle(String prompt) {
        while (true) {
            String name = ask(prompt);
            if (name.isBlank()) {
                System.out.println("Name cannot be empty.");
                continue;
            }
            if (master.getNeedsController().getNeedByName(name) == null) {
                return name;
            }
            System.out.println("[ERROR] A Need or Bundle with that name already exists: " + name);
        }
    }

    /**
     * Prompts the user for a Funding Source name and ensures it exists in the catalog.
     * Used for editing, deleting, and income recording.
     * @param prompt The message to display to the user.
     * @return The name of an existing Funding Source.
     */
    private String askForExistingFundingSource(String prompt) {
        while (true) {
            String name = ask(prompt);
            if (name.isBlank()) {
                System.out.println("Name cannot be empty.");
                continue;
            }
            // Accessing repository via MasterController to check for existence
            if (master.getFundingController().getFundingRepository().getFundingSourceByName(name) != null) {
                return name;
            }
            System.out.println("[ERROR] Funding Source not found: " + name);
        }
    }

    /**
     * Prompts the user for a Funding Source name and ensures it *does not* exist in the catalog.
     * Used for adding new Funding Sources.
     * @param prompt The message to display to the user.
     * @return A unique name for a new Funding Source.
     */
    private String askForNonExistingFundingSource(String prompt) {
        while (true) {
            String name = ask(prompt);
            if (name.isBlank()) {
                System.out.println("Name cannot be empty.");
                continue;
            }
            // Accessing repository via MasterController to check for existence
            if (master.getFundingController().getFundingRepository().getFundingSourceByName(name) == null) {
                return name;
            }
            System.out.println("[ERROR] A Funding Source with that name already exists: " + name);
        }
    }

    private String ask(String p) {
        System.out.print(p);
        return in.nextLine().trim();
    }

    private boolean askYesNo(String p) {
        while (true) {
            System.out.print(p);
            String s = in.nextLine().trim().toLowerCase();
            if (s.equals("y") || s.equals("yes")) return true;
            if (s.equals("n") || s.equals("no")) return false;
            System.out.println("Enter y/n.");
        }
    }

    private double askDouble(String p) {
        while (true) {
            System.out.print(p);
            try {
                return Double.parseDouble(in.nextLine());
            } catch (Exception e) {
                System.out.println("Enter number.");
            }
        }
    }

    private int askInt(String p) {
        while (true) {
            System.out.print(p);
            try {
                return Integer.parseInt(in.nextLine());
            } catch (Exception e) {
                System.out.println("Enter integer.");
            }
        }
    }

    // private LocalDate askDate(String p) {
    //     System.out.print(p);
    //     String s = in.nextLine().trim();
    //     if (s.isBlank()) return LocalDate.now();
    //     try {
    //         return LocalDate.parse(s, YMD);
    //     } catch (Exception e) {
    //         System.out.println("Invalid date, using today.");
    //         return LocalDate.now();
    //     }
    // }
    
    private LocalDate askDate(String p) {
        LocalDate now = LocalDate.now();
        LocalDate earliestAllowedDate = now.minusDays(7); 
        
        while (true) {
            System.out.print(p);
            String s = in.nextLine().trim();
            if (s.isBlank()) return now;

            try {
                LocalDate date = LocalDate.parse(s, YMD);
                
                // Apply restriction #3
                if (date.isBefore(earliestAllowedDate)) {
                    System.out.println("Error: Entries cannot be logged for dates older than one week (" + earliestAllowedDate + ").");
                    continue;
                }
                return date;
            } catch (Exception e) {
                System.out.println("Invalid date format. Use YYYY-MM-DD.");
            }
        }
    }
}