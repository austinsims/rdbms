import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Pattern;

// TODO: Verify all attribute names with verifyAttrNameOrDie

// Phase 0.1.  All (yes, all) tokens must be separated by spaces, including commas and parens.
public class SQLParser {

	private void validateAttrNameOrDie(String attr) throws InvalidSQLException {
		if (!Attribute.validName(attr))
			throw new InvalidSQLException(attr + " is not a valid attribute name");
	}

	private static String pad(String s, String ch) {
		return s.replaceAll("(?x) " + String.format("%s         ", ch) + // Replace
																			// ch
				"(?=        " + // Followed by
				"  (?:      " + // Start a non-capture group
				"    [^']*  " + // 0 or more non-single quote characters
				"    '      " + // 1 single quote
				"    [^']*  " + // 0 or more non-single quote characters
				"    '      " + // 1 single quote
				"  )*       " + // 0 or more repetition of non-capture group
								// (multiple of 2 quotes will be even)
				"  [^']*    " + // Finally 0 or more non-single quotes
				"  $        " + // Till the end (This is necessary, else every _
								// will satisfy the condition)
				")          ", // End look-ahead
				String.format(" %s ", ch)); // Replace with " , "

	}

	public static int parse(String statement) throws InvalidSQLException {
		// Pad all commas and parens that aren't in single quotes with spaces so
		// that they become individual tokens.
		statement = pad(statement, ",");
		statement = pad(statement, ";");
		statement = pad(statement, "\\(");
		statement = pad(statement, "\\)");

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
					// Initialize to null; only needed if there is a constraint
					// in the statement
					String constraint = null;

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
						break;
					}

					String attrTypeStr = tokens.next();
					Attribute.Type attrType;
					if (attrTypeStr.equals("int")) {
						attrType = Attribute.Type.INT;
					} else if (attrTypeStr.equals("decimal")) {
						attrType = Attribute.Type.DECIMAL;
					} else if (attrTypeStr.equals("char")) {
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
						constraint = tokens.next();
						if (!tokens.next().equals(")"))
							throw new InvalidSQLException("Invalid argument list");
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
				// "PRIMARY KEY" already swallowed by attribute loop. still need
				// to swallow the open paren
				if (!tokens.next().equals("("))
					throw new InvalidSQLException("PRIMARY KEY must be followed by parenthesized attribute list");
				boolean hasNextPkAttr = true;
				Attributes pk = new Attributes();
				while (hasNextPkAttr) {
					String pkAttrStr = tokens.next();
					if (!attributes.contains(pkAttrStr))
						throw new InvalidSQLException("The attribute list " + attributes
								+ " does not contain the proposed PK attribute " + pkAttrStr);
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
						throw new InvalidSQLException(
								"Attributes within a PRIMARY KEY attribute list must be separated by commas");
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
				boolean selectAll = false;
				boolean whereClause = false;
				List<String> selectedAttributes = new ArrayList<String>();
				List<String> selectedTables = new ArrayList<String>();
				Conditions cond = new Conditions();

				// Parse attribute list (or star)
				if (tokens.hasNext("\\*")) {
					tokens.next(); // swallow *
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
						case ")":
							hasNextAttr = false;
							break;
						}
					}
				}

				// swallow FROM
				if (!tokens.next().equals("FROM"))
					throw new InvalidSQLException("Attribute list must be followed by FROM");

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
						Table t = Table.tables.get(selectedTable);
						for (Attribute a : t.getAttributes()) {
							if (!selectedAttributes.contains(a.getName())) {
								selectedAttributes.add(a.getName());
							}
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

					if (!tokens.next().equals("="))
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
			tokens.close();
			System.err.println(e.getMessage());
			throw e;
		} catch (SchemaViolationException e) {
			System.err.println("Schema violation: " + e.getMessage());
			e.printStackTrace();
		} finally {
			tokens.close();
		}

		return 0;

	}

	private static String parseToQuotedChar(Scanner tokens) throws InvalidSQLException {
		StringBuilder sb = new StringBuilder();
		boolean sawFirstQuote = false;
		bigloop:
		while (tokens.hasNext()) {
			String tok  = tokens.next();
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
				throw new InvalidSQLException("The value '" + charValue + "' is too long for the attribute + "
						+ attr.getName());
			return new CharValue(charValue);
		}
		return null;
	}

	private static Conditions parseConditionList(Scanner tokens, List<String> selectedTables)
			throws InvalidSQLException {
		Conditions cond = new Conditions();

		boolean and = false;
		boolean or = false;

		boolean hasNextCondition = true;
		while (hasNextCondition) {
			// TODO: Input validation??
			String attrName = tokens.next();
			String opStr = tokens.next();

			Attribute attr = null;
			Value val = null;
			// Find the attribute with the given name. Search through EVERY table.
			for (String selectedTableName : selectedTables) {
				Table selectedTable = null;
				try {
					selectedTable = Table.tables.get(selectedTableName);
				} catch (SchemaViolationException e) {
					throw new InvalidSQLException("Schema violation: " + e.getMessage());
				}
				for (Attribute a : selectedTable.getAttributes()) {
					if (a.getName().equals(attrName)) {
						attr = a;
						val = parseToValue(tokens, attr);
					}
				}
				if (attr == null)
					throw new InvalidSQLException("The attribute " + attrName
							+ " does not exist in any of the tables: " + selectedTables);
			}

			Conditions.Operator op;
			switch (opStr) {
			case "=":
				op = Conditions.Operator.EQUAL;
				break;
			case "!=":
				op = Conditions.Operator.NOT_EQUAL;
				break;
			case "<":
				op = Conditions.Operator.LESS;
				break;
			case ">":
				op = Conditions.Operator.GREATER;
				break;
			case "<=":
				op = Conditions.Operator.GREATER_OR_EQUAL;
				break;
			case ">=":
				op = Conditions.Operator.LESS_OR_EQUAL;
				break;
			default:
				throw new InvalidSQLException(opStr + " is not a valid operator");
			}

			cond.add(attr, op, val);
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
}
