import java.util.List;


public class Table {
	String name;
	Attributes attributes;
	Attributes pk;
	
	public Table(String name, Attributes attributes, Attributes pk) {
		this.name = name;
		this.attributes = attributes;
		if (!attributes.containsAll(pk))
			throw new IllegalArgumentException("All elements of pk " + pk + " must be contained in the attributes list " + attributes + "");
		this.pk = pk;
	}
	
	public Attributes getAttributes() {
		return attributes;
	}
	
	public String toString() {
		StringBuilder s = new StringBuilder();
		
		s.append(String.format("%s ( ", name));
		for (Attribute a : attributes) {
			s.append(a.toString() + ", ");
		}
		s.append(" )");
		
		return s.toString();
	}
}
