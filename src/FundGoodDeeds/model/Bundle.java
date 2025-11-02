package FundGoodDeeds.model;

import java.util.*;
import java.util.stream.Collectors;


public class Bundle implements NeedComponent {
    public String name;	 
    public List<NeedComponent> components;
    public Map<NeedComponent, Integer> componentCounts;
    /**
	 * Theorietically, a Bundle can look like this:
	 * 
	 * [Bus Pass, Buss Pass, Monthly Rent, PB&J, PB&J]
	 * 
	 * List needs to be converted to dictionary to hold NeedComponents and their counts.
	 * [{Bus Pass: 2}, {Monthly Rent: 1}, {PB&J: 2}]
	 * 
    */
    public Bundle(String name) {
        this.name = name;
		// initialize with an empty list NeedComponents, then use add() for each Need in the bundle
        this.components = new ArrayList<>();
        this.componentCounts = new HashMap<>();
    }

    // Constructor with predefined Needs for the bundle
    public Bundle(String name, List<NeedComponent> components) {
        this.name = name;
        this.components = components;

        // Count each component object using a for loop and store in the map
        for (NeedComponent component : components) {
            componentCounts.put(component, componentCounts.getOrDefault(component, 0) + 1);
        }

    }
    
    // Adds compnonent to the bundle and updates count
    public void add(NeedComponent component) {
        components.add(component);
        componentCounts.put(component, componentCounts.getOrDefault(component, 0) + 1);
    }
    
    // Removes component from the bundle and updates count
    public void remove(NeedComponent component) {
        components.remove(component);
        
        Integer currentCount = componentCounts.get(component);
        if (currentCount != null) {
            if (currentCount > 1) {
                componentCounts.put(component, currentCount - 1);
            } else {
                componentCounts.remove(component);
            }
        }
    }
    
    
    public Map<NeedComponent, Integer> getComponentsAndCounts() {
        return componentCounts;
    }
    public List<NeedComponent> getComponents() {
        return components;
    }
    /**
     * Returns a list of all need names in this bundle with their counts
     * Example: ["Bus Pass (2)", "Monthly Rent (1)", "PB&J (2)"]
     */
    public List<String> resolveAllNames() {
        return componentCounts.entrySet().stream()
                .map(entry -> entry.getKey().getName() + " (" + entry.getValue() + ")")
                .collect(Collectors.toList());
    }
    
    
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
