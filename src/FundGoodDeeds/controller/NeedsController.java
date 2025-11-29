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

    /**
     * Edits the total cost of an existing basic Need. Implements logic for Program Operation #14 (part 1).
     * @param name The name of the Need.
     * @param newTotal The new total cost.
     */
    public void editNeedTotal(String name, double newTotal) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Need name cannot be empty.");
        }
        if (newTotal < 0) {
            throw new IllegalArgumentException("Total cost cannot be negative.");
        }

        if (!needsRepository.editNeedTotal(name, newTotal)) {
            throw new IllegalArgumentException("Need not found or is a Bundle: " + name);
        }
    }

    /**
     * Adds a component to an existing Bundle. Implements logic for Program Operation #14 (part 2).
     * @param bundleName The name of the Bundle to modify.
     * @param componentName The name of the Need or Bundle component to add.
     * @param quantity The number of units to add.
     */
    public void addComponentToBundle(String bundleName, String componentName, int quantity) {
        NeedComponent bundleComponent = needsRepository.getNeedByName(bundleName);
        if (!(bundleComponent instanceof Bundle)) {
            throw new IllegalArgumentException("Bundle not found: " + bundleName);
        }
        
        NeedComponent componentToAdd = needsRepository.getNeedByName(componentName);
        if (componentToAdd == null) {
            throw new IllegalArgumentException("Component Need or Bundle not found in catalog: " + componentName);
        }

        Bundle bundle = (Bundle) bundleComponent;
        bundle.addComponentUnits(componentToAdd, quantity); 
        needsRepository.updateBundle(bundle); // Notify observers
    }

    /**
     * Removes units of a component from an existing Bundle. Implements logic for Program Operation #14 (part 2).
     * @param bundleName The name of the Bundle to modify.
     * @param componentName The name of the Need or Bundle component to remove.
     * @param quantity The number of units to remove.
     */
    public int removeComponentFromBundle(String bundleName, String componentName, int quantity) {
    // public void removeComponentFromBundle(String bundleName, String componentName, int quantity) {
        NeedComponent bundleComponent = needsRepository.getNeedByName(bundleName);
        if (!(bundleComponent instanceof Bundle)) {
            throw new IllegalArgumentException("Bundle not found: " + bundleName);
        }
        
        Bundle bundle = (Bundle) bundleComponent;
        int unitsRemoved = bundle.removeComponentUnits(componentName, quantity); 
        
        if (unitsRemoved == 0 && bundle.getComponentCount(componentName) > 0) {
            throw new IllegalArgumentException("Could not remove " + quantity + " units of " + componentName + ". Not enough units.");
        }

        needsRepository.updateBundle(bundle); // Notify observers
        return unitsRemoved;
    }

    /**
     * Sets the units of an existing component in a Bundle to a new quantity.
     * Implements logic for Program Operation #14 (part 2).
     * @param bundleName The name of the Bundle to modify.
     * @param componentName The name of the component to update.
     * @param newQuantity The new total quantity for the component.
     */
    public void updateBundleComponentUnits(String bundleName, String componentName, int newQuantity) {
        NeedComponent bundleComponent = needsRepository.getNeedByName(bundleName);
        if (!(bundleComponent instanceof Bundle)) {
            throw new IllegalArgumentException("Bundle not found: " + bundleName);
        }

        Bundle bundle = (Bundle) bundleComponent;
        if (!bundle.updateComponentUnits(componentName, newQuantity)) {
            throw new IllegalArgumentException("Component not found in bundle: " + componentName);
        }
        
        needsRepository.updateBundle(bundle); // Notify observers
    }
}
