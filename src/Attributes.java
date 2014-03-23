import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Wrapper for List of attributes with additional functionality
 */
public class Attributes extends LinkedHashSet<Attribute> {

	/**
	 * generated by eclipse 
	 */
	private static final long serialVersionUID = 3073117117232024505L;
	
	public boolean contains(String attrName) {
		for (Attribute a : this) {
			if (a.getName().equals(attrName)) return true;
		}
		return false;
	}
	
	public Attribute get(String attrName) throws SchemaViolationException {
		for (Attribute a : this) 
			if (a.getName().equals(attrName)) return a;
		throw new SchemaViolationException("Does not contain " + attrName);
	}
	
	public Attribute get(int index) {
		if (index >= this.size() || index < 0)
			throw new IndexOutOfBoundsException();
		int i = 0;
		for (Attribute a : this) {
			if (i == index) return a;
			i++;
		}
		return null; // this will never happen because of the exception handling above
	}
	
	public int indexOf(Attribute toFind) throws SchemaViolationException {
		if (!contains(toFind)) throw new SchemaViolationException("This schema does not contain an attribute " + toFind);
		int i = 0;
		for (Attribute a : this) {
			if (a.equals(toFind)) return i;
			i++;
		}
		return -1; // this will never happen because of the exception handling above
	}
	
	/**
	 * Determine whether or not the given Row matches this set of Attributes.
	 * @param row
	 * @return
	 */
	public boolean matches(Row r) {
		return equals(r.schema);
	}
	
	public boolean equals(Attributes other) {
		if (other.size() != this.size()) return false;
		
		for (int i = 0; i < size(); i++) {
			if (other.get(i).getType() != this.get(i).getType()) return false;
		}
		
		return true;
	}
	
	public String tableHeader() {
		StringBuilder sb = new StringBuilder();
		for (Attribute a : this) {
			sb.append(a.getName() + "\t");
		}
		return sb.toString();
		// TODO: Remove trailing tab
	}
	
}
















