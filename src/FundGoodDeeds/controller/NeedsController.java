package FundGoodDeeds.controller;

import java.util.List;

import FundGoodDeeds.model.Bundle;
import FundGoodDeeds.model.Need;
import FundGoodDeeds.model.NeedComponent;
import FundGoodDeeds.model.NeedsRepository;

public class NeedsController {
	private final NeedsRepository needsRepository;

	//Dependency Injection via constructor
	public NeedsController(NeedsRepository needsRepository) {
		this.needsRepository = needsRepository;
	}

	//Triggers the model to load all need and bundle data.
	//Note to self:
	//     -  This is for sequence diagram #1
	//     -  NeedsController is implied between ConsoleUI and NeedsRepository
    public void loadData() {
        //Delegate data loading to the repository
        needsRepository.loadNeeds();
    }
    
    //Creates and adds a new Basic Need to the catalog.
    public void addNeed(String name, double total, double fixed, double variable, double fees) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Need name cannot be empty.");
        }
        if (needsRepository.getNeedByName(name) != null) {
			throw new IllegalArgumentException("A Need or Bundle with that name already exists.");
        }
        
        Need newNeed = new Need(name, total, fixed, variable, fees);
        needsRepository.appendNeed(newNeed);
    }

    //Note: This assumes the BundleParts' names can be resolved later.
    public void addBundle(String name, List<BundlePart> parts) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Bundle name cannot be empty.");
        }
        if (parts == null || parts.isEmpty()) {
            throw new IllegalArgumentException("A Bundle must contain at least one part.");
        }
        if (needsRepository.getNeedByName(name) != null) {
			throw new IllegalArgumentException("A Need or Bundle with that name already exists.");
        }
        
        Bundle newBundle = new Bundle(name, parts);
        
        needsRepository.appendNeed(newBundle);
        needsRepository.resolveAll();
    }

    //--- Display Methods for ConsoleView ---
    //Retrieves the entire catalog of Needs and Bundles.
    public List<NeedComponent> getNeedsCatalog() {
        return needsRepository.getAllNeeds();
    }
}
