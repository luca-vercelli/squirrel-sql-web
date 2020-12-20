package net.sourceforge.squirrel_sql.ws.managers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.apache.log4j.Logger;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.DialectType;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.ws.exceptions.AuthorizationException;

/**
 * Manages statements execution.
 * 
 * @author lv 2020
 *
 */
@Stateless
public class SqlTabManager {

	@Inject
	WebApplication webapp;
	@Inject
	SessionsManager sessionsManager;
	@Inject
	TokensManager tokensManager;
	@Context
	HttpServletRequest request;

	Logger logger = Logger.getLogger(SqlTabManager.class);

	/**
	 * Return token in current Request.
	 * 
	 * @return
	 */
	protected String getCurrentToken() {
		try {
			return tokensManager.extractTokenFromRequest(request);
		} catch (AuthorizationException e) {
			throw new IllegalStateException("Error retrieving token. This should not happen.", e);
		}
	}

	// In SQuirreLSQL Core, the SQLExecuterTask is reached through the chain:
	// Session -> SessionPanel -> MainPanel -> SQLPanel.runCurrentExecuter() ->
	// SQLResultExecuterPanel -> SQLExecutionHandler -> SQLExecuterTask
	// and the Runnable is executed within a ThreadPool
	// We don't need such a mess

	/**
	 * Execute every kind of query (SELECT, UPDATE, CREATE TABLE, ...).
	 * 
	 * Execute only single queries, not multiple queries.
	 * 
	 * @param session
	 * @param query
	 * @return a TableDto in case of SELECT, null otherwise
	 * @throws SQLException
	 * @throws DataSetException
	 */
	public IDataSet executeSqlCommand(ISession session, String query) throws DataSetException {

		query = StringUtilities.cleanString(query);
		logger.info("Running query: " + query);

		// Following code is essentially copied from RowCountTab.java

		final ISQLConnection conn = session.getSQLConnection();
		try {
			final Statement stmt = conn.createStatement();
			try {
				final boolean returnResultSet = stmt.execute(query);
				if (!returnResultSet) {
					// Not a SELECT
					return null;
				}
				final ResultSet rs = stmt.getResultSet();
				try {
					final ResultSetDataSet rsds = new ResultSetDataSet();
					rsds.setResultSet(rs, getDialectType(session));
					return rsds;
				} finally {
					rs.close();
				}
			} finally {
				stmt.close();
			}
		} catch (SQLException ex) {
			throw new DataSetException(ex);
		}
	}

	public DialectType getDialectType(ISession session) {
		return DialectFactory.getDialectType(session.getMetaData());
	}
}
