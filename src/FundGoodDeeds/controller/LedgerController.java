package FundGoodDeeds.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

import FundGoodDeeds.model.*;
import FundGoodDeeds.model.AbstractLedgerEntry.EntryType;
import FundGoodDeeds.view.ConsoleView;

/**
 * Handles operations related to Ledger Entries and Donations.
 */
public class LedgerController {
	private final LedgerRepository ledgerRepository;
    

	//Note to self: This needsRepository will somehow be used in the recordFulfillment()
	//method which it will record the fulfillment of a Need or Bundle (NEED entry).
    private final NeedsRepository needsRepository;   //Needed for 'get need'
	//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    //Dependency Injection via constructor
    public LedgerController(LedgerRepository ledgerRepository, NeedsRepository needsRepository) {
        this.ledgerRepository = ledgerRepository;
        this.needsRepository = needsRepository; // This is still needed for other methods like recordFulfillment
        
    }

    //Allow the View to register as an Observer
    public void addObserver(Observer o) {
        if (ledgerRepository != null) {
            this.ledgerRepository.addObserver(o);
        }
    }

    //Triggers the model to load ledger data.
    // ***FUTURE FEATURE MARKED FOR REFACTORING***
    // public void loadData() {
	// 	//This loadLog() method should load all LedgerEntries from the CSV file.
	// 	//Need to sync up with Patrick on the logic
    //     ledgerRepository.();   //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    // }

    //Triggers the model to save ledger data.
    public void saveLog() {
        try {
            ledgerRepository.saveLogEntries();
        } catch (Exception e) {
            throw new RuntimeException("Save failed: " + e.getMessage(), e);
        }
    }

    public LedgerRepository getLedgerRepository() {
        return this.ledgerRepository;
    }

    public NeedsRepository getNeedsRepository() {
        return this.needsRepository;
    }

    public List<Bundle> getBundlesContainingNeed(String needName) {
        return needsRepository.findBundlesContainingNeed(needName);
    }

    //Retrieves the daily funding goal for the specified date.
    //Note to self: This is for sequence diagram #3, step 1-5 loop
    public double getDailyGoal(LocalDate date) {
        //2. LedgerController -> findGoal(todaysDate) -> LedgerRepository
        return ledgerRepository.findGoal(date);
    }

    //Retrieves the active funding goal for a specific date, applying fallback logic.
    public double getGoal(LocalDate date) {
        return ledgerRepository.findGoal(date);
    }
    
    //Expose the getFundsForDate method to the view
    public double getFunds(LocalDate date) {
        return ledgerRepository.findFunds(date);
    }

    public void setUser(User user)
    {
        this.ledgerRepository.setUser(user);
    }

    // /**
    //  * Sets the initial/current funds amount.
    //  * @param amount The new funds total.
    //  * @param date The date the funds are set.
    //  */
    // public void setFunds(double amount, LocalDate date) {
    //     LedgerEntity entity = new LedgerEntity(date, EntryType.FUND, amount);
    //     ledgerRepository.addEntry(entity);
    // }

    public double getThreshold(LocalDate date) {
        return ledgerRepository.findThreshold(date);
    }

    public void setThreshold(LocalDate date, double amount) {
        ledgerRepository.setThreshold(date, amount);
        // LedgerEntity entity = new LedgerEntity(date, LedgerEntity.EntryType.THRESHOLD, amount);
        // ledgerRepository.addEntry(entity);
    }
    
    //Retrieves the total donations/fulfillment value for the specified date.
    //Note to self: This is for sequence diagram #3, step 1-5 loop
    public double getTodayDonations(LocalDate date) {
		//Look back at our design doc on the sequence diagram #3 for clearer/visual understanding:
        //    -  3. LedgerRepository -> calculateDonations(todaysDate) -> LedgerRepository
        //    -  5. LedgerRepository -> return Donations -> LedgerController
        return ledgerRepository.calculateDonations(date);
    }

    //Expose log data to the view
    public List<LedgerEntity> getLog() {
        return ledgerRepository.getAllLogEntries();
    }

	//Records the fulfillment of a Need or Bundle (NEED entry).
    public void recordFulfillment(String needName, double quantity, LocalDate date) {
        if (quantity <= 0.0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }

        NeedComponent need = needsRepository.getNeedByName(needName);
        if (need == null) {
            throw new IllegalArgumentException("Need or Bundle not found in catalog: " + needName);
        }
        
        double totalCost = (need.getTotal() * quantity);

        // //Delete Need/Bundle after fulfillment
        // this.needsRepository.removeNeedComponent(needName);
        
        LedgerEntity entry = new LedgerEntity(date, LedgerEntity.EntryType.NEED, needName, quantity, totalCost);
        // LedgerEntity entry = new LedgerEntity(date, LedgerEntity.EntryType.NEED, needName, quantity);
        
        ledgerRepository.save(entry);
    }

    //Record the available funds for a date (writes a "FUND" entry)
    public void setFunds(LocalDate date, double amount) {
        if (date == null) {
            date = LocalDate.now();
        }

        validateDateForGoalOrFundChange(date, LedgerEntity.EntryType.FUND);

        if (amount < 0) {
            throw new IllegalArgumentException("Funds amount cannot be negative.");
        }

        LedgerEntity entry = new LedgerEntity(date, LedgerEntity.EntryType.FUND, amount);
        ledgerRepository.save(entry);
    }

    //Record the fundraising goal for a date (writes a "GOAL" entry)
    public void setGoal(LocalDate date, double goal) {
        if (date == null) {
            date = LocalDate.now();
        }

        validateDateForGoalOrFundChange(date, LedgerEntity.EntryType.GOAL);

        if (goal < 0) {
            throw new IllegalArgumentException("Goal cannot be negative.");
        }

        LedgerEntity entry = new LedgerEntity(date, LedgerEntity.EntryType.GOAL, goal);
        ledgerRepository.save(entry);
    }

    //Record fulfillment for a specific Need/Bundle by name (writes a "NEED" entry)
    public void addEntry(LocalDate date, String needOrBundleName, double quantity) {
        if (date == null) {
            date = LocalDate.now();
        }

        validateDateForEntry(date);

        if ((needOrBundleName == null) || (needOrBundleName.isBlank())) {
            throw new IllegalArgumentException("Name must not be empty.");
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be > 0.");
        }
        
        //Check for existence using the injected NeedsRepository
        NeedComponent need = needsRepository.getNeedByName(needOrBundleName);
        if (need == null) {
            throw new IllegalArgumentException("Need or Bundle not found in catalog: " + needOrBundleName);
        }

        double totalCost = (need.getTotal() * quantity);

        // //Delete Need/Bundle after fulfillment
        // this.needsRepository.removeNeedComponent(needOrBundleName);

        LedgerEntity entry = new LedgerEntity(date, LedgerEntity.EntryType.NEED, needOrBundleName, quantity, totalCost);
        // LedgerEntity entry = new LedgerEntity(date, LedgerEntity.EntryType.NEED, needOrBundleName, quantity);
        ledgerRepository.save(entry);
    }

    //FOR FUTURE IMPLEMENTATION
    public void addIncomeEntry(LocalDate date, String fundingSourceName, double units, double amount) {
        //Need to create a new LedgerEntity of type INCOME, tracking the funding source name and units.
        //This is for the implementation to add an Income Entry to the ledger.
        if (date == null) {
            date = LocalDate.now();
        }

        validateDateForEntry(date);

        if ((fundingSourceName == null) || (fundingSourceName.isBlank())) {
            throw new IllegalArgumentException("Funding source name must not be empty.");
        }

        if (units <= 0) {
            throw new IllegalArgumentException("Units must be > 0.");
        }

        double totalIncome = (units * amount);

        //Creates a ledger entry of type INCOME, using the needName/count fields for sourceName/units.
        LedgerEntity entry = new LedgerEntity(date, LedgerEntity.EntryType.INCOME, fundingSourceName, units, totalIncome);
        // LedgerEntity entry = new LedgerEntity(date, LedgerEntity.EntryType.INCOME, fundingSourceName, units);
        ledgerRepository.save(entry);
    }

    // //Saves BOTH sides (needs + ledger). One-stop for the CLI.
    // public void saveAllData() {
    //     try {
    //         if (this.needsRepository != null) {
    //             this.needsRepository.saveNeedsCatalog();
    //         }

    //         ledgerRepository.saveLogEntries();

    //     } catch (Exception e) {
    //         throw new RuntimeException("Save failed: " + e.getMessage(), e);
    //     }
    // }

    public void loadData() {
        ledgerRepository.loadLog();
    }

    /**
     * Records fulfillment of a basic Need by drawing down its units from a specific Bundle.
     * It handles recording the ledger entry, updating the bundle, and deleting the 
     * base Need from the catalog if it's no longer a component of any bundle.
     * @param date The date of fulfillment.
     * @param needName The name of the basic need being fulfilled.
     * @param bundle The specific bundle to draw the units from.
     * @param unitsToFulfill The quantity to fulfill.
     */
    public void fulfillNeedFromBundle(LocalDate date, String needName, Bundle bundle, int unitsToFulfill) {
        
        NeedComponent originalNeed = needsRepository.getNeedByName(needName);
        if (originalNeed == null) {
            throw new IllegalArgumentException("Need not found in catalog: " + needName);
        }
        
        double unitCost = originalNeed.getTotal();
        double totalCost = unitCost * unitsToFulfill;

        // 1. Reduce the units in the bundle
        int unitsRemoved = bundle.removeComponentUnits(needName, unitsToFulfill);
        
        if (unitsRemoved != unitsToFulfill) {
            throw new RuntimeException("Fulfillment error: Expected to remove " + unitsToFulfill + " units but removed " + unitsRemoved + ".");
        }

        // 2. Record the fulfillment entry (basic need fulfilled)
        LedgerEntity entry = new LedgerEntity(date, LedgerEntity.EntryType.NEED, needName, unitsRemoved, totalCost);
        ledgerRepository.save(entry);

        // 3. Check if the basic need should now be removed from the catalog.
        // It should be removed if it still exists as a top-level Need AND is no longer a component of any bundle.
        if (needsRepository.getNeedByName(needName) instanceof Need) { // Check if it is a basic Need, not a Bundle
            if (!needsRepository.isNeedComponentOfAnyBundle(needName)) {
                // Remove the basic Need from the top-level catalog
                needsRepository.removeNeedComponent(needName);
            }
        }
    }

    // Helper method for date validation (for #3)
    private void validateDateForEntry(LocalDate date) {
        LocalDate now = LocalDate.now();
        // Restriction #3: No entries allowed for days older than a week (8 days ago or more)
        LocalDate earliestAllowedDate = now.minusDays(7); 
        
        if (date.isBefore(earliestAllowedDate)) {
            throw new IllegalArgumentException("Entries cannot be logged for dates older than one week (" + earliestAllowedDate + ").");
        }
    }

    // Helper method for goal/fund validation (#5)
    private void validateDateForGoalOrFundChange(LocalDate date, LedgerEntity.EntryType type) {
        LocalDate now = LocalDate.now();
        
        // Check #5: Daily threshold/funds can only be changed for current or future dates.
        if (date.isBefore(now)) {
            throw new IllegalArgumentException(type.name() + " can only be set for the current date or future dates.");
        }

        // Special check for GOAL entry on the current date (#5)
        if (date.isEqual(now) && type == LedgerEntity.EntryType.GOAL) {
            if (ledgerRepository.hasNonGoalOrFundEntries(date)) {
                throw new IllegalArgumentException("Cannot change GOAL on current date if needs or income have already been entered.");
            }
        }
    }

    /**
     * Retrieves the total funds received (INCOME entries) for the specified date.
     * Implements logic for Program Operation #8.
     * @param date The date to check.
     * @return The total income for the day.
     */
    public double calculateDailyIncome(LocalDate date) {
        // Requires implementation in LedgerRepository.calculateIncomeReceived()
        return ledgerRepository.calculateIncomeReceived(date);
    }

    /**
     * Deletes a specific log entry by its index in the repository's logEntries list.
     * Implements logic for Program Operations #11 and #12.
     * @param index The 0-based index of the entry to delete.
     * @return true if the entry was successfully removed, false otherwise.
     */
    public boolean deleteEntryByIndex(int index) {
        return ledgerRepository.deleteLogEntry(index);
    }
}
