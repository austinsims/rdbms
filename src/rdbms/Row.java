package rdbms;
import java.util.Arrays;

/**
 * A collection of values that fit a particular schema.  Add methods verify that the value fits in the proper place in the schema.
 */
public class Row {
	Attributes schema;
	Value[] values;
	int colPos = 0;
	
	public Row (Attributes schema) { 
		this.schema = schema;
		values = new Value[schema.size()];
	}
	
	public Row(Attributes schema, Value... argValues) throws SchemaViolationException {
		this.schema = schema;
		
		if (argValues.length != schema.size()) throw new SchemaViolationException("The values do not match the schema");
		
		this.values = new Value[schema.size()];
		for (int i=0; i < schema.size(); i++) {
			Attribute attr = schema.get(i);
			if (!attr.getType().equals(argValues[i].getType())) {
				throw new SchemaViolationException("The values do not match the schema");
			}
			if (attr.equals(Attribute.Type.CHAR)) {
				CharValue cv = (CharValue) argValues[i];
				if (cv.value.length() > attr.charLen)
					throw new SchemaViolationException("The values do not match the schema (the char value " + cv.value + " is too long");
			}
			this.values[i] = argValues[i];
		}
		
	}

	/**
	 * Copy constructor
	 * @throws SchemaViolationException 
	 */
	public Row(Row original) throws SchemaViolationException {
		this(original.schema, original.values);
	}

	public void set(int index, Value element) throws SchemaViolationException {
		// TODO Auto-generated method stub
		Attribute attr = schema.get(index);
		if (element.getType() != attr.getType())
			throw new SchemaViolationException("The value " + element + " does not match the attribute type at index " + index + ", which is " + element.getType());
		if (!attr.checkConstraints(element))
			throw new SchemaViolationException("The value " + element + "does not meet the domain constraints for " + attr + ": " + attr.constraints);
		values[index] = element;
	}
	
	/**
	 * Set a specfic attribute's value.
	 * @throws SchemaViolationException if this row's schema doesn't contain that attribute
	 */
	public void set(Attribute attr, Value value) throws SchemaViolationException {
		int index = schema.indexOf(attr);
		set(index, value);
	}
	
	public void set (Value[] values) throws SchemaViolationException {
		if (this.values.length != values.length)
			throw new SchemaViolationException("Incorrect number of values");
		for (int i=0; i<values.length; i++) {
			set(i,values[i]);
		}
	}
	
	public Value get(int index) throws IndexOutOfBoundsException {
		return values[index];
	}
	
	public Value get(Attribute attr) throws SchemaViolationException {
		if (!schema.contains(attr)) throw new SchemaViolationException(String.format("The schema of this row (%s) does not contain a value of type %s.", schema, attr));
		int index = schema.indexOf(attr);
		return values[index];
	}
	
	public Value[] get(Attributes attr) throws SchemaViolationException {
		Value[] result = new Value[attr.size()];
		int i = 0;
		for (Attribute a : attr) {
			result[i] = get(a);
			i++;
		}
		return result;
	}
	
	public int size() {
		return values.length;
	}
	
	/**
	 * Project only the selected attributes from this row
	 */
	public Row project(Attributes projectionSchema) throws SchemaViolationException {
		if (!schema.containsAll(projectionSchema)) throw new SchemaViolationException("The requested subschema is not a subset of the schema of this row");
		
		Row projection = new Row(projectionSchema);
		int i = 0;
		int j = 0;
		for (Attribute a : schema) {
			if (projectionSchema.contains(a)) {
				projection.set(j,values[i]);
				j++;
			}
			i++;
		}
		return projection;
	}
	
	@Override
	public boolean equals(Object other) {
		Row o;
		try {
			o = (Row) other;
		} catch (ClassCastException e) {
			return false;
		}
		
		if (!schema.equals(o.schema)) return false;
		
		for (int i=0; i<values.length; i++) {
			if (!values[i].equals(o.values[i])) return false;
		}
		
		return true;
	}
	
	public String tableRow() {
		StringBuilder sb = new StringBuilder();
		for (Value v : values) {
			switch (v.getType()) {
			case CHAR:
				sb.append(((CharValue) v).value);
				break;
			case DECIMAL:
				sb.append(((DecValue) v).value);
				break;
			case INT:
				sb.append(((IntValue) v).value);
				break;
			}
			sb.append("\t");
		}
		return sb.toString();
		// TODO: Remove trailing tab
	}
	
	public String toString() {
		return Arrays.toString(values);
	}
	
	public int hashCode() {
		int hashCode = 0;
		for (Value v : values)
			hashCode += v.hashCode();
		return hashCode;
	}

}
