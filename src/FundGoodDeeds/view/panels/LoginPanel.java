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
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(loginButton);
        buttonPanel.add(signUpButton);
        add(buttonPanel,BorderLayout.CENTER);
    
        public void createUser()
        {
            if(!(this.userNameTextField.getText().isBlank() && this.passwordTextField.getText().isBlank()))
            {

            }
            else
            {
                JOptionPane.showMessageDialog(parent, "You must enter a username and password!");
            }

        }
    }


}
