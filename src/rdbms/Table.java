package rdbms;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Table implements Serializable {
	String name;
	private Attributes schema;
	private Attributes subschema;
	Attributes pk;
	List<ForeignKey> fks;
	Rows rows;

	static void insertIntoDB(Table t) {
		Database.tables.add(t);
	}

	static void load() {

	}

	public static void dropEverything() {
		Database.tables.clear();
	}

	class ForeignKey implements Serializable {
		Attribute domesticAttribute;
		Table foreignTable;
		Attribute foreignAttribute;

		ForeignKey(Attribute domesticAttribute, Table foreignTable, Attribute foreignAttribute) {
			this.domesticAttribute = domesticAttribute;
			this.foreignTable = foreignTable;
			this.foreignAttribute = foreignAttribute;
		}
	}

	public Table(String name, Attributes schema, Attributes pk) {
		this.name = name;
		this.schema = schema;
		if (!schema.containsAll(pk))
			throw new IllegalArgumentException("All elements of pk " + pk
					+ " must be contained in the attributes list " + schema + "");
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

	public void addFK(String domesticAttrName, String foreignTableName, String foreignAttrName) throws IntegrityException,
			SchemaViolationException {
		
		Attribute domesticAttribute = schema.get(domesticAttrName);
		Table foreignTable = Database.tables.get(foreignTableName);
		Attribute foreignAttribute = foreignTable.schema.get(foreignAttrName);
		
		fks.add(new ForeignKey(domesticAttribute, foreignTable, foreignAttribute));

	}

	public boolean insert(Row newRow) throws SchemaViolationException, PermissionException {
		if (Database.getLoggedInUser().getType().equals(User.Type.B))
			throw new PermissionException("Sorry, users of type User-B may not insert tuples");
		
		// Get the new row's PK.
		Value[] newRowPKValues = newRow.get(pk);
		// If a row with that PK exists, throw exception
		Conditions cond = new Conditions();
		int i = 0;
		for (Attribute a : pk) {
			cond.add(a, Operator.EQUAL, newRowPKValues[i]);
			i++;
		}
		if (rows.getAll(cond).size() > 0)
			throw new SchemaViolationException("This table already has a row with the PK = " + newRowPKValues);

		// Check foreign key constraints.
		// TODO: Question: Since there is no UNIQUE in our Mini-SQL, should we check to make sure that
		//       the value of the foreign key attribute references only one tuple in the foreign table?
		for (ForeignKey fk : fks) {
			
		}
		
		return rows.add(newRow);
	}
	
	public void setSubschema(Attributes subschema) throws SchemaViolationException, PermissionException {
		if (!Database.getLoggedInUser().getType().equals(User.Type.ADMIN))
			throw new PermissionException("Sorry, you must be admin to set a subschema.");
		
		for (Attribute subattr : subschema) {
			if (!schema.contains(subattr))
				throw new SchemaViolationException("The attribute " + subattr + " specified in the subschema does not exist in the overall schema.");
		}
		this.subschema = subschema;
	}

	public boolean insertAll(Row... all) throws SchemaViolationException, PermissionException {
		boolean success = true;
		for (Row row : all) {
			success = success && insert(row);
		}
		return success;
	}

	public void save(File file) throws IOException {
		FileOutputStream fileOut = null;
		ObjectOutputStream out = null;
		try {

			fileOut = new FileOutputStream(file);
			out = new ObjectOutputStream(fileOut);
			out.writeObject(this);
		} catch (IOException e) {
			throw e;
		} finally {
			fileOut.close();
			out.close();
		}
	}
	
	public static Table load(File file) throws IOException, ClassNotFoundException {
		FileInputStream fileIn = new FileInputStream(file);
		ObjectInputStream in = new ObjectInputStream(fileIn);
		Table table = (Table) in.readObject();
		fileIn.close();
		in.close();
		return table;
		
	}

	public boolean equals(Object o) {
		Table other;
		try {
			other = (Table) o;
		} catch (ClassCastException e) {
			return false;
		}

		if (!this.name.equals(other.name))
			return false;
		if (!this.schema.equals(other.schema))
			return false;
		if (!this.pk.equals(other.pk))
			return false;
		if (!this.fks.equals(other.fks))
			return false;
		if (!this.rows.equals(other.rows))
			return false;

		return true;
	}

	/**
	 * Drop table and delete its file stored on disk
	 * @param table
	 */
	public static boolean drop(Table table) {
		// TODO: search for tables that have foreign key references to this one; if any, do not delete and issue an error!
		File tableFile = new File(table.getName() + ".table");
		boolean success = tableFile.delete();
		Database.tables.remove(table);
		return success;
	}

	public String toString() {
		StringBuilder s = new StringBuilder();
	
		// List attributes
		s.append(String.format("%s ( ", name));
		for (Attribute a : visibleSchema()) {
			s.append(a.toString() + ", ");
		}
	
		// Show primary key
		s.append("PRIMARY KEY (");
		for (Attribute a : pk)
			s.append(a.getName() + ", ");
		s.append(")");
	
		s.append(" )");
		
		// Show foreign key constraints
		for (ForeignKey fk : this.fks ) {
			s.append(String.format(" FOREIGN KEY %s REFERENCES %s (%s) ", fk.domesticAttribute.getName(), fk.foreignTable.getName(), fk.foreignAttribute.getName()));
		}
	
		return s.toString();
	}

	// Get the subschema if there is one, or just return the schema if none is set
	public Attributes visibleSchema() {
		User loggedInUser = Database.getLoggedInUser();
		if (subschema != null && loggedInUser.getType().equals(User.Type.B))
			return subschema;
		else
			return schema;
	}
}
