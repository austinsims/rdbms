package rdbms;
import java.util.ArrayList;

public class Conditions extends ArrayList <Condition> {
	
	public boolean add(Attribute attr, Operator op, Value value) {
		return add(new ValueCondition(attr, op, value));
	}
	
	public boolean add(Attribute lhs, Operator op, Attribute rhs) {
		return add(new AttrCondition(lhs,op,rhs));
	}

	public boolean test(Row row) throws SchemaViolationException {
		for (Condition cond : this) {
			if (!cond.test(row))
				return false;
		}
		return true;
	}	
}














