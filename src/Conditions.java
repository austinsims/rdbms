import java.util.ArrayList;

public class Conditions extends ArrayList<Conditions.Condition> {
	
	public enum Operator {
		EQUAL,
		NOT_EQUAL,
		LESS,
		GREATER,
		LESS_OR_EQUAL,
		GREATER_OR_EQUAL
	}
	
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
			switch (cond.operator) {
			case EQUAL:
				pass = pass && rowValue.equals(cond.value);
				break;
			case GREATER:
				pass = pass && rowValue.greaterThan(cond.value);
				break;
			case GREATER_OR_EQUAL:
				pass = pass && rowValue.equals(cond.value) || rowValue.greaterThan(cond.value);
				break;
			case LESS:
				pass = pass && rowValue.lessThan(cond.value);
				break;
			case LESS_OR_EQUAL:
				pass = pass && rowValue.lessThan(cond.value) || rowValue.equals(cond.value);
				break;
			case NOT_EQUAL:
				pass = pass && !rowValue.equals(cond.value);
				break;
			}
		}
		return pass;
	}	
}














