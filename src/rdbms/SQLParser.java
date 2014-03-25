package rdbms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;

// TODO: Verify all attribute names with verifyAttrNameOrDie

// Phase 0.1.  All (yes, all) tokens must be separated by spaces, including commas and parens.
public class SQLParser {

	private final static String HELP_CREATE = "This command has the form:\n" + "\n"
			+ "CREATE TABLE table_name ( attribute_1 attribute1_type CHECK (constraint1),\n"
			+ "attribute_2 attribute2_type, �, PRIMARY KEY ( attribute_1, attribute_2 ), FOREIGN\n"
			+ "KEY ( attribute_y ) REFERENCES table_x ( attribute_t ), FOREIGN KEY\n" + "( attribute_w ) REFERENCES table_y ( attribute_z )... );\n" + "\n"
			+ "The �CREATE TABLE� token is followed by any number of attribute\n" + "name � attribute type pairs separated by commas. Each attribute\n"
			+ "name � attribute type pair can optionally be followed by a\n" + "constraint specified using the keyword �CHECK� followed by a\n"
			+ "domain constraint in one of the forms specified in Table 3,\n" + "enclosed in parentheses. This is followed by the token �PRIMARY\n"
			+ "KEY� and a list of attribute names separated by commas, enclosed\n" + "in parentheses. Note that the specification of the primary key\n"
			+ "constraint is mandatory in this project and will always follow\n" + "the listing of attributes. After the primary key constraint, the\n"
			+ "command should accept an optional list of foreign key constraints\n" + "specified with the token �FOREIGN KEY� followed by an attribute\n"
			+ "name enclosed in parentheses, followed by the keyword\n" + "�REFERENCES�, a table name and an attribute name enclosed in\n"
			+ "parentheses.  Multiple foreign key constraints are separated by\n" + "commas.\n";

	private final static String HELP_DROP = "This command has the form:\n" + "\n" + "DROP TABLE table_name;\n" + "\n"
			+ "The �DROP TABLE� token is followed by a table name.\n";

	private final static String HELP_SELECT = "This command has the form:\n" + "\n" + "SELECT attribute_list FROM table_list WHERE condition_list;\n" + "\n"
			+ "The token �SELECT� is followed by an attribute list, followed by the token �FROM�\n"
			+ "and a table name list. This is followed by an optional �WHERE� keyword and\n"
			+ "condition list. For simplicity, you are only asked to implement an attribute list\n"
			+ "consisting of attribute names separated by commas and not using the dot notation,\n"
			+ "in addition to �*�, which stands for all attributes. You can also assume that no\n"
			+ "attributes of different tables will have the same name. The table list will also be a\n"
			+ "simple list of table names separated by commas. The condition list has the following\n" + "format:\n" + "\n" + "attribute1 operator value1\n"
			+ "\n" + "OR\n" + "\n" + "attribute1 operator value1 AND/OR attribute2 operator value2 AND/OR attribute3 operator value3�\n" + "\n"
			+ "The operator can be any of �=�, �!=�, �<�, �>�, �<=�, �>=�.\n" + "\n" + "If there are multiple conjunction/disjunction operators in the\n"
			+ "predicate, they must all be the same operator (i.e. there can not\n" + "be AND and OR operators mixed in the same condition). Hence, the\n"
			+ "conditions do not need to be enclosed in parentheses. The values\n" + "in the conditions can either be a constant value or the name of\n"
			+ "another attribute.\n";

	private static final String HELP_INSERT = "This command has the form:\n" + "\n" + "INSERT INTO table_name VALUES ( val1, val2, � );\n" + "\n"
			+ "The �INSERT INTO� token is followed by a table name, followed by\n" + "the token �VALUES� and a list of values separated by commas\n"
			+ "enclosed in parentheses. Each value should be either a\n" + "number (integer or decimal) or a string enclosed in single\n"
			+ "quotes. The values listed are inserted into the table in the same\n" + "order that they are specified, i.e. the first value corresponds\n"
			+ "to the value of the first attribute, the second value corresponds\n" + "to the value of the second attribute etc.\n";

	private static final String HELP_DELETE = "This command has the form:\n" + "\n" + "DELETE FROM table_name WHERE condition_list;\n" + "\n"
			+ "The �DELETE FROM� token is followed by a table name, followed by the optional\n"
			+ "�WHERE� keyword and a condition list. The condition list has the following format:\n" + "\n" + "attribute1 operator value1\n" + "OR\n"
			+ "attribute1 operator value1 AND/OR attribute2 operator value2 AND/OR attribute3 operator value3�\n" + "\n"
			+ "The operator can be any of �=�, �!=�, �<�, �>�, �<=�, �>=�.\n" + "\n" + "If there are multiple conjunction/disjunction operators in the\n"
			+ "predicate, they must all be the same operator (i.e. there will\n" + "not be AND and OR operators mixed in the same condition). Hence,\n"
			+ "the conditions do not need to be enclosed in parentheses.\n";

	private static final String HELP_UPDATE = "This command has the form:\n" + "\n"
			+ "UPDATE table_name SET attr1 = val1, attr2 = val2� WHERE condition_list;\n" + "\n"
			+ "The �UPDATE� token is followed by a table name, which is followed by the token\n"
			+ "�SET� and a list of attribute name=attribute value pairs separated by commas. This\n"
			+ "is followed by an optional �WHERE� token and a condition list in the same form as\n" + "the condition list in the DELETE command.\n";

	private static final String HELP_HELP = "You may find information about the database in these ways:\n" + "\n"
			+ "HELP TABLES; - to list all tables visible to you\n" + "HELP DESCRIBE table_name; - to describe the schema of a table\n" + "\n"
			+ "Or to get help on commands, type:\n" + "\n" + "HELP CREATE TABLE\n" + "HELP DROP TABLE\n" + "HELP SELECT\n" + "HELP INSERT\n" + "HELP DELETE\n"
			+ "HELP UPDATE\n";

	private void validateAttrNameOrDie(String attr) throws InvalidSQLException {
		if (!Attribute.validName(attr))
			throw new InvalidSQLException(attr + " is not a valid attribute name");
	}

	private static String pad(String string, String pattern, String character) {
		return string.replaceAll("(?x) " + String.format("%s         ", pattern) + // Replace
				// ch
				"(?=        " + // Followed by
				"  (?:      " + // Start a non-capture group
				"    [^']*  " + // 0 or more non-single quote characters
				"    '      " + // 1 single quote
				"    [^']*  " + // 0 or more non-single quote characters
				"    '      " + // 1 single quote
				"  )*       " + // 0 or more repetition of non-capture
								// group
								// (multiple of 2 quotes will be even)
				"  [^']*    " + // Finally 0 or more non-single quotes
				"  $        " + // Till the end (This is necessary, else
								// every _
								// will satisfy the condition)
				")          ", // End look-ahead
				String.format(" %s ", character)); // Replace with " ch "

	}

	private static String pad(String string, String character) {
		return pad(string, character, character);
	}

	public static int parse(String statement) throws InvalidSQLException {
		// Pad all commas and parens that aren't in single quotes with spaces so
		// that they become individual tokens.
		statement = pad(statement, ",");
		statement = pad(statement, ";");
		statement = pad(statement, "\\(");
		statement = pad(statement, "\\)");

		// Pad all relational operators
		statement = pad(statement, "[^!]=", "=");
		statement = pad(statement, "!=");
		statement = pad(statement, ">");
		statement = pad(statement, "<");
		statement = pad(statement, ">=");
		statement = pad(statement, "<=");

		// Pad all arithmetic operators
		statement = pad(statement, "\\*");
		statement = pad(statement, "/");
		statement = pad(statement, "\\+");
		statement = pad(statement, "-");

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

				// Begin parsing attributes of form: name type [CHECK (
				// constraint )]

				boolean hasNextAttribute = true;
				while (hasNextAttribute) {

					// Only needed if there is a char type attribute in the
					// statement
					Integer charLen = null;

					String attrName = tokens.next();
					if (attrName.equals("PRIMARY")) {
						// Whoops, we're past the attribute list. Now we're at
						// FOREIGN KEY ...
						if (!tokens.next().equals("KEY"))
							throw new InvalidSQLException("PRIMARY must be followed by KEY");
						hasNextAttribute = false;
						break; // exit attribute list
					}

					String attrTypeStr = tokens.next();
					Attribute.Type attrType;
					if (attrTypeStr.equalsIgnoreCase("int")) {
						attrType = Attribute.Type.INT;
					} else if (attrTypeStr.equalsIgnoreCase("decimal")) {
						attrType = Attribute.Type.DECIMAL;
					} else if (attrTypeStr.equalsIgnoreCase("char")) {
						try {
							if (!tokens.next().equals("("))
								throw new InvalidSQLException("must specify char ( LEN )");
							charLen = Integer.valueOf(tokens.next());
							String tok = tokens.next();
							if (!tok.equals(")"))
								throw new InvalidSQLException("must specify char ( LEN )");
						} catch (NumberFormatException e) {
							throw new InvalidSQLException("Invalid attribute type: " + attrTypeStr);
						}
						attrType = Attribute.Type.CHAR;

					} else {
						throw new InvalidSQLException("Invalid attribute type: " + attrTypeStr);
					}

					// Create new attribute object
					Attribute newAttr = null;
					try {
						if (attrType.equals(Attribute.Type.CHAR)) {
							newAttr = new Attribute(attrName, attrType, charLen);
						} else {
							newAttr = new Attribute(attrName, attrType);
						}
					} catch (InvalidAttributeException e) {
						throw new InvalidSQLException("Invalid SQL: " + e.getMessage());
					}

					// Detect either comma for next attr, or constraint, or
					// close paren to end attr list
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
						if (!tokens.next().equals("("))
							throw new InvalidSQLException("Invalid argument list");
						// parse constraint list
						boolean hasNextConstraint = true;
						while (hasNextConstraint) {
							boolean and, or;
							and = or = false;

							// Next token must be name of attribute we're
							// working on
							String asdf = tokens.next();
							if (!asdf.equals(attrName))
								throw new InvalidSQLException("Constraints must be specified on own attribute only");
							// Get the operator
							String constOp = tokens.next();
							Operator op = Operator.fromString(constOp);
							if (op == null)
								throw new InvalidSQLException("Operator must be one of " + Operator.values());
							Value val = parseToValue(tokens, newAttr);
							newAttr.addConstraint(new Constraint(op, val));

							// TODO: process next constraint... only getting
							// first one here. find OR and AND and all that crap
							switch (tokens.next()) {
							case "AND":
								if (or)
									throw new InvalidSQLException("AND and OR may not be mixed in a constraint");
								else {
									and = true;
									newAttr.constraints.setOperator(Constraints.Operator.AND);
								}
								hasNextConstraint = true;
								break;
							case "OR":
								if (and)
									throw new InvalidSQLException("AND and OR may not be mixed in a constraint");
								else {
									or = true;
									newAttr.constraints.setOperator(Constraints.Operator.OR);
								}
								hasNextConstraint = true;
								break;
							case ")":
								hasNextConstraint = false;
								if (tokens.hasNext("\\)")) {
									tokens.next();
									hasNextAttribute = false;
								} else if (tokens.hasNext(",")) {
									tokens.next();
									hasNextAttribute = true;
								}
								break;
							default:
								throw new InvalidSQLException("Constraint list for attribute " + attrName + " malformed");
							}
						}

						break;
					default:
						throw new InvalidSQLException("Invalid argument list");
					}

					attributes.add(newAttr);

				}

				// TODO: Error out if the PK is not specified
				// Parse PRIMARY KEY ( attr1, attr2, ... )
				// "PRIMARY KEY" already swallowed by attribute loop. still need
				// to swallow the open paren
				if (!tokens.next().equals("("))
					throw new InvalidSQLException("PRIMARY KEY must be followed by parenthesized attribute list");
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

				// Optionally Parse ', FOREIGN KEY' ( fk ) REFERENCES table (
				// attr ), ...
				String expectedNextToken = expectComma ? "," : "FOREIGN";
				if (tokens.hasNext(expectedNextToken)) {
					// Swallow FOREIGN KEY and the comma if it is expected
					if (expectComma)
						tokens.next(); // swallow comma

					if (!tokens.next().equals("FOREIGN"))
						throw new InvalidSQLException("FOREIGN must be followed by KEY");
					if (!tokens.next().equals("KEY"))
						throw new InvalidSQLException("FOREIGN must be followed by KEY");
					if (!tokens.next().equals("("))
						throw new InvalidSQLException("FOREIGN must be followed by KEY");

					boolean hasNextFK = true;
					while (hasNextFK) {
						String fkAttrName = tokens.next();
						if (!tokens.next().equals(")"))
							throw new InvalidSQLException("FOREIGN KEY must specify one attribute followed by a ')'");
						if (!tokens.next().equals("REFERENCES"))
							throw new InvalidSQLException("Invalid FOREIGN KEY expression");
						String foreignTable = tokens.next();
						if (!tokens.next().equals("("))
							throw new InvalidSQLException("Invalid FOREIGN KEY expression");
						String foreignAttr = tokens.next();

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

				if (!tokens.next().equals(")"))
					throw new InvalidSQLException("Must close CREATE TABLE with ');'");
				if (!tokens.next().equals(";"))
					throw new InvalidSQLException("Must close CREATE TABLE with ');'");

				System.out.println("Created table successfully");
				break;
			case "DROP":
				if (!tokens.next().equals("TABLE"))
					throw new InvalidSQLException("DROP must be followed by TABLE");
				String tableToDrop = tokens.next();
				if (!tokens.next().equals(";"))
					throw new InvalidSQLException("Missing semicolon");

				Table.tables.remove(Table.tables.get(tableToDrop));
				System.out.println("Table dropped successfully");
				break;
			case "SELECT":
				// TODO: The values in the conditions can either be a constant
				// value or the name of another attribute.

				boolean selectAll = false;
				boolean whereClause = false;
				List<String> selectedAttributes = new ArrayList<String>();
				List<String> selectedTables = new ArrayList<String>();
				Conditions cond = new Conditions();

				// Parse attribute list (or star)
				if (tokens.hasNext("\\*")) {
					tokens.next(); // swallow *
					if (!tokens.next().equals("FROM"))
						throw new InvalidSQLException("SELECT * must be followed by FROM");
					selectAll = true;
				} else {
					boolean hasNextAttr = true;
					while (hasNextAttr) {
						String attr = tokens.next();
						selectedAttributes.add(attr);

						String tok = tokens.next();
						switch (tok) {
						case ",":
							hasNextAttr = true;
							break;
						case "FROM":
							hasNextAttr = false;
							break;
						default:
							throw new InvalidSQLException("Malformed attribute list");
						}
					}
				}

				// parse table list
				boolean hasNextTable = true;
				while (hasNextTable) {
					String selectedTable = tokens.next();
					selectedTables.add(selectedTable);

					String tok = tokens.next();
					switch (tok) {
					case ",":
						hasNextTable = true;
						break;
					case ";":
						hasNextTable = false;
						break;
					case "WHERE":
						hasNextTable = false;
						whereClause = true;
						break;
					}
				}

				if (selectAll) {
					// Add each attribute of each table (but not duplicates)
					for (String selectedTable : selectedTables) {
						Attributes schema = Table.tables.get(selectedTable).schema;
						for (Attribute a : schema) {
							if (selectedAttributes.contains(a.getName()))
								throw new InvalidSQLException("Duplicate attribute found in list.");
							else
								selectedAttributes.add(a.getName());
						}
					}
				}

				// Parse condition list
				if (whereClause) {
					cond = parseConditionList(tokens, selectedTables);
				} // if (whereClause)

				Rows result = Table.select(selectedAttributes, selectedTables, cond);

				// Print table header
				System.out.println(result.schema.tableHeader());
				// Print rows
				for (Row r : result)
					System.out.println(r.tableRow());

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
					if (tokens.hasNext("\\)")) {
						tokens.next();
						hasNextValue = false;
					} else if (tokens.hasNext(",")) {
						tokens.next();
						hasNextValue = true;
					} else {
						hasNextValue = true;
						Attribute attr;
						try {
							attr = schema.get(colNum);
						} catch (IndexOutOfBoundsException e) {
							throw new InvalidSQLException("You have specified too many values for this table");
						}
						Value newValue = parseToValue(tokens, attr);
						newRow.set(colNum, newValue);
						colNum++;
					}

				} // while (hasNextValue)

				// Check for semicolon
				String tok = tokens.next();
				if (!tok.equals(";"))
					throw new InvalidSQLException("INSERT must end with a semicolon");

				tableToInsert.insert(newRow);
				System.out.println("Tuple inserted successfully");
				break;
			case "DELETE":
				if (!tokens.next().equals("FROM"))
					throw new InvalidSQLException("DELETE must be followed by FROM");

				Table tableToDeleteFrom = Table.tables.get(tokens.next());

				List<String> deletionTables = new ArrayList<String>();
				deletionTables.add(tableToDeleteFrom.getName());

				if (!tokens.next().equals("WHERE"))
					throw new InvalidSQLException("DELETE FROM table_name must be followed by WHERE condition_list");

				Conditions deletionConditions = parseConditionList(tokens, deletionTables);

				Rows toDelete;
				toDelete = tableToDeleteFrom.rows.getAll(deletionConditions);
				tableToDeleteFrom.rows.removeAll(toDelete);

				break;
			case "UPDATE":
				Table tableToUpdate = Table.tables.get(tokens.next());

				List<String> updateTables = new ArrayList<String>();
				updateTables.add(tableToUpdate.getName());

				if (!tokens.next().equals("SET"))
					throw new InvalidSQLException("UPDATE table_name must be followed by SET");

				Map<Attribute, Value> updates = new HashMap<Attribute, Value>();
				boolean hasNextAttrVal = true;
				while (hasNextAttrVal) {
					String attrName = tokens.next();
					Attribute attr = tableToUpdate.schema.get(attrName);

					String asdf = tokens.next();
					if (!asdf.equals("="))
						throw new InvalidSQLException("Attribute must be separated from value with an =");

					Value newVal = parseToValue(tokens, attr);
					updates.put(attr, newVal);

					String tokk = tokens.next();
					switch (tokk) {
					case ",":
						hasNextAttrVal = true;
						break;
					case "WHERE":
						hasNextAttrVal = false;
						break;
					default:
						throw new InvalidSQLException("Update list attr1 = val1 ... not followed by WHERE clause");
					}
				}

				// Parse WHERE clause (condition list)

				Conditions updateConditions = parseConditionList(tokens, updateTables);
				Rows rowsToUpdate = tableToUpdate.rows.getAll(updateConditions);
				for (Row row : rowsToUpdate) {
					for (Attribute attr : updates.keySet()) {
						row.set(attr, updates.get(attr));
					}
				}

				// TODO: print number of rows affected
				System.out.printf("%d rows affected", rowsToUpdate.size());

				break;
			case "HELP":
				String helpTok = null;
				try {
					helpTok = tokens.next();
				} catch (NoSuchElementException e) {
					System.out.print(HELP_HELP);
				}
				switch (helpTok) {
				case "TABLES":
					if (Table.tables.size() == 0) {
						System.out.println("No tables found.");
					} else {
						for (Table t : Table.tables)
							System.out.println(t.getName());
					}
					if (!tokens.next().equals(";"))
						throw new InvalidSQLException("Missing semicolon");
					break;
				case "DESCRIBE":
					String tableToDescribeName = tokens.next();
					Table tableToDescribe = Table.tables.get(tableToDescribeName);
					// TODO: Use exact format from example??
					System.out.println(tableToDescribe);
					if (!tokens.next().equals(";"))
						throw new InvalidSQLException("Missing semicolon");
					break;
				case "CREATE":
					System.out.print(HELP_CREATE);
					break;
				case "DROP":
					System.out.print(HELP_DROP);
					break;
				case "SELECT":
					System.out.print(HELP_SELECT);
					break;
				case "INSERT":
					System.out.print(HELP_INSERT);
					break;
				case "DELETE":
					System.out.print(HELP_DELETE);
					break;
				case "UPDATE":
					System.out.print(HELP_UPDATE);
					break;
				default:
					System.err.println(HELP_HELP);
				}
				break;
			case "QUIT;":
				// TODO: Make sure all changes are committed and all resources
				// are closed
				System.exit(0);
				break;
			default:
				System.err.println("Error, that is not a valid SQL command.");
				break;
			} // switch
		} catch (InvalidSQLException e) {
			System.err.println(e.getMessage());
		} catch (SchemaViolationException e) {
			System.err.println("Schema violation: " + e.getMessage());
		} catch (NoSuchElementException e) {
			System.err.println("Input ended unexpectedly.  Did you forget to close parens or a semicolon?");
		} finally {
			tokens.close();
		}

		return 0;

	}

	private static String parseToQuotedChar(Scanner tokens) throws InvalidSQLException {
		StringBuilder sb = new StringBuilder();
		boolean sawFirstQuote = false;
		bigloop: while (tokens.hasNext()) {
			String tok = tokens.next();
			for (char c : tok.toCharArray()) {
				if (c == '\'') {
					if (sawFirstQuote) {
						break bigloop;
					} else {
						sawFirstQuote = true;
					}
				} else if (!sawFirstQuote) {
					throw new InvalidSQLException("Char value not wrapped in single quotes");
				} else {
					sb.append(c);
				}
			}
			sb.append(" ");
		}
		return sb.toString();
	}

	private static Value parseToValue(Scanner tokens, Attribute attr) throws InvalidSQLException {
		switch (attr.getType()) {
		case INT:
			int intValue;
			String intValStr = tokens.next();
			try {
				intValue = Integer.valueOf(intValStr);
			} catch (NumberFormatException e) {
				throw new InvalidSQLException("The value " + intValStr + " cannot be interpreted as an integer");
			}
			return new IntValue(intValue);
		case DECIMAL:
			double decValue;
			String decValStr = tokens.next();
			try {
				decValue = Double.valueOf(decValStr);
			} catch (NumberFormatException e) {
				throw new InvalidSQLException("The value " + decValStr + " cannot be interpreted as a decimal");
			}
			return new DecValue(decValue);
		case CHAR:
			String charValue = parseToQuotedChar(tokens);
			if (charValue.length() > attr.charLen)
				throw new InvalidSQLException("The value '" + charValue + "' is too long for the attribute + " + attr.getName());
			return new CharValue(charValue);
		}
		return null;
	}

	private static Conditions parseConditionList(Scanner tokens, List<String> selectedTables) throws InvalidSQLException, SchemaViolationException {
		Conditions cond = new Conditions();

		boolean and = false;
		boolean or = false;

		boolean hasNextCondition = true;
		while (hasNextCondition) {
			// TODO: Input validation??
			String lhsStr = tokens.next();
			String opStr = tokens.next();
			Operator op = Operator.fromString(opStr);

			Attribute lhs = findAttrInTables(selectedTables, lhsStr);
			
			// If the next token begins with alphanumeric, it's an attribute name
			if (tokens.hasNext("[a-zA-Z]+")) {
				String rhsName = tokens.next();
				Attribute rhs = findAttrInTables(selectedTables, rhsName);
				cond.add(lhs, op, rhs);
			} else { // Otherwise, it's a straight up value
				Value val = parseToValue(tokens, lhs);
				cond.add(lhs, op, val);
			}
			
			String tok;
			try {
				tok = tokens.next();
			} catch (NoSuchElementException e) {
				throw new InvalidSQLException("Unexpected end of statement");
			}
			switch (tok) {
			case ";":
				hasNextCondition = false;
				break;
			case "AND":
				if (or) {
					throw new InvalidSQLException("AND and OR may not be mixed in a WHERE clause");
				}
				and = true;
				hasNextCondition = true;
				break;
			case "OR":
				if (and) {
					throw new InvalidSQLException("AND and OR may not be mixed in a WHERE clause");
				}
				or = true;
				hasNextCondition = true;
				break;
			default:
				throw new InvalidSQLException("Syntax error in WHERE clause");
			}
		}
		return cond;
	}

	private static Attribute findAttrInTables(List<String> selectedTables, String attrName) throws InvalidSQLException, SchemaViolationException {
		// TODO Auto-generated method stub
		for (String selectedTableName : selectedTables) {
			Table selectedTable = null;
			selectedTable = Table.tables.get(selectedTableName);
			for (Attribute a : selectedTable.getAttributes()) {
				if (a.getName().equals(attrName)) {
					return a;
				}
			}
		}
		throw new InvalidSQLException("The attribute " + attrName + " does not exist in any of the tables: " + selectedTables);
	}
}
