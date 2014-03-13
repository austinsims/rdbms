import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

// TODO: Verify all attribute names with verifyAttrNameOrDie

// Phase 0.1.  All (yes, all) tokens must be separated by spaces, including commas and parens.
public class SQLParser {
	
	private void validateAttrNameOrDie(String attr) throws InvalidSQLException {
		if (!Attribute.validName(attr))
			throw new InvalidSQLException(attr + " is not a valid attribute name");
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
				Attributes attributes = new Attributes();
				
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
				// "PRIMARY KEY" already swallowed by attribute loop. still need to swallow the open paren
				if (!tokens.next().equals("(")) throw new InvalidSQLException("PRIMARY KEY must be followed by parenthesized attribute list");
				boolean hasNextPkAttr = true;
				Attributes pk = new Attributes();
				while (hasNextPkAttr) {
					String pkAttrStr = tokens.next();
					if (!attributes.contains(pkAttrStr))
						throw new InvalidSQLException("The attribute list " + attributes + " does not contain the proposed PK attribute " + pkAttrStr);
					pk.add(attributes.get(pkAttrStr));
					
					switch (tokens.next()) {
					case ",":
						hasNextPkAttr = true;
						break;
					case ")":
						hasNextPkAttr = false;
						break;
					default:
						throw new InvalidSQLException("Attributes within a PRIMARY KEY attribute list must be separated by commas");
					}
					
				}
				
				// Create Table object
				Table newTable = new Table(tableName, attributes, pk);
				Table.insertIntoDB(newTable);
				
				// TODO:  Figure out why it isn't catching  FOREIGN for the next token
				System.out.println(tokens.next());
				
				// Optionally Parse FOREIGN KEY ( fk ) REFERENCES table ( attr ), ...
				if (tokens.hasNext("FOREIGN")) {
					
					boolean hasNextFK = true;
					while (hasNextFK) {
						// Swallow FOREIGN KEY
						tokens.next();
						if (!tokens.next().equals("KEY")) throw new InvalidSQLException("FOREIGN must be followed by KEY");
						String fkAttrName = tokens.next();
						if (!tokens.next().equals(")")) throw new InvalidSQLException("FOREIGN KEY must specify one attribute followed by a ')'");
						if (!tokens.next().equals("REFERENCES")) throw new InvalidSQLException("Invalid FOREIGN KEY expression");
						String foreignTable = tokens.next();
						if (!tokens.next().equals("(")) throw new InvalidSQLException("Invalid FOREIGN KEY expression");
						String foreignAttr  = tokens.next();
						
						try {
							newTable.addFK(fkAttrName, foreignTable, foreignAttr);
						} catch (IntegrityException e) {
							throw new InvalidSQLException("Got an integrity violation: " + e.getMessage());
						}
						
						String tok = tokens.next();
						switch (tok) {
						case "),":
							hasNextFK = true;
							break;
						case ")":
							hasNextFK = false;
							break;
						}

					}

				}
				
				String tok = tokens.next();
				if (!tok.equals(");")) throw new InvalidSQLException("Must close CREATE TABLE with ');'");
				
				System.out.println("Created new table successfully:");
				System.out.println(newTable);
				// TODO: Actually put the table into the database.
				
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
