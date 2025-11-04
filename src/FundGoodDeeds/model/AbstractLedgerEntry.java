package FundGoodDeeds.model;

import java.time.LocalDate;

public interface AbstractLedgerEntry {
	public enum EntryType {FUND, GOAL, NEED}
    
	EntryType getType();

	LocalDate getDate();
}
