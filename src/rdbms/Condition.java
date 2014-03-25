package rdbms;

public interface Condition {
	public boolean test(Row row) throws SchemaViolationException;
}
