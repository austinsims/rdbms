import java.util.HashSet;
import java.util.Set;


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
			return String.format("%s int", name);
		else
			return String.format("%s decimal", name);
	}
	
	public String getName() {
		return name;
	}
	
	public Type getType() {
		return type;
	}
	
	public static boolean validName(String attr) {
		Set<Character> alphaSet = new HashSet<Character>();
		for (Character c : "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_".toCharArray())
			alphaSet.add(c);
		
		Set<Character> numericSet = new HashSet<Character>();
		for (Character c : "1234567890".toCharArray())
			numericSet.add(c);
		
		Set<Character> alphanumericSet = new HashSet<Character>();
		alphanumericSet.addAll(alphaSet);
		alphanumericSet.addAll(numericSet);
		
		Set<Character> attrSet = new HashSet<Character>();
		for (Character c : attr.toCharArray())
			attrSet.add(c);
		
		return 
				attr.length() <= 256 && // length <= 256
				(alphanumericSet).containsAll(attrSet) && // is alphanumeric plus _
				alphaSet.contains(attr.charAt(0)); // first char is alphanumeric
	}
	
	public boolean equals(Attribute other) {
		if (this.type != other.type) return false;
		if (!(this.name.equals(other.name))) return false;
		if (this.type == Attribute.Type.CHAR && this.charLen != other.charLen) return false;
	
		return true;
	}
	
	// TODO: Constraints
	public boolean matches(Value v) {
		if (type != v.getType()) return false;
		if (type == Type.CHAR) {
			if (((CharValue) v).value.length() > charLen) return false;
		}
		return true;
	}
}
