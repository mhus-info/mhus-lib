package de.mhus.lib.sql;

import java.sql.SQLException;
import java.util.Map;

import de.mhus.lib.core.lang.MObject;

/**
 * This represents a qyery statement. Use it to execute queries.
 * 
 * @author mikehummel
 *
 */
public abstract class DbStatement extends MObject {

	public static final String RETURN_BINARY_KEY = "return_binary_attribute_";

	protected void finalize() throws Throwable {
		close();
	};
	
	/**
	 * Executes the given SQL statement, which may return multiple results. In this statement
	 * InputStream as attribute values are allowed.
	 * 
	 * @See Statement.execute
	 * @param attributes 
	 * @return
	 * @throws Exception
	 */
	public abstract boolean execute(Map<String, Object> attributes) throws Exception;
	
	public abstract DbResult getResultSet() throws SQLException;
	
	public abstract int getUpdateCount() throws SQLException;

	/**
	 * Return the result of an select query.
	 * 
	 * @param attributes
	 * @return
	 * @throws Exception
	 */
	public abstract DbResult executeQuery(Map<String, Object> attributes) throws Exception;
	
	/**
	 * Return the result of an update query. In the attributes InputStreams are allowed (blobs).
	 * 
	 * @param attributes
	 * @return
	 * @throws Exception
	 */
	public abstract int executeUpdate(Map<String, Object> attributes) throws Exception;

	/**
	 * Return the used connection.
	 * 
	 * @return
	 */
	public abstract DbConnection getConnection();
	
	/**
	 * Close the statement and free resources.
	 */
	public abstract void close();

	/**
	 * This is a small hack to add binaries (BLOB) to the statement. The blob is stored in the attributes and will
	 * e used in execute time to be added to the statement. See JdbcStatement.
	 * 
	 * @param attributes
	 * @param value
	 */
	public static void addBinary(Map<String, Object> attributes, Object value) {
		int nr = 0;
		while (attributes.containsKey(DbStatement.RETURN_BINARY_KEY + nr)) nr++;
		attributes.put(DbStatement.RETURN_BINARY_KEY + nr, value);
	}
	
}