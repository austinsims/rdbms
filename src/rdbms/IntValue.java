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
		final int prime = 31;
		int result = 1;
		result = prime * result + value;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof IntValue))
			return false;
		IntValue other = (IntValue) obj;
		if (value != other.value)
			return false;
		return true;
	}
}
