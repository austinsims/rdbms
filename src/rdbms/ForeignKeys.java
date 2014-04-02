package rdbms;

import java.util.HashSet;

public class ForeignKeys extends HashSet<ForeignKey> {
	public ForeignKey getByDomesticAttr(Attribute domesticAttr) {
		for (ForeignKey fk : this) {
			if (fk.domesticAttribute.equals(domesticAttr))
				return fk;
		}
		return null;
	}
}
