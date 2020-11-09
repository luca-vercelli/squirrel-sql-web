package net.sourceforge.squirrel_sql.ws.managers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PreDestroy;
import javax.ejb.Stateful;
import javax.inject.Inject;

import org.apache.log4j.Logger;

import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.session.ISQLExecuterHandler;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLExecutionHandlerPublic;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IntegerIdentifier;
import net.sourceforge.squirrel_sql.fw.id.UidIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriver;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.ws.model.Table;
import net.sourceforge.squirrel_sql.ws.resources.SessionsEndpoint;

/**
 * Manages HTTP Sessions.
 * 
 * This bean is Stateful: it keeps only the sessions opened by the current HTTP
 * session.
 * 
 * @author lv 2020
 *
 */
@Stateful
public class SessionsManager {

	@Inject
	WebApplication webapp;
	@Inject
	AliasesManager aliasesManager;
	@Inject
	DriversManager driversManager;

	protected Set<ISession> openSessions = new HashSet<>();

	Logger logger = Logger.getLogger(SessionsEndpoint.class);

	/**
	 * If the user leave some open sessions, close them at last
	 */
	@PreDestroy
	public void preDestroy() {
		for (ISession session : openSessions) {
			disconnect(session);
			logger.info("Just closed session '" + session.getTitle() + "'");
		}
	}

	/**
	 * Return all SQL sessions opened in current HTTP session
	 */
	public Set<ISession> getConnectedSessions() {
		return Collections.unmodifiableSet(openSessions);
	}

	/**
	 * Return the SQL session with given identifier, <i>if</i> it was opened in
	 * current HTTP session
	 */
	public ISession getSessionById(IIdentifier id) {
		for (ISession session : openSessions) {
			logger.info("COMPARING SESSION ID: '" + session.getIdentifier());
			if (session.getIdentifier().equals(id)) {
				return session;
			}
		}
		logger.error("NO SESSION FOUND FOR ID:" + id);
		return null;
	}

	/**
	 * Return the SQL session with given identifier, <i>if</i> it was opened in
	 * current HTTP session
	 */
	public ISession getSessionById(String id) {
		return getSessionById(getSessionId(id));
	}

	/**
	 * Create a new SQL session, and also a SQLConnection if needed
	 */
	public ISession connect(String aliasIdentifier, String user, String passwd) {
		SQLAlias alias = aliasesManager.getAliasById(aliasIdentifier);
		SQLDriver driver = driversManager.getDriverById(alias.getDriverIdentifier());
		SQLConnection conn = aliasesManager.createConnection(alias, user, passwd);
		ISession session = webapp.getSessionManager().createSession(webapp, driver, alias, conn, user, passwd);
		openSessions.add(session);
		return session;
	}

	/**
	 * Disconnect an existing SQL session, <i>if</i> it was opened in current HTTP
	 * session, and also the relative SQLConnection if needed
	 * 
	 * @param sessionId
	 * @return
	 */
	public ISession disconnect(String sessionId) {
		ISession session = getSessionById(sessionId);
		return disconnect(session);
	}

	/**
	 * Disconnect an existing SQL session, <i>if</i> it was opened in current HTTP
	 * session, and also the relative SQLConnection if needed
	 * 
	 * @param session
	 * @return
	 */
	public ISession disconnect(ISession session) {
		if (session == null) {
			logger.warn("Trying to close non-existing session");
			return null;
		}
		try {
			session.getSQLConnection().close();
		} catch (SQLException e) {
			logger.error("Exception while closing SQLConnection", e);
		}
		try {
			session.close();
		} catch (SQLException e) {
			logger.error("Exception while closing Session", e);
		}
		openSessions.remove(session);
		return session;
	}

	private IntegerIdentifier getSessionId(String stringId) {
		int intId = Integer.parseInt(stringId);
		IntegerIdentifier id = new IntegerIdentifier(intId);
		return id;
	}

	// In SQuirreLSQL Core, the SQLExecuterTask is reached through the chain:
	// Session -> SessionPanel -> MainPanel -> SQLPanel.runCurrentExecuter() ->
	// SQLResultExecuterPanel -> SQLExecutionHandler -> SQLExecuterTask
	// and the Runnable is executed within a ThreadPool
	// We don't need such a mess
	public Table executeQuery(String sessionId, String query) throws SQLException {
		ISession session = getSessionById(sessionId);

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
	public Table rs2table(ResultSet rs) throws SQLException {
		Table t = new Table();

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
