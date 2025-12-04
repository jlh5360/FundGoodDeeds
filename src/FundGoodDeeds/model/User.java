package FundGoodDeeds.model;

public class User {
    private String username;
    private String password;
    private String firstName;
    private String lastName;

    public User(String username,String password,String firstName,String lastName)
    {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getUsername()
    {
        return this.username;
    }

    public String getPassword()
    {
        return this.password;
    }

    public String getFirstName()
    {
        return this.firstName;
    }

    public String getLastName()
    {
        return this.lastName;
    }

    public String toCSVString()
    {
        return this.username + "," + this.password + "," + this.firstName + "," + this.lastName;
    }
}
