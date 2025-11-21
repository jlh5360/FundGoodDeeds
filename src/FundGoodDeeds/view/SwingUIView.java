// File: src/FundGoodDeeds/view/SwingUIView.java

package FundGoodDeeds.view;

import FundGoodDeeds.controller.MasterController;
import FundGoodDeeds.model.*;
import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;
import FundGoodDeeds.view.panels.*;

/**
 * SwingUIView (V2)
 * Main GUI Window
 * - Dark Mode enabled
 * - Uses tabbed interface
 * - Auto-refreshes whenever repos update (Observer pattern)
 * - Completely driven by MasterController
 */
public class SwingUIView extends JFrame implements Observer {

    private static final long serialVersionUID = 1L;

    private final MasterController master;

    // Panels (modular, self-contained)
    private final SummaryPanel summaryPanel;
    private final NeedsPanel needsPanel;
    private final FundingPanel fundingPanel;
    private final LedgerPanel ledgerPanel;
    private final DatePanel datePanel;

    public SwingUIView(MasterController master) {
        super("FundGoodDeeds (Swing UI V2)");
        this.master = master;

        // Ensure EDT safety if UI is created outside main
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> new SwingUIView(master).start());
            return;
        }

        setSize(950, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        enableDarkMode();

        // Instantiate panels
        summaryPanel = new SummaryPanel(master);
        needsPanel   = new NeedsPanel(master);
        fundingPanel = new FundingPanel(master);
        ledgerPanel  = new LedgerPanel(master);
        datePanel    = new DatePanel(master);

        // Tab System
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Needs", needsPanel);
        tabs.addTab("Funding", fundingPanel);
        tabs.addTab("Ledger", ledgerPanel);

        // Add components to frame
        add(summaryPanel, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
        add(datePanel, BorderLayout.SOUTH);
    }

    /** Begin showing the window */
    public void start() {
        setVisible(true);
        refresh();
    }

    @Override
    public void update(Observable o, Object arg) {
        refresh();
    }

    /** Refresh all panels safely */
    public void refresh() {
        if (summaryPanel != null) summaryPanel.refresh();
        if (needsPanel != null)    needsPanel.refresh();
        if (fundingPanel != null)  fundingPanel.refresh();
        if (ledgerPanel != null)   ledgerPanel.refresh();
        if (datePanel != null)     datePanel.refresh();
    }

    /** Apply Nimbus dark mode */
    private void enableDarkMode() {
        try {
            UIManager.put("control", new Color(45, 45, 45));
            UIManager.put("info", new Color(60, 60, 60));
            UIManager.put("nimbusBase", new Color(30, 30, 30));
            UIManager.put("nimbusBlueGrey", new Color(60, 60, 60));
            UIManager.put("text", new Color(230, 230, 230));

            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}
    }
}