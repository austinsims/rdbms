package rdbms;

import java.io.Serializable;

public abstract class Value implements Serializable {
	
	public abstract Attribute.Type getType();
	
	/**
	 * Superclass method that does type checking; subclasses must do value checking by overriding and calling this through super.equals(...) at some point. 
	 */
	public boolean equals(Value other) {
		return getType().equals(other.getType());
	}

	public abstract boolean greaterThan(Value other) throws SchemaViolationException;
	public abstract boolean lessThan(Value other) throws SchemaViolationException;
	
}
