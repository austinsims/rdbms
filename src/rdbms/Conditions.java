package rdbms;
import java.util.ArrayList;

import rdbms.Conditions.Mode;

public class Conditions extends ArrayList <Condition> {
	
	public static enum Mode {
		AND,
		OR
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 8951750080003785481L;
	private Mode mode;
	
	public Conditions() {
		// default to and
		this.mode = Mode.AND;
	}

	public boolean add(Attribute attr, Operator op, Value value) {
		return add(new ValueCondition(attr, op, value));
	}
	
	public boolean add(Attribute lhs, Operator op, Attribute rhs) {
		return add(new AttrCondition(lhs,op,rhs));
	}

	public boolean test(Row row) throws SchemaViolationException {
		switch (mode) {
		case AND:
			for (Condition cond : this) {
				if (!cond.test(row))
					return false;
			}
			return true;
		case OR:
			for (Condition cond : this) {
				if (cond.test(row)) return true;
			}
			return false;
		}
		return false;
	}

	public void setMode(Conditions.Mode mode) {
		this.mode = mode;		
	}	
}














