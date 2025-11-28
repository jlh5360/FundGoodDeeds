package FundGoodDeeds.controller;

import java.util.List;
import java.util.Map;
import java.util.Observer;

import FundGoodDeeds.model.Bundle;
import FundGoodDeeds.model.LedgerRepository;
import FundGoodDeeds.model.Need;
import FundGoodDeeds.model.NeedComponent;
import FundGoodDeeds.model.NeedsRepository;

public class NeedsController {
	private final NeedsRepository needsRepository;

	//Dependency Injection via constructor
	public NeedsController(NeedsRepository needsRepository) {
		this.needsRepository = needsRepository;
	}

    //Allow the View to register as an Observer
    public void addObserver(Observer o) {
        if (needsRepository != null) {
            this.needsRepository.addObserver(o);
        }
    }

	//Triggers the model to load all need and bundle data.
	//Note to self:
	//     -  This is for sequence diagram #1
	//     -  NeedsController is implied between ConsoleUI and NeedsRepository
    public void loadData() {
        //Delegate data loading to the repository
        needsRepository.loadNeeds();
    }
    // ***Future feature marked for refactoring***
    //Triggers the model to save all need and bundle data.
    // public void saveNeeds() {
        
    //     //Delegate data loading to the repository
    //     needsRepository.saveNeeds();
    // }
    
    //Creates and adds a new Basic Need to the catalog.
    public void addNeed(String name, double total) {
    // public void addNeed(String name, double total, double fixed, double variable, double fees) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Need name cannot be empty.");
        }
        if (needsRepository.getNeedByName(name) != null) {
			throw new IllegalArgumentException("A Need or Bundle with that name already exists.");
        }
        
        Need newNeed = new Need(name, total);
        // Need newNeed = new Need(name, total, fixed, variable, fees);
        needsRepository.appendNeed(newNeed);
    }

    //Note: This assumes the BundleParts' names can be resolved later.
    public void addBundle(String name, Map<NeedComponent, Integer> parts) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Bundle name cannot be empty.");
        }
        if (parts == null || parts.isEmpty()) { // This check is correct for a Map as well
            throw new IllegalArgumentException("A Bundle must contain at least one part.");
        }
        if (needsRepository.getNeedByName(name) != null) {
			throw new IllegalArgumentException("A Need or Bundle with that name already exists.");
        }
        
        Bundle newBundle = new Bundle(name, parts);
        
        needsRepository.appendNeed(newBundle);
    }

    //--- Display Methods for ConsoleView ---
    //Retrieves the entire catalog of Needs and Bundles.
    public List<NeedComponent> getNeedsCatalog() {
        return needsRepository.getNeedsCatalog();
    }

    public void saveNeeds() {
        try {
            this.needsRepository.saveNeedsCatalog();
        } catch (Exception e) {
            throw new RuntimeException("Save failed: " + e.getMessage(), e);
        }
    }

    public NeedComponent getNeedByName(String name) {
        return needsRepository.getNeedByName(name);
    }

    public NeedsRepository getNeedsRepository() {
        return this.needsRepository;
    }
}
