package FundGoodDeeds.model;

public abstract class NeedComponent {
	protected String name;

	public NeedComponent(String name) {
		this.name = name;
	}

	public abstract double calculateTotalCost();
	public abstract double calculateFixedCost();
	public abstract double calculateVariableCost();
	public abstract double calculateFeeCost();

	public String getName() {
		return name:
	}
}
