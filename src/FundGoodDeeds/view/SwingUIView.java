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
 * - Implements all ConsoleView features.
 */
@SuppressWarnings("deprecation")
public class SwingUIView extends JFrame implements Observer {

    private static final long serialVersionUID = 1L;

    private final MasterController master;

    // Create all the different panels up front.
    // This keeps the GUI modular — you can modify one panel without breaking the others.
    private SummaryPanel summaryPanel;
    private NeedsPanel needsPanel;
    private FundingPanel fundingPanel;
    private LedgerPanel ledgerPanel;
    private DatePanel datePanel; // Now a panel at the top

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

        // Create all the different panels up front.
        // This keeps the GUI modular — you can modify one panel without breaking the others.
        summaryPanel = new SummaryPanel(master);
        needsPanel = new NeedsPanel(master);
        fundingPanel = new FundingPanel(master);
        ledgerPanel = new LedgerPanel(master);
        datePanel = new DatePanel(master); // New/Updated panel for date and theme toggle

        // Registering this view as an Observer lets it react when the repositories change.
        // Swing handles its own repainting, so we just trigger updates inside the panels.
        master.registerObservers(this);

        // 3. Set up main container and layout
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        // Date panel sits at the top kind of like a "status bar" (current date + theme toggle)
        contentPane.add(datePanel, BorderLayout.NORTH);

        // Tabs make the UI feel more organized — each feature gets its own space.
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Summary & System", summaryPanel);
        tabbedPane.addTab("Needs Catalog", needsPanel);
        tabbedPane.addTab("Funding Sources", fundingPanel);
        tabbedPane.addTab("Ledger Log", ledgerPanel);

        contentPane.add(tabbedPane, BorderLayout.CENTER);

        // 6. Basic Final frame setup
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null); // Center the window

        // Hook up top-panel button actions (date reset + theme toggle)
        setupDatePanelListeners();
        datePanel.getThemeToggleBtn().addActionListener(e -> toggleTheme());
        
        // Apply initial theme
        applyTheme(isDarkModeEnabled);
    }

    // New start method for Swing UI (equivalent to startup in ConsoleView)
    public void start() {
        // Since master.loadAll() is called in FundGoodDeedsApp.main, 
        // we just need to make the GUI visible.
        SwingUtilities.invokeLater(() -> setVisible(true));
        
        // Initial update to populate all panels
        update(null, null);
    }
    
    // Observer pattern implementation
    @Override
    public void update(Observable o, Object arg) {

        // Needed for functionality to make sure the datePanel is updated
        datePanel.update(o, arg);
    }
    
    private void applyTheme(boolean isDark) {
        try {
            // Set properties for Nimbus Look and Feel based on theme
            if (isDark) {
                // Dark Theme Colors
                UIManager.put("control", new Color(64, 64, 64)); // Background color of most components
                UIManager.put("info", new Color(40, 40, 40)); // Tooltip/Info background
                UIManager.put("nimbusBase", new Color(30, 30, 30)); // Base color for primary components
                UIManager.put("nimbusBlueGrey", new Color(50, 50, 50)); // Darker shading color
                UIManager.put("text", Color.WHITE); // Text color
                UIManager.put("nimbusLightBackground", new Color(80, 80, 80)); // Lightest background (e.g., text area)
                UIManager.put("List.background", new Color(80, 80, 80)); // List background
                UIManager.put("TextArea.background", new Color(80, 80, 80)); // TextArea background
                UIManager.put("TextField.background", new Color(100, 100, 100));
                UIManager.put("Table.background", new Color(80, 80, 80));
                UIManager.put("Table.alternateRowColor", new Color(90, 90, 90));
                UIManager.put("Table.foreground", Color.WHITE);
                UIManager.put("Label.foreground", Color.WHITE);
                UIManager.put("TitledBorder.titleColor", Color.LIGHT_GRAY);
                
            }
            else {
                //Light Theme Colors (Restored Defaults or standard light colors)
                UIManager.put("control", new Color(240, 240, 240));
                UIManager.put("info", new Color(255, 255, 255));
                UIManager.put("nimbusBase", new Color(200, 200, 200));
                UIManager.put("nimbusBlueGrey", new Color(170, 184, 200));
                UIManager.put("text", Color.BLACK);
                UIManager.put("nimbusLightBackground", Color.WHITE);
                UIManager.put("List.background", Color.WHITE);
                UIManager.put("TextArea.background", Color.WHITE);
                UIManager.put("TextField.background", Color.WHITE);
                UIManager.put("Table.background", Color.WHITE);
                UIManager.put("Table.alternateRowColor", new Color(240, 240, 255));
                UIManager.put("Table.foreground", Color.BLACK);
                UIManager.put("Label.foreground", Color.BLACK);
                UIManager.put("TitledBorder.titleColor", Color.BLACK);
            }

            // Re-apply look and feel after updates
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
            
            //This is necessary to apply the new UIManager settings to all existing components
            SwingUtilities.updateComponentTreeUI(this);
            
            //Update button text
            datePanel.getThemeToggleBtn().setText(isDark ? "Toggle Light Mode" : "Toggle Dark Mode");

        }
        catch (Exception e) {
            System.err.println("Could not set Nimbus Look and Feel or apply theme: " + e.getMessage());
        }
    }

    private void setupDatePanelListeners() {
        datePanel.getResetDateButton().addActionListener(e -> {
            master.resetSelectedDateToToday();
        });
    }
}