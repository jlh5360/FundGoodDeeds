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
        LedgerRepository ledgerRepo = new LedgerRepository(csvManager, needsRepo);
        FundingRepository fundingRepo = new FundingRepository();

        // -----------------------------
        // 2) CONTROLLERS
        // -----------------------------
        NeedsController needsCtrl = new NeedsController(needsRepo);
        LedgerController ledgerCtrl = new LedgerController(ledgerRepo, needsRepo);
        FundingController fundingCtrl = new FundingController(fundingRepo);

        MasterController master = new MasterController(
                needsRepo,
                ledgerRepo,
                fundingRepo,
                needsCtrl,
                ledgerCtrl,
                fundingCtrl
        );

        // -----------------------------
        // 3) VIEW
        // -----------------------------
        boolean useSwing = args.length > 0 && args[0].equalsIgnoreCase("swing");

        if (useSwing) {

            SwingUIView ui = new SwingUIView(master);
            master.registerObserver(ui);

            master.loadAll();
            ui.start();

        } else {

            ConsoleView ui = new ConsoleView(master);
            master.registerObserver(ui);

            master.loadAll();
            ui.startup();
            master.saveAll();
        }
    }
}
