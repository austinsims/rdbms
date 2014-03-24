package rdbms;
import java.util.ArrayList;

public class Conditions extends ArrayList<Conditions.Condition> {
	
	class Condition {
		Attribute attr;
		Operator operator;
		Value value;
		
		public Condition(Attribute attr, Operator op, Value val) {
			this.attr = attr;
			this.operator = op;
			this.value = val;
		}
		
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(attr.getName() + " ");
			switch (operator) {
			case EQUAL:
				sb.append("=");
				break;
			case GREATER:
				sb.append(">");
				break;
			case GREATER_OR_EQUAL:
				sb.append(">=");
				break;
			case LESS:
				sb.append("<");
				break;
			case LESS_OR_EQUAL:
				sb.append("<=");
				break;
			case NOT_EQUAL:
				sb.append("<>");
				break;
			}
			sb.append (" " + value);
			return sb.toString();
		}

	}
	
	public boolean add(Attribute attr, Operator op, Value value) {
		return add(new Condition(attr, op, value));
	}

	public boolean test(Row row) throws SchemaViolationException {
		boolean pass = true;
		for (Condition cond : this) {
			Value rowValue = row.get(cond.attr);
			pass = pass && Operator.evaluateExpression(rowValue, cond.operator, cond.value);
		}
		return pass;
	}	
}














