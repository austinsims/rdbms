package rdbms;

import java.io.Serializable;

public interface Condition extends Serializable {
	public boolean test(Row row) throws SchemaViolationException;
}
