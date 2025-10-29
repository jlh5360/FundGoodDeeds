package FundGoodDeeds.controller;

public class LedgerController {
	private final Needsrepository needsRepository;
	private final Ledgersrepository ledgerRepository;

	public LedgerController(NeedsRepository needsRepository, ledgerRepository ledgerRepository) {
		this.needsRepository = needsRepository;
		this.ledgerRepository = ledgerRepository;
	}

	public void recordsFullfillment(String needName, int count) {
		LedgerEntity fulfillment = new LedgerEntity(LocalDate.now(), LedgerEntity.EntryType.NEED, needName, count);

		ledgerRepository.save(fulfillment);

		needsRepository.getNeedByName(needName);
	}
		
	public void displaySummary(ConsoleView view) {
		String summary = ledgerRepository.getSummary();

		view.displaySummary(summary);
    }
        
    public void processDailyGoal(ConsoleView view) {
        LocalDate today = LocalDate.now();
        int need_count = 5; // Placeholder for loop condition

        while (need_count > 0) {
            double goal = ledgerRepository.findGoal(today);
            double donations = ledgerRepository.calculateDonations(today);
            double remainingGoal = goal - donations;

            view.displayGoal(remainingGoal);

            need_count = (need_count - 1);
        }
    }

	public void registerDonations(double donation1, double donation2, ConsoleView view) {
		ledgerRepository.addDonations(donation1, donation2);

		//Logic to recalculate the goal status

		view.donationCompleted();
	}
}
