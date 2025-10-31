package FundGoodDeeds.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class Bundle implements NeedComponent {
    private String name;
	/**
	 * Theorietically, a Bundle can look like this:
	 * 
	 * [Bus Pass, Buss Pass, Monthly Rent, PB&J, PB&J]
	 * 
	 * FUTURE REFACTORING NOTE:
	 * List needs to be converted to dictionary to hold NeedComponents and their counts.
	 * [{Bus Pass: 2}, {Monthly Rent: 1}, {PB&J: 2}]
	 * 
    */
	 
    private List<NeedComponent> components;
	
    public Bundle(String name) {
        this.name = name;
		// initialize with an empty list NeedComponents, then use add() for each Need in the bundle
        this.components = new ArrayList<>();
    }

    // Constructor with predefined Needs for the bundle
    public Bundle(String name, List<NeedComponent> components) {
        this.name = name;
		
        this.components = components;
    }
    
    // List related methods
    public void add(NeedComponent component) {
        components.add(component);
    }
    
    public void remove(NeedComponent component) {
        components.remove(component);
    }
    
    public List<NeedComponent> getComponents() {
        return new ArrayList<>(components);
    }
     /**
     * Returns a list of all need names in this bundle
     * Example: ["Bus Pass", "Bus Pass", "Monthly Rent", "PB&J", "PB&J"]
     */
    public List<String> resolveAllNames() {
        return components.stream()
                .map(NeedComponent::getName)
                .collect(Collectors.toList());
    }
    
    // Implement interface by aggregating child values
    @Override
    public String getName() {
        return name;
    }
    /*
	 * This specific method gets the total cost of all Needs in the Bundle.
	 * It will sum up the total values from each NeedComponent contained within the Bundle.
	 * Process:
	 * [Need("Rent"), Need("Utilities"), Need("Internet")]
	 * [1200.0, 150.0, 60.0]
	 * 1200.0 + 150.0 + 60.0 = 1410.0
	 */
    @Override
    public double getTotal() {
        return components.stream()
                .mapToDouble(NeedComponent::getTotal)
                .sum();
    }
    
    @Override
    public double getFixed() {
        return components.stream()
                .mapToDouble(NeedComponent::getFixed)
                .sum();
    }
    
    @Override
    public double getVariable() {
        return components.stream()
                .mapToDouble(NeedComponent::getVariable)
                .sum();
    }
    
    @Override
    public double getFees() {
        return components.stream()
                .mapToDouble(NeedComponent::getFees)
                .sum();
    }

	// ***FUTURE REFACTORING*** 
	// Need cool_need = mybundle.getNeedFromBundle("Cool Need");
    // public double getNeedFromBundle() {
    //     return ...
    // }
    
}
