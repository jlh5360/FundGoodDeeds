package FundGoodDeeds.model;

public class Need implements NeedComponent {
    private String name;
    private double total;
    
    
    public Need(String name, double total) {
        this.name = name;
        this.total = total;
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

    
    // To help with CRUD: (Setters)
    public void setName(String name) {
        this.name = name;
    }
    
    public void setTotal(double total) {
        this.total = total;
    }


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

    
}