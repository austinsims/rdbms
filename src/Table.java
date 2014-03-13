import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


public class Table {
	String name;
	Attributes attributes;
	Attributes pk;
	List<ForeignKey> fks;
	
	static Tables tables;
	
	static {
		tables = new Tables();
	}
	
	static void insertIntoDB(Table t) {
		tables.add(t);
	}

	
	class ForeignKey {
		String domesticAttribute;
		String foreignTable;
		String foreignAttribute;
		
		ForeignKey(String domesticAttribute, String foreignTable, String foreignAttribute) {
			this.domesticAttribute = domesticAttribute;
			this.foreignTable = foreignTable;
			this.foreignAttribute = foreignAttribute;
		}
	}
	
	public Table(String name, Attributes attributes, Attributes pk) {
		this.name = name;
		this.attributes = attributes;
		if (!attributes.containsAll(pk))
			throw new IllegalArgumentException("All elements of pk " + pk + " must be contained in the attributes list " + attributes + "");
		this.pk = pk;
		this.fks = new LinkedList<ForeignKey>();
	}
	
	public Attributes getAttributes() {
		return attributes;
	}
	
	public String getName() {
		return name;
	}
	
	// TODO: ...where to do all the integrity checking???? Here or somewhere else?
	public void addFK(String domesticAttr, String foreignTable, String foreignAttr) throws IntegrityException {
		if (!attributes.contains(domesticAttr)) throw new IntegrityException(String.format("The table %s does not contain an attribute named %s.", name, domesticAttr));
		if (!tables.contains(foreignTable)) throw new IntegrityException(String.format("The foreign table %s does not exist.", foreignTable));
		if (!tables.get(foreignTable).getAttributes().contains(foreignAttr)) throw new IntegrityException(String.format("The foreign table %s does not contain the attribute %s.", foreignTable, foreignAttr));
		
		fks.add(new ForeignKey(domesticAttr, foreignTable, foreignAttr));
	}
	
	public String toString() {
		StringBuilder s = new StringBuilder();
		
		// List attributes
		s.append(String.format("%s ( ", name));
		for (Attribute a : attributes) {
			s.append(a.toString() + ", ");
		}
		
		// Show primary key
		s.append("PRIMARY KEY (");
		for (Attribute a : pk)
			s.append(a.getName() + ", ");
		s.append(")");
		
		s.append(" )");
		
		return s.toString();
	}
}
