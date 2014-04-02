package rdbms;

import java.io.Serializable;

public class Constraint implements Serializable {
	Operator op;
	Value referenceValue;
	
	public Constraint(Operator op, Value value) {
		this.op = op;
		this.referenceValue = value;
	}
	
	public boolean check(Value v) throws SchemaViolationException {
		return Operator.evaluateExpression(v, op, referenceValue);
	}
	
	public String toString() {
		switch (referenceValue.getType()) {
		case CHAR:
			return String.format("%s '%s'", op, referenceValue);
		default:
			return op + " " + referenceValue;
		}
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		
		if ( !(o instanceof Constraint) ) return false;
		
		Constraint that = (Constraint) o;
		
		if (!this.op.equals(that.op)) return false;
		if (!this.referenceValue.equals(that.referenceValue)) return false;
		
		return true;
	}
	
	@Override
	public int hashCode() {
		int result = HashCodeUtil.SEED;
		result = HashCodeUtil.hash(result, op);
		result = HashCodeUtil.hash(result, referenceValue);
		return result;
	}
}