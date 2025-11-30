package FundGoodDeeds.model;

public class FundingSource {
    private String name;
    private double amount;  

    public FundingSource(String name, double amount) {
        this.name = name;
        this.amount = amount;
    }

    // getters
    public double getAmount() {
        return amount;
    }

    public String getName() {
        return name;
    }
    
    // setters
    public void setName(String name) {
        this.name = name;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    @Override
    public String toString(){
        return getName() + ": " + getAmount();
    }
    
}
