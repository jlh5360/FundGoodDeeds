package FundGoodDeeds.controller;

import java.util.List;
import java.util.Observer;

import FundGoodDeeds.model.FundingRepository;
import FundGoodDeeds.model.FundingSource;
import FundGoodDeeds.model.User;

public class FundingController {
    private final FundingRepository fundingRepository;

    //Dependency Injection via constructor
    public FundingController(FundingRepository fundingRepository) {
        this.fundingRepository = fundingRepository;
    }

    //Allow the View to register as an Observer
    public void addObserver(Observer o) {
        if (fundingRepository != null) {
            this.fundingRepository.addObserver(o);
        }
    }

    public void setUser(User user)
    {
        this.fundingRepository.setUser(user);
    }

    //Triggers the model to load all funding source data
    public void loadData() {
        fundingRepository.loadFunds();
    }
    
    //Triggers the model to save all funding source data
    public void saveData() {
        try {
            fundingRepository.saveFundsCatalog();
        } catch (Exception e) {
            throw new RuntimeException("Save failed for funding sources: " + e.getMessage(), e);
        }
    }

    //Creates and adds a new Funding Source to the catalog.
    //The recurrence field is removed per the new CSV requirement.
    public void addFundingSource(String name, double amount) {
        
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Funding source name cannot be empty.");
        }
        if (amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative.");
        }

        //Update constructor call
        fundingRepository.addFundingSource(name, amount);
    }

    public void updateFundingSource(String fundingSourceName, Double fundingSourceAmount) {
        fundingRepository.editFundingSource(fundingSourceName, fundingSourceAmount);
    }

    public void deleteFundingSource(String fundingSourceName) {
        fundingRepository.removeFundingSource(fundingSourceName);
    }

    //Retrieves the entire catalog of funding sources
    public List<FundingSource> getAll() {
        return fundingRepository.getFundingSources();
    }

    public FundingRepository getFundingRepository() {
        return this.fundingRepository;
    }
}
