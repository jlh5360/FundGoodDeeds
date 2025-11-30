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
        // return componentCounts;
        //Use a new LinkedHashMap to return a copy
        return new LinkedHashMap<>(this.componentCounts);
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
    
    public void setName(String name) {
        this.name = name;
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

	// ***FUTURE REFACTORING*** 
	// Need cool_need = mybundle.getNeedFromBundle("Cool Need");
    // public double getNeedFromBundle() {
    //     return ...
    // }

    /**
     * Decreases the quantity of a component in the bundle.
     * @param componentName The name of the component to decrease.
     * @param quantity The amount to decrease by.
     * @return The number of units successfully removed (max is current count, min is 0).
     */
    public int removeComponentUnits(String componentName, int quantity) {
        if (quantity <= 0) {
            return 0;
        }
        
        NeedComponent componentToUpdate = componentCounts.keySet().stream()
                .filter(nc -> nc.getName().equalsIgnoreCase(componentName))
                .findFirst()
                .orElse(null);

        if (componentToUpdate == null) {
            return 0; // Component not found
        }

        // int currentCount = componentCounts.get(componentToUpdate);

        // Integer currentQuantityObject = componentCounts.get(componentToUpdate);
        Integer currentQuantityObject = componentCounts.getOrDefault(componentToUpdate, 0); 
        
        if (currentQuantityObject == null) {
            return 0; // Should not happen if the key was found, but defends against NPE
        }

        int currentCount = currentQuantityObject.intValue();
        
        // Only remove up to the available quantity
        int unitsToRemove = Math.min(quantity, currentCount);
        int newCount = currentCount - unitsToRemove;

        if (newCount <= 0) {
            // If count is zero or less, remove the component entirely
            componentCounts.remove(componentToUpdate);
        } else {
            // Otherwise, update the count
            componentCounts.put(componentToUpdate, newCount);
        }
        
        return unitsToRemove;
    }

    /**
     * Adds units of an existing or new component to the bundle.
     * Implements logic for Program Operation #14.
     * @param component The NeedComponent to add/update.
     * @param quantity The number of units to add.
     */
    public void addComponentUnits(NeedComponent component, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity to add must be positive.");
        }
        
        // Find the component that matches by name (case-insensitive)
        // We must ensure the component in the map is the one from the NeedsCatalog
        NeedComponent existingComponent = componentCounts.keySet().stream()
            .filter(nc -> nc.getName().equalsIgnoreCase(component.getName()))
            .findFirst()
            .orElse(null);

        // If existing component found, use it, otherwise use the passed component
        NeedComponent key = (existingComponent != null) ? existingComponent : component;

        int currentCount = componentCounts.getOrDefault(key, 0);
        int newCount = currentCount + quantity;
        
        componentCounts.put(key, newCount);
    }

    /**
     * Updates the quantity of an existing component in the bundle.
     * Implements logic for Program Operation #14.
     * If newQuantity is 0 or less, the component is removed.
     * @param componentName The name of the component to update.
     * @param newQuantity The new total quantity of the component.
     * @return true if the component was found and updated/removed, false otherwise.
     */
    public boolean updateComponentUnits(String componentName, int newQuantity) {
        NeedComponent componentToUpdate = componentCounts.keySet().stream()
            .filter(nc -> nc.getName().equalsIgnoreCase(componentName))
            .findFirst()
            .orElse(null);

        if (componentToUpdate == null) {
            return false; // Component not found
        }

        if (newQuantity <= 0) {
            // If count is zero or less, remove the component entirely
            componentCounts.remove(componentToUpdate);
        } else {
            // Otherwise, update the count
            componentCounts.put(componentToUpdate, newQuantity);
        }
        
        return true;
    }

    // Helper to get the count of a component (for view/controller logic)
    public int getComponentCount(String componentName) {
        return componentCounts.keySet().stream()
            .filter(nc -> nc.getName().equalsIgnoreCase(componentName))
            .map(componentCounts::get)
            .findFirst()
            .orElse(0);
    }
    
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
