package rdbms;

public class AttrCondition implements Condition {

	Attribute lhs;
	Operator op;
	Attribute rhs;

	public AttrCondition(Attribute lhs, Operator op, Attribute rhs) {
		this.lhs = lhs;
		this.rhs = rhs;
		this.op = op;
	}
	
	public boolean test(Row row) throws SchemaViolationException {
		Value lhsValue = row.get(lhs);
		Value rhsValue = row.get(rhs);
		if (!Operator.evaluateExpression(lhsValue, op, rhsValue))
			return false;
		return true;
	}

}
