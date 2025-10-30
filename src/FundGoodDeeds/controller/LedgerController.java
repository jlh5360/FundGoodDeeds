package FundGoodDeeds.controller;

import java.time.LocalDate;
import java.util.List;

import FundGoodDeeds.model.Donation;
import FundGoodDeeds.model.LedgerEntry;
import FundGoodDeeds.model.LedgerRepository;
import FundGoodDeeds.model.NeedComponent;
import FundGoodDeeds.model.NeedsRepository;
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
        this.needsRepository = needsRepository;
    }

    //Triggers the model to load ledger data.
    public void loadData() {
		//This loadLog() method should load all LedgerEntries from the CSV file.
		//Need to sync up with Patrick on the logic
        ledgerRepository.loadLog();   //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    }

    //Retrieves the daily funding goal for the specified date.
    //Note to self: This is for sequence diagram #3, step 1-5 loop
    public double getDailyGoal(LocalDate date) {
        //2. LedgerController -> findGoal(todaysDate) -> LedgerRepository
        return ledgerRepository.findGoal(date);
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
        
        double totalCost = need.calculateTotalCost() * quantity;
        LedgerEntry entry = new LedgerEntry(date, "NEED", quantity, needName);
        
        ledgerRepository.appendLog(entry);
    }
}
