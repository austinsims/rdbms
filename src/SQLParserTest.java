import static org.junit.Assert.*;

import org.junit.Test;


public class SQLParserTest {
		
	/**
	 * Very basic table with spaces between all tokens
	 * @throws Exception 
	 */
	@Test
	public void testBasicCreateTableSpacey() throws Exception {
		try {
			SQLParser.parse("CREATE TABLE climate ( city char(50) , temperature decimal );");
		} catch (Exception e) {
			throw e;			
		}
	}
	
	@Test
	public void testValidAttrName() {
		assertTrue(SQLParser.validAttrName("king_kong"));
		assertFalse(SQLParser.validAttrName("1monkeys_"));
		assertTrue(SQLParser.validAttrName("_"));
		assertFalse(SQLParser.validAttrName("123"));
		assertFalse(SQLParser.validAttrName("Hello world!"));
	}

}
