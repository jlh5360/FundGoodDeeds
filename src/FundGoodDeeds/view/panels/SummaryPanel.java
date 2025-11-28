// ==========================
// SummaryPanel.java
// ==========================
package FundGoodDeeds.view.panels;

import FundGoodDeeds.controller.MasterController;
import FundGoodDeeds.model.Day;
import FundGoodDeeds.view.SwingUIView;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.time.LocalDate;

/**
 * SummaryPanel (V2)
 * - Displays the daily financial snapshot
 * - Mirrors CLI summary header visually
 * - Pulls all summary data through MasterController.getDaySummary()
 */
public class SummaryPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final MasterController master;
    private final SwingUIView parentFrame;

    private final JLabel dateLbl       = new JLabel();
    private final JLabel fundsLbl      = new JLabel();
    private final JLabel thresholdLbl  = new JLabel();
    private final JLabel needCostLbl   = new JLabel();
    private final JLabel incomeLbl     = new JLabel();
    private final JLabel netLbl        = new JLabel();

    private final DecimalFormat MONEY = new DecimalFormat("#,##0.00");
    
    private static final double ALERT_THRESHOLD_RATIO = 0.90;

    public SummaryPanel(MasterController master, SwingUIView parentFrame) {
        this.master = master;
        this.parentFrame = parentFrame;

        setLayout(new GridLayout(2, 3, 10, 4));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // // Apply nicer label styling for dark mode
        // for (JLabel lbl : new JLabel[]{
        //         dateLbl, fundsLbl, thresholdLbl,
        //         needCostLbl, incomeLbl, netLbl
        // }) {
        //     lbl.setForeground(new Color(230, 230, 230));
        //     lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 13f));
        // }

        //Initial theme application
        refreshTheme();
        
        //Configuration for the Net Cost alert label
        netLbl.setOpaque(true);
        netLbl.setHorizontalAlignment(SwingConstants.CENTER);
        netLbl.setFont(netLbl.getFont().deriveFont(Font.BOLD, 14f));

        add(dateLbl);
        add(fundsLbl);
        add(thresholdLbl);
        add(needCostLbl);
        add(incomeLbl);
        add(netLbl);

        refresh();
    }

    /** Applies the correct foreground color to all labels and the panel background based on theme */
    public void refreshTheme() {
        boolean isDark = parentFrame.isDarkModeEnabled();
        Color textColor = isDark ? new Color(230, 230, 230) : Color.BLACK;
        //Panel's background color should be the dark/light 'control' color
        Color panelBgColor = isDark ? new Color(45, 45, 45) : UIManager.getColor("control");

        //Need to set the panel's background
        this.setBackground(panelBgColor);
        
        //Apply dynamic styling to all standard labels
        for (JLabel lbl : new JLabel[]{
                dateLbl, fundsLbl, thresholdLbl,
                needCostLbl, incomeLbl
        }) {
            lbl.setForeground(textColor);
            //Ensure labels are not opaque so they pick up the panel background
            lbl.setOpaque(false); 
            lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 13f));
        }
        
        //Net Label styling
        netLbl.setFont(netLbl.getFont().deriveFont(Font.BOLD, 14f)); 
        
        //Force refresh to update the netLbl background color based on the new theme
        refresh(); 
    }

    /** Pull fresh summary metrics from MasterController. */
    public void refresh() {
        //Get the currently active date
        LocalDate activeDate = master.getSelectedDate();
        Day d = master.getDaySummary(activeDate);
        double netCost = master.getNetCost();
        double threshold = d.getThreshold();

        dateLbl.setText("Date: " + d.getDate());
        fundsLbl.setText("Funds: $" + MONEY.format(d.getFunds()));
        thresholdLbl.setText("Threshold: $" + MONEY.format(d.getThreshold()));
        // needCostLbl.setText("Need Costs: $" + MONEY.format(d.getTotalNeedCost()));
        // incomeLbl.setText("Income: $" + MONEY.format(d.getTotalIncome()));
        // netLbl.setText("Net: $" + MONEY.format(d.getNetCost()));
        needCostLbl.setText("Need Costs: $" + MONEY.format(master.getTotalNeedCost()));
        incomeLbl.setText("Income: $" + MONEY.format(master.getTotalIncome()));
        
        //ALERT LOGIC FOR Net Cost
        Color statusColor;
        String exceededText;
        Color foregroundColor = Color.WHITE;   //Use white text for all background colors

        if (netCost > threshold) {
            //RED: Exceeded (Spending too much)
            statusColor = new Color(180, 0, 0);   //Darker Red for critical alert
            exceededText = "YES - CRITICAL";
        }
        else if (netCost >= threshold * ALERT_THRESHOLD_RATIO) {
            //YELLOW: Approaching (Within 90% of threshold)
            statusColor = new Color(255, 180, 0).darker();   //Orange/Amber for warning
            exceededText = "NO - WARNING";
        }
        else {
            //GREEN: Safe
            statusColor = new Color(0, 150, 0).darker();   //Dark Green for safe
            exceededText = "NO - SAFE";
        }

        netLbl.setBackground(statusColor);
        netLbl.setForeground(foregroundColor);   //Set text color
        
        //Set the final text for the label
        netLbl.setText(String.format("Net Cost: $%.2f | Exceeded? %s", netCost, exceededText));
    }
}