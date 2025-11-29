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
    private SummaryPanel summaryPanel;
    private NeedsPanel needsPanel;
    private FundingPanel fundingPanel;
    private LedgerPanel ledgerPanel;
    private DatePanel datePanel;

    private boolean isDarkModeEnabled = true;   //Start in dark mode by default
    
    public boolean isDarkModeEnabled() {
        return isDarkModeEnabled;
    }
    
    public void toggleTheme() {
        isDarkModeEnabled = !isDarkModeEnabled;
        applyTheme(isDarkModeEnabled);
    }

    public SwingUIView(MasterController master) {
        super("FundGoodDeeds (Swing UI V2)");
        this.master = master;

        // Ensure EDT safety if UI is created outside main
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> new SwingUIView(master).start());
            return;
        }

        setSize(1050, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // enableDarkMode();

        // Instantiate panels
        summaryPanel = new SummaryPanel(master, this);
        needsPanel   = new NeedsPanel(master, this);
        fundingPanel = new FundingPanel(master, this);
        ledgerPanel  = new LedgerPanel(master, this);
        datePanel    = new DatePanel(master, this);

        applyTheme(isDarkModeEnabled);
        master.registerObservers(this);

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
        refreshTheme();
    }

    /** Refreshes only theme-related aspects */
    private void refreshTheme() {
        if (summaryPanel != null) summaryPanel.refreshTheme();
        if (needsPanel != null)    needsPanel.refreshTheme();
        if (fundingPanel != null)  fundingPanel.refreshTheme();
        if (ledgerPanel != null)   ledgerPanel.refreshTheme();
        if (datePanel != null)     datePanel.refreshTheme();
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

    /** Applies the Nimbus light or dark theme. */
    public void applyTheme(boolean isDark) {
        try {
            if (isDark) {
                //Dark Theme Colors
                this.getContentPane().setBackground(new Color(45, 45, 45));
                UIManager.put("control", new Color(45, 45, 45));
                UIManager.put("info", new Color(60, 60, 60));
                UIManager.put("nimbusBase", new Color(30, 30, 30));
                UIManager.put("nimbusBlueGrey", new Color(60, 60, 60));
                UIManager.put("text", new Color(230, 230, 230));
                UIManager.put("nimbusLightBackground", new Color(60, 60, 60));   //Crucial for lists/textareas
                UIManager.put("List.background", new Color(60, 60, 60));
                UIManager.put("TextArea.background", new Color(60, 60, 60));
                
            }
            else {
                //Light Theme Colors (Restored Defaults or standard light colors)
                this.getContentPane().setBackground(Color.WHITE);
                UIManager.put("control", new Color(240, 240, 240));
                UIManager.put("info", new Color(255, 255, 255));
                UIManager.put("nimbusBase", new Color(200, 200, 200));
                UIManager.put("nimbusBlueGrey", new Color(170, 184, 200));
                UIManager.put("text", Color.BLACK);
                UIManager.put("nimbusLightBackground", Color.WHITE);
                UIManager.put("List.background", Color.WHITE);
                UIManager.put("TextArea.background", Color.WHITE);
            }

            //Apply Nimbus Look and Feel
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
            
            //This is necessary to apply the new UIManager settings to all existing components
            SwingUtilities.updateComponentTreeUI(this);

            // Tells Swing to repaint everything

            repaint();

            //Update button text
            datePanel.getThemeToggleBtn().setText(isDark ? "Toggle Light Mode" : "Toggle Dark Mode");

        }
        catch (Exception e) {
            System.err.println("Could not set Nimbus Look and Feel or apply theme: " + e.getMessage());
        }
    }
}