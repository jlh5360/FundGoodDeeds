package FundGoodDeeds.model;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Observable;
import java.util.Optional;

public class LedgerRepository extends Observable {
	private final List<LedgerEntity> logEntries = new ArrayList<>();
	public CSVManager manager;

	private static final double DEFAULT_GOAL = 2000.0;

	public LedgerRepository(CSVManager manager)
	{
		this.manager = manager;
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
			LedgerEntity entity;

			LocalDate entityDate = LocalDate.of(year,month,day);
			switch(raw[3])
			{
				
				case "n":
					entity = new LedgerEntity(entityDate, LedgerEntity.EntryType.NEED,raw[4] ,Integer.parseInt(raw[5]));
				break;

				case "f":
					entity = new LedgerEntity(entityDate, LedgerEntity.EntryType.FUND, Double.parseDouble(raw[4]));
				break;
					
				case "g":
					entity = new LedgerEntity(entityDate, LedgerEntity.EntryType.GOAL, Double.parseDouble(raw[4]));
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

	public String getSummary() {
		//Logic to calculate summary
		double availableFunds = 0;
		if(!logEntries.isEmpty())
		{
			for(LedgerEntity entity : logEntries)
			{
				if(entity.getType() == LedgerEntity.EntryType.FUND)
				{
					availableFunds = entity.getAmount();
					break;
				}
			}
		}

		// In case there was no entry

		if(!(availableFunds > 0))
		{
			List<String> rawData = manager.readData("log_test.csv");

			// Placeholder to compare against

			LocalDate previousDate = LocalDate.MIN;
			double funds = 0;

			// Gets the most recent funding entry

			for(String dataString : rawData)
			{
				String[] splittedString = dataString.split(",");
				if(splittedString[3].equals("f"))
				{
					// Converting the raw data

					int year = Integer.parseInt(splittedString[0]);
					int month = Integer.parseInt(splittedString[1]);
					int day = Integer.parseInt(splittedString[2]);

					LocalDate currentIndexDate = LocalDate.of(year,month,day);

					if(currentIndexDate.isAfter(previousDate))
					{
						previousDate = currentIndexDate;
						funds = Double.parseDouble(splittedString[3]);
					}
				}
			}


			availableFunds = funds;
		}

		return "Daily Summary: " + availableFunds + " funding available.";
	}

	public double findGoal(LocalDate todaysDate) {
		//Logic to find the current goal for the date

		//I'll retrieve all the goals that have been entered and compare them to today's date to find today's goal
		
		return 200.00;
	}

	public double calculateDonations(LocalDate todaysDate) {
		//Logic to aggregate donations for the date

		// Almost the same logic as findGoal

		return 50.00;
	}

	public void addDonations(double donation1, double donation2) {
		LocalDate today = LocalDate.now();

		this.logEntries.add(new LedgerEntity(today, LedgerEntity.EntryType.FUND, donation1));
		this.logEntries.add(new LedgerEntity(today, LedgerEntity.EntryType.FUND, donation2));
		setChanged();
	}

	// Writes all log entries back to the CSV file.
	public void saveLogEntries() throws IOException
	{
		List<String> csvLines = new ArrayList<>();
		
		
		
		for(LedgerEntity entry : logEntries)
		{
			LocalDate date = entry.getDate();
			int year = date.getYear();
			int month = date.getMonthValue();
			int day = date.getDayOfMonth();
			
			String line;
			
			switch(entry.getType())
			{
				case FUND:
					
					line = String.format("%d,%d,%d,f,%.1f",
						year, month, day, entry.getAmount());
					break;
					
				case GOAL:
					
					line = String.format("%d,%d,%d,g,%.1f",
						year, month, day, entry.getAmount());
					break;
					
				case NEED:
					
					line = String.format("%d,%d,%d,n,%s,%d",
						year, month, day, entry.getNeedName(), entry.getCount());
					break;
					
				default:
					continue; 
			}
			
			csvLines.add(line);
		}
		
		// Write to file
		manager.writeData("ledger-new.csv", csvLines);
		setChanged();
		notifyObservers();
	}

	//THIS NEEDS WORK ESPECIALLY CHECKING WITH THE FILE AND MEMORY

	/**
     * Finds the active funding goal for the given date.
     * The logic follows these rules:
     *     1. If a goal exists for the exact date, use that value.
     *     2. If no goal for the exact date, use the goal from the most recent 
     *        funding goal entry that is on or before the given date.
     *     3. If a funding goal was never entered, the system default is 2000.0.
     * * @param date The date for which to find the active goal.
     * @return The active funding goal amount.
     */
	public double getGoalForDate(LocalDate date) {
        //Fix for "effectively final" error: Use a new variable for stream logic
        LocalDate finalDate = (date == null) ? LocalDate.now() : date;

        //1. & 2. Find the most recent GOAL entry on or before the given date.
        Optional<LedgerEntity> mostRecentGoal = logEntries.stream()
            .filter(entry -> entry.getType() == LedgerEntity.EntryType.GOAL)
            //Use finalDate which is effectively final
            .filter(entry -> !entry.getDate().isAfter(finalDate)) 
            .max(Comparator.comparing(LedgerEntity::getDate)); 
        
        //3. Return the goal amount or the default value
        return mostRecentGoal.isPresent() 
            ? mostRecentGoal.get().getAmount() 
            : DEFAULT_GOAL;
    }
}
