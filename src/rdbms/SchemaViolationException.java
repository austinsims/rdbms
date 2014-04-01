package rdbms;

public class SchemaViolationException extends RDBMSException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8303571189018390460L;

	public SchemaViolationException(String m) {
		super(m);
	}
}
