package rdbms;

public class Constraint {
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
		return op + " " + referenceValue;
	}
}