package FundGoodDeeds.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

import FundGoodDeeds.model.CSVManager;
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
    private CSVManager manager;

    private final NeedsController needsController;
    private final LedgerController ledgerController;
    private final FundingController fundingController;

    private LocalDate selectedDate = LocalDate.now();

    private final List<Observer> views = new ArrayList<>();

    //Dependency Injection via constructor
    public MasterController(CSVManager manager,NeedsController needsController, LedgerController ledgerController, FundingController fundingController) {
        this.manager = manager;
        this.needsController = needsController;
        this.ledgerController = ledgerController;
        this.fundingController = fundingController;
    }

    //Allow the views to be registered in the view list
    public void registerViews(Observer o)
    {
        views.add(o);
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
        fundingController.loadData();
        ledgerController.loadData();

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
        notifyViews();
    }

    public void resetSelectedDateToToday() {
        LocalDate today = LocalDate.now();
        setSelectedDate(today);
        notifyViews();
    }
    
    public Day getDaySummary(LocalDate date) {
        //The buildDay() function should get all the information from the 
        //needs and funding and ledger repositories on a particular day
        // return ledgerRepository.buildDay(selectedDate, needsRepository, fundingRepository);
        
        return this.ledgerController.getLedgerRepository().buildDay(selectedDate);
    }

    public double getTotalIncome(LocalDate date) {
        // return this.fundingController.getFundingRepository().getTotalFunds();
        // changed to use ledgerController to calculate total income for specific date
        return this.ledgerController.getLedgerRepository().findIncome(date);
    }

    public double getTotalNeedCost() {
        return this.needsController.getNeedsRepository().getTotalNeedsCost();
    }

    //FOR FUTURE IMPLEMENTATION
    public double getNetCost(LocalDate date) 
    {
        // Make sure it doesn't give negative values

        return Math.max(0,getTotalNeedCost() - getTotalIncome(date));
    }

    public boolean userExists(String username, String password)
    {
        List<String> users = manager.readData("users.csv");

    }

    /**
     * Calculates the net cost (Costs Fulfilled - Funds Received) for the selected date.
     * Implements logic for Program Operation #8.
     * @return The net cost for the selected date.
     */
    public double getNetDayCost() {
        LocalDate date = getSelectedDate();
        // Assuming getTodayDonations gets NEED costs
        double fulfilledCosts = ledgerController.getTodayDonations(date); 
        // New method for INCOME
        double fundsReceived = ledgerController.calculateDailyIncome(date); 
        
        return fulfilledCosts - fundsReceived;
    }

    //FOR FUTURE IMPLEMENTATION
    public boolean isThresholdExceeded() {
        return getTotalNeedCost() > this.ledgerController.getLedgerRepository().getEntryForDate(LedgerEntity.EntryType.THRESHOLD, getSelectedDate());
    }

    /**
     * Checks if the daily net costs exceed the set threshold for the selected date.
     * Implements logic for Program Operation #8.
     * @return true if net costs for the day exceed the threshold.
     */
    public boolean isDailyThresholdExceeded() {
        LocalDate date = getSelectedDate();
        double netCosts = getNetDayCost(); 
        double threshold = ledgerController.getThreshold(date); 
        
        return netCosts > threshold;
    }

    private void notifyViews() {
        for (Observer view : views) {
            // Note: The null arguments are standard for the java.util.Observer pattern
            view.update(null, null); 
        }
    }
}
