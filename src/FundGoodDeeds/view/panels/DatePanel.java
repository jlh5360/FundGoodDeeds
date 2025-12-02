// ==========================
// DatePanel.java
// ==========================
package FundGoodDeeds.view.panels;

import FundGoodDeeds.controller.MasterController;
import FundGoodDeeds.model.LedgerEntity;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Observable;
import java.util.Observer;

/**
 * DatePanel (V2)
 * - Lets user view & change the active date
 * - Small utility panel used by both CLI and Swing flows
 * - Mirrors CLI "Change Active Date" (option 12)
 */
@SuppressWarnings("deprecation")
public class DatePanel extends JPanel implements Observer {

    private final MasterController master;
    private final JLabel activeDateLabel;
    private final JButton changeDateButton;
    private final JButton resetDateButton;
    private final JButton themeToggleBtn;
    
    private final DateTimeFormatter YMD = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    public DatePanel(MasterController master) {
        this.master = master;
        
        // Registering here allows this tiny panel to update independently
        // whenever the selected date changes in the controller.
        master.registerObservers(this);
        
        // Use a FlowLayout for a simple, horizontal arrangement
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        // Shows the currently active date used throughout the app.
        activeDateLabel = new JLabel("Active Date: " + master.getSelectedDate().format(YMD));
        activeDateLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        
        changeDateButton = new JButton("Change Active Date");
        changeDateButton.addActionListener(e -> changeActiveDate());

        resetDateButton = new JButton("Reset to Today");

        // Alerts the user that the date has been reset to today's date

        resetDateButton.addActionListener(e -> resetDateAlert());
        
        themeToggleBtn = new JButton("Toggle Light Mode"); // Set initial text

        add(activeDateLabel);
        add(changeDateButton);
        add(resetDateButton);
        add(new JSeparator(SwingConstants.VERTICAL)); // Separator
        add(themeToggleBtn);
    }


    
    public JButton getThemeToggleBtn() {
        return this.themeToggleBtn;
    }

    public JButton getResetDateButton() {
        return resetDateButton;
    }

    private void resetDateAlert()
    {
        JOptionPane.showMessageDialog(this,"The date has been reset to today's date");
    }

    /**
     * Pops up a Swing dialog asking the user for a new date.
     * This keeps input logic out of the controller so the controller stays clean.
     */
    private void changeActiveDate() {
        // Use a JOptionPane to ask for the new date
        String current = master.getSelectedDate().format(YMD);
        String newDateStr = JOptionPane.showInputDialog(
            this, 
            "Enter new active date (yyyy-MM-dd):",
            "Change Active Date", 
            JOptionPane.PLAIN_MESSAGE
        );

        if (newDateStr != null && !newDateStr.trim().isEmpty()) {
            try {
                LocalDate newDate = LocalDate.parse(newDateStr.trim(), YMD);
                master.setSelectedDate(newDate);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Please use yyyy-MM-dd.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Whenever the active date changes, update the displayed label.
     */
    @Override
    public void update(Observable o, Object arg) {
        // Update the displayed date when the model changes (or the date is explicitly set)
        LocalDate date = master.getSelectedDate();
        activeDateLabel.setText("Active Date: " + date.format(YMD));
    }
}