import static org.junit.Assert.*;
import java.util.Arrays;
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

	@Test
	public void testEquals() throws Exception {
		try {
			Attributes a1 = new Attributes();
			a1.addAll(Arrays.asList(new Attribute[] {
					new Attribute("name", Attribute.Type.CHAR, 50),
					new Attribute("age", Attribute.Type.INT),
					new Attribute("height", Attribute.Type.INT) }));
			
			Attributes a2 = new Attributes();
			a2.addAll(Arrays.asList(new Attribute[] {
					new Attribute("name", Attribute.Type.CHAR, 50),
					new Attribute("age", Attribute.Type.INT),
					new Attribute("height", Attribute.Type.INT) }));
			
			Attributes b1 = new Attributes();
			a2.addAll(Arrays.asList(new Attribute[] {
					new Attribute("name", Attribute.Type.CHAR, 40),
					new Attribute("age", Attribute.Type.INT),
					new Attribute("height", Attribute.Type.INT) }));
			
			Attributes b2 = new Attributes();
			a2.addAll(Arrays.asList(new Attribute[] {
					new Attribute("name", Attribute.Type.CHAR, 40),
					new Attribute("height", Attribute.Type.INT) }));
			
			assertTrue(a1.equals(a2));
			assertFalse(a1.equals(b1));
			assertFalse(a1.equals(b2));
			
		} catch (Exception e) {
			fail(e.getMessage());
			throw e;
		}
	}
}
