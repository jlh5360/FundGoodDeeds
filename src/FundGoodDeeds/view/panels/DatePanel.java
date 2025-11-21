// ==========================
// DatePanel.java
// ==========================
package FundGoodDeeds.view.panels;

import FundGoodDeeds.controller.MasterController;

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

    private final JTextField dateField = new JTextField(10);
    private final JButton applyBtn     = new JButton("Apply");
    private final JButton todayBtn     = new JButton("Today");

    private final DateTimeFormatter YMD = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public DatePanel(MasterController master) {
        this.master = master;

        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 8));
        setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        // Initial display of the date
        dateField.setText(master.getSelectedDate().format(YMD));

        // Apply date logic
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

        // Reset to today's date
        todayBtn.addActionListener(e -> {
            LocalDate now = LocalDate.now();
            master.setSelectedDate(now);
            dateField.setText(now.format(YMD));
        });

        // Add components
        add(new JLabel("Active Date:"));
        add(dateField);
        add(applyBtn);
        add(todayBtn);
    }

    /** Update the text field when the selected date changes. */
    public void refresh() {
        dateField.setText(master.getSelectedDate().format(YMD));
    }
}