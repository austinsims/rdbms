package rdbms;
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
			b1.addAll(Arrays.asList(new Attribute[] {
					new Attribute("name", Attribute.Type.CHAR, 40),
					new Attribute("age", Attribute.Type.INT),
					new Attribute("height", Attribute.Type.INT) }));
			
			Attributes b2 = new Attributes();
			b2.addAll(Arrays.asList(new Attribute[] {
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
	
	@Test
	public void testContainsASameNameAttr() {
		Attributes sa = new Attributes();
		sa.add(new Attribute("a", Attribute.Type.INT));
		
		Attributes sb1 = new Attributes();
		sb1.add(new Attribute("b", Attribute.Type.INT));
		
		Attributes sb2 = new Attributes();
		sb2.add(new Attribute("b", Attribute.Type.INT));
		
		assertFalse(sa.containsASameNameAttr(sb1));
		assertFalse(sa.containsASameNameAttr(sb2));
		assertTrue(sb1.containsASameNameAttr(sb2));
		assertTrue(sb2.containsASameNameAttr(sb1));
	}
}
