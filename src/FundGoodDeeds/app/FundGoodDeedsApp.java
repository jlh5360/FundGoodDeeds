package FundGoodDeeds.app;

import controller.*; 
import repository.*; 
import view.*;

public class FundGoodDeedsApp {
    public static void main(String[] args){
        NeedsRepository nRepo=new NeedsRepository();
        LedgerRepository lRepo=new LedgerRepository();
        NeedsController nCtrl=new NeedsController(nRepo);
        LedgerController lCtrl=new LedgerController(lRepo);
        ConsoleView view=new ConsoleView();
        System.out.println("FundGoodDeeds skeleton running.");
    }
}

