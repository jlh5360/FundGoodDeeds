// ==========================
// DatePanel.java
// ==========================
package FundGoodDeeds.view.panels;

import FundGoodDeeds.controller.MasterController;
import FundGoodDeeds.view.SwingUIView;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * DatePanel (V2)
 * - Lets user view & change the active date
 * - Small utility panel used by both CLI and Swing flows
 * - Mirrors CLI "Change Active Date" (option 12)
 */
public class DatePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final MasterController master;
    private final SwingUIView parentFrame;

    private final JLabel activeDateLbl = new JLabel("Active Date:");
    private final JTextField dateField = new JTextField(10);
    private final JButton applyBtn     = new JButton("Apply");
    private final JButton todayBtn     = new JButton("Today");

    private final DateTimeFormatter YMD = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    private final JButton themeToggleBtn;

    public DatePanel(MasterController master, SwingUIView parentFrame) {
        this.master = master;
        this.parentFrame = parentFrame;
        this.themeToggleBtn = new JButton(
                parentFrame.isDarkModeEnabled() ? "Light Mode" : "Dark Mode"
        );

        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 8));
        setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        //Initial display of the date
        dateField.setText(master.getSelectedDate().format(YMD));

        //Apply date logic
        applyBtn.addActionListener(e -> {
            try {
                LocalDate d = LocalDate.parse(dateField.getText().trim(), YMD);
                master.setSelectedDate(d);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Invalid date. Please use yyyy-MM-dd.",
                        "Invalid Date",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        //Reset to today's date
        todayBtn.addActionListener(e -> {
            LocalDate now = LocalDate.now();
            master.setSelectedDate(now);
            dateField.setText(now.format(YMD));
        });

        //Listener for theme toggle
        themeToggleBtn.addActionListener(e -> {
            parentFrame.toggleTheme();
        });

        // Add components
        add(activeDateLbl);
        // add(new JLabel("Active Date:"));
        add(dateField);
        add(applyBtn);
        add(todayBtn);
        add(Box.createHorizontalStrut(20));   //Separator
        add(themeToggleBtn);
    }

    public JButton getThemeToggleBtn() {
        return themeToggleBtn;
    }

    /** Applies the correct background and foreground colors based on theme */
    public void refreshTheme() {
        boolean isDark = parentFrame.isDarkModeEnabled();
        
        //Text/Foreground color: Light for dark mode, Black for light mode
        Color fgColor = isDark ? new Color(230, 230, 230) : Color.BLACK;
        
        //Background color: Darker grey for dark mode, System default (or light) for light mode
        //Note: For a JPanel, we often use the UIManager's 'control' color
        Color panelBgColor = isDark ? new Color(45, 45, 45) : UIManager.getColor("control");

        //Panel background
        this.setBackground(panelBgColor); 
        
        //Label foreground
        activeDateLbl.setForeground(fgColor);
        
        //Text field colors (need this for high contrast)
        Color textFieldBg = isDark ? new Color(60, 60, 60) : Color.WHITE;
        Color textFieldFg = isDark ? new Color(230, 230, 230) : Color.BLACK;

        dateField.setBackground(textFieldBg);
        dateField.setForeground(textFieldFg);
    }

    /** Update the text field when the selected date changes. */
    public void refresh() {
        dateField.setText(master.getSelectedDate().format(YMD));
    }
}