package rdbms;

public enum Operator {
	EQUAL("="), NOT_EQUAL("!="), LESS("<"), GREATER(">"), LESS_OR_EQUAL("<="), GREATER_OR_EQUAL(">=");

	private String sqlRepresentation;

	Operator(String rep) {
		this.sqlRepresentation = rep;
	}

	public String toString() {
		return sqlRepresentation;
	}

	public static Operator fromString(String s) {
		if (s != null) {
			for (Operator o : Operator.values()) {
				if (s.equalsIgnoreCase(o.sqlRepresentation)) {
					return o;
				}
			}
		}
		return null;
	}

	public static boolean evaluateExpression(Value lhs, Operator op, Value rhs) throws SchemaViolationException {
		switch (op) {
		case EQUAL:
			return lhs.equals(rhs);
		case GREATER:
			return lhs.greaterThan(rhs);
		case GREATER_OR_EQUAL:
			return lhs.greaterThan(rhs) || lhs.equals(rhs);
		case LESS:
			return lhs.lessThan(rhs);
		case LESS_OR_EQUAL:
			return lhs.lessThan(rhs) || lhs.equals(rhs);
		case NOT_EQUAL:
			return !lhs.equals(rhs);
		default:
			return false;
		}
	}

}
