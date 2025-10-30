/**
 * The main entry point for the FundGoodDeeds App.
 * Acts as the Composition Root, setting up the MVC and injecting dependencies.
 * Connor Bashaw - FundGoodDeedsApp.java
 */


package FundGoodDeeds.app;

import FundGoodDeeds.controller.LedgerController;
import FundGoodDeeds.controller.NeedsController;
import FundGoodDeeds.model.CSVManager;
import FundGoodDeeds.model.LedgerRepository;
import FundGoodDeeds.model.NeedsRepository;
import FundGoodDeeds.view.ConsoleView;



public class FundGoodDeedsApp {
    public static void main(String[] args){
        //1. Model 
        //Instantiate csv manager
        CSVManager csvManager = new CSVManager();

        //Instantiate Repositories and inject CSVManager
        NeedsRepository nRepo = new NeedsRepository(csvManager);
        LedgerRepository lRepo = new LedgerRepository(csvManager);
        
        // 2. Controller  
        //Instantiate Controllers, inject Repositories (Model)
        NeedsController nCtrl=new NeedsController(nRepo);
        LedgerController lCtrl=new LedgerController(lRepo ,nRepo);

        
        //3. VIEW IMPORTANT TO CONNOR WORK
        //Instantiate View and inject Controllers into View (View knows Controller controller knows repo)
        ConsoleView view=new ConsoleView(nCtrl, lCtrl);

        //Inject View into LedgerController (Needed for alerts in Sequence Diagram #2)
        // lCtrl.setView(view);

        //4.STARTUP
        //Initialize the application via the View/Controller
        //(Sequence Diagram #1: ConsoleView -> startup() initiates the load)
        view.startup();

    }
}

