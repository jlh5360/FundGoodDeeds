package FundGoodDeeds.model;

import java.time.LocalDate;

public interface AbstractLedgerEntry {
	public enum EntryType {FUND, GOAL, NEED,THRESHOLD}
    
	EntryType getType();

	LocalDate getDate();
}
