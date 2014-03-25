package rdbms;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class RowsTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testCross() throws InvalidAttributeException, SchemaViolationException {
		Attributes rowsSchema = new Attributes();
		Attribute rowAttr = new Attribute("row", Attribute.Type.CHAR, 1);
		rowsSchema.add(rowAttr);
		Rows rows = new Rows(rowsSchema);
		rows.add(new Row(rowsSchema, new CharValue("a")));
		rows.add(new Row(rowsSchema, new CharValue("b")));
		rows.add(new Row(rowsSchema, new CharValue("c")));
		
		Attributes colsSchema = new Attributes();
		Attribute colAttr = new Attribute("col", Attribute.Type.INT);
		colsSchema.add(colAttr);
		Rows cols = new Rows(colsSchema);
		cols.add(new Row(colsSchema, new IntValue(1)));
		cols.add(new Row(colsSchema, new IntValue(2)));
		cols.add(new Row(colsSchema, new IntValue(3)));
		
		Attributes crossSchema = new Attributes();
		crossSchema.addAll(rowAttr, colAttr);
		Rows expected = new Rows(crossSchema);
        expected.add(new Row(crossSchema, new CharValue("a"), new IntValue(1)));
        expected.add(new Row(crossSchema, new CharValue("a"), new IntValue(2)));
        expected.add(new Row(crossSchema, new CharValue("a"), new IntValue(3)));
        expected.add(new Row(crossSchema, new CharValue("b"), new IntValue(1)));
        expected.add(new Row(crossSchema, new CharValue("b"), new IntValue(2)));
        expected.add(new Row(crossSchema, new CharValue("b"), new IntValue(3)));
        expected.add(new Row(crossSchema, new CharValue("c"), new IntValue(1)));
        expected.add(new Row(crossSchema, new CharValue("c"), new IntValue(2)));
        expected.add(new Row(crossSchema, new CharValue("c"), new IntValue(3)));
                                                                                                                             
		Rows actual = rows.cross(cols);
		
		assertEquals(expected, actual);
		
	}
}
