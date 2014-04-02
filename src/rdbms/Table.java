package rdbms;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Table implements Serializable {
	String name;
	private Attributes schema;
	private Attributes subschema;
	Attributes pk;
	ForeignKeys fks;
	Rows rows;

	public Table(String name, Attributes schema, Attributes pk) {
		this.name = name;
		this.schema = schema;
		if (!schema.containsAll(pk))
			throw new IllegalArgumentException("All elements of pk " + pk
					+ " must be contained in the attributes list " + schema + "");
		this.pk = pk;
		this.fks = new ForeignKeys();
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

	public boolean insert(Row newRow) throws RDBMSException {
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
			throw new SchemaViolationException("Error: Duplicate entry!");

		// Check foreign key constraints.
		for (ForeignKey fk : fks) {
			Conditions fkCond = new Conditions();
			Value domesticValue = newRow.get(fk.domesticAttribute);
			fkCond.add(fk.foreignAttribute, Operator.EQUAL, domesticValue);
			List<String> selectedAttributes = Arrays.asList(new String[] {fk.foreignAttribute.getName()});
			List<String> selectedTables = Arrays.asList(new String[] {fk.foreignTable.getName()});
			Rows results;
			try {
				results = Database.select(selectedAttributes, selectedTables, fkCond);
			} catch (RDBMSException e) {
				throw new RDBMSException("Error: Foreign key constraints violated!");
			}
			if (results.size() != 1) {
				throw new SchemaViolationException("Could not find strictly one tuple in " + fk.foreignTable.getName() + " that has a value " + domesticValue + " for " + fk.foreignAttribute);
			}
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

	public boolean insertAll(Row... all) throws RDBMSException {
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fks == null) ? 0 : fks.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((pk == null) ? 0 : pk.hashCode());
		result = prime * result + ((rows == null) ? 0 : rows.hashCode());
		result = prime * result + ((schema == null) ? 0 : schema.hashCode());
		result = prime * result + ((subschema == null) ? 0 : subschema.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Table))
			return false;
		Table other = (Table) obj;
		if (fks == null) {
			if (other.fks != null)
				return false;
		} else if (!fks.equals(other.fks))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (pk == null) {
			if (other.pk != null)
				return false;
		} else if (!pk.equals(other.pk))
			return false;
		if (rows == null) {
			if (other.rows != null)
				return false;
		} else if (!rows.equals(other.rows))
			return false;
		if (schema == null) {
			if (other.schema != null)
				return false;
		} else if (!schema.equals(other.schema))
			return false;
		if (subschema == null) {
			if (other.subschema != null)
				return false;
		} else if (!subschema.equals(other.subschema))
			return false;
		return true;
	}

	public String toString() {
		StringBuilder attrSB = new StringBuilder();
	
		// List attributes
		for (int i=0; i<visibleSchema().size(); i++) {
			Attribute a = visibleSchema().get(i);
			// Name
			attrSB.append(a.getName());
			
			// Type
			attrSB.append(" -- ");
			switch (a.getType()) {
			case CHAR:
				attrSB.append(String.format("CHAR(%d)", a.charLen));
				break;
			case DECIMAL:
				attrSB.append("DECIMAL");
				break;
			case INT:
				attrSB.append("INT");
			}
			
			// [Primary Key Component]
			if (pk.contains(a))
				attrSB.append(" -- PRIMARY KEY");
			
			// [Domain Constraints]
			if (a.constraints.size() > 0) {
				attrSB.append(" -- ");
				for (int j=0; j < a.constraints.size(); j++) {
					Constraint c = a.constraints.get(j);
					attrSB.append(a.getName() + " ");
					attrSB.append(c);
					if (j < a.constraints.size() - 1) {
						attrSB.append(" " + a.constraints.getOperator().toString()+ " ");
					}
				}
			}
			
			// [Foreign Key References]
			ForeignKey fk;
			if ((fk = fks.getByDomesticAttr(a)) != null) {
				attrSB.append(" -- ");
				attrSB.append(String.format("FOREIGN KEY REFERENCES %s (%s)", fk.foreignTable.getName(), fk.foreignAttribute.getName()));
			}
			
			if (i < visibleSchema().size() - 1)
				attrSB.append(System.getProperty("line.separator"));
		}
	
		return attrSB.toString();
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
