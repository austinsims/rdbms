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
	
	private static String pad(String s, String ch) {
        return s.replaceAll("(?x) " + 
         String.format("%s         ",ch) +   // Replace ch
		               "(?=        " +   // Followed by
		               "  (?:      " +   // Start a non-capture group
		               "    [^']*  " +   // 0 or more non-single quote characters
		               "    '      " +   // 1 single quote
		               "    [^']*  " +   // 0 or more non-single quote characters
		               "    '      " +   // 1 single quote
		               "  )*       " +   // 0 or more repetition of non-capture group (multiple of 2 quotes will be even)
		               "  [^']*    " +   // Finally 0 or more non-single quotes
		               "  $        " +   // Till the end  (This is necessary, else every _ will satisfy the condition)
		               ")          " ,   // End look-ahead
         String.format(" %s ",ch));      // Replace with " , "

	}
	
	public static int parse(String statement) throws InvalidSQLException {
		/*
		// Pad all commas and parens that aren't in single quotes with spaces so that they are individual tokens.
		statement = pad(statement, ",");
		statement = pad(statement, "\\(");
		statement = pad(statement, "\\)");
		*/
		
		Scanner tokens = new Scanner(statement);
		String command = tokens.next();
		boolean expectComma = true;
		
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
				
				// Make sure a table by that name does not already exist
				if (Table.tables.contains(tableName))
					throw new InvalidSQLException("A table named " + tableName + " already exists.");
				
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

				// TODO: Error out if the PK is not specified
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
					
					String tok = tokens.next();
					switch (tok) {
					case ",":
						hasNextPkAttr = true;
						break;
					case ")":
						hasNextPkAttr = false;
						expectComma = true;
						break;
					case "),":
						hasNextPkAttr = false;
						expectComma = false;
						break;
					default:
						throw new InvalidSQLException("Attributes within a PRIMARY KEY attribute list must be separated by commas");
					}
					
				}
				
				// Create Table object
				Table newTable = new Table(tableName, attributes, pk);
				Table.insertIntoDB(newTable);
				
				// TODO:  Figure out why it isn't catching  FOREIGN for the next token
				
				// Optionally Parse ', FOREIGN KEY' ( fk ) REFERENCES table ( attr ), ...
				String expectedNextToken = expectComma ? "," : "FOREIGN";
				if (tokens.hasNext(expectedNextToken)) {
					// Swallow FOREIGN KEY and the comma if it is expected
					if (expectComma) tokens.next(); // swallow comma
					
					if (!tokens.next().equals("FOREIGN")) throw new InvalidSQLException("FOREIGN must be followed by KEY");
					if (!tokens.next().equals("KEY")) throw new InvalidSQLException("FOREIGN must be followed by KEY");
					if (!tokens.next().equals("(")) throw new InvalidSQLException("FOREIGN must be followed by KEY");
					
					boolean hasNextFK = true;
					while (hasNextFK) {
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
				break;
			case "DROP":
				if (!tokens.next().equals("TABLE")) throw new InvalidSQLException("DROP must be followed by TABLE");
				String tableToDrop = tokens.next();
				if (tableToDrop.charAt(tableToDrop.length() - 1) != ';') 
					throw new InvalidSQLException("Missing semicolon");
				tableToDrop = tableToDrop.substring(0, tableToDrop.length() - 1);
				if (!Table.tables.contains(tableToDrop))
					throw new InvalidSQLException("Table " + tableToDrop + " does not exist");
				Table.tables.remove(Table.tables.get(tableToDrop));
				System.out.println("Table dropped successfully");
				break;
			case "SELECT":
				// Parse attribute list
				boolean hasNextAttr = true;
				while (hasNextAttr) {
					String attr = tokens.next();
					// check if attribute exists
					
				}
				// swallow FROM
				
				// parse table list
				
				// swallow optional WHERE
				
				// parse optional condition list
				
				break;
			case "INSERT":
				if (!tokens.next().equals("INTO"))
					throw new InvalidSQLException("INSERT must be followed by INTO");
				String tableToInsertName = tokens.next();
				if (!Table.tables.contains(tableToInsertName))
					throw new InvalidSQLException("Table " + tableToInsertName + " does not exist");
				if (!tokens.next().equals("VALUES"))
					throw new InvalidSQLException("Table name must be followed by VALUES");
				if (!tokens.next().equals("("))
					throw new InvalidSQLException("Missing open paren");
				
				Table tableToInsert = Table.tables.get(tableToInsertName);
				Attributes schema = tableToInsert.getAttributes();
				Row newRow = new Row(schema);
				int colNum = 0;
				boolean hasNextValue = true;
				while (hasNextValue) {
					String tk = tokens.next();
					switch (tk) {
					case ");":
						hasNextValue = false;
						break;
					default:
						hasNextValue = true;
						String value = tk;
						Attribute attr;
						try {
							attr = schema.get(colNum);
						} catch (IndexOutOfBoundsException e) {
							throw new InvalidSQLException("You have specified too many values for this table");
						}
						switch (attr.getType()) {
						case INT:
							int intValue;
							try {
								intValue = Integer.valueOf(value);
							} catch (NumberFormatException e) {
								throw new InvalidSQLException("The value " + value + " cannot be interpreted as an integer");
							}
							newRow.set(colNum, new IntValue(intValue));
							break;
						case DECIMAL:
							double decValue;
							try {
								decValue = Double.valueOf(value);
							} catch (NumberFormatException e) {
								throw new InvalidSQLException("The value " + value + " cannot be interpreted as a decimal");
							}
							newRow.set(colNum, new DecValue(decValue));
							break;
						case CHAR:
							if (value.charAt(0) != '\'' || value.charAt(value.length() - 1) != '\'')
								throw new InvalidSQLException("String value not enclosed in single quotes");
							String charValue = value.substring(1,value.length()-1);
							if (charValue.length() > attr.charLen)
								throw new InvalidSQLException("The value '" + charValue + "' is too long for the attribute + " + attr.getName());
							newRow.set(colNum, new CharValue(charValue));
							break;
						}
						break;
					}
					colNum++;
				} // while (hasNextValue)
				
				tableToInsert.insert(newRow);
				
				if (!tokens.next().equals(")"))
					throw new InvalidSQLException("Missing close paren");
				
				break;
			case "DELETE":
				
				break;
			case "UPDATE":
				
				break;
			case "HELP":
				
				break;
			case "QUIT;":
				// TODO: Make sure all changes are committed and all resources are closed
				System.exit(0);
				break;
			default:
				System.err.println("Error, that is not a valid SQL command.");
				break;
			} // switch
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
