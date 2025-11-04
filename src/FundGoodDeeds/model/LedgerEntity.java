package FundGoodDeeds.model;

import java.time.LocalDate;

public class LedgerEntity implements AbstractLedgerEntry {
	private final LocalDate date;
	private final EntryType type;
	private final String needName;
	private final double amount;
	private final int count;

	//Constructor for FUND/GOAL entries
	public LedgerEntity(LocalDate date, EntryType type, double amount) {
		this.date = date;
		this.type = type;
		this.amount = amount;
		this.needName = null;
		this.count = 0;
	}

	//Contructor for NEED fulfillment entries
	public LedgerEntity(LocalDate date, EntryType type, String needName, int count) {
		this.date = date;
		this.type = type;
		this.needName = needName;
		this.count = count;
		this.amount = 0.0;
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
	
	public int getCount() {
		return count;
	}
}
