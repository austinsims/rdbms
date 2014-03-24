package rdbms;
import java.util.LinkedList;


public class Arguments extends LinkedList<Arguments.Argument> {
	class Argument {
		Attribute attr;
		Value value;
		public Argument(Attribute attr, Value value) {
			this.attr = attr;
			this.value = value;
		}
	}
	
	public Value getValueOf(Attribute attr) throws SchemaViolationException {
		for (Argument a : this) {
			if (a.attr.equals(attr)) {
				return a.value;
			}
			throw new SchemaViolationException("There is no value for " + attr.getName() + " in these arguments " + this);
		}
		return null;
	}
	
	public boolean add(Attribute attr, Value val) {
		return add(new Argument(attr, val));
	}
}
