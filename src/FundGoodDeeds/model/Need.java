package FundGoodDeeds.model;

public class Need extends NeedComponent {
	private final double fixedCost;
	private final double variableCost;
	private final double fees;

	public BasicNeed(String name, double, fixed, double variable, double fees) {
		super(name);
		this.fixedCost = fixed;
		this.variableCost = variable;
		this.fees = fees;
	}
	
	@Override
	public abstract double calculateTotalCost() {
        return (fixedCost + variable + fees);
    }

	@Override
	public abstract double calculateFixedCost() {
		return fixedCost;
	}

	@Override
	public abstract double calculateVariableCost() {
		reutrn variableCost;
	}

	@Override
	public abstract double calculateFeeCost() {
        return fees;
    }
}
