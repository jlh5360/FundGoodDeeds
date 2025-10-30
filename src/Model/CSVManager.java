package Model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CSVManager 
{
    
    public static List<String> readData(String csvPath) throws IOException
    {

        // List to keep all the csv data

        List<String> dataList = new ArrayList<>();

        // Header in case it is needed for future implementations

        String header;

        try(BufferedReader reader = Files.newBufferedReader(Paths.get(csvPath)))
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

    public static void writeData(String csvPath, List<String> data) throws IOException
    {

        // Appends to a file using a encapsulating class
        
        FileWriter writer = new FileWriter(csvPath,true);

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
