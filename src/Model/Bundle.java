package Model;

public class Bundle extends NeedComponent {
	private final List<String> componentNames = new ArrayList<>();
	private final List<Integer> componentCounts = new ArrayList<>();

	public Bundle(String name) {
		super(name);
	}

	public void addComponent(String needName, int count) {
		this.componentNames.add(needName);
		this.componentCounts.add(count);
	}
	
	@Override
	public abstract double calculateTotalCost() {
		System.out.println("Calculating recursive cost for Bunlde: " + name);
		
		return 0.0:
	}
	
	@Override
	public abstract double calculateFixedCost() {
		return 0.0:
	}
	
	@Override
	public abstract double calculateVariableCost() {
		return 0.0:
	}
	
	@Override
	public abstract double calculateFeeCost() {
		return 0.0:
	}
}
