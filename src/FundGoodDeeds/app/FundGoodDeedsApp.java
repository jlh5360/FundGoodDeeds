package FundGoodDeeds.app;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;

import FundGoodDeeds.controller.*;
import FundGoodDeeds.model.*;
import FundGoodDeeds.view.*;

public class FundGoodDeedsApp {

    private static MasterController startup() throws FileNotFoundException
    {
        // -----------------------------
        // 1) MODEL
        // -----------------------------
        CSVManager csvManager = new CSVManager("log.csv");

        NeedsRepository needsRepo = new NeedsRepository(csvManager);
        FundingRepository fundingRepo = new FundingRepository(csvManager);
        LedgerRepository ledgerRepo = new LedgerRepository(csvManager, needsRepo, fundingRepo);

        // -----------------------------
        // 2) CONTROLLERS
        // -----------------------------
        NeedsController needsCtrl = new NeedsController(needsRepo);
        LedgerController ledgerCtrl = new LedgerController(ledgerRepo, needsRepo);
        FundingController fundingCtrl = new FundingController(fundingRepo);
        
        // -----------------------------
        // 2) USER PERSISTENCE
        // -----------------------------
        
        UserStore users = new UserStore(csvManager);

        MasterController master = new MasterController(
                needsCtrl,
                ledgerCtrl,
                fundingCtrl,
                users
        );

        return master;
    }

    private static void showGUIApp(MasterController master)
    {
         SwingUIView ui = new SwingUIView(master);
         master.registerObservers(ui);
         master.registerGUI(ui);
         master.loadAll();
         ui.start();
    }

    public static void restartGUI() throws FileNotFoundException
    {
        MasterController master = startup();
        UserFrame loginUi = new UserFrame(master,() -> {
            showGUIApp(master);    
        });
        loginUi.start();
        
    }

    public static void main(String[] args) throws FileNotFoundException {

        System.out.println("Starting FundGoodDeeds...\n");

        MasterController master = startup();

        // -----------------------------
        // 3) VIEW
        // -----------------------------
        boolean useSwing = args.length > 0 && args[0].equalsIgnoreCase("swing");
        if (useSwing) {

                UserFrame loginUi = new UserFrame(master,() -> {
                   showGUIApp(master);    
                });
                loginUi.start();
                
                

        } else {

            ConsoleView ui = new ConsoleView(master);
            master.registerObservers(ui);
            
            master.loadAll();
            ui.startup();
            master.saveAll();
        }
    }
}