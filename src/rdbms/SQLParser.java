package rdbms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

// TODO: Verify all attribute names with verifyAttrNameOrDie

// Phase 0.1.  All (yes, all) tokens must be separated by spaces, including commas and parens.
public class SQLParser {
	


	private final static String HELP_CREATE = "This command has the form:\n" + "\n" + "CREATE TABLE table_name ( attribute_1 attribute1_type CHECK (constraint1),\n" + "attribute_2 attribute2_type, �, PRIMARY KEY ( attribute_1, attribute_2 ), FOREIGN\n" + "KEY ( attribute_y ) REFERENCES table_x ( attribute_t ), FOREIGN KEY\n" + "( attribute_w ) REFERENCES table_y ( attribute_z )... );\n" + "\n" + "The �CREATE TABLE� token is followed by any number of attribute\n" + "name � attribute type pairs separated by commas. Each attribute\n" + "name � attribute type pair can optionally be followed by a\n" + "constraint specified using the keyword �CHECK� followed by a\n" + "domain constraint in one of the forms specified in Table 3,\n" + "enclosed in parentheses. This is followed by the token �PRIMARY\n" + "KEY� and a list of attribute names separated by commas, enclosed\n" + "in parentheses. Note that the specification of the primary key\n" + "constraint is mandatory in this project and will always follow\n" + "the listing of attributes. After the primary key constraint, the\n" + "command should accept an optional list of foreign key constraints\n" + "specified with the token �FOREIGN KEY� followed by an attribute\n" + "name enclosed in parentheses, followed by the keyword\n" + "�REFERENCES�, a table name and an attribute name enclosed in\n" + "parentheses.  Multiple foreign key constraints are separated by\n" + "commas.\n";

	private final static String HELP_DROP = "This command has the form:\n" + "\n" + "DROP TABLE table_name;\n" + "\n" + "The �DROP TABLE� token is followed by a table name.\n";

	private final static String HELP_SELECT = "This command has the form:\n" + "\n" + "SELECT attribute_list FROM table_list WHERE condition_list;\n" + "\n" + "The token �SELECT� is followed by an attribute list, followed by the token �FROM�\n" + "and a table name list. This is followed by an optional �WHERE� keyword and\n" + "condition list. For simplicity, you are only asked to implement an attribute list\n" + "consisting of attribute names separated by commas and not using the dot notation,\n" + "in addition to �*�, which stands for all attributes. You can also assume that no\n" + "attributes of different tables will have the same name. The table list will also be a\n" + "simple list of table names separated by commas. The condition list has the following\n" + "format:\n" + "\n" + "attribute1 operator value1\n" + "\n" + "OR\n" + "\n" + "attribute1 operator value1 AND/OR attribute2 operator value2 AND/OR attribute3 operator value3�\n" + "\n" + "The operator can be any of �=�, �!=�, �<�, �>�, �<=�, �>=�.\n" + "\n" + "If there are multiple conjunction/disjunction operators in the\n" + "predicate, they must all be the same operator (i.e. there can not\n" + "be AND and OR operators mixed in the same condition). Hence, the\n" + "conditions do not need to be enclosed in parentheses. The values\n" + "in the conditions can either be a constant value or the name of\n" + "another attribute.\n";

	private static final String HELP_INSERT = "This command has the form:\n" + "\n" + "INSERT INTO table_name VALUES ( val1, val2, � );\n" + "\n" + "The �INSERT INTO� token is followed by a table name, followed by\n" + "the token �VALUES� and a list of values separated by commas\n" + "enclosed in parentheses. Each value should be either a\n" + "number (integer or decimal) or a string enclosed in single\n" + "quotes. The values listed are inserted into the table in the same\n" + "order that they are specified, i.e. the first value corresponds\n" + "to the value of the first attribute, the second value corresponds\n" + "to the value of the second attribute etc.\n";

	private static final String HELP_DELETE = "This command has the form:\n" + "\n" + "DELETE FROM table_name WHERE condition_list;\n" + "\n" + "The �DELETE FROM� token is followed by a table name, followed by the optional\n" + "�WHERE� keyword and a condition list. The condition list has the following format:\n" + "\n" + "attribute1 operator value1\n" + "OR\n" + "attribute1 operator value1 AND/OR attribute2 operator value2 AND/OR attribute3 operator value3�\n" + "\n" + "The operator can be any of �=�, �!=�, �<�, �>�, �<=�, �>=�.\n" + "\n" + "If there are multiple conjunction/disjunction operators in the\n" + "predicate, they must all be the same operator (i.e. there will\n" + "not be AND and OR operators mixed in the same condition). Hence,\n" + "the conditions do not need to be enclosed in parentheses.\n";

	private static final String HELP_UPDATE = "This command has the form:\n" + "\n" + "UPDATE table_name SET attr1 = val1, attr2 = val2� WHERE condition_list;\n" + "\n" + "The �UPDATE� token is followed by a table name, which is followed by the token\n" + "�SET� and a list of attribute name=attribute value pairs separated by commas. This\n" + "is followed by an optional �WHERE� token and a condition list in the same form as\n" + "the condition list in the DELETE command.\n";

	private static final String HELP_HELP = "You may find information about the database in these ways:\n" + "\n" + "HELP TABLES; - to list all tables visible to you\n" + "HELP DESCRIBE table_name; - to describe the schema of a table\n" + "\n" + "Or to get help on commands, type:\n" + "\n" + "HELP CREATE TABLE\n" + "HELP DROP TABLE\n" + "HELP SELECT\n" + "HELP INSERT\n" + "HELP DELETE\n" + "HELP UPDATE\n";

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

	public static final int QUIT = 1;
	public static final int CONTINUE = 0;

	/**
	 * Parse the statement and then return a code indicating whether to continue
	 * execution
	 * 
	 * @param statement
	 * @return SQLParser.QUIT to quit or SQLParser.CONTINUE to continue
	 * @throws InvalidSQLException
	 * @throws PermissionException
	 */
	public static int parse(String statement) throws InvalidSQLException, PermissionException {
		// Get logged in user from Database
		User loggedInUser = Database.getLoggedInUser();

		// Pad all commas and parens that aren't in single quotes with spaces so
		// that they become individual tokens.
		statement = pad(statement, ",");
		statement = pad(statement, ";");
		statement = pad(statement, "\\(");
		statement = pad(statement, "\\)");

		// Pad all relational operator pieces (they will be parsed separately)
		statement = pad(statement, "=");
		statement = pad(statement, ">");
		statement = pad(statement, "<");
		statement = pad(statement, "!");

		/*
		 * statement = pad(statement, "!="); statement = pad(statement, ">=");
		 * statement = pad(statement, "<=");
		 */

		// Pad all arithmetic operators
		/*
		 * statement = pad(statement, "\\*"); statement = pad(statement, "/");
		 * statement = pad(statement, "\\+"); statement = pad(statement, "-");
		 */

		Scanner tokens = new Scanner(statement);
		String command = tokens.next();

		try {
			switch (command) {
			case "CREATE":
				String whatToCreate = tokens.next();
				if (whatToCreate.equals("TABLE")) {

					// Initialize what we need to make a new table
					Attributes attributes = new Attributes();

					// Parse name
					String tableName = tokens.next();
					if (!tokens.next().equals("("))
						throw new InvalidSQLException("Table name must be followed by (");

					// Make sure a table by that name does not already exist
					if (Database.tables.contains(tableName))
						throw new InvalidSQLException("Error: Duplicate table!");

					// Begin parsing attributes of form: name type [CHECK (
					// constraint )]

					boolean hasNextAttribute = true;
					while (hasNextAttribute) {

						if (tokens.hasNext("PRIMARY"))
							break;

						if (tokens.hasNext("FOREIGN"))
							break;

						// Only needed if there is a char type attribute in the
						// statement
						Integer charLen = null;

						String attrName = tokens.next();

						String attrTypeStr = tokens.next();
						Attribute.Type attrType;
						if (attrTypeStr.equalsIgnoreCase("INT")) {
							attrType = Attribute.Type.INT;
						} else if (attrTypeStr.equalsIgnoreCase("DECIMAL")) {
							attrType = Attribute.Type.DECIMAL;
						} else if (attrTypeStr.equalsIgnoreCase("CHAR")) {
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
								Operator op = parseToOperator(tokens);
								if (op == null)
									throw new InvalidSQLException("Operator must be one of " + Operator.values());
								Value val = parseToValue(tokens, newAttr);
								newAttr.addConstraint(new Constraint(op, val));

								// TODO: process next constraint... only getting
								// first one here. find OR and AND and all that
								// crap
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

					if (!tokens.next().equals("PRIMARY"))
						throw new InvalidSQLException(("Invalid CREATE TABLE statement"));

					if (!tokens.next().equals("KEY"))
						throw new InvalidSQLException(("Invalid CREATE TABLE statement"));

					// Parse PRIMARY KEY ( attr1, attr2, ... )
					// "PRIMARY KEY" already swallowed by attribute loop. still
					// need
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
							break;
						default:
							throw new InvalidSQLException("Attributes within a PRIMARY KEY attribute list must be separated by commas");
						}

					}

					// Create Table object
					Table newTable = new Table(tableName, attributes, pk);

					// Optionally Parse ', FOREIGN KEY' ( fk ) REFERENCES table,
					// ...

					if (tokens.hasNext(",")) {
						
						String d= tokens.next();

						boolean hasNextFK = true;
						while (hasNextFK) {
							String c = tokens.next();
							if (!c.equals("FOREIGN"))
								throw new InvalidSQLException("FOREIGN must be followed by KEY");
							if (!tokens.next().equals("KEY"))
								throw new InvalidSQLException("FOREIGN must be followed by KEY");
							if (!tokens.next().equals("("))
								throw new InvalidSQLException("FOREIGN must be followed by KEY");
							
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

							if (!tokens.next().equals(")"))
								throw new InvalidSQLException("Invalid FOREIGN KEY expression");

							
							if (tokens.hasNext(",")) {
								tokens.next();
								hasNextFK = true;
							} else {
								hasNextFK = false;
							}

						}
					}

					String a = tokens.next();
					// if (!tokens.next().equals(")"))
					if (!a.equals(")"))
						throw new InvalidSQLException("Must close CREATE TABLE with ');'");

					String b = tokens.next();
					// if (!tokens.next().equals(";"))
					if (!b.equals(";"))
						throw new InvalidSQLException("Must close CREATE TABLE with ');'");

					Database.insertIntoDB(newTable);
					System.out.println("Table created successfully");
					break;
				} else if (whatToCreate.equals("USER")) {
					// Check user permissions
					if (!loggedInUser.getType().equals(User.Type.ADMIN))
						throw new PermissionException("Sorry, but you must be an admin user to do that.");

					// Create user
					String username = tokens.next();

					// Here comes some nasty hackish code to circumvent "User-A"
					// containing SQL arithmetic operator
					User.Type usertype = User.Type.fromString(tokens.next());
					if (usertype == null) {
						throw new InvalidSQLException("User type must be either User-A or User-B");
					}

					if (!tokens.next().equals(";"))
						throw new InvalidSQLException("Missing semicolon");

					User newUser = new User(username, usertype);
					Database.addUser(newUser);
					System.out.println("User created successfully");

				} else if (whatToCreate.equals("SUBSCHEMA")) {
					if (!loggedInUser.getType().equals(User.Type.ADMIN)) {
						throw new PermissionException("Sorry, you must be an admin to create a subschema.");
					}

					String tableName = tokens.next();
					Table table = Database.tables.get(tableName);
					Attributes schema = table.getAttributes();

					Attributes subschema = new Attributes();
					boolean hasNextAttr = true;
					while (hasNextAttr) {
						String subattrName = tokens.next();
						Attribute subattr = schema.get(subattrName);
						subschema.add(subattr);

						if (tokens.hasNext(",")) {
							tokens.next();
							hasNextAttr = true;
						} else if (tokens.hasNext(";"))
							hasNextAttr = false;
						else
							throw new InvalidSQLException("Malformed attribute list");
					}
					table.setSubschema(subschema);
					System.out.println("Subschema created successfully");

				} else {
					throw new InvalidSQLException("CREATE must be followed by USER or TABLE.");
				}
				break;
			case "DROP":
				if (Database.getLoggedInUser().getType().equals(User.Type.B))
					throw new PermissionException("Error: Authorization failure!");
					
				String whatToDrop = tokens.next();

				if (whatToDrop.equals("TABLE")) {

					String tableToDrop = tokens.next();
					if (!tokens.next().equals(";"))
						throw new InvalidSQLException("Missing semicolon");

					if (Database.dropTable(Database.tables.get(tableToDrop))) {
						System.out.println("Table dropped successfully");
					} else {
						System.err.println("Sorry, could not delete table file on disk.  Is it in use by another process?");
					}
				} else if (whatToDrop.equals("SUBSCHEMA")) {
					String tableName = tokens.next();
					Table table = Database.tables.get(tableName);
					table.setSubschema(null);
					System.out.println("Successfully dropped subschema");
				} else {
					throw new InvalidSQLException("DROP must be followed by TABLE");
				}
				break;
			case "SELECT":
				// TODO: The values in the conditions can either be a constant
				// value or the name of another attribute.

				boolean selectAll = false;
				boolean whereClause = false;
				List<String> selectedAttributeNames = new ArrayList<String>();
				List<String> selectedTableNames = new ArrayList<String>();
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
						String attrName = tokens.next();
						selectedAttributeNames.add(attrName);

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
					String selectedTableName = tokens.next();
					selectedTableNames.add(selectedTableName);

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
					for (String selectedTable : selectedTableNames) {
						Attributes schema = Database.tables.get(selectedTable).visibleSchema();
						for (Attribute a : schema) {
							if (selectedAttributeNames.contains(a.getName()))
								throw new InvalidSQLException("Duplicate attribute found in list.");
							else
								selectedAttributeNames.add(a.getName());
						}
					}
				}

				// Parse condition list
				if (whereClause) {
					cond = parseConditionList(tokens, selectedTableNames);
				} // if (whereClause)

				Rows result = Database.select(selectedAttributeNames, selectedTableNames, cond);
				
				if (result.size() > 0) {
					// Print table header
					System.out.println(result.schema.tableHeader());
					// Print rows
					for (Row r : result)
						System.out.println(r.tableRow());
				} else {
					// Print empty line if there are no results.
					System.out.println();
				}

				break;
			case "INSERT":
				if (!tokens.next().equals("INTO"))
					throw new InvalidSQLException("INSERT must be followed by INTO");
				String tableToInsertName = tokens.next();
				if (!Database.tables.contains(tableToInsertName))
					throw new InvalidSQLException("Table " + tableToInsertName + " does not exist");
				if (!tokens.next().equals("VALUES"))
					throw new InvalidSQLException("Table name must be followed by VALUES");
				if (!tokens.next().equals("("))
					throw new InvalidSQLException("Missing open paren");

				Table tableToInsert = Database.tables.get(tableToInsertName);
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
				if (Database.getLoggedInUser().getType().equals(User.Type.B))
					throw new PermissionException("Error: Authorization failure!");
				String whatToDelete = tokens.next();
				if (whatToDelete.equals("FROM")) {
					Table tableToDeleteFrom = Database.tables.get(tokens.next());

					List<String> deletionTables = new ArrayList<String>();
					deletionTables.add(tableToDeleteFrom.getName());

					if (!tokens.next().equals("WHERE"))
						throw new InvalidSQLException("DELETE FROM table_name must be followed by WHERE condition_list");

					Conditions deletionConditions = parseConditionList(tokens, deletionTables);

					Rows toDelete;
					toDelete = tableToDeleteFrom.rows.getAll(deletionConditions);
					tableToDeleteFrom.rows.removeAll(toDelete);
				} else if (whatToDelete.equals("USER")) {
					if (!loggedInUser.getType().equals(User.Type.ADMIN)) {
						throw new PermissionException("Sorry, you must be an admin to delete users.");
					}
					String username = tokens.next();
					User userToDelete = Database.getUser(username);
					if (userToDelete == null) {
						throw new InvalidSQLException("The user " + username + " does not exist.");
					}
					Database.removeUser(userToDelete);
					System.out.println("User deleted successfully");
				} else {
					throw new InvalidSQLException("DELETE must be followed by FROM");
				}
				break;
			case "UPDATE":
				Table tableToUpdate = Database.tables.get(tokens.next());

				List<String> updateTables = new ArrayList<String>();
				updateTables.add(tableToUpdate.getName());

				if (!tokens.next().equals("SET"))
					throw new InvalidSQLException("UPDATE table_name must be followed by SET");

				boolean updateWhereClause = false;
				
				Map<Attribute, Value> updates = new HashMap<Attribute, Value>();
				boolean hasNextAttrVal = true;
				while (hasNextAttrVal) {
					String attrName = tokens.next();
					Attribute attr = tableToUpdate.visibleSchema().get(attrName);

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
						updateWhereClause = true;
						break;
					case ";":
						hasNextAttrVal = false;
						break;
					default:
						throw new InvalidSQLException("Update list attr1 = val1 ... not followed by WHERE clause");
					}
				}

				Conditions updateConditions;
				if (updateWhereClause) {
					// Parse WHERE clause (condition list)
					updateConditions = parseConditionList(tokens, updateTables);
				} else {
					// Create empty Conditions that will pass every row
					updateConditions = new Conditions();
				}
				
				Rows rowsToUpdate = tableToUpdate.rows.getAll(updateConditions);
				for (Row row : rowsToUpdate) {
					for (Attribute attr : updates.keySet()) {
						row.set(attr, updates.get(attr));
					}
				}
								
				if (rowsToUpdate.size() == 1)
					System.out.printf("1 row affected");
				else
					System.out.printf("%d rows affected", rowsToUpdate.size());

				break;
			case "HELP":
				String helpTok = null;
				try {
					helpTok = tokens.next();
				} catch (NoSuchElementException e) {
					System.out.print(HELP_HELP);
					return CONTINUE;
				}
				switch (helpTok) {
				case "TABLES":
					if (Database.tables.size() == 0) {
						System.out.println("No tables found.");
					} else {
						for (Table t : Database.tables)
							System.out.println(t.getName());
					}
					if (!tokens.next().equals(";"))
						throw new InvalidSQLException("Missing semicolon");
					break;
				case "DESCRIBE":
					String tableToDescribeName = tokens.next();
					Table tableToDescribe = Database.tables.get(tableToDescribeName);
					// TODO: Use exact format from example??
					System.out.println(tableToDescribe);
					if (!tokens.next().equals(";"))
						throw new InvalidSQLException("Missing semicolon");
					break;
				case "USERS":
					if (!loggedInUser.getType().equals(User.Type.ADMIN))
						throw new PermissionException("Sorry, you must be admin to list users.");
					System.out.println("User\tType");
					for (User user : Database.getUsers()) {
						System.out.printf("%s\t%s\n", user.getName(), user.getType().toString());
					}
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
			case "QUIT":
				// TODO: Make sure all changes are committed and all resources
				// are closed
				System.out.println("Goodbye.");
				return QUIT;
			default:
				System.err.println("Error, that is not a valid SQL command.");
				break;
			} // switch
		} catch (RDBMSException e) {
			System.err.println(e.getMessage());
		} catch (NoSuchElementException e) {
			System.err.println("Input ended unexpectedly.  Did you forget to close parens or a semicolon?");
		} finally {
			tokens.close();
		}

		return CONTINUE;

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

			Operator op = parseToOperator(tokens);

			Attribute lhs = findAttrInTables(selectedTables, lhsStr);

			// If the next token begins with alphanumeric, it's an attribute
			// name
			if (tokens.hasNext("[a-zA-Z_]+")) {
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
		if (and) {
			cond.setMode(Conditions.Mode.AND);
		} else {
			cond.setMode(Conditions.Mode.OR);
		}
		return cond;
	}

	public static Operator parseToOperator(Scanner tokens) {
		// Parse operator pieces
		StringBuilder opStr = new StringBuilder();
		String piece = tokens.next();
		switch (piece) {
		case "=":
			opStr.append(piece);
			break;
		case "!":
		case "<":
		case ">":
			opStr.append(piece);
			if (tokens.hasNext("="))
				opStr.append(tokens.next());
			break;
		}
		Operator op = Operator.fromString(opStr.toString());
		return op;
	}

	private static Attribute findAttrInTables(List<String> selectedTables, String attrName) throws InvalidSQLException, SchemaViolationException {
		// TODO Auto-generated method stub
		for (String selectedTableName : selectedTables) {
			Table selectedTable = null;
			selectedTable = Database.tables.get(selectedTableName);
			for (Attribute a : selectedTable.getAttributes()) {
				if (a.getName().equals(attrName)) {
					return a;
				}
			}
		}
		throw new InvalidSQLException("The attribute " + attrName + " does not exist in any of the tables: " + selectedTables);
	}
}
