package rdbms;
import java.util.LinkedHashSet;


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
	
}
