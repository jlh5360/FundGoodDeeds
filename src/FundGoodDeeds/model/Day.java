package FundGoodDeeds.model;


import java.time.*;

public class Day {
    private LocalDate currentDate;
    private double threshold;
    private double funds;
    
    

    // make brand new day with current time
    public Day() {
        this.currentDate = LocalDate.now();
        this.threshold = 2000.0;
        this.funds = 150.0;
        
    }
    
    // make brand new day with current time
    // possibly not used but good to have
    public Day(double funds) {
        this.currentDate = LocalDate.now();
        this.threshold = 2000.0;
        this.funds = funds;
    }
    // make brand new day with current time and funds and thresholds
    // possibly not used but good to have
    public Day(double funds, double threshold) {
        this.currentDate = LocalDate.now();
        this.threshold = threshold;
        this.funds = funds;
    }

    // travel through time and create specific dates with thresholds and funds
    public Day(int year, int month, int date, double threshold, double funds) {
        this.currentDate = LocalDate.of(year, month, date);
        this.threshold = threshold;
        this.funds = funds;
    }

    // getters
    public LocalDate getCurrentDate() {
        return this.currentDate;
    }
    public double getThreshold() {
        return this.threshold;
    }
    public double getFunds() {
        return this.funds;
    }

    // setters
    public void setFunds(double funds) {
        this.funds = funds;
    }
    // set threshold for today or future days in the system BUT NOT THE PAST
    public double setThreshold() {
        return this.threshold;
    }

}
