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

}
