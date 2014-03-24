package rdbms;

public class CharValue extends Value {
	String value;
	
	public CharValue(String value) {
		this.value = value;
	}
	
	public Attribute.Type getType() {
		return Attribute.Type.CHAR;
	}
	
	@Override
	public boolean equals(Value other) {
		if (!super.equals(other)) return false;
		CharValue o = (CharValue) other;
		
		return o.value.equals(value);
	}
	
	public String toString() {
		return value;
	}
	
	@Override
	public boolean greaterThan(Value other) throws SchemaViolationException {
		if (!super.equals(other)) throw new SchemaViolationException("Cannot compare values of different types");
		return value.charAt(0) > ((CharValue) other).value.charAt(0);
	}

	@Override
	public boolean lessThan(Value other) throws SchemaViolationException{
		if (!super.equals(other)) throw new SchemaViolationException("Cannot compare values of different types");
		return value.charAt(0) < ((CharValue) other).value.charAt(0);
	}
	
	@Override
	public int hashCode() {
		return value.hashCode();
	}
}
