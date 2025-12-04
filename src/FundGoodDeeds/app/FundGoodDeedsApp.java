package FundGoodDeeds.app;

import java.io.FileNotFoundException;

import FundGoodDeeds.controller.*;
import FundGoodDeeds.model.*;
import FundGoodDeeds.view.*;

public class FundGoodDeedsApp {

    public static void main(String[] args) throws FileNotFoundException {

        System.out.println("Starting FundGoodDeeds...\n");

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

        // -----------------------------
        // 3) VIEW
        // -----------------------------
        boolean useSwing = args.length > 0 && args[0].equalsIgnoreCase("swing");
        boolean loggedIn = false;

        if (useSwing) {
        
            if(!loggedIn)
            {
                UserFrame loginUi = new UserFrame(master);
                loginUi.start();
            }
            else
            {
                SwingUIView ui = new SwingUIView(master);
                master.registerObservers(ui);
                master.registerViews(ui);

                master.loadAll();
                ui.start();
            }


        } else {

            ConsoleView ui = new ConsoleView(master);
            master.registerObservers(ui);
            master.registerViews(ui);
            
            master.loadAll();
            ui.startup();
            master.saveAll();
        }
    }
}