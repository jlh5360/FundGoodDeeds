package Model;

public class NeedsRepository {
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
		this.needsCalalog.addAll(basicNeeds);
	}

	public void addBundlesToBundlesArray(List<NeedComponent> bundles) {
		this.needsCalalog.addAll(bundles);
	}

	public void finishedAlert(ConsoleView view) {
		view.finishedAlert();
	}

public NeedComponent getNeedByName(String name) {
    return (needsCatalog.stream()
        .filter(n -> n.getName().equalsIgnoreCase(name))
        .findFirst()
        .orElse(null));
    }
}
