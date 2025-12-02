package FundGoodDeeds.model;

import java.time.LocalDate;

public class LedgerEntity implements AbstractLedgerEntry {
	private final LocalDate date;
	private final EntryType type;
	private final String needName;
	private final double amount;
	private final double count;

	//Constructor for FUND/GOAL entries
	public LedgerEntity(LocalDate date, EntryType type, double amount) {
		this.date = date;
		this.type = type;
		this.amount = amount;
		this.needName = null;
		this.count = 0;
	}

	//Contructor for NEED fulfillment entries
	public LedgerEntity(LocalDate date, EntryType type, String needName, double count) {
		this.date = date;
		this.type = type;
		this.needName = needName;
		this.count = count;
		this.amount = 0.0;
	}

	//Contructor for NEED fulfillment entries
	public LedgerEntity(LocalDate date, EntryType type, String needName, double count, double totalCostOrIncome) {
	// public LedgerEntity(LocalDate date, EntryType type, String needName, double count) {
		this.date = date;
		this.type = type;
		this.needName = needName;
		this.count = count;
		// this.amount = 0.0;
		this.amount = totalCostOrIncome;
	}

	public String getNeedName() {
		return needName;
	}

	@Override
	public EntryType getType(){
		return type;
	}

	public double getAmount() {
		return amount;
	}

	@Override
	public LocalDate getDate() {
		return date;
	}
	
	public double getCount() {
		return count;
	}

	/**
     * Converts the LedgerEntity to a CSV-formatted string according to the specifications:
     * yyyy,mm,dd,f,funds
     * yyyy,mm,dd,t,threshold
     * yyyy,mm,dd,n,name,count
     * yyyy,mm,dd,i,name,units
     */
	public String toCSV() {
        // Format: yyyy,mm,dd
        String dateStr = String.format("%d,%d,%d", date.getYear(), date.getMonthValue(), date.getDayOfMonth());
        String typeStr = type.toString().substring(0, 1).toLowerCase(); // f, g, n, i, t

        switch (type) {
            case FUND:
            case GOAL:
            case THRESHOLD:
                // 5 fields total: yyyy,mm,dd,type,amount
                return String.format("%s,%s,%.2f", dateStr, typeStr, amount);

            case NEED:
            case INCOME:
                // 6 fields total: yyyy,mm,dd,type,name,count/units
                return String.format("%s,%s,%s,%.2f", dateStr, typeStr, needName, count);

            default:
                return ""; 
        }
    }

	@Override
	public String toString() {
		return String.format("[%s] Type: %s, Name: %s, Count: %.2f, Amount: %.2f",
				date, type, needName, count, amount);
	}
}
