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
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(value);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof DecValue))
			return false;
		DecValue other = (DecValue) obj;
		if (Double.doubleToLongBits(value) != Double.doubleToLongBits(other.value))
			return false;
		return true;
	}
}
