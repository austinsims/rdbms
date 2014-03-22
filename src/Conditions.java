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
		Operator op;
		
		public Condition(Attribute attr, Operator op) {
			this.attr = attr;
			this.op = op;
		}
		
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(attr.getName() + " ");
			switch (op) {
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
			return sb.toString();
		}

	}
	
	public boolean add(Attribute attr, Operator op) {
		return add(new Condition(attr, op));
	}

	public boolean test(Row row, Arguments args) throws SchemaViolationException {
		boolean pass = true;
		for (Condition cond : this) {
			Value rowValue = row.get(cond.attr);
			Value argValue = args.getValueOf(cond.attr);
			switch (cond.op) {
			case EQUAL:
				pass = pass && rowValue.equals(argValue);
				break;
			case GREATER:
				pass = pass && rowValue.greaterThan(argValue);
				break;
			case GREATER_OR_EQUAL:
				pass = pass && rowValue.equals(argValue) || rowValue.greaterThan(argValue);
				break;
			case LESS:
				pass = pass && rowValue.lessThan(argValue);
				break;
			case LESS_OR_EQUAL:
				pass = pass && rowValue.lessThan(argValue) || rowValue.equals(argValue);
				break;
			case NOT_EQUAL:
				pass = pass && !rowValue.equals(argValue);
				break;
			}
		}
		return pass;
	}	
}














