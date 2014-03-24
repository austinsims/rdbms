package rdbms;
public class DecValue extends Value {
	double value;

	public DecValue(double value) {
		this.value = value;
	}

	public Attribute.Type getType() {
		return Attribute.Type.DECIMAL;
	}

	@Override
	public boolean equals(Value other) {
		if (!super.equals(other)) return false;
		DecValue o = (DecValue) other;

		return o.value == value;
	}
	
	public String toString() {
		return String.valueOf(value);
	}
	
	@Override
	public boolean greaterThan(Value other) throws SchemaViolationException {
		if (!super.equals(other)) throw new SchemaViolationException("Cannot compare values of different types");
		return value > ((DecValue) other).value;
	}

	@Override
	public boolean lessThan(Value other) throws SchemaViolationException{
		if (!super.equals(other)) throw new SchemaViolationException("Cannot compare values of different types");
		return value < ((DecValue) other).value;
	}
	
	@Override
	public int hashCode() {
		return Double.valueOf(value).hashCode();
	}
}
