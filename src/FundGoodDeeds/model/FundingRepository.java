package FundGoodDeeds.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

@SuppressWarnings("deprecation")
public class FundingRepository extends Observable {
    private CSVManager csvManager;
    private List<FundingSource> fundingSources;

    // constructor
    public FundingRepository(CSVManager csvManager) {
        this.csvManager = csvManager;
        this.fundingSources = new ArrayList<>();
    }

    /**
     * takes in raw funding source data from CSVManager
     * splits each line into its components and creates FundingSource objects.
     * i,Student Loan,5000.0 > FundingSource("Student Loan", 5000.0)
     */
    public void getSourcesFromCSV() {

        List<String> rawSourcesList = csvManager.readData("funding.csv");
        for (String rawSource : rawSourcesList) {
            String[] individualSource = rawSource.split(",");
            FundingSource source = new FundingSource(individualSource[1], Double.parseDouble(individualSource[2]));
            this.fundingSources.add(source);
        }
    }


    // Delegates saving funding sources

    public void saveFundsCatalog() throws IOException {
        saveSourcesToCSV();
        setChanged();
        notifyObservers();
    }

    // Loads funds from CSV

    public void loadFunds()
    {
        this.fundingSources.clear();
        getSourcesFromCSV();
        setChanged();
        notifyObservers();
    }


    // list current funding sources
    public List<FundingSource> getFundingSources() {
        return this.fundingSources;
    }

    // save funding sources to CSV
    public void saveSourcesToCSV() throws IOException {
        List<String> rawSourcesList = new ArrayList<>();

        for (FundingSource source : this.fundingSources) {
            String name = source.getName();
            String amount = Double.toString(source.getAmount());
            String rawSource = "i," + name + "," + amount;

            // String rawSource = String.format("i,%s,%.2f", source.getName(), source.getAmount());

            rawSourcesList.add(rawSource);

        }

        try {
            csvManager.writeData("funding.csv", rawSourcesList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // OR

    public void addFundingSource(String name, Double amount) {
        this.fundingSources.add(new FundingSource(name, amount));
        setChanged();
        notifyObservers();
    }

    public FundingSource getFundingSourceByName(String name) {
        for (FundingSource source : this.fundingSources) {
            if (source.getName().equals(name)) {
                return source;
            }
        }
        return null;
    }

    // incoming from controller source name and new amount
    public void editFundingSource(String sourceName, Double newAmount) {
        FundingSource source = getFundingSourceByName(sourceName);
        if (source == null) {
            System.out.println("ERROR: FUNDING SOURCE NOT FOUND");
        } else {
            source.setAmount(newAmount);
            setChanged();
            notifyObservers();
        }

    }

    public void removeFundingSource(String name) {
        getFundingSourceByName(name);
        this.fundingSources.removeIf(source -> source.getName().equals(name));
        setChanged();
        notifyObservers();
    }

    public double getTotalFunds() {
        double total = 0.0;
        for (FundingSource source : this.fundingSources) {
            total += source.getAmount();
        }
        return total;
    }

    public void setUser(User user)
    {
        this.csvManager.setUserPath(user);
    }

}
