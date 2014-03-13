import java.util.HashSet;


public class Tables extends HashSet<Table> {
	public boolean contains(String name) {
		for (Table t : this)
			if (t.getName().equals(name)) return true;
		return false;
	}
	
	/**
	 * Return the table named "name", or null if it does not exist
	 * @param name
	 * @return
	 */
	public Table get(String name) {
		for (Table t : this) 
			if (t.getName().equals(name)) return t;
		return null;
	}
}
