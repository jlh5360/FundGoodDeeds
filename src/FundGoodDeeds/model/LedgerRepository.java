package FundGoodDeeds.model;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Observable;
import java.util.Optional;
import java.util.stream.Collectors;

import FundGoodDeeds.model.LedgerEntity;

@SuppressWarnings("deprecation")
public class LedgerRepository extends Observable {
	private final List<LedgerEntity> logEntries = new ArrayList<>();
	public CSVManager manager;
	private final NeedsRepository needsRepository; // Dependency for cost lookups

	private static final double DEFAULT_GOAL = 2000.0;
	private static final double DEFAULT_FUNDS = 150.0;
	private static final double DEFAULT_THRESHOLD = 2000.0;

	// Updated constructor to accept NeedsRepository
	public LedgerRepository(CSVManager manager, NeedsRepository needsRepository)
	{
		this.manager = manager;
		this.needsRepository = needsRepository;
	}

	public void loadLog()
	{
		List<String[]> rawData = getDataFromCSV();
		List<LedgerEntity> entries = new ArrayList<>();
		for(String[] raw : rawData)
		{
			int year = Integer.parseInt(raw[0]);
			int month = Integer.parseInt(raw[1]);
			int day = Integer.parseInt(raw[2]);
			LedgerEntity entity; // Declare entity once

			LocalDate entityDate = LocalDate.of(year,month,day);
			switch(raw[3])
			{
				
				case "n":
					entity = new LedgerEntity(entityDate, LedgerEntity.EntryType.NEED, raw[4], Double.parseDouble(raw[5]), Double.parseDouble(raw[6]));
					entries.add(entity);
				break;

				case "f":
					entity = new LedgerEntity(entityDate, LedgerEntity.EntryType.FUND, Double.parseDouble(raw[4]));
					entries.add(entity);
				break;

				// In case we still need goal
					
				case "g":
					entity = new LedgerEntity(entityDate, LedgerEntity.EntryType.GOAL, Double.parseDouble(raw[4]));
					entries.add(entity);
				break;
					
				case "t":
					entity = new LedgerEntity(entityDate, LedgerEntity.EntryType.THRESHOLD, Double.parseDouble(raw[4]));
					entries.add(entity);
				break;
				
				default:

				break;
			}
		}

		logEntries.addAll(entries);
		

	}

	public void save(LedgerEntity entry) {
		logEntries.add(entry);
	}

	public List<String[]> getDataFromCSV()
	{
		List<String[]> rawData = new ArrayList<>();

		List<String> csvData = manager.readData(manager.ledgerCSV);
		for(String data : csvData)
		{
			rawData.add(data.split(","));
		}

		return rawData;
	}

	/** Retrieves a copy of all log entries. */
    public List<LedgerEntity> getAllLogEntries() {
        return List.copyOf(logEntries);
    }

	public String getSummary() {
		// This method should provide a summary for the current day.
		// It now correctly uses the getFundsForDate logic.
		LocalDate today = LocalDate.now();
		double availableFunds = getEntryForDate(LedgerEntity.EntryType.FUND,today);
		return "Daily Summary: " + availableFunds + " funding available.";
	}

	public double findGoal(LocalDate todaysDate) {
		return getEntryForDate(LedgerEntity.EntryType.GOAL,todaysDate);
	}

	public double findFunds(LocalDate date) {
		return getEntryForDate(LedgerEntity.EntryType.FUND,date);
	}

	public double findThreshold(LocalDate date) {
		return getEntryForDate(LedgerEntity.EntryType.THRESHOLD,date);
	}

	// public double calculateDonations(LocalDate todaysDate) {
	// 	// Sum the total value of all NEED entries for the given date.
	// 	return logEntries.stream()
	// 		.filter(entry -> entry.getType() == LedgerEntity.EntryType.NEED && entry.getDate().equals(todaysDate))
	// 		.mapToDouble(entry -> {
	// 			NeedComponent component = needsRepository.getNeedByName(entry.getNeedName());
	// 			if (component != null) {
	// 				// Total cost for this entry is the component's total cost * quantity fulfilled
	// 				return component.getTotal() * entry.getCount();
	// 			} else {
	// 				// If need/bundle not found, it contributes 0 to the total.
	// 				// A warning could be logged here.
	// 				System.out.println("[Warning] Could not find need/bundle '" + entry.getNeedName() + "' in catalog for donation calculation.");
	// 				return 0.0;
	// 			}
	// 		}).sum();
	// }

	/**
	 * Calculates the total value of all 'NEED' fulfillment entries 
	 * for the specified date by summing the pre-calculated 'amount' field 
	 * in the LedgerEntity.
	 * This prevents the bug where the application tried to look up the cost of 
	 * fulfilled (and now deleted) Needs from the NeedsRepository.
	 * @param date The date for which to calculate total donations.
	 * @return The total cost of fulfilled needs for that date.
	 */
	public double calculateDonations(LocalDate date) {
		LocalDate finalDate = (date == null) ? LocalDate.now() : date;
		
		//Sum the 'amount' field (which stores the total cost) of all NEED entries for the date.
		double totalDonations = logEntries.stream()
			.filter(entry -> entry.getType() == LedgerEntity.EntryType.NEED)
			.filter(entry -> entry.getDate().equals(finalDate))
			.mapToDouble(LedgerEntity::getAmount)
			.sum();
			
		return totalDonations;
	}

	public void addDonations(double donation1, double donation2) {
		LocalDate today = LocalDate.now();

		this.logEntries.add(new LedgerEntity(today, LedgerEntity.EntryType.FUND, donation1));
		this.logEntries.add(new LedgerEntity(today, LedgerEntity.EntryType.FUND, donation2));
		setChanged();
	}

	// //V1
	// // Writes all log entries back to the CSV file.
	// public void saveLogEntries() throws IOException
	// {
	// 	List<String> csvLines = new ArrayList<>();
		
		
		
	// 	for(LedgerEntity entry : logEntries)
	// 	{
	// 		LocalDate date = entry.getDate();
	// 		int year = date.getYear();
	// 		int month = date.getMonthValue();
	// 		int day = date.getDayOfMonth();
			
	// 		String line;
			
	// 		switch(entry.getType())
	// 		{
	// 			case FUND:
					
	// 				line = String.format("%d,%d,%d,f,%.1f",
	// 					year, month, day, entry.getAmount());
	// 				break;
					
	// 			case GOAL:
					
	// 				line = String.format("%d,%d,%d,g,%.1f",
	// 					year, month, day, entry.getAmount());
	// 				break;
					
	// 			case NEED:
					
	// 				line = String.format("%d,%d,%d,n,%s,%.1f",
	// 					year, month, day, entry.getNeedName(), entry.getCount());
	// 				break;
					
	// 			default:
	// 				continue;
	// 		}
			
	// 		csvLines.add(line);
	// 	}
		
	// 	// Write to file
	// 	manager.writeData(manager.ledgerCSV, csvLines);
	// 	setChanged();
	// 	notifyObservers("Ledger saved to " + manager.ledgerCSV);
	// }

	private String toCSVLine(LedgerEntity entry) {
        LocalDate date = entry.getDate();
        
        //Use String.format("%d,%02d,%02d") for zero-padding Day and Month
        String datePart = String.format("%d,%02d,%02d", 
                date.getYear(), 
                date.getMonthValue(), 
                date.getDayOfMonth());

        switch (entry.getType()) {
            case NEED:
                return String.format("%s,n,%s,%.1f", datePart, entry.getNeedName(), entry.getCount());
            case FUND:
                return String.format("%s,f,%.1f", datePart, entry.getAmount());
            case GOAL:
                return String.format("%s,g,%.1f", datePart, entry.getAmount());
            case THRESHOLD:
                return String.format("%s,t,%.1f", datePart, entry.getAmount());
            case INCOME:
                return String.format("%s,i,%s,%.1f", datePart, entry.getNeedName(), entry.getCount());
            default:
                throw new UnsupportedOperationException("Unknown EntryType for CSV saving.");
        }
    }

	// private List<String> logEntriesToString() {
	// 	List<String> csvLines = new ArrayList<>();
		
	// 	for (LedgerEntity entry : logEntries) {
	// 		String datePart = String.format("%d,%d,%d", 
	// 			entry.getDate().getYear(), 
	// 			entry.getDate().getMonthValue(), 
	// 			entry.getDate().getDayOfMonth());

	// 		// Map the EntryType enum to the required single-character literal
	// 		String typeLiteral = switch (entry.getType()) {
	// 			case FUND -> "f";         // Requirement: f (funds)
	// 			case GOAL, THRESHOLD -> "t"; // ConsoleView sets GOAL for Threshold, Requirement: t
	// 			case NEED -> "n";         // Requirement: n (fulfilled need)
	// 			case INCOME -> "i";       // Requirement: i (funding income)
	// 			default -> throw new IllegalStateException("Unknown EntryType: " + entry.getType());
	// 		};

	// 		String line = switch (entry.getType()) {
	// 			// FUND (f), GOAL/THRESHOLD (t) entries: yyyy,mm,dd,type,amount
	// 			case FUND, GOAL, THRESHOLD -> String.format("%s,%s,%.2f", 
	// 														datePart, 
	// 														typeLiteral, 
	// 														entry.getAmount());
	// 			// NEED (n), INCOME (i) entries: yyyy,mm,dd,type,name,count/units
	// 			case NEED, INCOME -> String.format("%s,%s,%s,%.2f", 
	// 												datePart, 
	// 												typeLiteral, 
	// 												entry.getNeedName(), // name/source name
	// 												entry.getCount()); // count/units
	// 			default -> throw new IllegalStateException("Unknown EntryType: " + entry.getType());
	// 		};
	// 		csvLines.add(line);
	// 	}
	// 	return csvLines;
	// }
	
	//V2
	public void saveLogEntries() throws IOException {
		// List<String> data = logEntriesToString();
		// manager.writeData(manager.ledgerCSV, data);
		List<String> csvLines = logEntries.stream()
                                  .map(this::toCSVLine)
                                  .collect(Collectors.toList());
        manager.writeData(manager.ledgerCSV, csvLines);
		setChanged();
		notifyObservers();
	}

	/**
     * Finds the active available entry for the given date.
     * The logic follows these rules:
     *     1. If a entry exist for the exact date, use that value.
     *     2. If no entries for the exact date, use the funds from the most recent
     *        entry that is on or before the given date.
     *     3. If the entry was never entered, use the system default for the specified entry.
     * @param date The date for which to find the active funds.
	 * @param entryType the Entry type you want to retrieve
     * @return The active available funds amount.
     */
	public double getEntryForDate(LedgerEntity.EntryType entryType, LocalDate date) {
		LocalDate finalDate = (date == null) ? LocalDate.now() : date;
		double defaultValue;

		switch(entryType)
		{
			case LedgerEntity.EntryType.FUND:
				defaultValue = DEFAULT_FUNDS;
				break;
			
			case LedgerEntity.EntryType.GOAL:
				defaultValue = DEFAULT_GOAL;
				break;

			case LedgerEntity.EntryType.THRESHOLD:
				defaultValue = DEFAULT_THRESHOLD;
				break;

			default:
				throw new IllegalArgumentException("Unknown Ledger Entry Type: " + entryType);
			
		}

		// 1. Find the last entry for the exact date.
		Optional<LedgerEntity> entryForDate = logEntries.stream()
			.filter(entry -> entry.getType() == entryType)
			.filter(entry -> entry.getDate().equals(finalDate))
			.reduce((first, second) -> second); // Get the last element

		if (entryForDate.isPresent()) {
			return entryForDate.get().getAmount();
		}

		// 2. If no entry for the exact date, find the most recent one before it.
		return logEntries.stream()
			.filter(entry -> entry.getType() == entryType)
			.filter(entry -> entry.getDate().isBefore(finalDate))
			.max(Comparator.comparing(LedgerEntity::getDate))
			.map(LedgerEntity::getAmount)
			.orElse(defaultValue); // 3. Fallback to default
	}

	public Day buildDay(LocalDate date) {
		double goal = getEntryForDate(LedgerEntity.EntryType.GOAL,date);
		double funds = getEntryForDate(LedgerEntity.EntryType.FUND, date);
		return new Day(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), goal, funds);
	}
}
