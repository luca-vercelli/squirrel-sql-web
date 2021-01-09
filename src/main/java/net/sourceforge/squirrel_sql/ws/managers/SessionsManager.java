package net.sourceforge.squirrel_sql.ws.managers;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IntegerIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriver;
import net.sourceforge.squirrel_sql.ws.exceptions.AuthorizationException;
import net.sourceforge.squirrel_sql.ws.resources.SessionsEndpoint;

/**
 * Manages HTTP Sessions.
 * 
 * Keep all currently open sessions of all users.
 * 
 * @author lv 2020
 *
 */
@Singleton
public class SessionsManager {

	@Inject
	WebApplication webapp;
	@Inject
	AliasesManager aliasesManager;
	@Inject
	DriversManager driversManager;
	@Inject
	TokensManager tokensManager;

	/**
	 * A map of all open sessions, grouped by login token
	 */
	protected Map<String, Set<ISession>> openSessions = new HashMap<>();

	Logger logger = Logger.getLogger(SessionsEndpoint.class);

	/**
	 * If the user leave some open sessions, close them at last
	 */
	@PreDestroy
	public void disconnectReallyReallyAll() {
		for (Set<ISession> sessions : openSessions.values()) {
			for (ISession session : sessions) {
				internalDisconnect(session);
				logger.info("Just closed session '" + session.getTitle() + "'");
			}
			sessions.clear();
		}
		openSessions.clear();
	}

	private IntegerIdentifier getSessionId(String stringId) {
		int intId = Integer.parseInt(stringId);
		IntegerIdentifier id = new IntegerIdentifier(intId);
		return id;
	}

	/**
	 * Return all SQL sessions opened with current JWT token
	 * 
	 * @param token Authentication JWT token
	 */
	public Set<ISession> getOpenSessions(String token) {
		Set<ISession> sessions = openSessions.get(token);
		return (sessions == null) ? Collections.emptySet() : Collections.unmodifiableSet(sessions);
	}

	/**
	 * Return the SQL session with given identifier, <i>if</i> it was opened with
	 * current JWT token
	 * 
	 * @param token Authentication JWT token
	 * @return null if not found
	 */
	public ISession getSessionById(IIdentifier id, String token) {
		for (ISession session : getOpenSessions(token)) {
			logger.info("DEBUG ======= COMPARING SESSION ID: '" + session.getIdentifier());
			if (session.getIdentifier().equals(id)) {
				return session;
			}
		}
		logger.error("NO SESSION FOUND FOR ID:" + id);
		return null;
	}

	/**
	 * Return the SQL session with given identifier, <i>if</i> it was opened with
	 * given JWT token
	 * 
	 * @param token Authentication JWT token
	 * @return null if not found
	 */
	public ISession getSessionById(String id, String token) {
		return getSessionById(getSessionId(id), token);
	}

	/**
	 * Return the SQL session with given identifier, <i>if</i> it was opened with
	 * current JWT token
	 * 
	 * @return null if not found
	 * @throws AuthorizationException
	 */
	public ISession getSessionById(String id) throws AuthorizationException {
		return getSessionById(getSessionId(id), tokensManager.getCurrentToken());
	}

	/**
	 * Create a new SQL session, and also a SQLConnection if needed
	 * 
	 * @param token Authentication JWT token
	 */
	public ISession connect(String aliasIdentifier, String user, String passwd, String token) {

		// Here, we create ISession and all related stuff
		SQLAlias alias = aliasesManager.getAliasById(aliasIdentifier);
		SQLDriver driver = driversManager.getDriverById(alias.getDriverIdentifier());
		SQLConnection conn = aliasesManager.createConnection(alias, user, passwd);
		ISession session = webapp.getSessionManager().createSession(webapp, driver, alias, conn, user, passwd);

		// Now, we save ISession in openSessions
		openSessionsAdd(session, token);
		return session;
	}

	/**
	 * Disconnect an existing SQL session, <i>if</i> it was opened with current JWT
	 * token, and also the relative SQLConnection if needed.
	 * 
	 * If session does not exist, do nothing.
	 * 
	 * @param sessionId
	 * @param token     Authentication JWT token
	 */
	public void disconnect(String sessionId, String token) {
		ISession session = getSessionById(sessionId, token);
		if (session == null) {
			return; // ok, it was closed
		}
		internalDisconnect(session);
		openSessionsRemove(session, token);
	}

	/**
	 * Disconnect an existing SQL session, and also the relative SQLConnection.
	 * 
	 * Do not remove SQL session from openSessions.
	 * 
	 * @param session
	 * @param token   Authentication JWT token
	 */
	protected void internalDisconnect(ISession session) {
		if (session == null) {
			logger.warn("Trying to close non-existing session");
			return;
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
	}

	/**
	 * Add item to internal openSessions structure
	 * 
	 * @param session
	 * @param token   Authentication JWT token
	 */
	protected void openSessionsAdd(ISession session, String token) {
		if (!openSessions.containsKey(token)) {
			openSessions.put(token, new HashSet<>());
		}
		openSessions.get(token).add(session);
	}

	/**
	 * Remove item from internal openSessions structure
	 * 
	 * @param session
	 * @param token   Authentication JWT token
	 */
	protected void openSessionsRemove(ISession session, String token) {
		openSessions.get(token).remove(session);
		if (openSessions.get(token).isEmpty()) {
			openSessions.remove(token);
		}
	}

	/**
	 * Disconnect all sessions of currently logged user.
	 * 
	 * @param token Authentication JWT token
	 */
	public void disconnectAll(String token) {
		Set<ISession> sessions = openSessions.get(token);
		for (ISession session : sessions) {
			internalDisconnect(session);
		}
		sessions.clear();
		openSessions.remove(token);
	}

	/**
	 * Set all (?) properties from given bean into Session object.
	 * 
	 * @param session
	 * @param newProps
	 */
	public void setProperties(ISession session, SessionProperties newProps) {
		SessionProperties prop = session.getProperties();
		prop.setAbortOnError(newProps.getAbortOnError());
		prop.setAllowCtrlBJumpToObjectTree(newProps.getAllowCtrlBJumpToObjectTree());
		prop.setAllowCtrlMouseClickJumpToObjectTree(newProps.getAllowCtrlMouseClickJumpToObjectTree());
		prop.setAutoCommit(newProps.getAutoCommit());
		prop.setCatalogFilterExclude(newProps.getCatalogFilterExclude());
		prop.setCatalogFilterInclude(newProps.getCatalogFilterInclude());
		prop.setCommitOnClosingConnection(newProps.getCommitOnClosingConnection());
		prop.setContentsLimitRows(newProps.getContentsLimitRows());
		prop.setContentsNbrRowsToShow(newProps.getContentsNbrRowsToShow());
		prop.setFontInfo(newProps.getFontInfo());
		prop.setKeepTableLayoutOnRerun(newProps.getKeepTableLayoutOnRerun());
		prop.setLimitSQLEntryHistorySize(newProps.getLimitSQLEntryHistorySize());
		prop.setLimitSQLResultTabs(newProps.getLimitSQLResultTabs());
		prop.setLoadColumnsInBackground(newProps.getLoadColumnsInBackground());
		prop.setLoadConnectionsCurrentCatalogOnly(newProps.getLoadConnectionsCurrentCatalogOnly());
		prop.setLoadSchemasCatalogs(newProps.getLoadSchemasCatalogs());
		prop.setMainTabPlacement(newProps.getMainTabPlacement());
		prop.setMetaDataOutputClassName(newProps.getMetaDataOutputClassName());
		prop.setObjectFilterExclude(newProps.getObjectFilterExclude());
		prop.setObjectFilterInclude(newProps.getObjectFilterInclude());
		prop.setObjectTabPlacement(newProps.getObjectTabPlacement());
		prop.setRemoveMultiLineComment(newProps.getRemoveMultiLineComment());
		prop.setSchemaFilterExclude(newProps.getSchemaFilterExclude());
		prop.setSchemaFilterInclude(newProps.getSchemaFilterInclude());
		prop.setShowResultsMetaData(newProps.getShowResultsMetaData());
		prop.setShowRowCount(newProps.getShowRowCount());
		prop.setShowSQLErrorsInTab(newProps.getShowSQLErrorsInTab());
		prop.setShowToolBar(newProps.getShowToolBar());
		prop.setSQLEntryHistorySize(newProps.getSQLEntryHistorySize());
		prop.setSQLExecutionTabPlacement(newProps.getSQLExecutionTabPlacement());
		prop.setSQLFetchSize(newProps.getSQLFetchSize());
		prop.setSQLLimitRows(newProps.getSQLLimitRows());
		prop.setSQLUseFetchSize(newProps.getSQLUseFetchSize());
		prop.setStartOfLineComment(newProps.getStartOfLineComment());
		prop.setSQLNbrRowsToShow(newProps.getSQLNbrRowsToShow());
		prop.setTableContentsOutputClassName(newProps.getTableContentsOutputClassName());
		prop.setTableTypeFilterExclude(newProps.getTableTypeFilterExclude());
		prop.setTableTypeFilterInclude(newProps.getTableTypeFilterInclude());
		prop.setWriteSQLErrorsToLog(newProps.getWriteSQLErrorsToLog());
	}

	/**
	 * Throw exception if session is null
	 * 
	 * @param session
	 */
	public void checkSession(ISession session) {
		if (session == null) {
			throw new WebApplicationException("Invalid session id", Status.BAD_REQUEST);
		}
	}

}
