package rdbms;

import java.io.Serializable;

class ForeignKey implements Serializable {
	Attribute domesticAttribute;
	Table foreignTable;
	Attribute foreignAttribute;

	ForeignKey(Attribute domesticAttribute, Table foreignTable, Attribute foreignAttribute) throws SchemaViolationException {
		this.domesticAttribute = domesticAttribute;
		this.foreignTable = foreignTable;
		Attributes foreignPk = foreignTable.pk;
		if (foreignPk.size() != 1 || !foreignAttribute.equals(foreignPk.get(0)))
			throw new SchemaViolationException("Foreign keys may only be specified on a single-attribute primary key.");
		this.foreignAttribute = foreignAttribute;
	}
	
	@Override
	public boolean equals(Object o) {
		ForeignKey other;
		try {
			other = (ForeignKey) o;
		} catch (ClassCastException e) {
			return false;
		}
		
		if (!this.domesticAttribute.equals(other.domesticAttribute))
			return false;
		if (!this.foreignTable.equals(other.foreignTable))
			return false;
		if (!this.foreignAttribute.equals(other.foreignAttribute))
			return false;
		
		return true;
	}
}