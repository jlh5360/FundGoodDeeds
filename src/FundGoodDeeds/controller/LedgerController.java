package FundGoodDeeds.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Observer;

import FundGoodDeeds.model.*;
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

    //Retrieves the daily funding goal for the specified date.
    //Note to self: This is for sequence diagram #3, step 1-5 loop
    public double getDailyGoal(LocalDate date) {
        //2. LedgerController -> findGoal(todaysDate) -> LedgerRepository
        return ledgerRepository.findGoal(date);
    }

    //Retrieves the active funding goal for a specific date, applying fallback logic.
    public double getGoal(LocalDate date) {
        return ledgerRepository.getGoalForDate(date);
    }
    
    //Expose the getFundsForDate method to the view
    public double getFunds(LocalDate date) {
        return ledgerRepository.getFundsForDate(date);
    }
    
    //Retrieves the total donations/fulfillment value for the specified date.
    //Note to self: This is for sequence diagram #3, step 1-5 loop
    public double getTodayDonations(LocalDate date) {
		//Look back at our design doc on the sequence diagram #3 for clearer/visual understanding:
        //    -  3. LedgerRepository -> calculateDonations(todaysDate) -> LedgerRepository
        //    -  5. LedgerRepository -> return Donations -> LedgerController
        return ledgerRepository.calculateDonations(date);
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
        
        LedgerEntity entry = new LedgerEntity(date, LedgerEntity.EntryType.NEED, needName, quantity);
        
        ledgerRepository.save(entry);
    }

    //Record the available funds for a date (writes a "FUND" entry)
    public void setFunds(java.time.LocalDate date, double amount) {
        if (date == null) {
            date = java.time.LocalDate.now();
        }

        if (amount < 0) {
            throw new IllegalArgumentException("Funds amount cannot be negative.");
        }

        LedgerEntity entry = new LedgerEntity(date, LedgerEntity.EntryType.FUND, amount);
        ledgerRepository.save(entry);
    }

    //Record the fundraising goal for a date (writes a "GOAL" entry)
    public void setGoal(java.time.LocalDate date, double goal) {
        if (date == null) {
            date = java.time.LocalDate.now();
        }

        if (goal < 0) {
            throw new IllegalArgumentException("Goal cannot be negative.");
        }

        LedgerEntity entry = new LedgerEntity(date, LedgerEntity.EntryType.GOAL, goal);
        ledgerRepository.save(entry);
    }

    //Record fulfillment for a specific Need/Bundle by name (writes a "NEED" entry)
    public void addEntry(java.time.LocalDate date, String needOrBundleName, double quantity) {
        if (date == null) {
            date = java.time.LocalDate.now();
        }

        if ((needOrBundleName == null) || (needOrBundleName.isBlank())) {
            throw new IllegalArgumentException("Name must not be empty.");
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be > 0.");
        }

        LedgerEntity entry = new LedgerEntity(date, LedgerEntity.EntryType.NEED, needOrBundleName, quantity);
        ledgerRepository.save(entry);
    }

    //Saves BOTH sides (needs + ledger). One-stop for the CLI.
    public void saveAllData() {
        try {
            if (this.needsRepository != null) {
                this.needsRepository.saveNeedsCatalog();
            }

            ledgerRepository.saveLogEntries();

        } catch (Exception e) {
            throw new RuntimeException("Save failed: " + e.getMessage(), e);
        }
    }

    public void loadData() {
        ledgerRepository.loadLog();
    }
}
