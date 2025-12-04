package FundGoodDeeds.view.panels;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import FundGoodDeeds.controller.MasterController;
import java.awt.*;

public class LoginPanel extends JPanel{

    private MasterController master;
    
    private JLabel userNameJLabel = new JLabel("Username");
    private JLabel passwordJLabel = new JLabel("Password");

    private JTextField userNameTextField = new JTextField(40);
    private JTextField passwordTextField = new JTextField(40);

    private JButton loginButton = new JButton("Login");
    private JButton signUpButton = new JButton("Sign Up");


    public LoginPanel(MasterController master)
    {
        this.master = master;
        setLayout(new BorderLayout());

        // For layout smoothness

        JPanel parent = new JPanel(new BorderLayout());

        JPanel userNamePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        userNamePanel.add(userNameJLabel);
        userNamePanel.add(userNameTextField);

        JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        passwordPanel.add(passwordJLabel);
        passwordPanel.add(passwordTextField);


        parent.add(userNamePanel,BorderLayout.NORTH);
        parent.add(passwordPanel,BorderLayout.SOUTH);

        add(parent,BorderLayout.NORTH);

        // Action listeners for the buttons

        signUpButton.addActionListener(e -> createUser());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(loginButton);
        buttonPanel.add(signUpButton);
        add(buttonPanel,BorderLayout.CENTER);

    }

        
    public void createUser()
    {
        if(!(this.userNameTextField.getText().isBlank() && this.passwordTextField.getText().isBlank()))
        {
            String userName = this.userNameTextField.getText();
            String password = this.passwordTextField.getText();
            if(!master.userExists(userName))
            {
                String firstName = JOptionPane.showInputDialog("Enter your first name");
                String lastName = JOptionPane.showInputDialog("Enter your last name");
                master.createUser(userName,password, firstName, lastName);
                JOptionPane.showMessageDialog(this.getParent(), "User successfully created.");
            }
            else
            {
                JOptionPane.showMessageDialog(this.getParent(), "A user with that username already exists!");
            }
        }
        else
        {
            JOptionPane.showMessageDialog(this.getParent(),"You must enter a username and password!");
        }

    }

}
