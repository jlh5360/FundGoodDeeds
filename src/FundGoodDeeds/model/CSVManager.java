package FundGoodDeeds.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CSVManager 
{
    private final String absoluteDataPath = "term-project-rit-swen-383-01-1a/src/data/";
    public String ledgerCSV;
    
    public CSVManager(String ledgerCSVFile) throws FileNotFoundException
    {
        Path path = Path.of(absoluteDataPath);
        if(!Files.exists(path))
        {   
            throw new FileNotFoundException(absoluteDataPath + " does not exist!");
        }
        this.ledgerCSV = ledgerCSVFile;
    }

    public List<String> readData(String csvPath)
    {

        // List to keep all the csv data

        List<String> dataList = new ArrayList<>();

        // Header in case it is needed for future implementations

        String header;



        try(BufferedReader reader = Files.newBufferedReader(Path.of(this.absoluteDataPath + csvPath)))
        {

            // Skips the header

            header = reader.readLine();

            while(reader.ready())
            {
                String line = reader.readLine();
                dataList.add(line);
                
            }

            reader.close();

            
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        // Returns after ensuring there are no errors

        return dataList;
        
    }

    public void writeData(String csvPath, List<String> data) throws IOException
    {

        // Appends to a file using a encapsulating class
        
        FileWriter writer = new FileWriter(this.absoluteDataPath + csvPath,true);

        // Buffered writer for increased performance

        try(BufferedWriter bufferedWriter = new BufferedWriter(writer))
        {
            for(String dataString : data)
            {
                bufferedWriter.write(dataString + "\n");
            }
            bufferedWriter.close();
        }

        // Outputs errors

        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}

