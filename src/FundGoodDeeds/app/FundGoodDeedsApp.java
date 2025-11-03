/**
 * The main entry point for the FundGoodDeeds App.
 * Acts as the Composition Root, setting up the MVC and injecting dependencies.
 * Connor Bashaw - FundGoodDeedsApp.java
 */


package FundGoodDeeds.app;

import java.io.FileNotFoundException;

import FundGoodDeeds.controller.LedgerController;
import FundGoodDeeds.controller.NeedsController;
import FundGoodDeeds.model.CSVManager;
import FundGoodDeeds.model.LedgerRepository;
import FundGoodDeeds.model.NeedsRepository;
import FundGoodDeeds.view.ConsoleView;



public class FundGoodDeedsApp {
    public static void main(String[] args) throws FileNotFoundException{
        //1. Model 
        //Instantiate csv manager
        if (args.length < 1) {
            System.out.println("Usage: java FundGoodDeedsApp.java <ledger-csv-file>");
            return;
        }
        else if (args.length < 1) {
            System.out.println("Warning: Extra arguments will be ignored.");
            return;
        }

        // Read arguments
        String ledgerCSV = args[0];
        CSVManager csvManager = new CSVManager(ledgerCSV);

        //Instantiate Repositories and inject CSVManager
        NeedsRepository needsRepo = new NeedsRepository(csvManager);
        LedgerRepository ledgerRepo = new LedgerRepository(csvManager);
        
        // 2. Controller  
        //Instantiate Controllers, inject Repositories (Model)
        NeedsController needs=new NeedsController(needsRepo);
        LedgerController ledger=new LedgerController(ledgerRepo ,needsRepo);

        
        //3. VIEW IMPORTANT TO CONNOR WORK
        //Instantiate View and inject Controllers into View (View knows Controller controller knows repo)
        ConsoleView view=new ConsoleView(needs, ledger);

        // Register view as observer 
        needs.addObserver(view);
        ledger.addObserver(view);
        

        //4.STARTUP
        //Initialize the application via the View/Controller
        //(Sequence Diagram #1: ConsoleView -> startup() initiates the load)
        

        view.startup();

    }
}

