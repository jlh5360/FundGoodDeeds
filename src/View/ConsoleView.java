package View;

import java.util.Observer;

import Controller.LedgerController;
import Controller.NeedsController;
import Model.NeedsRepository;

public class ConsoleView implements Observer {
	private final NeedsController needsController;
	private final LedgerController ledgerController;

	public ConsoleView(NeedsController needsController, LedgerController ledgerController) {
		this.needsController = needsController;
		this.ledgerController = ledgerController;
	}

	public void startup(NeedsRepository needsRepository) {
		System.out.println("ConsoleView: Initiating Model startup...");
		
		needsRepository.getNeeds("needs.csv");
		needsRepository.getBundles("bundles.csv");

		//Alert will be triggered by NeedsRepository (if we allow the import)
		//If not, the application main method must call finishedAlert() after the repository operations.
	}
	
	public void finishedAlert() {
		System.out.println("ConsoleView: Data loading complete.");
	}

	public void recordsFullfillment(String needName, int count) {
		System.out.println("ConsoleView: Request to fulfill need: " + needName);

        ledgerController.recordsFullfillment(needName, count);
    }

    public void displaySummary(String summary) {
        System.out.println("\n--- Displaying Summary ---");

        System.out.println(summary);
    }

    public void exitButton() {
        System.out.println("ConsoleView: Exiting program.");
    }

    public void getDailyGoal() {
        System.out.println("ConsoleView: Requesting daily goal calculation...");

        ledgerController.processDailyGoal(this);
    }

    public void displayGoal(double remainingGoal) {
        System.out.printf("ConsoleView: Current Remaining Goal: $%.2f\n", remainingGoal);
    }

    public void enterDonations(double donation1, double donation2) {
        registerDonations(donation1, donation2);
    }

    public void registerDonations(double donation1, double donation2) {
        System.out.printf("ConsoleView: Registering donations $%.2f and $%.2f\n", donation1, donation2);

        ledgerController.registerDonations(donation1, donation2, this);
    }

    public void donationCompleted() {
        thankYouAlert();
    }

    public void thankYouAlert() {
        System.out.println("ConsoleView: Thank you message displayed.");
    }
}
