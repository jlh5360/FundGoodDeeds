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

            System.out.println("\nFunding Sources");
            System.out.println(" 4) List Funding Sources");
            System.out.println(" 5) Add Funding Source");
            System.out.println(" 6) Edit Funding Source");
            System.out.println(" 7) Delete Funding Source");

            System.out.println("\nLedger");
            System.out.println(" 8) Show All Ledger Entries");
            System.out.println(" 9) Add Need Fulfillment");
            System.out.println("10) Add Funding Income");
            System.out.println("11) Set Funds");
            System.out.println("12) Set Goal / Threshold");
            System.out.println("13) Change Active Date");
            System.out.println("14) Show Daily Summary");

            System.out.println("\nSystem");
            System.out.println("15) Reload CSVs");
            System.out.println("16) Save CSVs");

            System.out.println("\n 0) Exit");
            System.out.print("\nSelect: ");

            switch (in.nextLine().trim()) {
                case "1" -> listNeeds();
                case "2" -> addNeed();
                case "3" -> addBundle();

                case "4" -> listFundingSources();
                case "5" -> addFundingSource();
                case "6" -> editFundingSource();
                case "7" -> deleteFundingSource();

                case "8" -> listLedgerEntries();
                case "9" -> addNeedEntry();
                // case "9" -> addNeedEntryLogic();
                case "10" -> addIncomeEntry();
                case "11" -> setFunds();
                case "12" -> setThreshold();
                case "13" -> changeDate();
                case "14" -> printSummaryHeader();

                case "15" -> master.loadAll();
                case "16" -> master.saveAll();
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

        master.getNeedsController().addBundle(name, parts);
        System.out.println("Bundle added.");
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
        master.getFundingController().deleteFundingSource(name);
        System.out.println("Deleted.");
    }

    // ===========================================================
    // LEDGER
    // ===========================================================
    private void listLedgerEntries() {
        var list = master.getLedgerController().getLog();
        
        if (list.isEmpty()) {
            System.out.println("(ledger is empty)");
            return;
        }

        list.forEach(entry -> {
            String details;
            
            //Handle entries with names (NEED, INCOME) vs. amounts (FUND, GOAL, THRESHOLD)
            if (entry.getNeedName() != null) { 
                details = String.format("%s (x%.1f)", entry.getNeedName(), entry.getCount());
            }
            else {
                details = String.format("$%.1f", entry.getAmount());
            }
            
            System.out.printf(" - [%s] %s: %s%n", 
                              entry.getDate().format(YMD), 
                              entry.getType().toString(), 
                              details);
        });
    }

    private void addNeedEntry() {
        LocalDate d = askDate("Date (yyyy-MM-dd) [blank=today]: ");

        String name = askForExistingNeedOrBundle("Need/Bundle: ");
        double units = askDouble("Units: ");
        addNeedEntryLogic(d, name, units);
        // master.getLedgerController().addEntry(d, name, units);
        System.out.println("Entry added.");
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
        LocalDate d = askDate("Date (yyyy-MM-dd) [blank=today]: ");
        // String src = ask("Funding Source: ");
        String src = askForExistingFundingSource("Funding Source: ");
        double units = askDouble("Units: ");
        
        var all = master.getFundingController().getAll();

        if (all.isEmpty()) {
            System.out.println("(no funding sources)");
            return;
        }
        else {
            for (FundingSource fs : all) {
                if (src == fs.getName()) {
                    //Need to validate source for income entries
                    master.getLedgerController().addIncomeEntry(d, src, units, fs.getAmount());
                    
                    System.out.println("Income added.");
                }
            }
        }
    }

    private void setFunds() {
        LocalDate d = askDate("Date (yyyy-MM-dd) [blank=today]: ");
        double amt = askDouble("Funds: ");
        master.getLedgerController().setFunds(d, amt);
        System.out.println("Funds updated.");
    }

    private void setThreshold() {
        LocalDate d = askDate("Date (yyyy-MM-dd) [blank=today]: ");
        double amt = askDouble("Threshold: ");
        master.getLedgerController().setGoal(d, amt);
        System.out.println("Threshold updated.");
    }

    private void changeDate() {
        LocalDate d = askDate("New active date: ");
        master.setSelectedDate(d);
        System.out.println("Active date set.");
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

    private LocalDate askDate(String p) {
        System.out.print(p);
        String s = in.nextLine().trim();
        if (s.isBlank()) return LocalDate.now();
        try {
            return LocalDate.parse(s, YMD);
        } catch (Exception e) {
            System.out.println("Invalid date, using today.");
            return LocalDate.now();
        }
    }
}