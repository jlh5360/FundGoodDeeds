package FundGoodDeeds.view.panels;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import FundGoodDeeds.controller.MasterController;

public class UserFrame extends JFrame implements Observer{

    private final MasterController master;
    private JTextField userNameTextField = new JTextField(40);
    private JTextField passwordTextField = new JTextField(40);
    private JButton loginButton = new JButton("Login");
    private JButton signUpButton = new JButton("Sign Up");

    public UserFrame(MasterController master)
    {
        this.master = master;
    }

    // Method to add an user to the system

    private addUser(String username, String password)
    {

    }


    @Override
    public void update(Observable o, Object arg) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
}
