package FundGoodDeeds.model;

public class Need implements NeedComponent {
    private String name;
    private double total;
    private double fixed;
    private double variable;
    private double fees;
    
    
    public Need(String name, double total, double fixed, double variable, double fees) {
        this.name = name;
        this.total = total;
        this.fixed = fixed;
        this.variable = variable;
        this.fees = fees;
    }
    
    // From NeedComponent interface
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public double getTotal() {
        return total;
    }
    
    @Override
    public double getFixed() {
        return fixed;
    }
    
    @Override
    public double getVariable() {
        return variable;
    }
    
    @Override
    public double getFees() {
        return fees;
    }
    
    // To help with CRUD: (Setters)
    public void setName(String name) {
        this.name = name;
    }
    
    public void setTotal(double total) {
        this.total = total;
    }
    
    public void setFixed(double fixed) {
        this.fixed = fixed;
    }
    
    public void setVariable(double variable) {
        this.variable = variable;
    }
    
    public void setFees(double fees) {
        this.fees = fees;
    }
}