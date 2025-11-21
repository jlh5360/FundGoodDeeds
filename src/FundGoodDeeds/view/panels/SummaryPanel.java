// ==========================
// SummaryPanel.java
// ==========================
package FundGoodDeeds.view.panels;

import FundGoodDeeds.controller.MasterController;
import FundGoodDeeds.model.Day;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

/**
 * SummaryPanel (V2)
 * - Displays the daily financial snapshot
 * - Mirrors CLI summary header visually
 * - Pulls all summary data through MasterController.getDaySummary()
 */
public class SummaryPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final MasterController master;

    private final JLabel dateLbl       = new JLabel();
    private final JLabel fundsLbl      = new JLabel();
    private final JLabel thresholdLbl  = new JLabel();
    private final JLabel needCostLbl   = new JLabel();
    private final JLabel incomeLbl     = new JLabel();
    private final JLabel netLbl        = new JLabel();

    private final DecimalFormat MONEY = new DecimalFormat("#,##0.00");

    public SummaryPanel(MasterController master) {
        this.master = master;

        setLayout(new GridLayout(2, 3, 10, 4));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // Apply nicer label styling for dark mode
        for (JLabel lbl : new JLabel[]{
                dateLbl, fundsLbl, thresholdLbl,
                needCostLbl, incomeLbl, netLbl
        }) {
            lbl.setForeground(new Color(230, 230, 230));
            lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 13f));
        }

        add(dateLbl);
        add(fundsLbl);
        add(thresholdLbl);
        add(needCostLbl);
        add(incomeLbl);
        add(netLbl);

        refresh();
    }

    /** Pull fresh summary metrics from MasterController. */
    public void refresh() {
        Day d = master.getDaySummary();

        dateLbl.setText("Date: " + d.getDate());
        fundsLbl.setText("Funds: $" + MONEY.format(d.getFunds()));
        thresholdLbl.setText("Threshold: $" + MONEY.format(d.getThreshold()));
        needCostLbl.setText("Need Costs: $" + MONEY.format(d.getTotalNeedCost()));
        incomeLbl.setText("Income: $" + MONEY.format(d.getTotalIncome()));
        netLbl.setText("Net: $" + MONEY.format(d.getNetCost()));
    }
}