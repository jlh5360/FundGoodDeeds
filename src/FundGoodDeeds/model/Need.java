package FundGoodDeeds.model;

import java.util.Locale;

public class Need implements NeedComponent {
    private String name;
    private double total;
    // private double fixed;
    // private double variable;
    // private double fees;
    
    public Need(String name, double total) {
    // public Need(String name, double total, double fixed, double variable, double fees) {
        this.name = name;
        this.total = total;
        // this.fixed = fixed;
        // this.variable = variable;
        // this.fees = fees;
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
    
    // @Override
    // public double getFixed() {
    //     return fixed;
    // }
    
    // @Override
    // public double getVariable() {
    //     return variable;
    // }
    
    // @Override
    // public double getFees() {
    //     return fees;
    // }
    
    // To help with CRUD: (Setters)
    public void setName(String name) {
        this.name = name;
    }
    
    public void setTotal(double total) {
        this.total = total;
    }
    
    // public void setFixed(double fixed) {
    //     this.fixed = fixed;
    // }
    
    // public void setVariable(double variable) {
    //     this.variable = variable;
    // }
    
    // public void setFees(double fees) {
    //     this.fees = fees;
    // }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        //Need to ensure the class type is the same
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        
        Need need = (Need) o;
        
        //Two needs are equal if they have the same name
        return name.equalsIgnoreCase(need.name);
    }

    @Override
    public int hashCode() {
        //Hash code based on the unique name
        //Use Locale.ROOT to ensure case-insensitive hashing is consistent across all systems
        return name.toLowerCase(Locale.ROOT).hashCode();
    }
}