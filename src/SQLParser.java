import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

// TODO: Verify all attribute names with verifyAttrNameOrDie

// Phase 0.1.  All (yes, all) tokens must be separated by spaces, including commas and parens.
public class SQLParser {
	public static boolean validAttrName(String attr) {
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
	
	private void validateAttrNameOrDie(String attr) throws InvalidSQLException {
		if (!validAttrName(attr))
			throw new InvalidSQLException(attr + " is not a valid attribute name");
	}
	
	private Attribute parseAttribute(Scanner in) {
		
	}
	
	public static int parse(String statement) throws InvalidSQLException {
		Scanner tokens = new Scanner(statement);
		String command = tokens.next();
		try {
			switch (command) {
			case "CREATE":
				if (!tokens.next().equals("TABLE"))
					throw new InvalidSQLException("CREATE must be followed by TABLE.");
				
				// Initialize what we need to make a new table
				List<Attribute> attributes = new LinkedList<Attribute>();
				
				// Parse name
				String tableName = tokens.next();
				if (!tokens.next().equals("("))
					throw new InvalidSQLException("Table name must be followed by (");
				
				// Begin parsing attributes of form: name type [CHECK ( constraint )]
				
				boolean hasNextAttribute = true;
				while (hasNextAttribute) {
					// Initialize to null; only needed if there is a constraint in the statement
					String constraint = null;
					
					// Only needed if there is a char type attribute in the statement
					Integer charLen = null;
					
					String attrName = tokens.next();
					if (attrName.equals("PRIMARY")) {
						// Whoops, we're past the attribute list. Now we're at FOREIGN KEY ...
						if (!tokens.next().equals("KEY"))
							throw new InvalidSQLException("PRIMARY must be followed by KEY");
						hasNextAttribute = false;
						break;
					}
					
					String attrTypeStr = tokens.next();
					Attribute.Type attrType;
					if (attrTypeStr.equals("int")) {
						attrType = Attribute.Type.INT;
					} else if (attrTypeStr.equals("decimal")) {
						attrType = Attribute.Type.DECIMAL;
					} else if (attrTypeStr.startsWith("char(")) {
						try {
							charLen = Integer.valueOf(attrTypeStr.substring(attrTypeStr.indexOf('(')+1, attrTypeStr.length()-1));
						} catch (NumberFormatException e) {
							throw new InvalidSQLException("Invalid attribute type: " + attrTypeStr);
						}
						attrType = Attribute.Type.CHAR;
						
					} else {
						throw new InvalidSQLException("Invalid attribute type: " + attrTypeStr);
					}
					
					// Detect either comma for next attr, or constraint, or close paren to end attr list
					String tok = tokens.next();
					switch (tok) {
					case ",":
						hasNextAttribute = true;
						break;
					case ")":
					case ");":
						hasNextAttribute = false;
						break;
					case "CHECK":
						// Begin parsing constraint of form: ( constraint )
						if (!tokens.next().equals("(")) throw new InvalidSQLException("Invalid argument list");
						constraint = tokens.next();
						if (!tokens.next().equals(")")) throw new InvalidSQLException("Invalid argument list");
						break;
					default:
						throw new InvalidSQLException("Invalid argument list");
					}
					
					Attribute newAttr = null;
					// Create new attribute object
					try {
						if (attrType.equals(Attribute.Type.CHAR)) {
							newAttr = new Attribute(attrName, attrType, charLen);
						} else {
							newAttr = new Attribute(attrName, attrType);
						}
					} catch (InvalidAttributeException e) {
						throw new InvalidSQLException("Invalid SQL: " + e.getMessage());
					}
					
					if (constraint != null) {
						newAttr.setConstraint(new Attribute.Constraint(constraint));
					}
					attributes.add(newAttr);
					
				}
				
				// Parse PRIMARY KEY ( attr1, attr2, ... )
				// "PRIMARY KEY" already swallowed by attribute loop
				boolean hasNextPkAttr = true;
				Attributes pk = new Attributes();
				while (hasNextPkAttr) {
					String pkAttrStr = tokens.next();
					if (pk.contains(pkAttrStr)) throw new InvalidSQLException("The table already contains an attribute with the name " + pkAttrStr);
					String pkAttrTypeStr = tokens.next();
					
					
					Attribute pkAttr = new Attribute(pkAttrStr, pkAttrType);
					
					switch (tokens.next()) {
					case ",":
						hasNextPkAttr = true;
						break;
					case ")":
						hasNextPkAttr = false;
						break;
					}
					
				}
				
				// Parse FOREIGN KEY ( fk ) REFERENCES table ( attr ), ... 
				
				// Create Table object
				Table newTable = new Table(tableName, attributes, pk);
				System.out.println("Created new table successfully:");
				System.out.println(newTable);
				
			}
		} catch (InvalidSQLException e) {
			tokens.close();
			System.err.println(e.getMessage());
			throw e;
		} finally {
			tokens.close();
		}
		System.out.println("SQL executed successfully");
		return 0;
		
	}
}
