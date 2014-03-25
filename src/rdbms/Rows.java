package rdbms;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;


public class Rows extends LinkedHashSet<Row> {
	Attributes schema;
	
	public Rows(Attributes schema) {
		this.schema = schema;
	}

	@Override
	/**
	 * Only add if the row fits the schema of this collection
	 */
	public boolean add(Row row) {
		// TODO Auto-generated method stub
		if (row.schema.equals(schema)) {
			return super.add(row);
		} else {
			return false;
		}
	}
	
	/**
	 * Get all rows that satisfy the condition.
	 * @throws SchemaViolationException if the condition cannot be satisfied by the schema of these Rows.
	 */
	public Rows getAll(Conditions conditions) throws SchemaViolationException {
		Rows result = new Rows(schema);
		for (Row r : this) {
			if (conditions.test(r))
				result.add(r);
		}
		return result;
	}
	
	public Rows project(Attributes subschema) throws SchemaViolationException {
		Rows projection = new Rows(subschema);
		for (Row r : this) {
			projection.add(r.project(subschema));
		}
		return projection;
	}
	
	public Rows cross(Rows other) throws SchemaViolationException {
		if (schema.containsASameNameAttr(other.schema))
			throw new SchemaViolationException("You may not join tables that contain attributes of the same name.");
		
		Attributes crossSchema = new Attributes();
		crossSchema.addAll(this.schema);
		
		crossSchema.addAll(other.schema);
		Rows crossProduct = new Rows(crossSchema);
		for (Row myRow : this) {
			for (Row otherRow : other) {
				Row crossRow = new Row(crossSchema);
				// Set my attributes
				for (Attribute myAttr : this.schema) {
					crossRow.set(myAttr, myRow.get(myAttr));
				}
				// Set other attributes
				for (Attribute otherAttr : other.schema) {
					crossRow.set(otherAttr, otherRow.get(otherAttr));
				}
				crossProduct.add(crossRow);
			}
		}
		
		return crossProduct;
	}

}
