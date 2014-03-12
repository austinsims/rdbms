
public class Attribute {

	public enum Type {
		INT, 
		CHAR,
		DECIMAL
	}
	
	public static class Constraint {
		String condition;
		public Constraint(String condition) {
			this.condition = condition;
		}
		public boolean verify() {
			// TODO: implement verify method of Constraint, Attribute's inner class
			return true;
		}
	}
	
	String name;
	Type type;
	int charLen;
	Constraint constraint;
	
	/**
	 * Construct either an INT or a DECIMAL
	 * @param name
	 * @param type
	 */
	public Attribute(String name, Type type) {
		this.name = name;
		this.type = type;
	}
	
	
	public Attribute(String name, Type type, int charLen) throws InvalidAttributeException {
		if (type != Type.CHAR) throw new InvalidAttributeException("Only a CHAR attribute may have a charLen.");
		this.name = name;
		this.type = type;
		this.charLen = charLen;
	}
	
	public void setConstraint(Constraint c) {
		this.constraint = c;
	}
	
	public String toString() {
		if (type.equals(Type.CHAR))
			return String.format("%s char(%d)", name, charLen);
		else if (type.equals(Type.INT))
			return String.format("%s int");
		else
			return String.format("%s decimal", name);
	}
	
	public String getName() {
		return name;
	}
	
	public boolean equals(Attribute other) {
		if (this.type != other.type) return false;
		if (!(this.name.equals(other.name))) return false;
		if (this.type == Attribute.Type.CHAR && this.charLen != other.charLen) return false;
	
		return true;
	}
}
