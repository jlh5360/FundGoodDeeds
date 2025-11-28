package FundGoodDeeds.controller;

import java.time.LocalDate;
import java.util.Observer;

import FundGoodDeeds.model.Day;
import FundGoodDeeds.model.FundingRepository;
import FundGoodDeeds.model.LedgerEntity;
import FundGoodDeeds.model.LedgerRepository;
import FundGoodDeeds.model.NeedsRepository;

/**
 * The MasterController orchestrates and delegates high-level operations 
 * across all major subsystems (Needs, Ledger, Funding).  This centralizes 
 * the startup and shutdown logic for the application's data.
 */
public class MasterController {
    // private final NeedsRepository needsRepository;
    // private final LedgerRepository ledgerRepository;
    // private final FundingRepository fundingRepository;

    private final NeedsController needsController;
    private final LedgerController ledgerController;
    private final FundingController fundingController;

    private LocalDate selectedDate = LocalDate.now();

    //Dependency Injection via constructor
    public MasterController(NeedsController needsController, LedgerController ledgerController, FundingController fundingController) {
        this.needsController = needsController;
        this.ledgerController = ledgerController;
        this.fundingController = fundingController;
    }

    //Allow the View/App to register as an Observer
    public void registerObservers(Observer o) {
        this.needsController.addObserver(o);
        this.ledgerController.addObserver(o);
        this.fundingController.addObserver(o);
    }

    //Loading all data View's startup() function
    public void loadAll() {
        needsController.loadData();
        ledgerController.loadData();
        fundingController.loadData();

        System.out.println("CSV reloaded");
    }

    //Saving all data the View's saveAll() function
    public void saveAll() {
        try {
            needsController.saveNeeds();
            ledgerController.saveLog();
            fundingController.saveData();
        } catch (RuntimeException e) {
            //Re-throw with a more general message to the View/App
            throw new RuntimeException("One or more data save operations failed: " + e.getMessage(), e);
        }
    }

    //Accessors for sub-controllers (to be used by View)
    public NeedsController getNeedsController() {
        return needsController;
    }

    public LedgerController getLedgerController() {
        return ledgerController;
    }
    
    public FundingController getFundingController() {
        return fundingController;
    }

    public LocalDate getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(LocalDate date) {
        this.selectedDate = date;
    }

    public Day getDaySummary(LocalDate date) {
        //The buildDay() function should get all the information from the 
        //needs and funding and ledger repositories on a particular day
        // return ledgerRepository.buildDay(selectedDate, needsRepository, fundingRepository);
        
        return this.ledgerController.getLedgerRepository().buildDay(selectedDate);
    }

    public double getTotalIncome() {
        return this.fundingController.getFundingRepository().getTotalFunds();
    }

    public double getTotalNeedCost() {
        return this.needsController.getNeedsRepository().getTotalNeedsCost();
    }

    //FOR FUTURE IMPLEMENTATION
    public double getNetCost() 
    {
        // Make sure it doesn't give negative values

        return Math.max(0,getTotalNeedCost() - getTotalIncome());
    }

    //FOR FUTURE IMPLEMENTATION
    public boolean isThresholdExceeded() {
        return getTotalNeedCost() > this.ledgerController.getLedgerRepository().getEntryForDate(LedgerEntity.EntryType.THRESHOLD, getSelectedDate());
    }
}
