package FundGoodDeeds.app;

import FundGoodDeeds.controller.LedgerController;
import FundGoodDeeds.controller.NeedsController;
import FundGoodDeeds.model.CSVManager;
import FundGoodDeeds.model.LedgerRepository;
import FundGoodDeeds.model.NeedsRepository;
import FundGoodDeeds.view.ConsoleView;

/**
 * The main entry point for the FundGoodDeeds App.
 * Acts as the Composition Root, setting up the MVC architecture and injecting dependencies.
 */
public class FundGoodDeedsApp {
    public static void main(String[] args){
        //--- 1. MODEL COMPONENTS (Persistence/Utility) ---
        //Instantiate Utility/Abstraction Layer
        CSVManager csvManager = new CSVManager();

        // //Instantiate Repositories (Model/Data Layer) - inject CSVManager
        // NeedsRepository nRepo=new NeedsRepository(csvManager);
        // LedgerRepository lRepo=new LedgerRepository(csvManager);
        
        //--- 2. CONTROLLER COMPONENTS (Business Logic Layer) ---
        //Instantiate Controllers - inject Repositories (Model)
        NeedsController nCtrl=new NeedsController();
        LedgerController lCtrl=new LedgerController();

        
        //--- 3. VIEW COMPONENTS & WIRE-UP (Complete Dependencies) ---
        //Instantiate View and inject Controllers into View (Uni-directional dependency: View knows Controller)
        ConsoleView view=new ConsoleView(nCtrl, lCtrl);

        //Inject View into LedgerController (Needed for alerts in Sequence Diagram #2)
        // lCtrl.setView(view);

        //--- 4. APPLICATION STARTUP ---
        //Initialize the application via the View/Controller
        //(Sequence Diagram #1: ConsoleView -> startup() initiates the load)
        view.startup(nRepo, lRepo);

        // System.out.println("FundGoodDeeds skeleton running.");
        //The main thread would typically block here, waiting for user input (in ConsoleView.displayMenu())
    }
}

