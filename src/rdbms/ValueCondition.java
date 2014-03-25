package rdbms;


public class ValueCondition implements Condition {
	Attribute attr;
	Operator operator;
	Value referenceValue;

	public ValueCondition(Attribute attr, Operator op, Value val) {
		this.attr = attr;
		this.operator = op;
		this.referenceValue = val;
	}
	
	public String toString() {
		return attr.getName() + " " + operator.toString() + " " + referenceValue;
	}
	
	public boolean test(Row row) throws SchemaViolationException {
		Value rowValue = row.get(attr);
		return Operator.evaluateExpression(rowValue, operator, referenceValue);
	}
}
