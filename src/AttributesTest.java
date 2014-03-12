import static org.junit.Assert.*;

import org.junit.Test;


public class AttributesTest {

	@Test
	public void testContains() {
		Attributes attrs = new Attributes();
		String name = "whatever";
		attrs.add(new Attribute(name, Attribute.Type.INT));
		assertTrue(attrs.contains(name));
		assertFalse(attrs.contains("SomethingElse"));
	}

}
