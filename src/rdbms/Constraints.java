package rdbms;

import java.util.ArrayList;

public class Constraints extends ArrayList<Constraint> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2288558564426955955L;

	enum Operator {
		AND, OR,
	}

	Operator operator;

	public void setOperator(Operator op) {
		this.operator = op;
	}
	
	public Constraints(Operator op) {
		this.operator = op;
	}

	public boolean check(Value v) throws SchemaViolationException {
		// TODO Auto-generated method stub
		switch (operator) {
		case AND: {
			boolean pass = true;
			for (Constraint constr : this) {
				pass = pass && constr.check(v);
			}
			return pass;
		}
		case OR: {
			boolean pass = false;
			for (Constraint constr : this) {
				pass = pass || constr.check(v);
			}
			return pass;
		}
		}
		return false;
	}
}
