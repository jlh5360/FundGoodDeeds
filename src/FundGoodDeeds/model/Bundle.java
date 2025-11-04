package FundGoodDeeds.model;

import java.util.*;
import java.util.stream.Collectors;


public class Bundle implements NeedComponent {
    public String name;	 
    public Map<NeedComponent, Integer> componentCounts;
    /**
	 * Theoretically, a Bundle can look like this:
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
        this.componentCounts = new LinkedHashMap<>();
    }

    // Constructor with predefined Needs for the bundle
    // Changed to accept a Map for component and their integer counts
    public Bundle(String name, Map<NeedComponent, Integer> componentsWithCounts) {
        this.name = name;
        this.componentCounts = new LinkedHashMap<>(componentsWithCounts);
    }
    
    // Adds component to the bundle with a specific integer quantity
    public void add(NeedComponent component, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Bundle component quantity must be positive.");
        }
        componentCounts.put(component, componentCounts.getOrDefault(component, 0) + quantity);
    }
    
    // Removes component from the bundle and updates count
    public void remove(NeedComponent component) {
        componentCounts.remove(component); // Removes the component entirely
    }
    
    
    public Map<NeedComponent, Integer> getComponentsAndCounts() {
        return componentCounts;
    }
    public List<NeedComponent> getComponents() {
        // Returns a list of unique components, not reflecting quantities
        return new ArrayList<>(componentCounts.keySet());
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
        return componentCounts.entrySet().stream()
                .mapToDouble(entry -> entry.getKey().getTotal() * entry.getValue())
                .sum();
    }
    
    @Override
    public double getFixed() {
        return componentCounts.entrySet().stream()
                .mapToDouble(entry -> entry.getKey().getFixed() * entry.getValue())
                .sum();
    }
    
    @Override
    public double getVariable() {
        return componentCounts.entrySet().stream()
                .mapToDouble(entry -> entry.getKey().getVariable() * entry.getValue())
                .sum();
    }
    
    @Override
    public double getFees() {
        return componentCounts.entrySet().stream()
                .mapToDouble(entry -> entry.getKey().getFees() * entry.getValue())
                .sum();
    }

	// ***FUTURE REFACTORING*** 
	// Need cool_need = mybundle.getNeedFromBundle("Cool Need");
    // public double getNeedFromBundle() {
    //     return ...
    // }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        //Must check for the exact class to distinguish Bundle from a Need with the same name
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Bundle bundle = (Bundle) o;

        return name.equalsIgnoreCase(bundle.name);
    }

    
    @Override
    public int hashCode() {
        //Hash code based on the unique name
        //Use Locale.ROOT to ensure case-insensitive hashing is consistent across all systems
        return name.toLowerCase(Locale.ROOT).hashCode();
    }
}
