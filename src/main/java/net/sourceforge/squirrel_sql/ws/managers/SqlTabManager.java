package net.sourceforge.squirrel_sql.ws.managers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.log4j.Logger;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.dto.TableDto;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

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

	Logger logger = Logger.getLogger(SqlTabManager.class);

	// In SQuirreLSQL Core, the SQLExecuterTask is reached through the chain:
	// Session -> SessionPanel -> MainPanel -> SQLPanel.runCurrentExecuter() ->
	// SQLResultExecuterPanel -> SQLExecutionHandler -> SQLExecuterTask
	// and the Runnable is executed within a ThreadPool
	// We don't need such a mess
	public TableDto executeQuery(String sessionId, String query) throws SQLException {
		ISession session = sessionsManager.getSessionById(sessionId);

		query = StringUtilities.cleanString(query);
		logger.info("Running query: " + query);

		Connection conn = session.getSQLConnection().getConnection();
		try (Statement stmt = conn.createStatement()) {
			try (ResultSet rs = stmt.executeQuery(query)) {
				return rs2table(rs);
			}
		}
	}

	/**
	 * Convert a ResultSet into a Table
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	public TableDto rs2table(ResultSet rs) throws SQLException {
		TableDto t = new TableDto();

		// Metadata
		ResultSetMetaData md = rs.getMetaData();
		String[] names = new String[md.getColumnCount()];
		for (int i = 0; i < names.length; ++i) {
			names[i] = md.getColumnName(i + 1);
		}
		t.setColumnHeaders(names);

		// Data
		while (rs.next()) {
			Object[] row = new Object[names.length];
			for (int i = 0; i < names.length; ++i) {
				row[i] = rs.getObject(i + 1);
			}
			t.getRows().add(row);
		}

		return t;
	}
}
