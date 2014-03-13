import static org.junit.Assert.*;

import org.junit.Test;


public class AttributeTest {

	@Test
	public void testEquals() throws InvalidAttributeException {
		assertTrue(
				(new Attribute("hello", Attribute.Type.INT))
				.equals
				(new Attribute("hello", Attribute.Type.INT))
		);
		
		assertFalse(
				(new Attribute("asdff", Attribute.Type.INT))
				.equals
				(new Attribute("hello", Attribute.Type.INT))
		);
		
		try {
			assertFalse(
					(new Attribute("hello", Attribute.Type.CHAR, 50))
					.equals
					(new Attribute("hello", Attribute.Type.CHAR, 49))
					);
		} catch (InvalidAttributeException e) {
			fail(e.getMessage());
			throw e;
		}
	}
	
	/**
	 * Test attribute name check.
	 */
	
	@Test
	public void testValidAttrName() {
		assertTrue(Attribute.validName("king_kong"));
		assertFalse(Attribute.validName("1monkeys_"));
		assertTrue(Attribute.validName("_"));
		assertFalse(Attribute.validName("123"));
		assertFalse(Attribute.validName("Hello world!"));
	}

}
