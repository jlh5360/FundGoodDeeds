package FundGoodDeeds.view;

import FundGoodDeeds.controller.MasterController;
import FundGoodDeeds.model.Day;
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
        master.registerObserver(this);
    }

    /** Entry point for the CLI. Assumes data is already loaded by the App. */
    public void startup() {
        System.out.println("=== FundGoodDeeds CLI (V2) ===");

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
            System.out.println(" 8) Add Need Fulfillment");
            System.out.println(" 9) Add Funding Income");
            System.out.println("10) Set Funds");
            System.out.println("11) Set Goal / Threshold");
            System.out.println("12) Change Active Date");
            System.out.println("13) Show Daily Summary");

            System.out.println("\nSystem");
            System.out.println("14) Reload CSVs");
            System.out.println("15) Save CSVs");

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

                case "8" -> addNeedEntry();
                case "9" -> addIncomeEntry();
                case "10" -> setFunds();
                case "11" -> setThreshold();
                case "12" -> changeDate();
                case "13" -> printSummaryHeader();

                case "14" -> master.loadAll();
                case "15" -> master.saveAll();
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
        Day d = master.getDaySummary();
        System.out.println("\n---------------------------------------------");
        System.out.println(" Active Date: " + master.getSelectedDate());
        System.out.println(" Snapshot Date: " + d.getDate());
        System.out.printf(" Funds: $%.2f | Threshold: $%.2f%n",
                d.getFunds(), d.getThreshold());
        System.out.printf(" Need Costs: $%.2f | Income: $%.2f%n",
                d.getTotalNeedCost(), d.getTotalIncome());
        System.out.printf(" Net Cost: $%.2f | Exceeded? %s%n",
                d.getNetCost(), d.isThresholdExceeded() ? "YES" : "NO");
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
        list.forEach(n -> System.out.println(" - " + n.getName()));
    }

    private void addNeed() {
        String name = ask("Name: ");
        double fixed = askDouble("Fixed cost: ");
        double var   = askDouble("Variable cost: ");
        double fees  = askDouble("Fees: ");
        double total = fixed + var + fees;

        master.getNeedsController().addNeed(name, total, fixed, var, fees);
        System.out.println("Need added.");
    }

    private void addBundle() {
        String name = ask("Bundle name: ");
        Map<NeedComponent, Integer> parts = new LinkedHashMap<>();

        boolean more = true;
        while (more) {
            String cname = ask("Component name (blank=stop): ");
            if (cname.isBlank()) break;

            NeedComponent nc = master.getNeedsController().getNeedByName(cname);
            if (nc == null) {
                System.out.println("Not found.");
                continue;
            }

            int qty = askInt("Quantity: ");
            parts.put(nc, qty);

            more = askYesNo("Add another component? ");
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
        all.forEach(fs -> System.out.println(" - " + fs));
    }

    private void addFundingSource() {
        String name = ask("Funding name: ");
        double amt = askDouble("Amount per unit: ");
        master.getFundingController().addFundingSource(name, amt);
        System.out.println("Added.");
    }

    private void editFundingSource() {
        String name = ask("Editing which source? ");
        double amt = askDouble("New $/unit: ");
        master.getFundingController().updateFundingSource(name, amt);
        System.out.println("Updated.");
    }

    private void deleteFundingSource() {
        String name = ask("Delete which? ");
        master.getFundingController().deleteFundingSource(name);
        System.out.println("Deleted.");
    }

    // ===========================================================
    // LEDGER
    // ===========================================================
    private void addNeedEntry() {
        LocalDate d = askDate("Date: ");
        String name = ask("Need/Bundle: ");
        double units = askDouble("Units: ");
        master.getLedgerController().addEntry(d, name, units);
        System.out.println("Entry added.");
    }

    private void addIncomeEntry() {
        LocalDate d = askDate("Date: ");
        String src = ask("Funding Source: ");
        double units = askDouble("Units: ");

        // need to validate source for income entries
        master.getLedgerController().addIncomeEntry(d, src, units);
        
        System.out.println("Income added.");
    }

    private void setFunds() {
        LocalDate d = askDate("Date: ");
        double amt = askDouble("Funds: ");
        master.getLedgerController().setFunds(d, amt);
        System.out.println("Funds updated.");
    }

    private void setThreshold() {
        LocalDate d = askDate("Date: ");
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
