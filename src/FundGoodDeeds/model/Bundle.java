package FundGoodDeeds.model;

import java.util.ArrayList;
import java.util.List;


public class Bundle implements NeedComponent {
    private String name;
    private List<NeedComponent> components;
    
    public Bundle(String name) {
        this.name = name;
		// initialize with an empty list NeedComponents, then use add() for each Need in the bundle
        this.components = new ArrayList<>();
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
    
}
