package rdbms;

public class IntValue extends Value {
	int value;
	
	public IntValue(int value) {
		this.value = value;
	}
	
	public Attribute.Type getType() {
		return Attribute.Type.INT;
	}
	
	@Override
	public boolean equals(Value other) {
		if (!super.equals(other)) return false;
		IntValue o = (IntValue) other;
		
		return o.value == value;
	}
	
	public String toString() {
		return String.valueOf(value);
	}

	@Override
	public boolean greaterThan(Value other) throws SchemaViolationException {
		if (!super.equals(other)) throw new SchemaViolationException("Cannot compare values of different types");
		return value > ((IntValue) other).value;
	}

	@Override
	public boolean lessThan(Value other) throws SchemaViolationException{
		if (!super.equals(other)) throw new SchemaViolationException("Cannot compare values of different types");
		return value < ((IntValue) other).value;
	}
	
	@Override
	public int hashCode() {
		return Integer.valueOf(value).hashCode();
	}
}
