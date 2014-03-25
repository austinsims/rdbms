package rdbms;

public class SchemaViolationException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8303571189018390460L;

	public SchemaViolationException(String m) {
		super(m);
	}
}
