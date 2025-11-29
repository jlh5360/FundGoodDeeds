package FundGoodDeeds.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
public class NeedsRepository extends Observable {
	private final List<NeedComponent> needsCatalog = new ArrayList<>();
	private final CSVManager manager;
	

	public NeedsRepository(CSVManager manager)
	{
		this.manager = manager;
	}

	public void loadNeeds()
	{
		this.needsCatalog.clear();
		
		List<String[]> rawNeeds = getNeedsFromCSV();
		List<String[]> rawBundles = getBundlesFromCSV();
		
		addNeedsToNeedsArray(convertNeedsToObject(rawNeeds));
		// String names = needsCatalog.stream()
		// 	.map(NeedComponent::getName)
		// 	.collect(Collectors.joining(", "));
		// System.out.println("Needs catalog after loading needs only: " + names + "\n");
		
		convertBundlesToBundlesObject(rawBundles);
		// String bundles = needsCatalog.stream()
		// 	.map(NeedComponent::getName)
		// 	.collect(Collectors.joining(", "));
		// System.out.println("Needs catalog after loading needs only: " + bundles + "\n");
	}


	public List<String[]> getNeedsFromCSV() 
	{
		//Reads BasicNeeds from CSV
		List<String[]> needs = new ArrayList<>();
		List<String> needsAndBundles = manager.readData("needs.csv");

		for(String dataString : needsAndBundles)
		{
			String[] splittedString = dataString.split(",");

			if(splittedString[0].equals("n")) {
				needs.add(splittedString);
			}
		}

		return needs;

	}

	public List<NeedComponent> convertNeedsToObject(List<String[]> rawNeeds) 
	{
		//Parses data into BasicNeed objects

		List<NeedComponent> needsList = new ArrayList<>();

		for(String[] need : rawNeeds)
		{
			//Need(String name, double total)
			NeedComponent needComponent = new Need(need[1], Double.parseDouble(need[2]));
			// //Need(String name, double total, double fixed, double variable, double fees)
			// NeedComponent needComponent = new Need(need[1], Double.parseDouble(need[2]),Double.parseDouble(need[3]),Double.parseDouble(need[4]), Double.parseDouble(need[5]));
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
			if(component instanceof Need need)
			{
				// Format: n,name,total,fixed,variable,fees
                String line = String.format("n,%s,%.1f",
					need.getName(),
					need.getTotal()
					// need.getFixed(),
					// need.getVariable(),
					// need.getFees()
				);
				csvLines.add(line);
			}
			else if(component instanceof Bundle bundle)
			{
				// Format: b,name,n1name,n1count,n2name,n2count,...
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

	public double getTotalNeedsCost() {
		double totalCost = 0.0;
		for (NeedComponent component : needsCatalog) {
			totalCost += component.getTotal();
		}
		return totalCost;
	}

	/** Removes a NeedComponent (Need/Bundle) by name from the catalog. */
    public void removeNeedComponent(String name) {
        //Use removeIf to iterate and delete based on name comparison
        needsCatalog.removeIf(nc -> nc.getName().equals(name));
        setChanged();
        notifyObservers();
    }

	/**
     * Finds all Bundles that contain a Need component with the given name.
     * @param needName The name of the component to search for.
     * @return A list of Bundles that contain the specified Need.
     */
    public List<Bundle> findBundlesContainingNeed(String needName) {
        List<Bundle> bundles = new ArrayList<>();
        for (NeedComponent component : needsCatalog) {
            if (component instanceof Bundle bundle) {
                // Check if the bundle contains the need by iterating over its keys
                for (NeedComponent innerComponent : bundle.getComponentsAndCounts().keySet()) {
                    if (innerComponent.getName().equalsIgnoreCase(needName)) {
                        bundles.add(bundle);
                        break; // Move to the next bundle
                    }
                }
            }
        }
        return bundles;
    }

    /**
     * Checks if a basic NeedComponent is a part of any existing Bundle.
     * @param needName The name of the NeedComponent to check.
     * @return true if the Need is a component in one or more Bundles, false otherwise.
     */
    public boolean isNeedComponentOfAnyBundle(String needName) {
        return findBundlesContainingNeed(needName).size() > 0;
    }

    /**
     * Gets the total count of a specific Need component across all Bundles.
     * @param needName The name of the need component.
     * @return The total quantity of that need component across all bundles.
     */
    public int getTotalBundleComponentCount(String needName) {
        int totalCount = 0;
        List<Bundle> bundles = findBundlesContainingNeed(needName);
        for (Bundle bundle : bundles) {
            // Iterate over the bundle's components to find the specific component count
            for (Map.Entry<NeedComponent, Integer> entry : bundle.getComponentsAndCounts().entrySet()) {
                if (entry.getKey().getName().equalsIgnoreCase(needName)) {
                    totalCount += entry.getValue();
                }
            }
        }
        return totalCount;
    }

	/**
	 * Edits the total cost of a basic Need. Implements logic for Program Operation #14 (part 1).
	 * @param name The name of the Need.
	 * @param newTotal The new total cost.
	 * @return true if the Need was found and updated, false otherwise.
	 */
	public boolean editNeedTotal(String name, double newTotal) {
		NeedComponent component = getNeedByName(name);
		
		// Check if it's a basic Need (not a Bundle)
		if (component instanceof Need) {
			Need need = (Need) component;
			need.setTotal(newTotal); // Assume setTotal is public in Need.java
			setChanged();
			notifyObservers();
			return true;
		}
		return false;
	}

	/**
	 * Notifies observers that a Bundle's components have been updated. 
	 * This is effectively an 'update' method for bundles. Implements logic for Program Operation #14 (part 2).
	 * @param bundle The bundle that was updated.
	 * @return true if the bundle was found, false otherwise.
	 */
	public boolean updateBundle(Bundle bundle) {
		// The bundle object itself is mutable and already updated. 
		// We just need to ensure it's in the catalog and then notify.
		if (needsCatalog.contains(bundle)) {
			setChanged();
			notifyObservers();
			return true;
		}
		return false;
	}

	/**
     * Updates the name and/or total cost of an existing Need (not Bundle).
     * @param oldName The current name of the Need to be edited.
     * @param newName The new name for the Need. Must not be blank.
     * @param newTotal The new total cost. If -1.0, the cost is not changed.
     */
    public void editNeed(String oldName, String newName, double newTotal) {
        NeedComponent component = getNeedByName(oldName);
        if (component instanceof Need need) {
            // 1. Update Name (only if a new name is provided)
            if (newName != null && !newName.isBlank() && !oldName.equalsIgnoreCase(newName)) {
                need.setName(newName);
            }
            
            // 2. Update Total Cost (only if a valid new total is provided)
            if (newTotal >= 0.0) {
                need.setTotal(newTotal);
            }
            setChanged();
            notifyObservers();
        } else {
            System.err.println("Error: Cannot edit need '" + oldName + "'. Not found or is a Bundle.");
        }
    }

    /**
     * Updates the name of an existing Bundle.
     * @param oldName The current name of the Bundle to be edited.
     * @param newName The new name for the Bundle.
     */
    public void editBundleName(String oldName, String newName) {
        NeedComponent component = getNeedByName(oldName);
        // We ensure we are only editing a Bundle object
        if (component instanceof Bundle bundle) { 
            bundle.setName(newName);
            setChanged();
            notifyObservers();
        } else {
            System.err.println("Error: Cannot rename bundle '" + oldName + "'. Not found or is a simple Need.");
        }
    }
    
    /**
     * Adds a component to an existing Bundle, or increases its quantity.
     * @param bundleName The name of the Bundle.
     * @param component The NeedComponent to add.
     * @param quantity The number of units to add (must be > 0).
     */
    public void addBundleComponent(String bundleName, NeedComponent component, int quantity) {
        NeedComponent targetComponent = getNeedByName(bundleName);
        if (targetComponent instanceof Bundle bundle) {
            if (quantity > 0) {
                // The Bundle class handles checking for positive quantity
                bundle.add(component, quantity);
                setChanged();
                notifyObservers();
            } else {
                System.err.println("Error: Quantity must be positive to add a component.");
            }
        } else {
            System.err.println("Error: Bundle '" + bundleName + "' not found or is a simple Need.");
        }
    }

    /**
     * Decreases the quantity of a component within a Bundle.
     * @param bundleName The name of the Bundle.
     * @param component The NeedComponent to decrease.
     * @param quantity The amount to decrease by (must be > 0).
     * @return The number of units actually removed.
     */
    public int removeBundleComponentQuantity(String bundleName, NeedComponent component, int quantity) {
        NeedComponent targetComponent = getNeedByName(bundleName);
        if (targetComponent instanceof Bundle bundle) {
            if (quantity > 0) {
                // removeComponentUnits returns the number of units removed (0 if not found/quantity too high)
                int removedCount = bundle.removeComponentUnits(component.getName(), quantity);
                if (removedCount > 0) {
                    setChanged();
                    notifyObservers();
                }
                return removedCount;
            } else {
                System.err.println("Error: Quantity must be positive to remove units.");
                return 0;
            }
        } else {
            System.err.println("Error: Bundle '" + bundleName + "' not found or is a simple Need.");
            return 0;
        }
    }
    
    /**
     * Removes a component type entirely from a Bundle.
     * @param bundleName The name of the Bundle.
     * @param component The NeedComponent type to remove.
     */
    public void removeBundleComponentType(String bundleName, NeedComponent component) {
        NeedComponent targetComponent = getNeedByName(bundleName);
        if (targetComponent instanceof Bundle bundle) {
            // Check if the component exists in the bundle before removing to avoid unnecessary notification
            if (bundle.getComponents().contains(component)) {
                bundle.remove(component); // Removes the component entirely
                setChanged();
                notifyObservers();
            } else {
                System.err.println("Warning: Component '" + component.getName() + "' not found in bundle '" + bundleName + "'. No change made.");
            }
        } else {
            System.err.println("Error: Bundle '" + bundleName + "' not found or is a simple Need.");
        }
    }

    /**
     * Removes a simple Need by name from the catalog. Uses the existing general removal method.
     * @param name The name of the Need.
     */
    public void removeNeed(String name) {
        removeNeedComponent(name);
    }

    /**
     * Removes a Bundle by name from the catalog. Uses the existing general removal method.
     * @param name The name of the Bundle.
     */
    public void removeBundle(String name) {
        removeNeedComponent(name);
    }
}
