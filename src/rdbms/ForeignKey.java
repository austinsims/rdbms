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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((domesticAttribute == null) ? 0 : domesticAttribute.hashCode());
		result = prime * result + ((foreignAttribute == null) ? 0 : foreignAttribute.hashCode());
		result = prime * result + ((foreignTable == null) ? 0 : foreignTable.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ForeignKey))
			return false;
		ForeignKey other = (ForeignKey) obj;
		if (domesticAttribute == null) {
			if (other.domesticAttribute != null)
				return false;
		} else if (!domesticAttribute.equals(other.domesticAttribute))
			return false;
		if (foreignAttribute == null) {
			if (other.foreignAttribute != null)
				return false;
		} else if (!foreignAttribute.equals(other.foreignAttribute))
			return false;
		if (foreignTable == null) {
			if (other.foreignTable != null)
				return false;
		} else if (!foreignTable.equals(other.foreignTable))
			return false;
		return true;
	}
	
	
	
}