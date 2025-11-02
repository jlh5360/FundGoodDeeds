package FundGoodDeeds.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class NeedsRepository extends Observable {

	private final List<NeedComponent> needsCatalog = new ArrayList<>();

	private final CSVManager manager;

	public NeedsRepository(CSVManager manager)
	{
		this.manager = manager;
	}

	public void loadNeeds()
	{
		List<String[]> rawNeeds = getNeedsFromCSV();
		List<String[]> rawBundles = getBundlesFromCSV();

		addNeedsToNeedsArray(convertNeedsToObject(rawNeeds));
		addBundlesToBundlesArray((convertBundlesToBundlesObject(rawBundles)));
	}


	public List<String[]> getNeedsFromCSV() 
	{
		//Reads BasicNeeds from CSV
		List<String[]> needs = new ArrayList<>();

		List<String> needsAndBundles = manager.readData("needs.csv");
		for(String dataString : needsAndBundles)
		{
			String[] splittedString = dataString.split(",");
			if(splittedString[0].equals("n"))
				needs.add(splittedString);
		}

		return needs;

	}

	public List<NeedComponent> convertNeedsToObject(List<String[]> rawNeeds) 
	{
		//Parses data into BasicNeed objects

		List<NeedComponent> needsList = new ArrayList<>();
		for(String[] need : rawNeeds)
		{
			NeedComponent needComponent = new Need(need[1], Double.parseDouble(need[2]),Double.parseDouble(need[3]),Double.parseDouble(need[4]), Double.parseDouble(need[5]));
			needsList.add(needComponent);
		}
		return needsList;

	}

	public List<String[]> getBundlesFromCSV() 
	{
		List<String[]> bundles = new ArrayList<>();

		// Filters out needs, focusing on the bundles
		List<String> needsAndBundles = manager.readData("needs.csv");
		for(String dataString : needsAndBundles)
		{
			String[] splittedString = dataString.split(",");
			if(splittedString[0].equals("b"))
				bundles.add(splittedString);
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
				if(count > -1 && !(needName.equals("undefined"))) {
					
					// Look up the need from the catalog - prevents adding needs that don't exist
                	NeedComponent need = getNeedByName(needName);
                
					if(need != null) {
						// Add the need 'count' times to the bundle
						for(int i = 0; i < (int)count; i++)
						{
							bundleObject.add(need);
						}
					}

				}
            		
                
            	
			}
			bundles.add(bundleObject);
		}
		return bundles;


	}

	public void addNeedsToNeedsArray(List<NeedComponent> basicNeeds) 
	{
		this.needsCatalog.addAll(basicNeeds);
		setChanged();
	}

	public void addBundlesToBundlesArray(List<NeedComponent> bundles) 
	{
		this.needsCatalog.addAll(bundles);
		setChanged();
	}

	public NeedComponent getNeedByName(String name) 
	{
		return (needsCatalog.stream()
			.filter(n -> n.getName().equalsIgnoreCase(name))
			.findFirst()
			.orElse(null));
    }

	public void appendNeed(NeedComponent need) 
	{
		this.needsCatalog.add(need);
		setChanged();
	}

	public List<NeedComponent> getNeedsCatalog() 
	{
		return this.needsCatalog;
	}


}
