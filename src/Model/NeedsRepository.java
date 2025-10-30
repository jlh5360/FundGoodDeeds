package Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;


public class NeedsRepository extends Observable {

	private final List<NeedComponent> needsCatalog = new ArrayList<>();

	public void getNeeds(String csvSource) {
		//Reads BasicNeeds from CSV
	}

	public void convertNeedsToObject(List<String[]> rawNeeds) {
		//Parses data into BasicNeed objects
	}

	public void getBundles(String csvSource) {
		//Reads Bundles from CSV
	}

	public void convertBundlesToBundlesObject(List<String[]> rawBundles) {
		//Parses data into Bundle objects
	}

	public void addNeedsToNeedsArray(List<NeedComponent> basicNeeds) {
		this.needsCatalog.addAll(basicNeeds);
	}

	public void addBundlesToBundlesArray(List<NeedComponent> bundles) {
		this.needsCatalog.addAll(bundles);
	}

public NeedComponent getNeedByName(String name) {
    return (needsCatalog.stream()
        .filter(n -> n.getName().equalsIgnoreCase(name))
        .findFirst()
        .orElse(null));
    }
}
