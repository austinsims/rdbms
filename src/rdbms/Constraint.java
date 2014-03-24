package rdbms;

public class Constraint {
	Operator op;
	Value value;
	
	public Constraint(Operator op, Value value) {
		this.op = op;
		this.value = value;
	}
	
	public boolean check(Value v) {
		// TODO Auto-generated method stub
		return false;
	}
}