package FundGoodDeeds.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import com.password4j.Password;

public class UserStore {
    private List<User> users = new ArrayList<>();
    private CSVManager manager;
    private final String[] fileNames = {
        "funding.csv",
        "ledger.csv",
        "log.csv",
        "needs.csv"
    };

    public UserStore(CSVManager manager)
    {
        this.manager = manager;
        this.loadUsers();
    }


    public void loadUsers()
    {
        List<String> rawUsers = manager.readData("users.csv");
        if(!rawUsers.isEmpty())
            this.users = rawUsers.stream().map(user -> createUserObject(user)).toList();
    }

    /**
     * 
     * Creates a user based on
     * 
     * @param rawData
     * @return User object
     */

    public User createUserObject(String rawData)
    {
        String[] columns = rawData.split(",");
        return new User(columns[0],columns[1],columns[2],columns[3]);
    }

    public boolean logIn(String userName,String password)
    {
        User user = getUser(userName);
        if(user != null)
        {
            return Password.check(password, user.getPassword()).withBcrypt();
        }

        return false;
    }

    /***
     * 
     * Creates a user object and adds it to the list
     * 
     * @param userName
     * @param password
     * @param firstName
     * @param lastName
     * @return String that describes what happened, if the user already exists, etc
     */
    public User addUser(String userName, String password, String firstName, String lastName)
    {

        if(getUser(userName) == null)
        {
            // Hash the password for security

            String hashedPassword = Password.hash(password).withBcrypt().getResult();

            User newUser = new User(userName, hashedPassword, firstName, lastName);
            users.add(newUser);
            createUserDirectory(userName);
            try {
                manager.writeData("users.csv",List.of(newUser.toCSVString()));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return getUser(userName);
    }

    private void createUserDirectory(String userName) {
       try
       {

        Path userDir = Files.createDirectory(manager.getDataPath().resolve(userName));

        for(String file : fileNames){
            Files.createFile(userDir.resolve(file));
        }
       }
       catch(IOException e)
       {
        e.printStackTrace();
       }
    }


    /**
     * 
     * Gets a user by their username
     * 
     * @param userName
     * @return The User object if a match is found
     */

    public User getUser(String userName)
    {
        for(User user : users)
        {
            if(user.getUsername().equalsIgnoreCase(userName))
                return user;
        }
        return null;
    }

}