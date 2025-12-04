package FundGoodDeeds.view;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Container;

import FundGoodDeeds.controller.MasterController;
import FundGoodDeeds.view.panels.LoginPanel;

public class UserFrame extends JFrame implements Observer{

    private final MasterController master;



    public UserFrame(MasterController master)
    {

        this.master = master;   
        LoginPanel loginPanel = new LoginPanel(master);


        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(loginPanel,BorderLayout.CENTER);

        // To resize components
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null); // Center the window
    }

    // Method to add an user to the system

    private void addUser(String username, String password)
    {
        throw new UnsupportedOperationException("Not supported");
    }

    // Method to login and pass the user to the master controller

    private void login(String userame, String password)
    {
        throw new UnsupportedOperationException("Not supported");
    }


    @Override
    public void update(Observable o, Object arg) {
    }

    // New start method for Swing UI (equivalent to startup in ConsoleView)
    public void start() {
        // Since master.loadAll() is called in FundGoodDeedsApp.main, 
        // we just need to make the GUI visible.
        SwingUtilities.invokeLater(() -> setVisible(true));
        
        // Initial update to populate all panels
        update(null, null);
    }
    
    
}
