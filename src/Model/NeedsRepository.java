package Model;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class NeedsRepository extends Observable {

	private final List<NeedComponent> needsCatalog = new ArrayList<>();

	// Stores the csv source path so that there's no need to specify it over and over again

	private final String csvSource;

	public NeedsRepository(String csvSource)
	{
		this.csvSource = csvSource;
	}


	public List<String[]> getNeeds() 
	{
		//Reads BasicNeeds from CSV
		List<String[]> needs = new ArrayList<>();

		try {
			
			List<String> needsAndBundles = CSVManager.readData(this.csvSource);
			for(String dataString : needsAndBundles)
			{
				String[] splittedString = dataString.split(",");
				if(splittedString[0].equals("n"))
					needs.add(splittedString);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return needs;

	}

	public List<NeedComponent> convertNeedsToObject(List<String[]> rawNeeds) 
	{
		//Parses data into BasicNeed objects

		List<NeedComponent> needsList = new ArrayList<>();
		for(String[] need : rawNeeds)
		{
			NeedComponent needComponent = new Need(need[1], Double.parseDouble(need[2]),Double.parseDouble(need[3]),Double.parseDouble(need[4]));
			needsList.add(needComponent);
		}
		return needsList;

	}

	public List<String[]> getBundles() 
	{
		List<String[]> bundles = new ArrayList<>();

		try 
		{
			// Filters out needs, focusing on the bundles
			List<String> needsAndBundles = CSVManager.readData(this.csvSource);
			for(String dataString : needsAndBundles)
			{
				String[] splittedString = dataString.split(",");
				if(splittedString[0].equals("b"))
					bundles.add(splittedString);
			}
			
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return bundles;

	
	}

	public List<NeedComponent> convertBundlesToBundlesObject(List<String[]> rawBundles) 
	{
		List<NeedComponent> bundles = new ArrayList<>();


		for(String[] bundle : rawBundles)
		{
			// Set the name, it is always the second index

			Bundle bundleObject = new Bundle(bundle[1]);

			// Variables to hold the data
			
			for(int index = 2; index < bundle.length; index++)
			{
				double count = -1;
				String needName = "undefined";
				// In the csv files, need names are always even columns while need counts are always odd columns

				if(index % 2 == 0)
				{
					needName = bundle[index];
					// Skip to the next index
					index++;
				}
				if(index % 2 != 0)
				{
					count = Double.parseDouble(bundle[index]);
				}
				// Ensure we don't accidentally add the placeholder values
				if(count > -1 && !(needName.equals("undefined")))
					bundleObject.addComponent(needName, (int) count);
			}
			bundles.add(bundleObject);
		}
		return bundles;


	}

	public void addNeedsToNeedsArray(List<NeedComponent> basicNeeds) 
	{
		this.needsCatalog.addAll(basicNeeds);
	}

	public void addBundlesToBundlesArray(List<NeedComponent> bundles) 
	{
		this.needsCatalog.addAll(bundles);
	}

	public NeedComponent getNeedByName(String name) 
	{
		return (needsCatalog.stream()
			.filter(n -> n.getName().equalsIgnoreCase(name))
			.findFirst()
			.orElse(null));
    }
}
