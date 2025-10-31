package FundGoodDeeds.model;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class LedgerRepository extends Observable {
	private final List<LedgerEntity> logEntries = new ArrayList<>();

	public void save(LedgerEntity entity) {
		
	}

	public String getSummary() 
	{
		//Logic to calculate summary

			double availableFunds = 0;
			List<String> rawData;
			try {
				rawData = CSVManager.readData("model/log.csv");
				for(String rawString : rawData)
				{
					String[] splittedString = rawString.split(",");
					if(splittedString[3].equals("f"))
						availableFunds += Double.parseDouble(splittedString[4]);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		return "Daily Summary: " + availableFunds + "funding available.";
	}

	public double findGoal(LocalDate todaysDate) {
		//Logic to find the current goal for the date
		// This will retrieve all the goals and compare their date to today's date
		return 200.00;
	}

	public double calculateDonations(LocalDate todaysDate) {
		//Logic to aggregate donations for the date
		
		return 50.00;
	}

	public void addDonations(double donation1, double donation2) {
		LocalDate today = LocalDate.now();

		this.logEntries.add(new LedgerEntity(today, LedgerEntity.EntryType.FUND, donation1));
		this.logEntries.add(new LedgerEntity(today, LedgerEntity.EntryType.FUND, donation2));
		setChanged();
	}
}
