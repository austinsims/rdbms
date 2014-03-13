import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;


public class TableTest {

	/**
	 * Make sure attributes come out in the same order they went in
	 */
	@Test
	public void testAttrOrder() {
		Attribute[] in = {
				new Attribute("first", Attribute.Type.INT),
				new Attribute("second", Attribute.Type.INT),
				new Attribute("third", Attribute.Type.INT),
		};
		
		Attributes pk = new Attributes();
		pk.add(in[0]);
		
		Attributes attrs = new Attributes();
		attrs.addAll(Arrays.asList(in));
		
		Table t = new Table("test", attrs, pk);
		
		// mysterious as to why i can't cast Attributes.toArray() to Attribute[]
		Object[] out = t.getAttributes().toArray();
		
		assertEquals(in,out);
	}

}
