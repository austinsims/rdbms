import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.sun.org.apache.xml.internal.utils.UnImplNode;


public class Table {
	String name;
	Attributes schema;
	Attributes pk;
	List<ForeignKey> fks;
	Rows rows;
	
	static Tables tables;
	
	static {
		tables = new Tables();
	}
	
	static void insertIntoDB(Table t) {
		tables.add(t);
	}
	
	static void dropEverything() {
		tables.clear();
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
	
	public Table(String name, Attributes schema, Attributes pk) {
		this.name = name;
		this.schema = schema;
		if (!schema.containsAll(pk))
			throw new IllegalArgumentException("All elements of pk " + pk + " must be contained in the attributes list " + schema + "");
		this.pk = pk;
		this.fks = new LinkedList<ForeignKey>();
		this.rows = new Rows(schema);
	}
	
	public Attributes getAttributes() {
		return schema;
	}
	
	public String getName() {
		return name;
	}
	
	// TODO: ...where to do all the integrity checking???? Here or somewhere else?
	public void addFK(String domesticAttr, String foreignTable, String foreignAttr) throws IntegrityException, SchemaViolationException {
		if (!schema.contains(domesticAttr)) throw new IntegrityException(String.format("The table %s does not contain an attribute named %s.", name, domesticAttr));
		if (!tables.contains(foreignTable)) throw new IntegrityException(String.format("The foreign table %s does not exist.", foreignTable));
		if (!tables.get(foreignTable).getAttributes().contains(foreignAttr))throw new IntegrityException(String.format("The foreign table %s does not contain the attribute %s.", foreignTable, foreignAttr));
		fks.add(new ForeignKey(domesticAttr, foreignTable, foreignAttr));
	}
	
	public String toString() {
		StringBuilder s = new StringBuilder();
		
		// List attributes
		s.append(String.format("%s ( ", name));
		for (Attribute a : schema) {
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

	public boolean insert(Row newRow) throws SchemaViolationException {
		// Get the new row's PK.
		Value[] newRowPKValues =  newRow.get(pk);		
		// If a row with that PK exists, throw exception
		Conditions cond = new Conditions();
		int i = 0;
		for (Attribute a : pk) {
			cond.add(a, Conditions.Operator.EQUAL, newRowPKValues[i]);
			i++;
		}
		if (rows.getAll(cond).size() > 0)
			throw new SchemaViolationException("This table already has a row with the PK = " + newRowPKValues);
		
		return rows.add(newRow);
	}

	public static Rows select(List<String> selectedAttributes, List<String> selectedTables, Conditions conditions) throws SchemaViolationException {
		if (selectedTables.size() > 1) throw new RuntimeException("Sorry, join is not yet implemented");
		
		// if no Conditions specified, create a new one with no conditions (always passes)
		if (conditions == null)
			conditions =  new Conditions();

		Table t = Table.tables.get(selectedTables.get(0));
		
		Attributes subschema = new Attributes();
		for (String attrName : selectedAttributes) {
			subschema.add(t.schema.get(attrName));
		}
		
		Rows result = t.rows.getAll(conditions).project(subschema);
		
		return result;
	}

	public boolean insertAll(Row... all) throws SchemaViolationException {
		boolean success = true;
		for (Row row : all) {
			success = success && insert(row);
		}
		return success;
	}
}





















