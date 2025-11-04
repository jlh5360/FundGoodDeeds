package FundGoodDeeds.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.stream.Collectors;

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
		String names = needsCatalog.stream()
			.map(NeedComponent::getName)
			.collect(Collectors.joining(", "));
		System.out.println("Needs catalog after loading needs only: " + names + "\n");
		
		convertBundlesToBundlesObject(rawBundles);
		String bundles = needsCatalog.stream()
			.map(NeedComponent::getName)
			.collect(Collectors.joining(", "));
		System.out.println("Needs catalog after loading needs only: " + bundles + "\n");
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

	public void convertBundlesToBundlesObject(List<String[]> rawBundles) 
	{
		List<NeedComponent> bundles = new ArrayList<>();


		for(String[] bundle : rawBundles)
		{
			//Set the name, it is always the second index
			Bundle bundleObject = new Bundle(bundle[1]);
			needsCatalog.add(bundleObject);

			//Start from index 2, iterate by 2 for needName and count pair
			//This is more robust than relying on index % 2 and manual index increments.
			for(int index = 2; index < bundle.length; index += 2)
			{
				String needName = bundle[index];
				double count = 0.0;
				
				//Ensure the count index is within bounds (should be index + 1)
				if(index + 1 < bundle.length) {
					// DEBUGGING PURPOSE
					// System.out.println("bundle object -----> " + bundleObject.getName());
					try {
						count = Double.parseDouble(bundle[index + 1]);
					} catch (NumberFormatException e) {
						System.out.println("Error: Bundle '" + bundleObject.getName() + "' has malformed count for component '" + needName + "'. Assuming 0.");
						count = 0.0;
					}
				}
				
				if(count > 0.0) {
					NeedComponent component = getNeedByName(needName);
					if (component != null) {
						// Parse as integer, as per clarified design
						int intCount = (int) count;
						if (intCount <= 0) {
							System.out.println("Warning: Bundle '" + bundleObject.getName() + "' has non-positive integer count for component '" + needName + "'. It will be skipped.");
							continue;
						}
						if (count != intCount) {
							System.out.println("Warning: Bundle '" + bundleObject.getName() + "' has a fractional count for component '" + needName + "'. The fractional part will be ignored.");
						}
						bundleObject.add(component, intCount);
					} else {
						System.out.println("Error: Component '" + needName + "' for bundle '" + bundleObject.getName() + "' not found in catalog. It will be skipped.");
					}
				}
			}
			bundles.add(bundleObject);
		}
		
	}

	public void addNeedsToNeedsArray(List<NeedComponent> basicNeeds) 
	{
		this.needsCatalog.addAll(basicNeeds);
		setChanged();
	}

	

	public NeedComponent getNeedByName(String name) 
	{		
		//Iterate over the needsCatalog which contains both Needs and Bundles
		for (NeedComponent component : needsCatalog) {
			//Compare the name (case-insensitive)
			if (component.getName().equalsIgnoreCase(name)) {
				//Found the actual existing object instance
				// DEBUGGING PURPOSE
				// System.out.println("component --------> " + component.getName());
				return component;
			}
		}

		return null;
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

	/**
	 * Writes the entire needsCatalog back to the CSV format for csv reader.
	 * Overwrites the existing file with current catalog data.
	 */
	public void saveNeedsCatalog() throws IOException
	{
		List<String> csvLines = new ArrayList<>();
		
		
		for(NeedComponent component : needsCatalog)
		{
			//After testing, I think the need logic is fine (did not mess up the need data)
			if(component instanceof Need)
			{
				// Format: n,name,total,fixed,variable,fees
				Need need = (Need) component;
				String line = String.format("n,%s,%.1f,%.1f,%.1f,%.1f",
					need.getName(),
					need.getTotal(),
					need.getFixed(),
					need.getVariable(),
					need.getFees()
				);
				csvLines.add(line);
			}
			//After testing, it messes up the order and even deletes some bundle data
			else if(component instanceof Bundle)
			{
				// Format: b,name,n1name,n1count,n2name,n2count,...
				Bundle bundle = (Bundle) component;
				StringBuilder line = new StringBuilder("b," + bundle.getName());
				
				Map<NeedComponent, Integer> componentCounts = bundle.getComponentsAndCounts();
				
				for(Map.Entry<NeedComponent, Integer> entry : componentCounts.entrySet())
				{
					String needName = entry.getKey().getName();
					double count = entry.getValue();
					line.append(",").append(needName).append(",").append(count);
					// DEBUGGING PURPOSE
					// System.out.println("entry ----------> " + entry);
					// System.out.println("line ----------> " + line);
				}
				
				csvLines.add(line.toString());
			}
		}
		
		// Write to file (this will append, so clear file first if needed)
		manager.writeData("needs.csv", csvLines);
		setChanged();
		notifyObservers("Needs catalog saved to needs.csv");
	}


}
