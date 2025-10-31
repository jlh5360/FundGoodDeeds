/**
 * CLI "front-end" per R1 spec.
 * - Thin and delegates all work to controllers
 * - No direct file IO
 *
 * Assumed controller APIs (tweak names if needed):
 *  NeedsController:
 *    void loadData();                // load needs.csv
 *    void loadData();                // save needs.csv
 *    List<NeedComponent> getNeedsCatalog();
 * 
 *    void addNeed(String name, double total, double fixed, double variable, double fees);
 *    void addBundle(String bundleName, Map<String, Double> partToQty);
 *
 *  LedgerController:
 *    NEEDED void loadLog();                  // load log.csv
 *    NEEDED void saveLog();                  // save log.csv
 *    NEEDED void setFunds(LocalDate date, double amount); // "f" line
 * 
 *    void setGoal(LocalDate date, double goal);    // "g" line
 *    void addEntry(LocalDate date, String name, double count); // "n" line (need or bundle)
 * Connor Bashaw - ConsoleView.java
 */



package FundGoodDeeds.view;

import FundGoodDeeds.controller.NeedsController;
import FundGoodDeeds.controller.LedgerController;
import FundGoodDeeds.model.NeedComponent;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;


public class ConsoleView implements Observer {

    private final NeedsController needs;
    private final LedgerController ledger;

    private final Scanner in = new Scanner(System.in);
    private final DateTimeFormatter YMD = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ConsoleView(NeedsController needs, LedgerController ledger) {
        this.needs = needs;
        this.ledger = ledger;

        needs.addObserver(this);
        ledger.addObserver(this);
    }

    /** Start the loop. Safe to run with empty CSVs. */
    public void startup() {
        // Initial loads (no dupes recomended)
        safe(() -> needs.loadData(), "Loading needs");
        safe(() -> ledger.loadData(),  "Loading ledger");

        System.out.println("=== FundGoodDeeds CLI (V1.0) ===");
        boolean run = true;
        while (run) {
            System.out.println();
            System.out.println("Catalog");
            System.out.println(" 1) List needs & bundles");
            System.out.println(" 2) Add Need (leaf)");
            System.out.println(" 3) Add Bundle (composite)");
            System.out.println();
            System.out.println("Ledger");
            System.out.println(" 4) Add donation/fulfillment");
            System.out.println(" 5) Set funds for date");
            System.out.println(" 6) Set goal for date");
            System.out.println();
            System.out.println("Data");
            System.out.println(" 7) Reload CSVs (needs/log)");
            System.out.println(" 8) Save CSVs   (needs/log)");
            System.out.println();
            System.out.println(" 0) Exit");
            System.out.print("Select: ");

            switch (in.nextLine().trim()) {
                case "1" -> listCatalog();
                case "2" -> addNeedFlow();
                case "3" -> addBundleFlow();
                case "4" -> addLedgerEntryFlow();
                case "5" -> setFundsFlow();
                case "6" -> setGoalFlow();
                case "7" -> reloadAll();
                case "8" -> saveAll();
                case "0" -> run = false;
                default  -> System.out.println("Invalid choice.");
            }
        }

        // save prompt (not mandatory)
        if (askYesNo("Save before exit? (y/n): ")) saveAll();
        System.out.println("Goodbye.");
    }

    // ---------- Menu handlers ----------

    //Will update when an Observable (NeedsRepository or LedgerRepository) changes.
    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof FundGoodDeeds.model.NeedsRepository) {
            System.out.println("\n[ALERT] Needs Catalog updated: " + arg);
        }
        else if (o instanceof FundGoodDeeds.model.LedgerRepository) {
            System.out.println("\n[ALERT] Ledger updated: " + arg);
        }
        else {
            System.out.println("\n[ALERT] Data model changed unexpectedly: " + arg);
        }
    }
    
    private void listCatalog() {
        List<NeedComponent> items = needs.getNeedsCatalog();
        if (items == null || items.isEmpty()) {
            System.out.println("(no needs/bundles)");
            return;
        }
        System.out.println("-- Catalog --");
        for (NeedComponent nc : items) {
            try {
                //get total cost comes from need component, will need implementation once code in model is created
                System.out.printf("- %s [total=%.2f]%n", nc.getName(), nc.getTotalCost());
            } catch (Exception e) {
                System.out.println("- " + String.valueOf(nc));
            }
        }
    }

    private void addNeedFlow() {
        System.out.println("-- Add Need --");
        String name   = askString("Name: ");
        double total  = askDouble("Total cost: ");
        double fixed  = askDouble("Fixed cost: ");
        double var    = askDouble("Variable cost: ");
        double fees   = askDouble("Fees: ");
        needs.addNeed(name, total, fixed, var, fees);
        System.out.println("Need added.");
    }

    // PLEASE INVESTIGATE THIS METHOD and workings.
    private void addBundleFlow() {
        System.out.println("-- Add Bundle --");
        String bundleName = askString("Bundle name: ");

        // need to make to list<bundlepart> to match controller api
        //can be made to hash map in future parts 
        // Map<String, Double> parts = new HashMap<>();
        //using bundlepart for now!
        List<BundlePart> parts = new ArrayList<>();
        boolean more = true;
        while (more) {
            String part = askString(" Component name (existing need or bundle): ");
            double qty  = askDouble(" Quantity: ");
            parts.add(part, qty);
            more = askYesNo(" Add another part? (y/n): ");
        }

        if (parts.isEmpty()) {
            System.out.println("Issue: A bundle must contain at least one component.");
            return;
        }

        needs.addBundle(bundleName, parts);
        System.out.println("Bundle added.");
        System.out.printf("Success: Bundle '%s' created with %d components and added to catalog.%n", name, parts.size());

    }

    private void addLedgerEntryFlow() {
        System.out.println("-- Add Donation/Fulfillment --");
        LocalDate day = askDate("Date (YYYY-MM-DD, blank=today): ");
        String name   = askString("Need/Bundle name: ");
        double count  = askDouble("Units fulfilled: ");
        ledger.registerDonations(count, name, day);
        System.out.println("Ledger entry added.");
    }

    // PLEASE INVESTIGATE THIS METHOD.
    private void setFundsFlow() {
        System.out.println("-- Set Available Funds --");
        LocalDate day = askDate("Date (YYYY-MM-DD, blank=today): ");
        double amt    = askDouble("Funds amount: ");
        ledger.setFunds(day, amt);
        System.out.println("Funds recorded.");
    }

    private void setGoalFlow() {
        System.out.println("-- Set Funding Goal --");
        LocalDate day = askDate("Date (YYYY-MM-DD, blank=today): ");
        double goal   = askDouble("Goal amount: ");
        ledger.setDailyGoal(goal, day);
        System.out.println("Goal recorded.");
    }

    private void reloadAll() {
        safe(() -> needs.loadData(), "Reload needs");
        safe(() -> ledger.loadData(),  "Reload ledger");
        System.out.println("Reloaded.");
    }

    // PLEASE INVESTIGATE THIS METHOD.
    // should live within repositories, but for simplicity placed here
    // private void saveAll() {
    //     safe(() -> needs.saveNeeds(), "Save needs");
    //     safe(() -> ledger.saveLog(),  "Save ledger");
    //     System.out.println("Saved.");
    // }

    // ---------- Tiny helpers ----------

    private void safe(Runnable r, String label) {
        try { r.run(); } catch (Exception e) {
            System.out.println(label + " failed: " + e.getMessage());
        }
    }

    private String askString(String prompt) {
        System.out.print(prompt);
        return in.nextLine().trim();
    }

    private boolean askYesNo(String prompt) {
        System.out.print(prompt);
        String s = in.nextLine().trim().toLowerCase(Locale.ROOT);
        return s.startsWith("y") || s.equals("1") || s.equals("yes");
    }

    private double askDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = in.nextLine().trim();
            try {
                return Double.parseDouble(s);
            } catch (NumberFormatException e) {
                System.out.println("Enter a number.");
            }
        }
    }

    private LocalDate askDate(String prompt) {
        System.out.print(prompt);
        String s = in.nextLine().trim();
        if (s.isEmpty()) return LocalDate.now();
        try {
            return LocalDate.parse(s, YMD);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date, using today.");
            return LocalDate.now();
        }
    }
}